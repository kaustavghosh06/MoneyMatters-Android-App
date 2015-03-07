package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class Addfriends extends Activity {

    ArrayList<String> userlist;
    String loginUser;
    ArrayList<String> friendlist;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);
        SharedPreferences sharedPref = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        loginUser = sharedPref.getString("Username", null);

        final AutoCompleteTextView friendname=(AutoCompleteTextView)findViewById(R.id.friendname);
        Button addfriend=(Button)findViewById(R.id.addfriend);
       listView = (ListView) findViewById(R.id.list);

        Firebase.setAndroidContext(this);


        final Firebase userscloud=new Firebase("https://crackling-inferno-5209.firebaseio.com/");
        Firebase friendcloud=new Firebase("https://crackling-inferno-5209.firebaseio.com/"+loginUser+"/Friends/");

        //For getting UserList


        userscloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> usersmap = (Map<String, Object>) snapshot.getValue();
                userlist = new ArrayList<String>();

                for (String key : usersmap.keySet()) {

                    userlist.add(key);
                }
                userlist.remove(loginUser);
                for (String str : userlist) {
                    Log.d("user", str);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line, userlist);
                friendname.setAdapter(adapter);


            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        //For getting Friendlist

        friendcloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> usersmap=null;

                if(!(snapshot.getValue().toString()).equals("true")){
                    usersmap = (Map<String, Object>) snapshot.getValue();
                }

                friendlist = new ArrayList<String>();

                if(usersmap!=null) {


                    for (String key : usersmap.keySet()) {

                        friendlist.add(key);
                    }
                }
                //userlist.remove(loginUser);
                for (String str : friendlist) {
                    Log.d("user", str);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.simplerow, friendlist);

                // Assign adapter to ListView
                listView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });






        friendname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!userlist.contains(s.toString())) {
                    friendname.setError("User doesn't exist");
                } else {
                    friendname.setError(null);
                }


            }
        });

        //For getting current us

        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Firebase receivercloud=new Firebase("https://crackling-inferno-5209.firebaseio.com/"+loginUser);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String friend=friendname.getText().toString();
                if(friendlist.contains(friend))
                {
                    Toast.makeText(getApplicationContext(),
                            "The user is already your friend", Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(getApplicationContext(),
                            "User added to your Friend List", Toast.LENGTH_SHORT).show();
                    receivercloud.child("Friends").child(friend).setValue("true");
                }





            }
        });

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addfriends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
