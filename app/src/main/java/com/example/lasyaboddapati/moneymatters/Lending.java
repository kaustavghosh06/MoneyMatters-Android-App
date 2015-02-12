package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class Lending extends Activity {

    com.example.lasyaboddapati.moneymatters.ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, LinkedHashMap<String,String>> listDataChild;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.exp);

        // preparing list data
        prepareListData();
        listAdapter = new com.example.lasyaboddapati.moneymatters.ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

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
        cred.put("Lasya","Lasya--->50");
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, USERS);
            final AutoCompleteTextView user = new AutoCompleteTextView(this);
            user.setHint("Enter User ID");
            user.setAdapter(adapter);



            amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            amount.setHint("Enter Amount");
            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(user);
            layout.addView(amount);
            alertDialog.setView(layout);


            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(getApplicationContext(),
                                            "Amount Lent!", Toast.LENGTH_SHORT).show();
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


        if(id==R.id.action_sendnotif)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Lending.this);
            alertDialog.setTitle("Send Notification");
            alertDialog.setMessage("Enter the Notification you want to send to your friend");

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

            final EditText message = new EditText(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, USERS);
            final AutoCompleteTextView user = new AutoCompleteTextView(this);
            user.setHint("Enter User ID");
            user.setAdapter(adapter);




            message.setHint("Enter The Message");
            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(user);
            layout.addView(message);
            alertDialog.setView(layout);


            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(getApplicationContext(),
                                            "Notification Sent!", Toast.LENGTH_SHORT).show();
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
