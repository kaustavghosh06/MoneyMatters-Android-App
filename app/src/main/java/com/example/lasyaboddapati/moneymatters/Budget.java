package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by lasyaboddapati on 1/30/15.
 */
public class Budget extends Activity implements CustomDialogFragment.CustomDialogListener {
    BudgetListViewFragment budgetListViewFragment;
    BudgetGraphViewFragment graphViewFragment;

    enum MONTHS {All, January, February, March, April, May, June, July, August, September, October, November, December};
    enum WEEKS {All, Week1, Week2, Week3, Week4};

    String currentView = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        /*final Spinner MonthSpinner = (Spinner) findViewById(R.id.MonthSpinner);
        MonthSpinner.setAdapter(new ArrayAdapter<MONTHS>(this, android.R.layout.simple_spinner_item, MONTHS.values()));
        MonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO : Filter items according to month selected
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Show items for all months
            }
        });

        final String[] ViewType = {"List", "Graph"};
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

    private void generateListView() {
        budgetListViewFragment = BudgetListViewFragment.newInstance(Budget.this);
        budgetListViewFragment.adapter.populateListView();
        //getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, budgetListViewFragment)
        //        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        //        .commit();
        getFragmentManager().beginTransaction().add(R.id.listViewContainer, budgetListViewFragment)
                .commit();
    }

    private void generateGraphView() {
        graphViewFragment = BudgetGraphViewFragment.newInstance(Budget.this);
        //getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, budgetListViewFragment)
        //        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        //        .commit();
        getFragmentManager().beginTransaction().add(R.id.graphViewContainer, graphViewFragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_budget, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            pop_up_add_budget_dialog();
        }
        else if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pop_up_add_budget_dialog() {
        View view = getLayoutInflater().inflate(R.layout.add_budget_layout, null);
        Spinner monthSpinner = (Spinner) view.findViewById(R.id.monthSpinner);
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September"
                         , "October", "November", "December"};
        monthSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, months));

        final CustomDialogFragment newFragment = CustomDialogFragment.newInstance(view);

        final EditText monthlyBudget = (EditText) view.findViewById(R.id.monthlyBudgetEditText);
        monthlyBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()) {
                    monthlyBudget.setError("Enter monthly budget", getDrawable(android.R.drawable.stat_notify_error));
                    newFragment.dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    monthlyBudget.setError(null);
                    newFragment.dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }

            }
        });

        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onDialogPositiveClick(CustomDialogFragment dialog) {
        View view = dialog.getDialogView();
        String month = ((Spinner) view.findViewById(R.id.monthSpinner)).getSelectedItem().toString();
        String monthlyBudget = ((EditText) view.findViewById(R.id.monthlyBudgetEditText)).getText().toString();
        String[] weeklyBudget = new String[4];
        weeklyBudget[0] = ((EditText) view.findViewById(R.id.week1EditText)).getText().toString();
        weeklyBudget[1] = ((EditText) view.findViewById(R.id.week2EditText)).getText().toString();
        weeklyBudget[2] = ((EditText) view.findViewById(R.id.week3EditText)).getText().toString();
        weeklyBudget[3] = ((EditText) view.findViewById(R.id.week4EditText)).getText().toString();

        budgetListViewFragment.adapter.addItem(month, monthlyBudget, weeklyBudget);
        graphViewFragment.populateGraphView();
    }

    @Override
    public void onDialogNegativeClick(CustomDialogFragment dialog) {

    }

    /*private void deleteDB() {
        Budget.this.deleteDatabase(BudgetDatabase.DATABASE_TABLE);
    }*/
}
