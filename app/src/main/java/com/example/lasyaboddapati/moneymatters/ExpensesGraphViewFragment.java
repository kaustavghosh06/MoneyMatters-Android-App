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
import com.github.mikephil.charting.utils.XLabels;
import java.util.ArrayList;

/**
 * Created by lasyaboddapati on 1/30/15.
 * A fragment containing the Graph view.
 */
public class ExpensesGraphViewFragment extends Fragment {

    protected SQLiteDatabase db;
    BarChart chart;

    public static ExpensesGraphViewFragment newInstance(Context context) {
        ExpensesGraphViewFragment expensesGraphViewFragment = new ExpensesGraphViewFragment();
        expensesGraphViewFragment.db = new ExpenseDatabase(context).getReadableDatabase();
        return expensesGraphViewFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph_view, container, false);
        chart = (BarChart) rootView.findViewById(R.id.chart);
        populateGraphView();
        return rootView;
    }

    public void populateGraphView() {
        String[] resultColumns = {ExpenseDatabase.AMOUNT_COLUMN };
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i=0; i< Months.size(); i++) {
            String month = Months.names()[i];
            String whereClause = ExpenseDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
            Cursor cursor = db.query(ExpenseDatabase.DATABASE_TABLE, resultColumns, whereClause, null, null, null, null);

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
            yVals1.add(new BarEntry(monthlyExpense, i, month));
            cursor.close();
        }
        BarDataSet set1 = new BarDataSet(yVals1, "Expenses");
        set1.setBarSpacePercent(30);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        chart.setData(data);
        chart.setDrawValueAboveBar(true);
        chart.enableScroll();
        chart.setDescription("");

        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setDrawHorizontalGrid(true);
        chart.setDrawVerticalGrid(false);

        XLabels xl = chart.getXLabels();
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);
        xl.setCenterXLabelText(true);
        xl.setSpaceBetweenLabels(0);

        chart.setDrawYLabels(false);
        chart.setDrawLegend(false);

        chart.animateY(1000);

        chart.invalidate();
    }
}
