package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;


/**
 * Created by lasyaboddapati on 3/6/15.
 */
public class AddExpense extends Activity {
    boolean dateValid;
    boolean amountValid;
    Intent returnIntent;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        returnIntent = getIntent();
        db = new ExpenseDatabase(this).getWritableDatabase();
        SystemNotificationFragment.initialize(this);

        final EditText dateEditText = (EditText) findViewById(R.id.dateEditText);
        final EditText amountSpentEditText = (EditText) findViewById(R.id.amountSpentEditText);
        final EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        Button cancel = (Button) findViewById(R.id.cancelButton);
        final Button save = (Button) findViewById(R.id.saveButton);
        save.setEnabled(false);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent dashboard = new Intent(Home.class);
                //startActivity(dashboard);
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = dateEditText.getText().toString();
                date = date+"/"+Calendar.getInstance().get(Calendar.YEAR);
                String amountSpent = amountSpentEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                addItemtoDb(date, amountSpent, description);
                SystemNotificationFragment.limit_exceeded_check(date);

                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String working = s.toString();
                boolean isValid = true;
                if (working.length()==2 && before ==0) {
                    if (working.substring(1,2).matches("[/.-]")) {
                        working = "0"+working.substring(0,1);
                    }
                    if (Integer.parseInt(working) < 1 || Integer.parseInt(working)>12) {
                        isValid = false;
                    } else {
                        if (working.length() == 1) {
                            working = "0"+working;
                        }
                        working+="/";
                        dateEditText.setText(working);
                        dateEditText.setSelection(working.length());
                    }
                } else if (working.length()==5 && before ==0) {
                    if (working.substring(4, 5).matches("[/.-]")) {
                        working = working.substring(0, 3) + "0" + working.substring(3, 4);
                    }
                    dateEditText.setText(working);
                    dateEditText.setSelection(working.length());
                } else if(working.length() != 5) {
                    isValid = false;
                }

                if (!isValid) {
                    dateEditText.setError("Enter a valid date: MM/DD");
                    dateValid = false;
                } else {
                    dateEditText.setError(null);
                    dateValid = true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (dateValid && amountValid) {
                    save.setEnabled(true);
                } else {
                    save.setEnabled(false);
                }
            }
        });

        amountSpentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("AMOUNT", s.toString().isEmpty() + "");
                if (s.toString().isEmpty()) {
                    amountSpentEditText.setError("", getDrawable(android.R.drawable.stat_notify_error));
                    amountValid = false;
                } else {
                    amountSpentEditText.setError(null);
                    amountValid = true;
                }

                if (dateValid && amountValid) {
                    save.setEnabled(true);
                } else {
                    save.setEnabled(false);
                }
            }
        });
    }

    private void addItemtoDb(String date, String amount, String description) {
        ContentValues newValues = new ContentValues();

        int mm = Integer.parseInt(date.split("[/.-]")[0]);
        int dd = Integer.parseInt(date.split("[/.-]")[1]);

        newValues.put(ExpenseDatabase.MONTH_COLUMN, Months.nameOf(mm));
        int week = 0;
        if(dd>=1 && dd<=7) {
            week = 1;
        } else if(dd>=8 && dd<=14) {
            week = 2;
        } else if(dd>=15 && dd<=21) {
            week = 3;
        } else if(dd>=22 && dd<=31) {
            week = 4;
        }
        Log.d("WEEEEEEEEEEEEK", "Date is "+date+"     WEEK OF MONTH is "+week);

        newValues.put(ExpenseDatabase.DATE_COLUMN, date);
        newValues.put(ExpenseDatabase.WEEK_COLUMN, week);
        newValues.put(ExpenseDatabase.AMOUNT_COLUMN, amount);
        newValues.put(ExpenseDatabase.DESCRIPTION_COLUMN, description);
        Log.d("INSERT", "inserting into db");
        db.insert(ExpenseDatabase.DATABASE_TABLE, null, newValues);
    }
}