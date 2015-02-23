package com.example.lasyaboddapati.moneymatters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lasyaboddapati on 2/8/15.
 */
public class BudgetDatabase extends SQLiteOpenHelper {
    public static final String MONTH_COLUMN = "MONTH";
    public static final String MONTHLY_BUDGET_COLUMN = "MONTHLY_BUDGET";
    public static final String WEEK1_COLUMN = "WEEK1_BUDGET";
    public static final String WEEK2_COLUMN = "WEEK2_BUDGET";
    public static final String WEEK3_COLUMN = "WEEK3_BUDGET";
    public static final String WEEK4_COLUMN = "WEEK4_BUDGET";

    public static final String DATABASE_TABLE = "BUDGET";
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_CREATE = "CREATE TABLE "+DATABASE_TABLE+" ( "

            +MONTH_COLUMN+" TEXT NOT NULL PRIMARY KEY, "
            +MONTHLY_BUDGET_COLUMN+" INTEGER, "
            +WEEK1_COLUMN+" INTEGER, "
            +WEEK2_COLUMN+" INTEGER, "
            +WEEK3_COLUMN+" INTEGER, "
            +WEEK4_COLUMN+" INTEGER "
            +" )";

    public BudgetDatabase(Context context) {
        super(context, DATABASE_TABLE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO
    }
}

