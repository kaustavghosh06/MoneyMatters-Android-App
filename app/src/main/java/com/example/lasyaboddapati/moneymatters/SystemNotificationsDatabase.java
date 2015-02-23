package com.example.lasyaboddapati.moneymatters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lasyaboddapati on 2/22/15.
 */
public class SystemNotificationsDatabase extends SQLiteOpenHelper {
    public static final String ID_COLUMN = "ID";
    public static final String DATE_COLUMN = "DATE";
    public static final String TITLE_COLUMN = "TITLE";
    public static final String MESSAGE_COLUMN = "MESSAGE";

    public static final String DATABASE_TABLE = "SYSTEM_NOTIFICATIONS";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_CREATE = "CREATE TABLE "+DATABASE_TABLE+" ( "
            +ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +DATE_COLUMN+" DATE NOT NULL, "
            +TITLE_COLUMN+" TEXT, "
            +MESSAGE_COLUMN+" TEXT"
            +" )";

    public SystemNotificationsDatabase(Context context) {
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
