package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;


/**
 * Created by lasyaboddapati on 1/28/15.
 */
public class Expenses extends Activity /*implements CustomDialogFragment.CustomDialogListener*/ {
    ExpensesListViewFragment expensesListViewFragment;
    ExpensesGraphViewFragment graphViewFragment;
    final String[] MONTHS = {"All", "January", "February", "March", "April", "May", "June", "July"
                           , "August", "September", "October", "November", "December"};
    final String[] WEEKS = {"All", "Week1", "Week2", "Week3", "Week4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        //deleteDB();
        graphViewFragment = ExpensesGraphViewFragment.newInstance(Expenses.this);
        expensesListViewFragment = ExpensesListViewFragment.newInstance(Expenses.this, graphViewFragment);


        final Spinner MonthSpinner = (Spinner) findViewById(R.id.MonthSpinner);
        MonthSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MONTHS));
        MonthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH)+1);

        final Spinner WeekSpinner = (Spinner) findViewById(R.id.WeekSpinner);
        WeekSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, WEEKS));

        TextView monthDisplay = (TextView) findViewById(R.id.monthDisplay);
        if (MonthSpinner.getSelectedItem()=="All") {
            monthDisplay.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        } else {
            monthDisplay.setText(MonthSpinner.getSelectedItem().toString()+ ", "
                    + Calendar.getInstance().get(Calendar.YEAR));
        }

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expensesListViewFragment.adapter.filterItems();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        };

        MonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    WeekSpinner.setEnabled(false);
                    WeekSpinner.setSelection(0);
                } else {
                    WeekSpinner.setEnabled(true);
                }
                expensesListViewFragment.adapter.filterItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        WeekSpinner.setOnItemSelectedListener(listener);

        generateListView();
        generateGraphView();
    }

    private void generateListView() {
        getFragmentManager().beginTransaction().add(R.id.listViewContainer, expensesListViewFragment)
                .commit();
    }

    private void generateGraphView() {
        getFragmentManager().beginTransaction().add(R.id.graphViewContainer, graphViewFragment)
                .commit();
    }

    private void deleteDB() {
        Expenses.this.deleteDatabase(ExpenseDatabase.DATABASE_TABLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expenses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add) {
            expensesListViewFragment.pop_up_add_expense_dialog();
        }
        return super.onOptionsItemSelected(item);
    }
}
