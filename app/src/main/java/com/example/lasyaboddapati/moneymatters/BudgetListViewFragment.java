package com.example.lasyaboddapati.moneymatters;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by lasyaboddapati on 2/8/15.
 */
public class BudgetListViewFragment extends Fragment {
    static BudgetListAdapter adapter;
    private static ExpandableListView lv;
    private int expandableListSelectionType;
    private ActionMode actionMode;
    //List<Object> groupsToRemove;
    List<Integer> groupsToRemove;
    Context context;

    public static BudgetListViewFragment newInstance(Context context) {
        BudgetListViewFragment budgetListViewFragment = new BudgetListViewFragment();
        budgetListViewFragment.context = context;
        adapter = new BudgetListAdapter(context);
        return budgetListViewFragment;
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
                            popup_edit_budget_dialog(groupPos);
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
        return rootView;
    }

    private void popup_edit_budget_dialog(final int groupPos) {

    }

    public static class BudgetListAdapter extends BaseExpandableListAdapter {

        private final LayoutInflater inf;
        HashMap<Budget.MONTHS, String[]> list;
        HashMap<Budget.MONTHS, String> monthlyBudgetList;
        protected SQLiteDatabase db;

        public BudgetListAdapter(Context context) {
            list = new LinkedHashMap<Budget.MONTHS, String[]>(Budget.MONTHS.values().length);
            monthlyBudgetList = new LinkedHashMap<Budget.MONTHS, String>(Budget.MONTHS.values().length);
            for (int i=1; i< Budget.MONTHS.values().length; i++) {
                list.put(Budget.MONTHS.values()[i], null);
            }

            this.db = new BudgetDatabase(context).getWritableDatabase();
            inf = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            return list.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (list.get(Budget.MONTHS.values()[groupPosition+1]) == null) {
                Log.d("GET CHILDREN COUNT", "0");
                return 0;
            } else {
                Log.d("GET CHILDREN COUNT", list.get(Budget.MONTHS.values()[groupPosition+1]).length+"");
                return list.get(Budget.MONTHS.values()[groupPosition+1]).length;
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return list.keySet().toArray()[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return list.get(Budget.MONTHS.values()[groupPosition+1])[childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
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
            ViewHolder weekHolder;
            ViewHolder amountHolder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.budget_list_child_item, parent, false);
                weekHolder = new ViewHolder();
                amountHolder = new ViewHolder();
                weekHolder.text = (TextView) convertView.findViewById(R.id.listItemLeft);
                amountHolder.text = (TextView) convertView.findViewById(R.id.listItemRight);
                convertView.setTag(R.string.TAG_LEFT, weekHolder);
                convertView.setTag(R.string.TAG_RIGHT, amountHolder);
            } else {
                weekHolder = (ViewHolder) convertView.getTag(R.string.TAG_LEFT);
                amountHolder = (ViewHolder) convertView.getTag(R.string.TAG_RIGHT);
            }

            //weekHolder.text.setText(getChild(groupPosition, childPosition).toString());
            weekHolder.text.setText("Week "+(childPosition+1));
            if(getChild(groupPosition, childPosition).toString().isEmpty()) {
                amountHolder.text.setText("NOT SET");
            } else {
                amountHolder.text.setText(getChild(groupPosition, childPosition).toString());
            }
            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder monthHolder;
            ViewHolder amountHolder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.budget_list_group_item, parent, false);
                monthHolder = new ViewHolder();
                amountHolder = new ViewHolder();
                monthHolder.text = (TextView) convertView.findViewById(R.id.listItemLeft);
                amountHolder.text = (TextView) convertView.findViewById(R.id.listItemRight);
                convertView.setTag(R.string.TAG_LEFT, monthHolder);
                convertView.setTag(R.string.TAG_RIGHT, amountHolder);
            } else {
                monthHolder = (ViewHolder) convertView.getTag(R.string.TAG_LEFT);
                amountHolder = (ViewHolder) convertView.getTag(R.string.TAG_RIGHT);
            }

