package com.example.lasyaboddapati.moneymatters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class SystemNotificationFragment extends Fragment {
    private static ListView lv;
    static SystemNotificationsListAdapter adapter;

    private ActionMode actionMode;
    List<Integer> groupsToRemove;
    static Context context;

    static SQLiteDatabase expenseDB;
    static SQLiteDatabase budgetDB;
    static SQLiteDatabase notificationsDB;

    static int WEEKLY_NOTIFICATION_ID = 1;
    static int MONTHLY_NOTIFICATION_ID = 2;
    static String username;

    public static Fragment newInstance(Context context) {
        SystemNotificationFragment systemNotificationFragment = new SystemNotificationFragment();
        initialize(context);
        return systemNotificationFragment;
    }

    public static void initialize(Context context1){
        context = context1;
        SharedPreferences sharedPref = context.getSharedPreferences("Credentials",Context.MODE_PRIVATE);
        username = sharedPref.getString("Username", "");
        adapter = new SystemNotificationsListAdapter(context, R.layout.custom_list_item);
        expenseDB = new ExpenseDatabase(context).getReadableDatabase();
        budgetDB = new BudgetDatabase(context).getReadableDatabase();
        notificationsDB = new SystemNotificationsDatabase(context).getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);

        lv = (ListView) rootView.findViewById(R.id.listView);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setAdapter(adapter);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int checkedCount;
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Capture total checked items
                checkedCount = lv.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + "Selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_multi_item_delete, menu);
                actionMode = mode;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                SparseBooleanArray checkedItemPositions = lv.getCheckedItemPositions();
                groupsToRemove = new ArrayList<Integer>();

                Log.d("CHECKED ITEM POSITIONS", checkedItemPositions.toString());
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    if (checkedItemPositions.valueAt(i)) {
                        int position = checkedItemPositions.keyAt(i);
                        groupsToRemove.add(position);
                    }
                }

                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                if (groupsToRemove!=null) {
                    adapter.removeItems(groupsToRemove);
                    groupsToRemove.clear();
                }
            }
        });

        adapter.populateListView();
        return rootView;
    }

    public static void limit_exceeded_check(String date) {
        Log.d("limit_exceeded_check", expenseDB+"");
        Log.d("limit_exceeded_check", budgetDB+"");

        int mm = Integer.parseInt(date.split("[./-]")[0]);
        int dd = Integer.parseInt(date.split("[./-]")[1]);
        String month = Months.nameOf(mm);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, mm);
        c.set(Calendar.DATE, dd);
        int week = (c.get(Calendar.WEEK_OF_MONTH));
        Log.d("limit_exceeded_check", "week is "+week);

        String[] budgetResultColumns = null;
        switch (week) {
            case 1: budgetResultColumns = new String[] {BudgetDatabase.MONTHLY_BUDGET_COLUMN, BudgetDatabase.WEEK1_COLUMN};
            case 2: budgetResultColumns = new String[] {BudgetDatabase.MONTHLY_BUDGET_COLUMN, BudgetDatabase.WEEK2_COLUMN};
            case 3: budgetResultColumns = new String[] {BudgetDatabase.MONTHLY_BUDGET_COLUMN, BudgetDatabase.WEEK3_COLUMN};
            case 4:
            case 5: budgetResultColumns = new String[] {BudgetDatabase.MONTHLY_BUDGET_COLUMN, BudgetDatabase.WEEK4_COLUMN};
        }
        Log.d("limit_exceeded_check", "budgetResultColumns is "+budgetResultColumns.toString());
        String budgetWhereClause = BudgetDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
        Cursor budgetCursor = budgetDB.query(BudgetDatabase.DATABASE_TABLE, budgetResultColumns, budgetWhereClause, null, null, null, null);
        if(budgetCursor.getCount() > 0) {
            float monthlyBudget = 0;
            float weeklyBudget = 0;
            while (budgetCursor.moveToNext()) {
                monthlyBudget = budgetCursor.getFloat(0);
                weeklyBudget = budgetCursor.getFloat(1);
            }
            budgetCursor.close();

            String[] expensesResultColumns = {ExpenseDatabase.AMOUNT_COLUMN, ExpenseDatabase.WEEK_COLUMN};
            String expensesWhereClause = ExpenseDatabase.MONTH_COLUMN + " = " + "'" + month + "'";
            Cursor expenseCursor = expenseDB.query(ExpenseDatabase.DATABASE_TABLE, expensesResultColumns, expensesWhereClause, null, null, null, null);
            float monthlyExpense = 0;
            float weeklyExpense = 0;
            while (expenseCursor.moveToNext()) {
                float amount = expenseCursor.getFloat(0);
                monthlyExpense += amount;
                if (expenseCursor.getInt(1) == week) {
                    weeklyExpense += amount;
                }
            }
            expenseCursor.close();

            if (weeklyExpense >= weeklyBudget) {
                //adapter.addItem(date, "Weekly Budget Limit!", "You have reached your expense limit for the week");
                insertIntoDatabase(date, "Weekly Budget Limit!", "You have reached your expense limit for the week");
                Log.d("WEEKLY ", "Weekly Budget Limit!");
                notify_user(WEEKLY_NOTIFICATION_ID, "Weekly budget limit reached");
            }

            if (monthlyExpense >= monthlyBudget) {
                //adapter.addItem(date, "Monthly Budget Limit!", "You have reached your expense limit for "+month);
                insertIntoDatabase(date, "Monthly Budget Limit!", "You have reached your expense limit for " + month);
                Log.d("MONTHLY", "Monthly Budget Limit!");
                notify_user(MONTHLY_NOTIFICATION_ID, "Monthly budget limit reached");
            }
        }
    }

    private static void notify_user(int id, String message){
        Intent resultIntent = new Intent(context, Notifications.class);
        resultIntent.putExtra("Username",username);
        //TODO : set tab to System Notifications
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);

        Notification notification = new Notification.Builder(context)
                .setContentTitle("Money Matters")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_social_notifications_on)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
        Log.d("NOTIFY", "user notified");
    }

    private static long insertIntoDatabase(String date, String title, String message) {
        ContentValues newValues = new ContentValues();
        newValues.put(SystemNotificationsDatabase.DATE_COLUMN, date);
        newValues.put(SystemNotificationsDatabase.TITLE_COLUMN, title);
        newValues.put(SystemNotificationsDatabase.MESSAGE_COLUMN, message);
        Log.d("INSERT", "inserting into db");
        return notificationsDB.insert(SystemNotificationsDatabase.DATABASE_TABLE, null, newValues);
    }

    private static void deleteFromDatabase(long id) {
        String whereClause = SystemNotificationsDatabase.ID_COLUMN + " = "+id;
        int n = notificationsDB.delete(SystemNotificationsDatabase.DATABASE_TABLE, whereClause, null);
        Log.d("DELETE", "Deleted "+n);
        //displayDb();
    }

    private void displayDb() {
        Cursor c = notificationsDB.rawQuery("SELECT * FROM "+SystemNotificationsDatabase.DATABASE_TABLE, null);
        while (c.moveToNext()) {
            Log.d("DB", "ID "+c.getString(0));
            Log.d("DB", "DATE "+c.getString(1));
            Log.d("DB", "TITLE "+c.getString(4));
            Log.d("DB", "MESSAGE "+c.getString(5));
        }
        c.close();
    }

    public static class SystemNotificationsListAdapter extends ArrayAdapter {
        private LinkedHashMap<Long, String[]> notifications;
        Context context;

        static final int DATE = 0;
        static final int TITLE = 1;
        static final int MESSAGE = 2;

        public SystemNotificationsListAdapter(Context context, int resource) {
            super(context, resource);
            this.notifications = new LinkedHashMap<Long, String[]>();
            this.context = context;
            populateListView();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.custom_list_item, null);

            long id = (long) notifications.keySet().toArray()[position];
            Log.d("GET VIEWWWWWWWWWWW", id + "  " + position + "      " + Integer.parseInt(notifications.get(id)[DATE].split("[./-]")[2]));

            TextView date = (TextView) row.findViewById(R.id.listItemRight);
            SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date MyDate=null;
            try {
                MyDate = newDateFormat.parse(notifications.get(id)[DATE]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            newDateFormat.applyPattern("EEEE, MMM d");
            String dateFormatted = newDateFormat.format(MyDate);
            date.setText(dateFormatted);
            date.setTextSize(10);

            TextView title = (TextView) row.findViewById(R.id.listItemLeft);
            title.setText(notifications.get(id)[TITLE]);

            TextView message = (TextView) row.findViewById(R.id.description);
            message.setText(notifications.get(id)[MESSAGE]);

            return row;
        }

        @Override
        public Object getItem(int position) {
            long id = (long) notifications.keySet().toArray()[position];
            return notifications.get(id);
        }

        @Override
        public int getCount() {
            return notifications.size();
        }

        public void addItem(long id, String date, String title, String message) {
            notifications.put(id, new String[] {date, title, message});
            notifyDataSetChanged();
        }

        public void removeItems(List<Integer> groupsToRemove) {
            long[] ids = new long[groupsToRemove.size()];
            for (int i=0; i<groupsToRemove.size(); i++) {
                ids[i] = (long) notifications.keySet().toArray()[groupsToRemove.get(i)];
            }
            for (int i=0; i<ids.length; i++) {
                Long id = ids[i];
                notifications.remove(id);
                SystemNotificationFragment.deleteFromDatabase(id);
            }
            notifyDataSetChanged();
        }

        public void populateListView() {
            SQLiteDatabase db = new SystemNotificationsDatabase(context).getReadableDatabase();
            String[] resultColumns = {SystemNotificationsDatabase.ID_COLUMN, SystemNotificationsDatabase.DATE_COLUMN
                    , SystemNotificationsDatabase.TITLE_COLUMN, SystemNotificationsDatabase.MESSAGE_COLUMN};
            Cursor cursor = db.query(SystemNotificationsDatabase.DATABASE_TABLE, resultColumns, null, null, null, null, null);

            notifications = new LinkedHashMap<Long, String[]>(cursor.getCount());
            Log.d("populateListView COUNT", "list size" + notifications.size()+ "cursor size"+ cursor.getCount());

            if(cursor.getCount() > 0) {
                cursor.moveToLast();
                //while (cursor.moveToPrevious()) {
                do {
                    Long id = cursor.getLong(0);
                    String date = cursor.getString(1);
                    String title = cursor.getString(2);
                    String message = cursor.getString(3);
                    notifications.put(id, new String[]{date, title, message});
                } while (cursor.moveToPrevious());
            }
            Log.d("populateListView After COUNT", "list size" + notifications.size()+ "cursor size"+ cursor.getCount());
            cursor.close();
        }

    }

}
