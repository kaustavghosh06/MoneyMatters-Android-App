package com.example.lasyaboddapati.moneymatters;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by lasyaboddapati on 1/30/15.
 * A fragment that contains the Expandable List View of expenses
 */
public class ExpensesListViewFragment extends Fragment {
    static ExpensesListAdapter adapter;
    private static ExpandableListView lv;
    private int expandableListSelectionType;
    private ActionMode actionMode;
    List<Integer> groupsToRemove;
    Context context;
    boolean dateValid;
    boolean amountValid;

    enum CATEGORIES {Bills, Rent, Groceries, Food, Personal, Shopping};

    public static ExpensesListViewFragment newInstance(Context context) {
        ExpensesListViewFragment expensesListViewFragment = new ExpensesListViewFragment();
        expensesListViewFragment.context = context;
        adapter = new ExpensesListAdapter(context);
        return expensesListViewFragment;
    }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_expandable_list_view, container, false);

            lv = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
            lv.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE_MODAL);

            lv.setAdapter(adapter);
            lv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if (actionMode != null) {
                        if (expandableListSelectionType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                            int flatPosition = parent.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPosition));
                            parent.setItemChecked(
                                    flatPosition,
                                    !parent.isItemChecked(flatPosition));
                            return true;
                        }
                    }
                    return false;
                }
            });
            lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    if (actionMode != null)  {
                        if (expandableListSelectionType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                            int flatPosition = parent.getFlatListPosition(
                                    ExpandableListView.getPackedPositionForChild(groupPosition,childPosition));
                            parent.setItemChecked(
                                    flatPosition,
                                    !parent.isItemChecked(flatPosition));
                        }
                        return true;
                    }
                    return false;
                }
            });
            lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                int checkedCount;
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    // Capture total checked items
                    checkedCount = lv.getCheckedItemCount();
                    if (checkedCount == 1) {
                        expandableListSelectionType = ExpandableListView.getPackedPositionType(
                                lv.getExpandableListPosition(position));
                    }
                    // Set the CAB title according to total checked items
                    mode.setTitle(checkedCount + "Selected");
                    // Hide edit button if multiple items selected
                    if (checkedCount > 1) {
                        mode.getMenu().findItem(R.id.action_edit).setVisible(false);
                    } else {
                        mode.getMenu().findItem(R.id.action_edit).setVisible(true);
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    if (checkedCount > 1) {
                        mode.getMenuInflater().inflate(R.menu.menu_multi_item_delete, menu);
                    } else {
                        mode.getMenuInflater().inflate(R.menu.menu_item_edit_detele, menu);
                    }
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
                    //groupsToRemove = new ArrayList<Object>();
                    groupsToRemove = new ArrayList<Integer>();

                    Log.d("CHECKED ITEM POSITIONS", checkedItemPositions.toString());
                    for (int i = 0; i < checkedItemPositions.size(); i++) {
                        if (checkedItemPositions.valueAt(i)) {
                            int position = checkedItemPositions.keyAt(i);
                            long pos = lv.getExpandableListPosition(position);
                            int groupPos = ExpandableListView.getPackedPositionGroup(pos);
                            if (id == R.id.action_delete) {
                                //groupsToRemove.add(adapter.getGroup(groupPos));
                                groupsToRemove.add(groupPos);
                            } else if (id == R.id.action_edit) {
                                popup_edit_expense_dialog(groupPos);
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

    private void popup_edit_expense_dialog(final int groupPos) {
        dateValid = true;
        amountValid = true;

        String[] group = adapter.getGroup(groupPos).toString().split(":");
        final String dateOld = group[0].trim();
        final String categoryOld = group[1].trim();
        final String amountOld = group[2].trim();
        final String descriptionOld = adapter.getChild(groupPos, 0).toString().trim();

        View view = View.inflate(context, R.layout.add_expense_layout, null);
        final EditText date = (EditText) view.findViewById(R.id.dateEditText);
        date.setText(dateOld);

        final Spinner categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        categorySpinner.setAdapter(new ArrayAdapter<CATEGORIES>(context, android.R.layout.simple_spinner_item, CATEGORIES.values()));
        categorySpinner.setSelection(CATEGORIES.valueOf(categoryOld).ordinal());

        final EditText amount = (EditText) view.findViewById(R.id.amountSpentEditText);
        amount.setText(amountOld);

        final EditText description = (EditText) view.findViewById(R.id.desciptionEditText);
        description.setText(descriptionOld);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String dateNew = date.getText().toString();
                        String categoryNew = categorySpinner.getSelectedItem().toString();
                        String amountNew = amount.getText().toString();
                        String descriptionNew = description.getText().toString();
                        adapter.updateListItem(groupPos, dateNew, categoryNew, amountNew, descriptionNew);
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
                        working+="/";
                        date.setText(working);
                        date.setSelection(working.length());
                    }
                } else if (working.length()==5 && before ==0) {
                    if (working.substring(4,5).matches("[/.-]")) {
                        working = working.substring(0,3)+"0"+working.substring(3,4);
                    }
                    working+="/";
                    date.setText(working);
                    date.setSelection(working.length());
                } else if (working.length()==10 && before ==0) {
                    int yyyy = Integer.parseInt(working.substring(6));
                    int mm = Integer.parseInt(working.substring(0,2));
                    int dd = Integer.parseInt(working.substring(3,5));
                    //if (yyyy > Calendar.getInstance().get(Calendar.YEAR)) {  //TODO: Add check for year
                    //    isValid = false;
                    //}
                    //else {
                        Calendar c = new GregorianCalendar(yyyy, mm, dd);
                        Log.d("DATE", c.get(Calendar.MONTH)+"");
                        Log.d("DATE", c.getActualMaximum(Calendar.DATE)+"");
                        if (dd<1 || dd>c.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                            isValid = false;    //TODO: Not working (check Feb 30)
                        }
                    //}
                } else if (working.length()!=10) {
                    isValid = false;
                }

                if (!isValid) {
                    date.setError("Enter a valid date: MM/DD/YYYY");
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

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((categorySpinner.getSelectedItem().toString().trim() != categoryOld) && dateValid && amountValid) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    public static class ExpensesListAdapter extends BaseExpandableListAdapter {

        private final LayoutInflater inf;
        private HashMap<Long, String> list;
        private HashMap<Long, String[]> details;
        protected SQLiteDatabase db;

        public ExpensesListAdapter(Context context) {
            this.list = new LinkedHashMap<Long, String>();
            this.details = new LinkedHashMap<Long, String[]>();

            this.db = new ExpenseDatabase(context).getWritableDatabase();
            inf = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return list.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if(details.values().toArray()[groupPosition] == null) {
                return 0;
            } else {
                return ((String[])details.values().toArray()[groupPosition]).length;
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return list.values().toArray()[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return ((String[])details.values().toArray()[groupPosition])[childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return (long)list.keySet().toArray()[groupPosition];
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

        @Override
        public boolean hasStableIds() {
                return true;
            }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.expenses_list_child_item, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.listItemDetail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(getChild(groupPosition, childPosition).toString());
            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.expenses_list_group_item, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.listItemHeader);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(getGroup(groupPosition).toString());
            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
                return false;
            }

        private class ViewHolder {
            TextView text;
        }

        public void addItem(String date, String category, float amountSpent, String description) {
            long rowId = insertIntoDatabase(date, category, amountSpent, description);
            String listItem = date+" : "+category+" : "+amountSpent;
            list.put(rowId, listItem);
            details.put(rowId, new String[] {description});
            notifyDataSetChanged();
        }

        public void removeItems(List<Integer> groupsToRemove) {
            long[] ids = new long[groupsToRemove.size()];
            for (int i=0; i<groupsToRemove.size(); i++) {
                ids[i] = (long) list.keySet().toArray()[groupsToRemove.get(i)];
            }
            for (int i=0; i<ids.length; i++) {
                Long id = ids[i];
                list.remove(id);
                details.remove(id);
                deleteFromDatabase(id);
            }
            notifyDataSetChanged();
        }

        private void updateListItem(int groupPos, String date, String category, String amount, String description) {
            long id = (long) list.keySet().toArray()[groupPos];
            list.put(id, date + " : " + category + " : " + amount);
            details.put(id, new String[] {description});
            updateInDatabase(id, date, category, Float.parseFloat(amount), description);
            notifyDataSetChanged();
        }

        public long insertIntoDatabase(String date, String category, float amountSpent, String description) {
            ContentValues newValues = new ContentValues();
            newValues.put(ExpenseDatabase.DATE_COLUMN, date);

            int mm = Integer.parseInt(date.split("[/.-]")[0]);
            int dd = Integer.parseInt(date.split("[/.-]")[1]);
            int yyyy = Integer.parseInt(date.split("[/.-]")[2]);

            newValues.put(ExpenseDatabase.MONTH_COLUMN, Expenses.MONTHS.values()[mm].toString());
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, yyyy);
            c.set(Calendar.MONTH, mm);
            c.set(Calendar.DATE, dd);
            int week = c.get(Calendar.WEEK_OF_MONTH);
            newValues.put(ExpenseDatabase.WEEK_COLUMN, week);

            newValues.put(ExpenseDatabase.CATEGORY_COLUMN, category);
            newValues.put(ExpenseDatabase.AMOUNT_COLUMN, amountSpent);
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

        public void updateInDatabase(long id, String date, String category, float amountSpent, String description) {
            String whereClause = ExpenseDatabase.ID_COLUMN + " = "+id;
            ContentValues newValues = new ContentValues();
            newValues.put(ExpenseDatabase.DATE_COLUMN, date);

            int mm = Integer.parseInt(date.split("[/.-]")[0]);
            int dd = Integer.parseInt(date.split("[/.-]")[1]);
            int yyyy = Integer.parseInt(date.split("[/.-]")[2]);
            newValues.put(ExpenseDatabase.MONTH_COLUMN, Expenses.MONTHS.values()[mm].toString());
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, yyyy);
            c.set(Calendar.MONTH, mm);
            c.set(Calendar.DATE, dd);
            int week = c.get(Calendar.WEEK_OF_MONTH);

            newValues.put(ExpenseDatabase.WEEK_COLUMN, week);
            newValues.put(ExpenseDatabase.CATEGORY_COLUMN, category);
            newValues.put(ExpenseDatabase.AMOUNT_COLUMN, amountSpent);
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
                Log.d("DB", "CATEGORY "+c.getString(4));
                Log.d("DB", "AMOUNT "+c.getString(5));
                Log.d("DB", "DESCRIPTION "+c.getString(6));
            }
            c.close();
        }

        public void populateListView() {
            String[] resultColumns = {ExpenseDatabase.ID_COLUMN, ExpenseDatabase.DATE_COLUMN, ExpenseDatabase.CATEGORY_COLUMN, ExpenseDatabase.AMOUNT_COLUMN
                    , ExpenseDatabase.DESCRIPTION_COLUMN};
            Cursor cursor = db.query(ExpenseDatabase.DATABASE_TABLE, resultColumns, null, null, null, null, null);

            list = new LinkedHashMap<Long, String>(cursor.getCount());
            details = new LinkedHashMap<Long, String[]>(cursor.getCount());
            Log.d("COUNT", "list size" +list.size()+ "curssor size"+ cursor.getCount());

            while (cursor.moveToNext()) {
                Long id = cursor.getLong(0);
                String date = cursor.getString(1);
                String category = cursor.getString(2);
                float amount = cursor.getFloat(3);
                String description = cursor.getString(4);
                list.put(id, date+" : "+category+" : "+amount);
                details.put(id, new String[] {description});
            }
            cursor.close();
        }

    }
}
