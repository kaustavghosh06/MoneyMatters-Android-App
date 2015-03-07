package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class StartUp extends Activity {
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_start_up);

        SharedPreferences sharedPref = getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        user = sharedPref.getString("Username", null);

        if(user == null) {
            Intent register = new Intent(this, Register.class);
            startActivity(register);
        } else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("Username", user);
            editor.commit();

            Intent homeIntent = new Intent(this, Home.class);
            startActivity(homeIntent);
        }
    }

}
