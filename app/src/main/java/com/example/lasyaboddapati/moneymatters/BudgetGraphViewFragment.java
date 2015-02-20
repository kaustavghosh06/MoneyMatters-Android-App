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

    public static BudgetGraphViewFragment newInstance(Context context) {
        BudgetGraphViewFragment budgetGraphViewFragment = new BudgetGraphViewFragment();
        budgetGraphViewFragment.budgetDB = new BudgetDatabase(context).getReadableDatabase();
        budgetGraphViewFragment.expenseDB = new ExpenseDatabase(context).getReadableDatabase();
        budgetGraphViewFragment.context = context;
        return budgetGraphViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph_view, container, false);
        chart = (BarChart) rootView.findViewById(R.id.chart);
        populateGraphView();
        return rootView;
    }

    public void populateGraphView() {
        View view = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        Button monthButton = (Button) view.findViewById(R.id.monthButton);
        Button yearButton = (Button) view.findViewById(R.id.yearButton);
        Log.d("YEAR", yearButton.getCurrentTextColor()+"");
        Log.d("MONTH", monthButton.getCurrentTextColor()+"");
        if (yearButton.getCurrentTextColor() == Color.BLUE) {
            Log.d("YEAR", yearButton.getCurrentTextColor()+"");
            populateGraphView("year");
        } else if (monthButton.getCurrentTextColor() == Color.BLUE) {
            Log.d("MONTH", monthButton.getCurrentTextColor()+"");
            populateGraphView("month");
        }
    }

    public void populateGraphView(String viewType) {
        xVals = new ArrayList<String>();
        budgetYVals = new ArrayList<BarEntry>();
        expenseYVals = new ArrayList<BarEntry>();

        if (viewType == "year") {
            populateYearGraphView();
        } else if (viewType == "month") {
            populateMonthGraphView();
        } else {
            Log.e("ERR", "Invalid View Type "+viewType);
        }

        BarDataSet budgetSet = new BarDataSet(budgetYVals, "Budget");
        budgetSet.setColor(Color.GREEN);

        BarDataSet expensesSet = new BarDataSet(expenseYVals, "Expenses");
        expensesSet.setColor(Color.BLUE);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(expensesSet);
        dataSets.add(budgetSet);

        BarData data = new BarData(xVals, dataSets);
        data.setGroupSpace(110f);

        chart.setData(data);

        chart.setDrawValueAboveBar(true);
        chart.setValueFormatter(new LargeValueFormatter());

        chart.enableScroll();
        chart.setDescription("");

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

    }

    public void populateMonthGraphView() {
        int mm = Calendar.getInstance().get(Calendar.MONTH);
        String month = Months.nameOf(mm+1);
        Log.d("MONTH", month);

        String[] budgetResultColumns = {BudgetDatabase.WEEK1_COLUMN, BudgetDatabase.WEEK2_COLUMN
                , BudgetDatabase.WEEK3_COLUMN, BudgetDatabase.WEEK4_COLUMN};
        String budgetWhereClause = BudgetDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
        Cursor budgetCursor = budgetDB.query(BudgetDatabase.DATABASE_TABLE, budgetResultColumns, budgetWhereClause, null, null, null, null);
        while (budgetCursor.moveToNext()) {
            for (int i=0; i<4; i++) {
                float weeklyBudget = budgetCursor.getFloat(i);
                String week = "Week "+(i+1);
                Log.d("WEEK", "Week "+week+" budget = "+weeklyBudget);
                budgetYVals.add(new BarEntry(weeklyBudget, i, week));
                xVals.add(week);
            }
        }
        budgetCursor.close();

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
        }
        expenseCursor.close();
    }

}
