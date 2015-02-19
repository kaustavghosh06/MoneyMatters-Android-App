package com.example.lasyaboddapati.moneymatters;

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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

import java.util.ArrayList;

/**
 * Created by lasyaboddapati on 2/16/15.
 */
public class BudgetGraphViewFragment extends Fragment {
    private SQLiteDatabase budgetDB;
    private SQLiteDatabase expenseDB;
    BarChart chart;

    public static BudgetGraphViewFragment newInstance(Context context) {
        BudgetGraphViewFragment budgetGraphViewFragment = new BudgetGraphViewFragment();
        budgetGraphViewFragment.budgetDB = new BudgetDatabase(context).getReadableDatabase();
        budgetGraphViewFragment.expenseDB = new ExpenseDatabase(context).getReadableDatabase();
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
        String[] budgetResultColumns = {BudgetDatabase.MONTHLY_BUDGET_COLUMN
                                , BudgetDatabase.WEEK1_COLUMN, BudgetDatabase.WEEK2_COLUMN
                                , BudgetDatabase.WEEK3_COLUMN, BudgetDatabase.WEEK4_COLUMN};

        String[] expensesResultColumns = {ExpenseDatabase.AMOUNT_COLUMN };

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> budgetYVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> expenseYVals = new ArrayList<BarEntry>();

        for (int i=0; i< Months.size(); i++) {
            String month = Months.names()[i];
            String budgetWhereClause = BudgetDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
            Cursor budgetCursor = budgetDB.query(BudgetDatabase.DATABASE_TABLE, budgetResultColumns, budgetWhereClause, null, null, null, null);



            if (budgetCursor.getCount() == 0) {
                budgetYVals.add(new BarEntry(0, i, month));
            } else {
                while (budgetCursor.moveToNext()) {
                    float monthlyBudget = budgetCursor.getFloat(0);
                    String[] weeklyBudget = new String[4];
                    weeklyBudget[0] = budgetCursor.getString(1);
                    weeklyBudget[1] = budgetCursor.getString(2);
                    weeklyBudget[2] = budgetCursor.getString(3);
                    weeklyBudget[3] = budgetCursor.getString(4);
                    budgetYVals.add(new BarEntry(monthlyBudget, i, month));
                }
            }
            budgetCursor.close();

            String expensesWhereClause = ExpenseDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
            Cursor expenseCursor = expenseDB.query(ExpenseDatabase.DATABASE_TABLE, expensesResultColumns, expensesWhereClause, null, null, null, null);

            float monthlyExpense = 0;
            Log.d(month, "count "+expenseCursor.getCount());
            xVals.add(month.substring(0,3));

            if (expenseCursor.getCount() == 0) {
            } else {
                while (expenseCursor.moveToNext()) {
                    monthlyExpense += expenseCursor.getFloat(0);
                    Log.d(month, monthlyExpense+"");
                }
            }
            expenseYVals.add(new BarEntry(monthlyExpense, i, month));
            expenseCursor.close();
        }


        BarDataSet budgetSet = new BarDataSet(budgetYVals, "Budget");
        //budgetSet.setBarSpacePercent(20);
        budgetSet.setColor(Color.GREEN);

        BarDataSet expensesSet = new BarDataSet(expenseYVals, "Expenses");
        //expensesSet.setBarSpacePercent(20);
        expensesSet.setColor(Color.BLUE);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(expensesSet);
        dataSets.add(budgetSet);

        BarData data = new BarData(xVals, dataSets);
        //data.setGroupSpace(50);
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

        //chart.setYRange(-10, chart.getYChartMax(), false);
        //chart.setDrawYLabels(false);
        chart.setDrawYValues(false);

        XLabels xl = chart.getXLabels();
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);
        xl.setCenterXLabelText(true);
        xl.setSpaceBetweenLabels(0);

        YLabels yl = chart.getYLabels();
        yl.setPosition(YLabels.YLabelPosition.BOTH_SIDED);
        yl.setLabelCount(5);
        /*yl.setFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                //return null;
                if ((int)v >= 1000 ) {
                    return String.valueOf((v/1000))+"k";
                } else {
                    return String.valueOf((int)v);
                }
            }
        });*/
        yl.setFormatter(new LargeValueFormatter());

        //chart.setDrawValuesForWholeStack(false);
        chart.animateY(1000);
        chart.invalidate();

    }

}
