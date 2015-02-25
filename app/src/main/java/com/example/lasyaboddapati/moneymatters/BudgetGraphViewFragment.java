package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by lasyaboddapati on 2/16/15.
 */
public class BudgetGraphViewFragment extends Fragment {
    private SQLiteDatabase budgetDB;
    private SQLiteDatabase expenseDB;

    BarChart chart;
    ArrayList<String> xVals;
    ArrayList<BarEntry> budgetYVals;
    ArrayList<BarEntry> expenseYVals;
    Context context;

    ImageButton prev;
    ImageButton next;
    Button monthButton;
    Button yearButton;

    String currMonth;

    public static BudgetGraphViewFragment newInstance(Context context) {
        BudgetGraphViewFragment budgetGraphViewFragment = new BudgetGraphViewFragment();
        budgetGraphViewFragment.budgetDB = new BudgetDatabase(context).getReadableDatabase();
        budgetGraphViewFragment.expenseDB = new ExpenseDatabase(context).getReadableDatabase();
        budgetGraphViewFragment.context = context;

        int mm = Calendar.getInstance().get(Calendar.MONTH);
        budgetGraphViewFragment.currMonth = Months.nameOf(mm+1);

        return budgetGraphViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.budget_graph_view, container, false);

        monthButton = (Button) rootView.findViewById(R.id.monthButton);
        yearButton = (Button) rootView.findViewById(R.id.yearButton);
        chart = (BarChart) rootView.findViewById(R.id.chart);
        prev = (ImageButton) rootView.findViewById(R.id.prev);
        next = (ImageButton) rootView.findViewById(R.id.next);

        yearButton.setTextColor(Color.parseColor("#ff166441"));
        yearButton.setEnabled(false);
        monthButton.setTextColor(Color.GRAY);

        yearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearButton.setTextColor(Color.parseColor("#ff166441"));
                yearButton.setEnabled(false);
                monthButton.setTextColor(Color.GRAY);
                monthButton.setEnabled(true);
                populateGraphView("year");
            }
        });

        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthButton.setTextColor(Color.parseColor("#ff166441"));
                monthButton.setEnabled(false);
                yearButton.setTextColor(Color.GRAY);
                yearButton.setEnabled(true);
                populateGraphView("month");
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prevMonth = Months.names()[Months.valueOf(currMonth) - 1];
                Log.d("PREV", currMonth+" -> "+prevMonth);
                currMonth = prevMonth;
                populateGraphView("month");
                if (currMonth == Months.names()[0]) {
                    prev.setEnabled(false);
                }
                next.setEnabled(true);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nextMonth = Months.names()[Months.valueOf(currMonth)+1];
                Log.d("PREV", currMonth+" -> "+nextMonth);
                currMonth = nextMonth;
                populateGraphView("month");
                if (currMonth == Months.names()[11]) {
                    next.setEnabled(false);
                }
                prev.setEnabled(true);
            }
        });

        populateGraphView();
        return rootView;
    }

    public void populateGraphView() {
        Log.d("YEAR", yearButton.getCurrentTextColor()+"");
        Log.d("MONTH", monthButton.getCurrentTextColor()+"");

        if (!yearButton.isEnabled()) {
            Log.d("YEAR COLOR", yearButton.getCurrentTextColor()+"");
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
            populateGraphView("year");
        } else if (!monthButton.isEnabled()) {
            Log.d("MONTH COLOR", monthButton.getCurrentTextColor()+"");
            prev.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            populateGraphView("month");
        }
    }

    public void populateGraphView(String viewType) {
        xVals = new ArrayList<String>();
        budgetYVals = new ArrayList<BarEntry>();
        expenseYVals = new ArrayList<BarEntry>();

        if (viewType == "year") {
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
            populateYearGraphView();
        } else if (viewType == "month") {
            prev.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            populateMonthGraphView(currMonth);
        } else {
            Log.e("ERR", "Invalid View Type "+viewType);
        }

        BarDataSet budgetSet = new BarDataSet(budgetYVals, "Budget");
        budgetSet.setColor(Color.parseColor("#FF30B36E"));

        BarDataSet expensesSet = new BarDataSet(expenseYVals, "Expenses");
        expensesSet.setColor(Color.parseColor("#ff39e18d"));

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(expensesSet);
        dataSets.add(budgetSet);

        BarData data = new BarData(xVals, dataSets);
        data.setGroupSpace(110f);

        chart.setData(data);

        chart.setDrawValueAboveBar(true);
        chart.setValueFormatter(new LargeValueFormatter());

        chart.enableScroll();

        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setDrawHorizontalGrid(false);
        chart.setDrawVerticalGrid(false);

        chart.setDrawYValues(false);

        XLabels xl = chart.getXLabels();
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);
        xl.setCenterXLabelText(true);
        xl.setSpaceBetweenLabels(0);

        YLabels yl = chart.getYLabels();
        yl.setPosition(YLabels.YLabelPosition.BOTH_SIDED);
        yl.setLabelCount(5);
        yl.setFormatter(new LargeValueFormatter());

        chart.animateY(500);
        chart.invalidate();
    }

    public void populateYearGraphView() {
        String[] budgetResultColumns = {BudgetDatabase.MONTHLY_BUDGET_COLUMN};
        String[] expensesResultColumns = {ExpenseDatabase.AMOUNT_COLUMN };

        for (int i=0; i< Months.size(); i++) {
            String month = Months.names()[i];
            xVals.add(month.substring(0,3));

            String budgetWhereClause = BudgetDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
            Cursor budgetCursor = budgetDB.query(BudgetDatabase.DATABASE_TABLE, budgetResultColumns, budgetWhereClause, null, null, null, null);
            float monthlyBudget = 0;
            while (budgetCursor.moveToNext()) {
                monthlyBudget = budgetCursor.getFloat(0);
            }
            budgetCursor.close();
            budgetYVals.add(new BarEntry(monthlyBudget, i, month));

            String expensesWhereClause = ExpenseDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
            Cursor expenseCursor = expenseDB.query(ExpenseDatabase.DATABASE_TABLE, expensesResultColumns, expensesWhereClause, null, null, null, null);
            float monthlyExpense = 0;
            while (expenseCursor.moveToNext()) {
                monthlyExpense += expenseCursor.getFloat(0);
            }
            expenseCursor.close();
            expenseYVals.add(new BarEntry(monthlyExpense, i, month));
        }
        chart.setDescription("");

    }

    public void populateMonthGraphView(String month) {
        Log.d("MONTH", month);

        String[] budgetResultColumns = {BudgetDatabase.WEEK1_COLUMN, BudgetDatabase.WEEK2_COLUMN
                , BudgetDatabase.WEEK3_COLUMN, BudgetDatabase.WEEK4_COLUMN};
        String budgetWhereClause = BudgetDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
        Cursor budgetCursor = budgetDB.query(BudgetDatabase.DATABASE_TABLE, budgetResultColumns, budgetWhereClause, null, null, null, null);
        float[] weeklyBudget = new float[4];
        while (budgetCursor.moveToNext()) {
            for (int i = 0; i < 4; i++) {
                weeklyBudget[i] = budgetCursor.getFloat(i);
            }
        }
        budgetCursor.close();
        for (int i=0; i<4; i++) {
            String week = "Week " + (i + 1);
            Log.d("WEEK", "Week " + week + " budget = " + weeklyBudget[i]);
            budgetYVals.add(new BarEntry(weeklyBudget[i], i, week));
        }

        String[] expensesResultColumns = {ExpenseDatabase.WEEK_COLUMN, ExpenseDatabase.AMOUNT_COLUMN};
        String expensesWhereClause = ExpenseDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
        Cursor expenseCursor = expenseDB.query(ExpenseDatabase.DATABASE_TABLE, expensesResultColumns, expensesWhereClause, null, null, null, null);
        float[] weeklyExpenses = new float[4];
        while (expenseCursor.moveToNext()) {
            int week = expenseCursor.getInt(0);
            weeklyExpenses[week-1] += expenseCursor.getFloat(1);
            Log.d("WEEK", "Week "+week+" expense = "+weeklyExpenses[week-1]);
        }
        for (int i = 0; i < 4; i++) {
           expenseYVals.add(new BarEntry(weeklyExpenses[i], i, "Week " + (i + 1)));
           String week = "Week "+(i+1);
           xVals.add(week);
        }
        expenseCursor.close();

        chart.setDescription(month);
    }

}
