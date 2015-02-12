package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by lasyaboddapati on 1/28/15.
 */
public class HomeScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Button budgetButton = (Button) findViewById(R.id.budget);
        Button expensesButton = (Button) findViewById(R.id.expenses);
        Button loanButton = (Button) findViewById(R.id.loan);

        budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent budgetIntent = new Intent(HomeScreen.this, Budget.class);
                startActivity(budgetIntent);  //TODO: Implement Budget activity
            }
        });

        expensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent expenseIntent = new Intent(HomeScreen.this, Expenses.class);
                startActivity(expenseIntent);
            }
        });

        loanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loanIntent = new Intent(HomeScreen.this, Lending.class);
                startActivity(loanIntent);    //TODO: loan activity
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
