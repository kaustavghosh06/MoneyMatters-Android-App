package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Created by lasyaboddapati on 1/28/15.
 */
public class Expenses extends Activity implements CustomDialogFragment.CustomDialogListener {
    ExpensesListViewFragment expensesListViewFragment;
    ExpensesGraphViewFragment graphViewFragment;
    enum MONTHS {All, January, February, March, April, May, June, July, August, September, October, November, December};
    enum WEEKS {All, Week1, Week2, Week3, Week4};

    String currentView = "";
    boolean dateValid;
    boolean amountValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        //expensesListViewFragment = ExpensesListViewFragment.newInstance(Expenses.this);

        final Spinner MonthSpinner = (Spinner) findViewById(R.id.MonthSpinner);
        MonthSpinner.setAdapter(new ArrayAdapter<MONTHS>(this, android.R.layout.simple_spinner_item, MONTHS.values()));
        MonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("COEN268", "Month Position selected: " + MonthSpinner.getItemAtPosition(position));
                //TODO : Filter items according to month selected
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Log.d("COEN268", "Month Nothing selected");
                //TODO: Show items for all months
            }
        });

        final Spinner WeekSpinner = (Spinner) findViewById(R.id.WeekSpinner);
        WeekSpinner.setAdapter(new ArrayAdapter<WEEKS>(this, android.R.layout.simple_spinner_item, WEEKS.values()));
        WeekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("COEN268", "Week Position selected: " + WeekSpinner.getItemAtPosition(position));
                //TODO: Filter items according to week selected
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("COEN268", "Nothing selected");
                //TODO: Show items for all weeks
            }
        });

        /*final String[] ViewType = {"List", "Graph"};
        final Spinner ViewTypeSpinner = (Spinner) findViewById(R.id.ViewTypeSpinner);
        ViewTypeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ViewType));

        ViewTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String viewSelected = ViewTypeSpinner.getItemAtPosition(position).toString();
                if (viewSelected == "List" && currentView != "List") {
                    generateListView();
                    currentView = "List";
                }
                else if (viewSelected == "Graph" && currentView != "Graph") {
                    generateGraphView();
                    currentView = "Graph";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Log.d("COEN268", "Nothing selected");
            }
        });*/
        generateListView();
        generateGraphView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expenses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            pop_up_add_expense_dialog();
        }
        else if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pop_up_add_expense_dialog() {
        dateValid = false;
        amountValid = false;

        View view = getLayoutInflater().inflate(R.layout.add_expense_layout, null);

        final String[] categories = {"Bills", "Rent", "Groceries", "Food", "Personal", "Shopping"};
        Spinner categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        categorySpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories));

        final CustomDialogFragment newFragment = CustomDialogFragment.newInstance(view);

        final EditText date = (EditText) view.findViewById(R.id.dateEditText);
        date.addTextChangedListener(new TextWatcher() {
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
                        date.setText(working);
                        date.setSelection(working.length());
                    }
                } else if (working.length()==5 && before ==0) {
                    if (working.substring(4,5).matches("[/.-]")) {
                        working = working.substring(0,3)+"0"+working.substring(3,4);
                    }
                    working+="/";
                    date.setText(working);
                    date.setSelection(working.length());
                } else if (working.length()==10 && before ==0) {
                    int yyyy = Integer.parseInt(working.substring(6));
                    int mm = Integer.parseInt(working.substring(0,2));
                    int dd = Integer.parseInt(working.substring(3,5));
                    //if (yyyy > Calendar.getInstance().get(Calendar.YEAR)) {
                    //    isValid = false;
                    //}
                    //else {
                        Calendar c = new GregorianCalendar(yyyy, mm, dd);
                        //c.set(Calendar.YEAR, yyyy);
                        //c.set(Calendar.MONTH, mm);
                        Log.d("DATE", c.get(Calendar.MONTH)+"");
                        Log.d("DATE", c.getActualMaximum(Calendar.DATE)+"");
                        if (dd<1 || dd>c.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                            isValid = false;
                        }
                    //}
                } else if (working.length()!=10) {
                    isValid = false;
                }

                if (!isValid) {
                    date.setError("Enter a valid date: MM/DD/YYYY");
                    dateValid = false;
                } else {
                    date.setError(null);
                    dateValid = true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (dateValid && amountValid) {
                    newFragment.dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    newFragment.dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        final EditText amount = (EditText) view.findViewById(R.id.amountSpentEditText);
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("AMOUNT", s.toString().isEmpty() + "");
                if (s.toString().isEmpty()) {
                    amount.setError("", getDrawable(android.R.drawable.stat_notify_error));
                    amountValid = false;
                } else {
                    amount.setError(null);
                    amountValid = true;
                }

                if (dateValid && amountValid) {
                    newFragment.dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
                else {
                    newFragment.dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onDialogPositiveClick(CustomDialogFragment dialog) {
        View view = dialog.getDialogView();
        String date = ((EditText) view.findViewById(R.id.dateEditText)).getText().toString();

        String category = ((Spinner) view.findViewById(R.id.categorySpinner)).getSelectedItem().toString();
        float amountSpent = Float.parseFloat(((EditText) view.findViewById(R.id.amountSpentEditText)).getText().toString());
        String description = ((EditText) view.findViewById(R.id.desciptionEditText)).getText().toString();
        expensesListViewFragment.adapter.addItem(date, category, amountSpent, description);
        graphViewFragment.populateGraphView();
    }

    @Override
    public void onDialogNegativeClick(CustomDialogFragment dialog) {
        //Toast.makeText(Expenses.this, "cancelled", Toast.LENGTH_SHORT).show();
    }

    private void generateListView() {
        expensesListViewFragment = ExpensesListViewFragment.newInstance(Expenses.this);
        //expensesListViewFragment.adapter.populateListView();
        getFragmentManager().beginTransaction().add(R.id.listViewContainer, expensesListViewFragment)
                .commit();
    }

    //TODO : Generate Graph Layout
    private void generateGraphView() {
        graphViewFragment = ExpensesGraphViewFragment.newInstance(Expenses.this);
        getFragmentManager().beginTransaction().add(R.id.graphViewContainer, graphViewFragment)
                .commit();
    }

}
