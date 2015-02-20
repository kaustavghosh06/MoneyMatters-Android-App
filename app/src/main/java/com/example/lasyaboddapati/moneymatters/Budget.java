package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by lasyaboddapati on 1/30/15.
 */
public class Budget extends Activity {
    BudgetListViewFragment budgetListViewFragment;
    BudgetGraphViewFragment graphViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        final Button monthButton = (Button) findViewById(R.id.monthButton);
        final Button yearButton = (Button) findViewById(R.id.yearButton);

        //yearButton.setBackgroundTintMode(PorterDuff.Mode.LIGHTEN);
        yearButton.setTextColor(Color.BLUE);

        yearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //yearButton.setBackgroundTintMode(PorterDuff.Mode.LIGHTEN);
                //monthButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                yearButton.setTextColor(Color.BLUE);
                monthButton.setTextColor(Color.GRAY);
                graphViewFragment.populateGraphView("year");
            }
        });

        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //monthButton.setBackgroundTintMode(PorterDuff.Mode.LIGHTEN);
                //yearButton.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                monthButton.setTextColor(Color.BLUE);
                yearButton.setTextColor(Color.GRAY);
                graphViewFragment.populateGraphView("month");
            }
        });

        graphViewFragment = BudgetGraphViewFragment.newInstance(Budget.this);
        budgetListViewFragment = BudgetListViewFragment.newInstance(Budget.this, graphViewFragment);

        generateListView();
        generateGraphView();
    }

    private void generateListView() {
        getFragmentManager().beginTransaction().add(R.id.listViewContainer, budgetListViewFragment)
                .commit();
    }

    private void generateGraphView() {
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
            budgetListViewFragment.pop_up_add_budget_dialog();
        }
        else if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*private void deleteDB() {
        Budget.this.deleteDatabase(BudgetDatabase.DATABASE_TABLE);
    }*/
}
