package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class Login extends Activity {
    String cloudpass;
    ArrayList<String> userlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login=(Button)findViewById(R.id.button);
        final EditText username=(EditText)findViewById(R.id.editText);
        final EditText password=(EditText)findViewById(R.id.editText2);
        TextView reg=(TextView)findViewById(R.id.reg);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(Login.this, Register.class);
                startActivity(registerIntent);

            }
        });

        Firebase.setAndroidContext(this);


        final Firebase userscloud=new Firebase("https://crackling-inferno-5209.firebaseio.com/");

        //For getting UserList


        userscloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> usersmap = (Map<String, Object>) snapshot.getValue();
                userlist = new ArrayList<String>();

                for (String key : usersmap.keySet()) {

                    userlist.add(key);
                }
                //userlist.remove(user);
                for (String str : userlist) {
                    Log.d("user", str);
                }


            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });



        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                final String user = username.getText().toString();
                final String pass = password.getText().toString();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter username and password", Toast.LENGTH_LONG).show();
                } else {
                    if(userlist.contains(user)) {
                        //final Firebase usernamecloud = new Firebase("https://crackling-inferno-5209.firebaseio.com/"+user);
                        final Firebase passwordcloud = new Firebase("https://crackling-inferno-5209.firebaseio.com/" + user + "/PersonalInfo/Password");

                        passwordcloud.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                cloudpass = snapshot.getValue().toString();
                                if (cloudpass.equals(pass))

                                {
                                    Toast.makeText(getApplicationContext(), "Success",
                                            Toast.LENGTH_SHORT).show();
                                    SharedPreferences sharedPref = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("Username", user);
                                    editor.commit();

                                    Intent homeIntent = new Intent(Login.this, Home.class);
                                    //homeIntent.putExtra("Username",user);
                                    startActivity(homeIntent);
                                } else

                                {
                                    Toast.makeText(getApplicationContext(), "Failed!",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                System.out.println("The read failed: " + firebaseError.getMessage());
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Username doesn't exist!Please Register First",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            }
            });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