            monthHolder.text.setText(getGroup(groupPosition).toString());
            if(monthlyBudgetList.get((Budget.MONTHS)getGroup(groupPosition)) != null) {
                amountHolder.text.setText(monthlyBudgetList.get((Budget.MONTHS) getGroup(groupPosition)).toString());
            } else {
                amountHolder.text.setText("NOT SET");
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private class ViewHolder {
            TextView text;
        }

        public void addItem(String month, String monthlyBudget, String[] weeklyBudget) {
            insertIntoDatabase(month, monthlyBudget, weeklyBudget);
            displayDb();

            String[] itemDetail = new String[4];
            for(int i=0; i<4; i++) {
                /*if(weeklyBudget[i].isEmpty()) {
                    itemDetail[i] = "Week "+(i+1)+"\t\t\t\t\t\t\t\t\t\t"+"NOT SET";
                } else {
                    itemDetail[i] = "Week " +(i+1)+"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + weeklyBudget[i];
                }*/
                itemDetail[i] = weeklyBudget[i];
            }
            Log.d("ADDITEM BUDGET", Budget.MONTHS.valueOf(month)+"\t"+itemDetail[0]+"|"+itemDetail[1]+"|"+itemDetail[2]+"|"+itemDetail[3]);
            //details.add(Budget.MONTHS.valueOf(month).ordinal()-1, itemDetail);
            list.put(Budget.MONTHS.valueOf(month), itemDetail);
            monthlyBudgetList.put(Budget.MONTHS.valueOf(month), monthlyBudget);
            notifyDataSetChanged();
        }

        public void removeItem(int groupPos) {
            list.put(Budget.MONTHS.values()[groupPos], null);
            notifyDataSetChanged();
        }

        public void removeItems(List<Integer> groupsToRemove) {
            for (int i=0; i<groupsToRemove.size(); i++) {
                int groupPos = groupsToRemove.get(i);
                Log.d("REMOVE ITEMS", "groupPos "+groupPos);
                Log.d("REMOVE ITEMS", "Budget.MONTHS.values()[groupPos] "+Budget.MONTHS.values()[groupPos]);
                deleteFromDatabase(Budget.MONTHS.values()[groupPos + 1]);
                list.put(Budget.MONTHS.values()[groupPos+1], null);
                monthlyBudgetList.put(Budget.MONTHS.values()[groupPos+1], null);
            }
            notifyDataSetChanged();
        }

        /*private void updateListItem(int groupPos, String month, String monthlyBudget, String[] weeklyBudget) {
            ArrayList<String> itemDetail = new ArrayList<String>();
            for (int i=0; i<4; i++) {
                itemDetail.add("Week "+(i+1)+"  "+weeklyBudget[i]);
            }
            details.add(groupPos, itemDetail);

            long id = ids.get(groupPos);
            updateInDatabase(id, month, monthlyBudget, weeklyBudget);
            //notifyDataSetChanged();   //TODO: update only relevant list item in view
        }*/

        public void insertIntoDatabase(String month, String monthlyBudget, String[] weeklyBudget) {
            ContentValues newValues = new ContentValues();
            //newValues.put(BudgetDatabase.YEAR_COLUMN, Calendar.getInstance().YEAR);
            newValues.put(BudgetDatabase.MONTHLY_BUDGET_COLUMN, monthlyBudget);
            newValues.put(BudgetDatabase.WEEK1_COLUMN, weeklyBudget[0]);
            newValues.put(BudgetDatabase.WEEK2_COLUMN, weeklyBudget[1]);
            newValues.put(BudgetDatabase.WEEK3_COLUMN, weeklyBudget[2]);
            newValues.put(BudgetDatabase.WEEK4_COLUMN, weeklyBudget[3]);

            Cursor c = db.rawQuery("SELECT * FROM " +BudgetDatabase.DATABASE_TABLE+ " WHERE " +BudgetDatabase.MONTH_COLUMN+ "="+"'"+month+"'", null);
            if (c.getCount() != 0) {
                displayDb();
                String whereClause = BudgetDatabase.MONTH_COLUMN + " = "+"'"+month+"'";
                Log.d("UPDATE", "Updating in db");
                db.update(BudgetDatabase.DATABASE_TABLE, newValues, whereClause, null);
            } else {
                newValues.put(BudgetDatabase.MONTH_COLUMN, month);
                Log.d("INSERT", "inserting into db");
                db.insert(BudgetDatabase.DATABASE_TABLE, null, newValues);
            }
            c.close();
        }

        public void deleteFromDatabase(Budget.MONTHS month) {
            String whereClause = BudgetDatabase.MONTH_COLUMN + " = "+"'"+month+"'";
            int n = db.delete(BudgetDatabase.DATABASE_TABLE, whereClause, null);
            Log.d("DELETE", "Deleted "+n);
            displayDb();
        }

        public void updateInDatabase(long id, String month, String monthlyBudget, String[] weeklyBudget) {
            String whereClause = BudgetDatabase.MONTH_COLUMN + " = "+"'"+month+"'";
            ContentValues newValues = new ContentValues();
            //newValues.put(BudgetDatabase.MONTH_COLUMN, month);
            newValues.put(BudgetDatabase.MONTHLY_BUDGET_COLUMN, monthlyBudget);
            newValues.put(BudgetDatabase.WEEK1_COLUMN, weeklyBudget[0]);
            newValues.put(BudgetDatabase.WEEK2_COLUMN, weeklyBudget[1]);
            newValues.put(BudgetDatabase.WEEK3_COLUMN, weeklyBudget[2]);
            newValues.put(BudgetDatabase.WEEK4_COLUMN, weeklyBudget[3]);
            int n = db.update(BudgetDatabase.DATABASE_TABLE, newValues, whereClause, null);
            Log.d("UPDATE", "Updated "+n);
        }

        public void displayDb() {
            Cursor c = db.rawQuery("SELECT * FROM "+BudgetDatabase.DATABASE_TABLE, null);
            Log.d("DB", "COUNT is "+c.getCount());
            while (c.moveToNext()) {
                //Log.d("DB", "ID "+c.getString(0));
                //Log.d("DB", "YEAR "+c.getString(1));
                Log.d("DB", "MONTH "+c.getString(0));
                Log.d("DB", "MONTHLY BUDGET "+c.getString(1));
                Log.d("DB", "WEEK1 BUDGET "+c.getString(2));
                Log.d("DB", "WEEK2 BUDGET "+c.getString(3));
                Log.d("DB", "WEEK3 BUDGET "+c.getString(4));
                Log.d("DB", "WEEK3 BUDGET "+c.getString(5));
            }
            c.close();
        }

        public void populateListView() {
            String[] resultColumns = {BudgetDatabase.MONTH_COLUMN, BudgetDatabase.MONTHLY_BUDGET_COLUMN
                                    , BudgetDatabase.WEEK1_COLUMN, BudgetDatabase.WEEK2_COLUMN
                                    , BudgetDatabase.WEEK3_COLUMN, BudgetDatabase.WEEK4_COLUMN};
            Cursor cursor = db.query(BudgetDatabase.DATABASE_TABLE, resultColumns, null, null, null, null, null);

            Log.d("COUNT", "list size" +list.size()+ "cursor size"+ cursor.getCount());

            while (cursor.moveToNext()) {
                //Long id = cursor.getLong(0);
                //String year = cursor.getString(1);
                String month = cursor.getString(0);
                String monthlyBudget = cursor.getString(1);
                String[] weeklyBudget = new String[4];
                weeklyBudget[0] = cursor.getString(2);
                weeklyBudget[1] = cursor.getString(3);
                weeklyBudget[2] = cursor.getString(4);
                weeklyBudget[3] = cursor.getString(5);


                String[] itemDetail = new String[4];
                for (int i=0; i<4; i++) {
                    /*if (weeklyBudget[i].isEmpty()) {
                        itemDetail[i] = "Week "+(i+1)+"\t\t\t\t\t\t\t\t\t\t"+"NOT SET";
                    } else {
                        itemDetail[i] = "Week " + (i + 1) + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + weeklyBudget[i];
                    }*/
                    itemDetail[i] = weeklyBudget[i];
                }
                Log.d("POPULATE", "Putting in "+Budget.MONTHS.valueOf(month));
                list.put(Budget.MONTHS.valueOf(month), itemDetail);
                monthlyBudgetList.put(Budget.MONTHS.valueOf(month), monthlyBudget);
                Log.d("POPULATE", list.keySet().toArray()[Budget.MONTHS.valueOf(month).ordinal()-1]+" "+list.get(Budget.MONTHS.valueOf(month)).toString());
                printList();
            }
            cursor.close();
        }

        private void printList() {
            for (int i=0; i< list.size(); i++) {
                Log.d("LIST", i+" "+list.keySet().toArray()[i]+" "+Budget.MONTHS.values()[i+1]+" "+list.get(Budget.MONTHS.values()[i+1]));
            }
        }

    }
}

