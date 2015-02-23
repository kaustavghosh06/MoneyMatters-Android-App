package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by kaustav1992 on 2/22/15.
 */
public class CustomListAdapter extends ArrayAdapter {
    private ArrayList<String> list;
    protected SQLiteDatabase db;
    Context context;

    static final int DATE = 0;
    static final int AMOUNT = 1;
    static final int DESCRIPTION = 2;

    public CustomListAdapter(Context context, int resource,ArrayList<String> d) {
        super(context, resource);
        this.list = new ArrayList<String>();
        list=d;
        this.db = new ExpenseDatabase(context).getWritableDatabase();
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.expenses_list_item, null);

        String value=list.get(position);


        String[] keyvalue = value.split("-");
        String datetime=keyvalue[0];
        String val=keyvalue[1];
        Log.d("entire",value);
        Log.d("valueval",val);


        String[] allval=val.split(":");
        String desc=allval[0];
        String user=allval[1];
        String amt=allval[2];

        String[] datetimepair= datetime.split("_");
        String dateval=datetimepair[0];




        TextView date = (TextView) row.findViewById(R.id.date);
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date MyDate = null;
        try {
            MyDate = newDateFormat.parse(dateval);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newDateFormat.applyPattern("EEEE, MMM d");
        String dateFormatted = newDateFormat.format(MyDate);
        if(user.equals("0")) {
            date.setText(desc);
        }
        else{
            date.setText(user+":"+desc);
        }

        TextView amount = (TextView) row.findViewById(R.id.amount);
        if(user.equals("0"))
        {
            amount.setText(amt);
        }
        else
            amount.setText("$" + amt);

        TextView description = (TextView) row.findViewById(R.id.description);
        if(user.equals("0")) {
            description.setText(dateFormatted);
        }
        else
        {
            description.setText(dateFormatted);
        }


        return row;
    }

    @Override
    public Object getItem(int position) {


        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}












