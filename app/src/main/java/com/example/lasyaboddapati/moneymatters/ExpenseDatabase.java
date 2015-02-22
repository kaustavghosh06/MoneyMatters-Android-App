package com.example.lasyaboddapati.moneymatters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lasyaboddapati on 1/30/15.
 */
public class ExpenseDatabase extends SQLiteOpenHelper {
    public enum COLUMNS {ID, DATE, MONTH, WEEK, CATEGORY, AMOUNT, DESCRIPTION};
    public static final String ID_COLUMN = "ID";
    public static final String DATE_COLUMN = "DATE";
    public static final String MONTH_COLUMN = "MONTH";
    public static final String WEEK_COLUMN = "WEEK";
    //public static final String CATEGORY_COLUMN = "CATEGORY";
    public static final String AMOUNT_COLUMN = "AMOUNT";
    public static final String DESCRIPTION_COLUMN = "DESCRIPTION";

    public static final String DATABASE_TABLE = "EXPENSES";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_CREATE = "CREATE TABLE "+DATABASE_TABLE+" ( "
                                                  +ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                  +DATE_COLUMN+" DATE NOT NULL, "
                                                  +MONTH_COLUMN+" TEXT, "
                                                  +WEEK_COLUMN+" INTEGER, "
                                                  //+CATEGORY_COLUMN+" TEXT, "
                                                  +AMOUNT_COLUMN+" REAL, "
                                                  +DESCRIPTION_COLUMN+" TEXT"
                                                  +" )";

    public ExpenseDatabase(Context context) {
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

    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + DATABASE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public String getData(int rowId, int column) {
        String query;
        if(column == -1) {
            query = "SELECT  * FROM " + DATABASE_TABLE + " WHERE " + ID_COLUMN + " = " + rowId;
        }
        else {
            query = "SELECT " + COLUMNS.values()[column] + " FROM " + DATABASE_TABLE + " WHERE " + ID_COLUMN+ " = "+rowId;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String data = cursor.toString();
        cursor.close();
        return data;
    }
}
