package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;


public class Register extends Activity {
    String sname;
    String susername;
    String spassword;
    String cspassowrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText name=(EditText)findViewById(R.id.name);
        final EditText username=(EditText)findViewById(R.id.username);
        final EditText password=(EditText)findViewById(R.id.password);
        final EditText cpassword=(EditText)findViewById(R.id.cpassword);

        Button reg=(Button)findViewById(R.id.regis);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sname=name.getText().toString();
                susername=username.getText().toString();
                spassword=password.getText().toString();
                cspassowrd=cpassword.getText().toString();


                if(sname.isEmpty() || susername.isEmpty() || spassword.isEmpty() || cspassowrd.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter all the values", Toast.LENGTH_SHORT).show();
                }

                else if(!spassword.equals(cspassowrd))
                {
                    Toast.makeText(getApplicationContext(),
                            "Both Passwords don't match", Toast.LENGTH_SHORT).show();
                }
                else {

                    Firebase.setAndroidContext(getApplicationContext());
                    final Firebase myFirebaseRef = new Firebase("https://crackling-inferno-5209.firebaseio.com/");
                    myFirebaseRef.child(susername).setValue(true);
                    myFirebaseRef.child(susername).child("Credit").setValue("true");

                    myFirebaseRef.child(susername).child("Debts").setValue("true");
                    myFirebaseRef.child(susername).child("Notifications").setValue("true");
                    myFirebaseRef.child(susername).child("PersonalInfo").setValue("true");
                    myFirebaseRef.child(susername).child("PersonalInfo").child("Name").setValue(sname);
                    myFirebaseRef.child(susername).child("PersonalInfo").child("Password").setValue(spassword);
                    Toast.makeText(getApplicationContext(),
                            "Successfully Registered!Login now", Toast.LENGTH_SHORT).show();

                    Intent registerIntent = new Intent(Register.this, Login.class);
                    startActivity(registerIntent);
                }









            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
