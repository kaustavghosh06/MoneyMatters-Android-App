package com.example.lasyaboddapati.moneymatters;

import android.content.Context;
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
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class UserNotificationFragment extends Fragment {
    ListView listView ;
    static String user;
    static CustomListAdapter adapter;
    //LinkedHashMap<String, String> d;
    //Set<String> d= new HashSet<String>();
    static Context context1;
    private ActionMode actionMode;

    public static Fragment newInstance(Context context,String username) {
        UserNotificationFragment f = new UserNotificationFragment();
        context1=context;
        user=username;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_notification, container, false);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_debit, null);
        listView = (ListView) rootView.findViewById(R.id.received);

        Firebase.setAndroidContext(context1);
        final Firebase myFirebaseRef = new Firebase("https://crackling-inferno-5209.firebaseio.com/"+user);
        Firebase notifcloud=myFirebaseRef.child("Notifications");



        //FOR DEBTS

        notifcloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> de=null;
                if(!(snapshot.getValue().toString()).equals("true")){
                    de=(Map<String, Object>) snapshot.getValue();
                }
                ArrayList<String> d= new ArrayList<String>();

                if(de!=null) {
                    for (String key : de.keySet()) {

                        d.add(key + "-" + de.get(key).toString());
                    }
                    String[] dArr = new String[d.size()];
                    dArr = d.toArray(dArr);
                }

                //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context1,
                        //R.layout.simplerow, dArr);

                adapter = new CustomListAdapter(context1, R.layout.custom_list_item,d);

                // Assign adapter to ListView
                listView.setAdapter(adapter);

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        // Delete on LongPress
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int checkedCount;
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Capture total checked items
                checkedCount = listView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
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
                int id = item.getItemId();
                SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();

                Log.d("CHECKED ITEM POSITIONS", checkedItemPositions.toString());
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    if (checkedItemPositions.valueAt(i)) {
                        int position = checkedItemPositions.keyAt(i);
                        if (id == R.id.action_delete) {
                            //groupsToRemove.add(position);
                            //TODO : Add function to delete
                        }
                    }
                }

                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                /*if (groupsToRemove!=null) {
                      adapter.removeItems(groupsToRemove);
                      groupsToRemove.clear();
                }*/
                //TODO: Clear checked items
            }
        });

        return rootView;
    }

}