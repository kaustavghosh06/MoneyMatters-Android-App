package com.example.lasyaboddapati.moneymatters;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.github.mikephil.charting.utils.XLabels;
import java.util.ArrayList;

/**
 * Created by lasyaboddapati on 1/30/15.
 * A fragment containing the Graph view.
 */
public class ExpensesGraphViewFragment extends Fragment {

    protected SQLiteDatabase expenseDB;
    Context context;

    BarChart chart;
    ArrayList<String> xVals;
    ArrayList<BarEntry> yVals;

    public static ExpensesGraphViewFragment newInstance(Context context) {
        ExpensesGraphViewFragment expensesGraphViewFragment = new ExpensesGraphViewFragment();
        expensesGraphViewFragment.expenseDB = new ExpenseDatabase(context).getReadableDatabase();
        expensesGraphViewFragment.context = context;
        return expensesGraphViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph_view, container, false);
        chart = (BarChart) rootView.findViewById(R.id.chart);
        //populateGraphView();
        return rootView;
    }

    public void populateGraphView(String month){
        xVals = new ArrayList<String>();
        yVals = new ArrayList<BarEntry>();
        if(month == "All") {
            populateYearGraphView();
        } else {
            populateMonthGraphView(month);
        }
        BarDataSet set = new BarDataSet(yVals, "Expenses");
        set.setBarSpacePercent(30);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set);

        BarData data = new BarData(xVals, dataSets);

        chart.setData(data);
        chart.setDrawValueAboveBar(true);
        chart.enableScroll();
        chart.setDescription("");

        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setDrawHorizontalGrid(false);
        chart.setDrawVerticalGrid(false);

        chart.setValueFormatter(new LargeValueFormatter());

        XLabels xl = chart.getXLabels();
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);
        xl.setCenterXLabelText(true);
        xl.setSpaceBetweenLabels(0);

        chart.setDrawYLabels(false);
        chart.setDrawLegend(false);

        chart.animateY(500);

        chart.invalidate();
    }

    public void populateYearGraphView() {
        String[] resultColumns = {ExpenseDatabase.AMOUNT_COLUMN };

        for (int i=0; i< Months.size(); i++) {
            String month = Months.names()[i];
            String whereClause = ExpenseDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
            Cursor cursor = expenseDB.query(ExpenseDatabase.DATABASE_TABLE, resultColumns, whereClause, null, null, null, null);

            float monthlyExpense = 0;
            Log.d(month, "count "+cursor.getCount());
            xVals.add(month.substring(0,3));

            if (cursor.getCount() == 0) {
            } else {
                while (cursor.moveToNext()) {
                    monthlyExpense += cursor.getFloat(0);
                    Log.d(month, monthlyExpense+"");
                }
            }
            yVals.add(new BarEntry(monthlyExpense, i, month));
            cursor.close();
        }
    }

    public void populateMonthGraphView(String month) {
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
            String week = "Week "+(i+1);
            xVals.add(week);
            yVals.add(new BarEntry(weeklyExpenses[i], i, "Week " + (i + 1)));
        }
        expenseCursor.close();

    }
}
