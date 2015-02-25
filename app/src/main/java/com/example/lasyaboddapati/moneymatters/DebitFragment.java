package com.example.lasyaboddapati.moneymatters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class DebitFragment extends Fragment {
    ListView listView ;
    static String user;
    //LinkedHashMap<String, String> d;
    //Set<String> d= new HashSet<String>();
    static Context context1;
    static CustomListAdapter adapter;

    public static Fragment newInstance(Context context,String username) {
        DebitFragment f = new DebitFragment();
        context1=context;
        user=username;



        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_debit, container, false);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_debit, null);
        listView = (ListView) rootView.findViewById(R.id.list);

        Firebase.setAndroidContext(context1);
        final Firebase myFirebaseRef = new Firebase("https://crackling-inferno-5209.firebaseio.com/"+user);
        Firebase debtcloud=myFirebaseRef.child("Debts");

       /* debtcloud.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                String timestamp;
                                                String info=null;
                                                Log.d("snapshotvalue",dataSnapshot.getValue().toString());
                                                String[] parts=(dataSnapshot.getValue().toString()).split(":");

                                                Intent resultIntent = new Intent(context1, LendStatus.class);
                                                resultIntent.putExtra("Username",user);
                                                //TODO : set tab to System Notifications
                                                PendingIntent pendingIntent = PendingIntent.getActivity(context1, 0, resultIntent, 0);

                                                Notification notification = new Notification.Builder(context1)
                                                        .setContentTitle("Money Matters")
                                                        .setContentText("hi")
                                                        .setSmallIcon(R.drawable.ic_social_notifications_on)
                                                        .setContentIntent(pendingIntent)
                                                        .setAutoCancel(true)
                                                        .build();

                                                NotificationManager notificationManager = (NotificationManager) context1.getSystemService(Context.NOTIFICATION_SERVICE);
                                                notificationManager.notify(1, notification);
                                                Log.d("NOTIFY", "user notified");




                                                /*Map<String, String> newPost = (Map<String, String>) dataSnapshot.getValue();
                                                for (String key : newPost.keySet()) {

                                                   info=newPost.get(key);
                                                }
                                                Log.d("debitinfo",info);*/
/*
                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onCancelled(FirebaseError firebaseError) {

                                            }
                                        });
*/


        //FOR DEBTS

        debtcloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> de = null;
                if (!(snapshot.getValue().toString()).equals("true")) {
                    de = (Map<String, Object>) snapshot.getValue();
                }
                ArrayList<String> d = new ArrayList<String>();
                if (de != null) {
                    for (String key : de.keySet()) {

                        d.add(key + "-" + de.get(key).toString());
                    }
                }
                //String[] dArr = new String[d.size()];
                //dArr = d.toArray(dArr);

                /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(context1,
                        R.layout.simplerow, dArr);*/
                adapter = new CustomListAdapter(context1, R.layout.custom_list_item, d);


                // Assign adapter to ListView
                listView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });



        return rootView;
    }

}