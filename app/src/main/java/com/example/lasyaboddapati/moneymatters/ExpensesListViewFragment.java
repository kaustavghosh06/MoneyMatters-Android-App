package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by lasyaboddapati on 2/20/15.
 */
public class ExpensesListViewFragment extends Fragment {
    Context context;
    static ExpensesListAdapter adapter;
    private static ListView lv;
    private static ExpensesGraphViewFragment graphViewFragment;

    private ActionMode actionMode;
    List<Integer> groupsToRemove;
    boolean dateValid;
    boolean amountValid;

    public static ExpensesListViewFragment newInstance(Context context, ExpensesGraphViewFragment graphViewFragment) {
        ExpensesListViewFragment expensesListViewFragment = new ExpensesListViewFragment();
        expensesListViewFragment.context = context;
        adapter = new ExpensesListAdapter(context, R.layout.custom_list_item);
        expensesListViewFragment.graphViewFragment = graphViewFragment;
        SystemNotificationFragment.initialize(context);
        return expensesListViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


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
                mode.setTitle(checkedCount + " Selected");
                // Hide edit button if multiple items selected
                if (checkedCount > 1) {
                    mode.getMenu().findItem(R.id.action_edit).setVisible(false);
                } else {
                    mode.getMenu().findItem(R.id.action_edit).setVisible(true);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_item_edit_detele, menu);
                actionMode = mode;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                SparseBooleanArray checkedItemPositions = lv.getCheckedItemPositions();
                groupsToRemove = new ArrayList<Integer>();

                Log.d("CHECKED ITEM POSITIONS", checkedItemPositions.toString());
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    if (checkedItemPositions.valueAt(i)) {
                        int position = checkedItemPositions.keyAt(i);
                        if (id == R.id.action_delete) {
                            groupsToRemove.add(position);
                        } else if (id == R.id.action_edit) {
                            popup_edit_expense_dialog(position);
                        }
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

    protected void pop_up_add_expense_dialog() {
        dateValid = false;
        amountValid = false;

        View view = View.inflate(context, R.layout.add_expense_layout, null);

        final EditText dateEditText = (EditText) view.findViewById(R.id.dateEditText);
        final EditText amountSpentEditText = (EditText) view.findViewById(R.id.amountSpentEditText);
        final EditText descriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String date = dateEditText.getText().toString();
                        String amountSpent = amountSpentEditText.getText().toString();
                        String description = descriptionEditText.getText().toString();
                        adapter.addItem(date, amountSpent, description);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String working = s.toString();
                boolean isValid = true;
                if (working.length()==2 && before ==0) {
                    if (working.substring(1,2).matches("[/.-]")) {
                        working = "0"+working.substring(0,1);
                    }
                    if (Integer.parseInt(working) < 1 || Integer.parseInt(working)>12) {
                        isValid = false;
                    } else {
                        if (working.length() == 1) {
                            working = "0"+working;
                        }
                        working+="/";
                        dateEditText.setText(working);
                        dateEditText.setSelection(working.length());
                    }
                } else if (working.length()==5 && before ==0) {
                    if (working.substring(4, 5).matches("[/.-]")) {
                        working = working.substring(0, 3) + "0" + working.substring(3, 4);
                    }
                    //working += "/";
                    dateEditText.setText(working);
                    dateEditText.setSelection(working.length());
                /*} else if (working.length()==10 && before ==0) {
                    int yyyy = Integer.parseInt(working.substring(6));
                    int mm = Integer.parseInt(working.substring(0,2));
                    int dd = Integer.parseInt(working.substring(3,5));
                    //if (yyyy > Calendar.getInstance().get(Calendar.YEAR)) {
                    //    isValid = false;
                    //}
                    //else {
                    Calendar c = new GregorianCalendar(yyyy, mm, dd);
                    //c.set(Calendar.YEAR, yyyy);
                    //c.set(Calendar.MONTH, mm);
                    Log.d("DATE", c.get(Calendar.MONTH)+"");
                    Log.d("DATE", c.getActualMaximum(Calendar.DATE)+"");
                    if (dd<1 || dd>c.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        isValid = false;
                    }
                    //}
                } else if (working.length()!=10) {
                    isValid = false;
                }*/
                } else if(working.length() != 5) {
                    isValid = false;
                }

                if (!isValid) {
                    dateEditText.setError("Enter a valid date: MM/DD");
                    dateValid = false;
                } else {
                    dateEditText.setError(null);
                    dateValid = true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (dateValid && amountValid) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        final EditText amount = (EditText) view.findViewById(R.id.amountSpentEditText);
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("AMOUNT", s.toString().isEmpty() + "");
                if (s.toString().isEmpty()) {
                    amount.setError("", context.getDrawable(android.R.drawable.stat_notify_error));
                    amountValid = false;
                } else {
                    amount.setError(null);
                    amountValid = true;
                }

                if (dateValid && amountValid) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
    }

    protected void popup_edit_expense_dialog(final int position) {
        dateValid = true;
        amountValid = true;

        String[] expense = (String[]) adapter.getItem(position);
        final String dateOld = expense[0].trim().substring(0,5);
        final String amountOld = expense[1].trim();
        final String descriptionOld = expense[2].trim();

        View view = View.inflate(context, R.layout.add_expense_layout, null);
        final EditText date = (EditText) view.findViewById(R.id.dateEditText);
        date.setText(dateOld);

        final EditText amount = (EditText) view.findViewById(R.id.amountSpentEditText);
        amount.setText(amountOld);

        final EditText description = (EditText) view.findViewById(R.id.descriptionEditText);
        description.setText(descriptionOld);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String dateNew = date.getText().toString();
                        String amountNew = amount.getText().toString();
                        String descriptionNew = description.getText().toString();
                        adapter.updateListItem(position, dateNew, amountNew, descriptionNew);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        final AlertDialog dialog = builder.create();

        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String working = s.toString();
                boolean isValid = true;
                if (working.length()==2 && before ==0) {
                    if (working.substring(1,2).matches("[/.-]")) {
                        working = "0"+working.substring(0,1);
                    }
                    if (Integer.parseInt(working) < 1 || Integer.parseInt(working)>12) {
                        isValid = false;
                    } else {
                        if (working.length() == 1) {
                            working = "0"+working;
                        }
                        date.setText(working);
                        date.setSelection(working.length());
                    }
                } else if (working.length()==5 && before ==0) {
                    if (working.substring(4,5).matches("[/.-]")) {
                        working = working.substring(0,3)+"0"+working.substring(3,4);
                    }
                    date.setText(working);
                    date.setSelection(working.length());
                } else if(working.length() != 5) {
                    isValid = false;
                }

                if (!isValid) {
                    date.setError("Enter a valid date: MM/DD");
                    dateValid = false;
                } else {
                    date.setError(null);
                    dateValid = true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (dateValid && amountValid) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("AMOUNT", s.toString().isEmpty() + "");
                if (s.toString().isEmpty()) {
                    amount.setError("", context.getDrawable(android.R.drawable.stat_notify_error));
                    amountValid = false;
                } else {
                    amount.setError(null);
                    amountValid = true;
                }

                if (dateValid && amountValid) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim() != descriptionOld && dateValid && amountValid) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

        dialog.show();
    }

    public static class ExpensesListAdapter extends ArrayAdapter {
        private LinkedHashMap<Long, String[]> expenses;
        protected SQLiteDatabase db;
        Context context;

        static final int DATE = 0;
        static final int AMOUNT = 1;
        static final int DESCRIPTION = 2;

        public ExpensesListAdapter(Context context, int resource) {
            super(context, resource);
            this.expenses = new LinkedHashMap<Long, String[]>();
            this.db = new ExpenseDatabase(context).getWritableDatabase();
            this.context = context;
            populateListView();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.custom_list_item, null);

            long id = (long) expenses.keySet().toArray()[position];
            Log.d("GET VIEWWWWWWWWWWW", id+"  "+position+ "      "+Integer.parseInt(expenses.get(id)[DATE].split("[./-]")[2]));

            TextView date = (TextView) row.findViewById(R.id.listItemLeft);
            SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date MyDate=null;
            try {
                MyDate = newDateFormat.parse(expenses.get(id)[DATE]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            newDateFormat.applyPattern("EEEE, MMM d");
            String dateFormatted = newDateFormat.format(MyDate);
            date.setText(dateFormatted);

            TextView amount = (TextView) row.findViewById(R.id.listItemRight);
            amount.setText("$"+expenses.get(id)[AMOUNT]);

            TextView description = (TextView) row.findViewById(R.id.description);
            description.setText(expenses.get(id)[DESCRIPTION]);

            return row;
        }

        @Override
        public Object getItem(int position) {
            long id = (long) expenses.keySet().toArray()[position];
            return expenses.get(id);
        }

        @Override
        public int getCount() {
            return expenses.size();
        }

        public void addItem(String date, String amount, String description) {
            date = date+"/"+Calendar.getInstance().get(Calendar.YEAR);
            insertIntoDatabase(date, amount, description);
            filterItems();
            //notifyDataSetChanged();
            //graphViewFragment.populateGraphView();

            SystemNotificationFragment.limit_exceeded_check(date);
        }

        public void removeItems(List<Integer> groupsToRemove) {
            long[] ids = new long[groupsToRemove.size()];
            for (int i=0; i<groupsToRemove.size(); i++) {
                ids[i] = (long) expenses.keySet().toArray()[groupsToRemove.get(i)];
            }
            for (int i=0; i<ids.length; i++) {
                Long id = ids[i];
                deleteFromDatabase(id);
            }
            filterItems();
            //graphViewFragment.populateGraphView();
        }

        private void updateListItem(int groupPos, String date, String amount, String description) {
            date = date+"/"+Calendar.getInstance().get(Calendar.YEAR);
            long id = (long) expenses.keySet().toArray()[groupPos];
            updateInDatabase(id, date, amount, description);
            filterItems();
            //graphViewFragment.populateGraphView();
            SystemNotificationFragment.limit_exceeded_check(date);
        }

        public long insertIntoDatabase(String date, String amount, String description) {
            ContentValues newValues = new ContentValues();

            int mm = Integer.parseInt(date.split("[/.-]")[0]);
            int dd = Integer.parseInt(date.split("[/.-]")[1]);

            newValues.put(ExpenseDatabase.MONTH_COLUMN, Months.nameOf(mm));
            /*Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, mm);
            c.set(Calendar.DATE, dd);
            int week = c.get(Calendar.WEEK_OF_MONTH);
            if (week == 5) {
                week = 4;
            }*/
            int week = 0;
            if(dd>=1 && dd<=7) {
                week = 1;
            } else if(dd>=8 && dd<=14) {
                week = 2;
            } else if(dd>=15 && dd<=21) {
                week = 3;
            } else if(dd>=22 && dd<=31) {
                week = 4;
            }
            Log.d("WEEEEEEEEEEEEK", "Date is "+date+"     WEEK OF MONTH is "+week);


            newValues.put(ExpenseDatabase.DATE_COLUMN, date);
            newValues.put(ExpenseDatabase.WEEK_COLUMN, week);
            newValues.put(ExpenseDatabase.AMOUNT_COLUMN, amount);
            newValues.put(ExpenseDatabase.DESCRIPTION_COLUMN, description);
            Log.d("INSERT", "inserting into db");
            return db.insert(ExpenseDatabase.DATABASE_TABLE, null, newValues);
        }

        public void deleteFromDatabase(long id) {
            String whereClause = ExpenseDatabase.ID_COLUMN + " = "+id;
            int n = db.delete(ExpenseDatabase.DATABASE_TABLE, whereClause, null);
            Log.d("DELETE", "Deleted "+n);
            //displayDb();
        }

        public void updateInDatabase(long id, String date, String amount, String description) {
            String whereClause = ExpenseDatabase.ID_COLUMN + " = "+id;
            ContentValues newValues = new ContentValues();

            int mm = Integer.parseInt(date.split("[/.-]")[0]);
            int dd = Integer.parseInt(date.split("[/.-]")[1]);
            //int yyyy = Integer.parseInt(date.split("[/.-]")[2]);
            //newValues.put(ExpenseDatabase.MONTH_COLUMN, Expenses.MONTHS.values()[mm].toString());
            newValues.put(ExpenseDatabase.MONTH_COLUMN, Months.nameOf(mm));
            /*Calendar c = Calendar.getInstance();
            //c.set(Calendar.YEAR, yyyy);
            c.set(Calendar.MONTH, mm);
            c.set(Calendar.DATE, dd);
            int week = c.get(Calendar.WEEK_OF_MONTH);*/
            int week = 0;
            if(dd>=1 && dd<=7) {
                week = 1;
            } else if(dd>=8 && dd<=14) {
                week = 2;
            } else if(dd>=15 && dd<=21) {
                week = 3;
            } else if(dd>=22 && dd<=31) {
                week = 4;
            }

            newValues.put(ExpenseDatabase.DATE_COLUMN, date);
            newValues.put(ExpenseDatabase.WEEK_COLUMN, week);
            newValues.put(ExpenseDatabase.AMOUNT_COLUMN, amount);
            newValues.put(ExpenseDatabase.DESCRIPTION_COLUMN, description);
            int n = db.update(ExpenseDatabase.DATABASE_TABLE, newValues, whereClause, null);
            Log.d("DELETE", "Deleted "+n);
        }

        public void displayDb() {
            Cursor c = db.rawQuery("SELECT * FROM "+ExpenseDatabase.DATABASE_TABLE, null);
            while (c.moveToNext()) {
                Log.d("DB", "ID "+c.getString(0));
                Log.d("DB", "DATE "+c.getString(1));
                Log.d("DB", "MONTH "+c.getString(2));
                Log.d("DB", "WEEK "+c.getString(3));
                Log.d("DB", "AMOUNT "+c.getString(4));
                Log.d("DB", "DESCRIPTION "+c.getString(5));
            }
            c.close();
        }

        public void populateListView() {
            String[] resultColumns = {ExpenseDatabase.ID_COLUMN, ExpenseDatabase.DATE_COLUMN
                    , ExpenseDatabase.AMOUNT_COLUMN, ExpenseDatabase.DESCRIPTION_COLUMN};
            Cursor cursor = db.query(ExpenseDatabase.DATABASE_TABLE, resultColumns, null, null, null, null, null);

            expenses = new LinkedHashMap<Long, String[]>(cursor.getCount());
            Log.d("populateListView COUNT", "list size" +expenses.size()+ "cursor size"+ cursor.getCount());

            while (cursor.moveToNext()) {
                Long id = cursor.getLong(0);
                String date = cursor.getString(1);
                String amount = cursor.getString(2);
                String description = cursor.getString(3);
                expenses.put(id, new String[] {date, amount, description});
            }
            Log.d("populateListView After COUNT", "list size" +expenses.size()+ "cursor size"+ cursor.getCount());
            cursor.close();
        }

        public void filterItems() {
            View view = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
            Log.d("FILTER", view+"  "+view.findViewById(R.id.MonthSpinner) +"   "+ view.findViewById(R.id.WeekSpinner));

            String month = ((Spinner) view.findViewById(R.id.MonthSpinner)).getSelectedItem().toString().trim();
            String week = ((Spinner) view.findViewById(R.id.WeekSpinner)).getSelectedItem().toString().trim();
            TextView monthDisplay = (TextView) view.findViewById(R.id.monthDisplay);
            if (month=="All") {
                monthDisplay.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            } else {
                monthDisplay.setText(month + ", " + Calendar.getInstance().get(Calendar.YEAR));
            }

            String[] resultColumns = {ExpenseDatabase.ID_COLUMN, ExpenseDatabase.DATE_COLUMN, ExpenseDatabase.AMOUNT_COLUMN
                    , ExpenseDatabase.DESCRIPTION_COLUMN};
            String whereClause = null;
            if (month != "All" && week != "All") {
                whereClause = ExpenseDatabase.MONTH_COLUMN + "='" + month + "' AND " + ExpenseDatabase.WEEK_COLUMN + "=" + week.charAt(week.length()-1);
            } else if (month != "All" && week == "All") {
                whereClause = ExpenseDatabase.MONTH_COLUMN + "='" + month + "'";
            } else if (month == "All" && week != "All") {
                whereClause = ExpenseDatabase.WEEK_COLUMN + "=" + week.charAt(week.length()-1);
            }
            Cursor cursor = db.query(ExpenseDatabase.DATABASE_TABLE, resultColumns, whereClause, null, null, null, null);

            expenses = new LinkedHashMap<Long, String[]>(cursor.getCount());
            Log.d("filterItems COUNT", "list size" +expenses.size()+ "cursor size"+ cursor.getCount());

            while (cursor.moveToNext()) {
                Long id = cursor.getLong(0);
                String date = cursor.getString(1);
                String amount = cursor.getString(2);
                String description = cursor.getString(3);
                expenses.put(id, new String[] {date, amount, description});
                Log.d("filterItems", id+"  ");
            }
            cursor.close();
            notifyDataSetChanged();
            Log.d("filterItems COUNT AFTER", "list size" +expenses.size()+ "cursor size"+ cursor.getCount());

            graphViewFragment.populateGraphView(month);

        }

    }
}