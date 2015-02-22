package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Lending extends Activity {

    com.example.lasyaboddapati.moneymatters.ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, LinkedHashMap<String,String>> listDataChild;

    Map<String, Integer> creds;
    HashMap<String,Integer> debt;
    HashMap<String,String> notf;
    String user="kaustav1992";
    String value;
    LinkedHashMap<String, String> cr;
    LinkedHashMap<String, String> d;
    LinkedHashMap<String, String> n;
    String key1;
    String lender;
    String receiver;
    String descr;
    String amt;
    Firebase lendercloud=null;
    Firebase receivercloud=null;
    String url="https://crackling-inferno-5209.firebaseio.com/";
    final ArrayList<String> userlist=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending);

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, LinkedHashMap<String,String>>();

        // Adding child data
        listDataHeader.add("Credits");
        listDataHeader.add("Debts");
        listDataHeader.add("Notifications");

        LinkedHashMap<String,String> deb = new LinkedHashMap<String,String>();
        deb.put("Lasya","Lasya--->70");
        deb.put("Anand","Anand--->80");
        deb.put("Manoj","Manoj--->90");


        LinkedHashMap<String,String> not = new LinkedHashMap<String,String>();
        not.put("Lasya","Hey send me my money");
        not.put("Anand","Send me my money bro");
        not.put("Manoj","where?!!");




        //creds=new HashMap<String,Integer>();


/*
        debt=new HashMap<String,Integer>();
        notf=new HashMap<String,String>();
*/

/*
        creds.put("Lasya",40);
        creds.put("Manoj",70);

        //creds.put("Lasya",40);
        //creds.put("Manoj",70);


        debt.put("Lasya",70);
        debt.put("Anand",2);

        notf.put("LASYA","YO!!");

*/
        Firebase.setAndroidContext(this);
        final Firebase myFirebaseRef = new Firebase("https://crackling-inferno-5209.firebaseio.com/"+user);
        final Firebase userscloud=new Firebase("https://crackling-inferno-5209.firebaseio.com/");


        Firebase credcloud=myFirebaseRef.child("Credit");
        for(String key: creds.keySet())
        {
            credcloud.child(key).setValue(creds.get(key));
        }

        Firebase debtcloud=myFirebaseRef.child("Debts");
        for(String key: debt.keySet())
        {
            debtcloud.child(key).setValue(debt.get(key));
        }
        Firebase notifcloud=myFirebaseRef.child("Notifications");
        for(String key: notf.keySet())
        {
            notifcloud.child(key).setValue(notf.get(key));
        }


        expListView = (ExpandableListView) findViewById(R.id.exp);

        //listDataChild.put(listDataHeader.get(1), deb);
        //listDataChild.put(listDataHeader.get(2), not);
        cr = new LinkedHashMap<String, String>();
        d=new LinkedHashMap<String,String>();
        n=new LinkedHashMap<String,String>();

        //For getting UserList


        userscloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> usersmap = (Map<String, Object>) snapshot.getValue();

                for (String key : usersmap.keySet()) {

                    userlist.add(key);
                }
                for(String str: userlist)
                {
                    Log.d("user",str);
                }



            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });




        //For Credits

        credcloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> cred = (Map<String, Object>) snapshot.getValue();

                for(String key: cred.keySet()) {

                    cr.put(key, key+ " owes " + "$"+cred.get(key).toString());
                }
                listDataChild.put(listDataHeader.get(0),cr); // Header, Child data
                    // get the listview


                    // preparing list data
                    //prepareListData();
                listAdapter = new com.example.lasyaboddapati.moneymatters.ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild);

                    // setting list adapter
                expListView.setAdapter(listAdapter);




            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        //FOR DEBTS

        debtcloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> de = (Map<String, Object>) snapshot.getValue();

                for(String key: de.keySet()) {

                    d.put(key,"You owe " + key + "$"+de.get(key).toString());
                }
                listDataChild.put(listDataHeader.get(1),d); // Header, Child data
                // get the listview


                // preparing list data
                //prepareListData();
                listAdapter = new com.example.lasyaboddapati.moneymatters.ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild);

                // setting list adapter
                expListView.setAdapter(listAdapter);




            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        //For Notifications

       notifcloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> no = (Map<String, Object>) snapshot.getValue();

                for(String key: no.keySet()) {

                    n.put(key, key +":" +no.get(key).toString());
                }
                listDataChild.put(listDataHeader.get(2),n); // Header, Child data
                // get the listview


                // preparing list data
                //prepareListData();
                listAdapter = new com.example.lasyaboddapati.moneymatters.ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild);

                // setting list adapter
                expListView.setAdapter(listAdapter);




            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });







    }

    void prepareListData()
    {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, LinkedHashMap<String,String>>();

        // Adding child data
        listDataHeader.add("Credits");
        listDataHeader.add("Debts");
        listDataHeader.add("Notifications");

        // Adding child data
        LinkedHashMap<String,String> cred = new LinkedHashMap<String,String>();
        cred.put("Lasya",value);
        cred.put("Anand","Anand--->60");
        cred.put("Manoj","Manoj--->40");

        LinkedHashMap<String,String> deb = new LinkedHashMap<String,String>();
        deb.put("Lasya","Lasya--->70");
        deb.put("Anand","Anand--->80");
        deb.put("Manoj","Manoj--->90");


        LinkedHashMap<String,String> not = new LinkedHashMap<String,String>();
        not.put("Lasya","Hey send me my money");
        not.put("Anand","Send me my money bro");
        not.put("Manoj","where?!!");

        listDataChild.put(listDataHeader.get(0), cred); // Header, Child data
        listDataChild.put(listDataHeader.get(1), deb);
        listDataChild.put(listDataHeader.get(2), not);








    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lending, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if(id==R.id.action_lend)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Lending.this);
            alertDialog.setTitle("Lend Money");
            alertDialog.setMessage("Enter the Amount you want to lend to your friend");

           /* final EditText input = new EditText(Lending.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);*/

            final String[] USERS = new String[] {
                    "User1", "User2", "User3", "User4", "User5"
            };

            //final EditText user = new EditText(this);

            final EditText amount = new EditText(this);
            final EditText description = new EditText(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, userlist);
            final AutoCompleteTextView user1 = new AutoCompleteTextView(this);
            user1.setHint("Enter User ID");
            user1.setAdapter(adapter);



            amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            amount.setHint("Enter Amount");
            description.setHint("Enter Description");

            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(user1);
            layout.addView(amount);
            layout.addView(description);
            alertDialog.setView(layout);


            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(getApplicationContext(),
                                            "Amount Lent!", Toast.LENGTH_SHORT).show();
                                    lender=user;
                                    amt=amount.getText().toString();
                                    receiver=user1.getText().toString();
                                    descr=description.getText().toString();
                                    lendercloud=new Firebase("https://crackling-inferno-5209.firebaseio.com/"+lender);
                                    receivercloud=new Firebase("https://crackling-inferno-5209.firebaseio.com/"+receiver);
                                    lendercloud.child("Credit").child(descr+":"+receiver).setValue(amt);
                                    receivercloud.child("Debts").child(descr+":"+lender).setValue(amt);





                                }


                    });

            alertDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }





        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
