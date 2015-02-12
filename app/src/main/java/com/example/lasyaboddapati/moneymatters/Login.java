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


public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login=(Button)findViewById(R.id.button);
        final EditText username=(EditText)findViewById(R.id.editText);
        final EditText password=(EditText)findViewById(R.id.editText2);

        setTitle("Login");


        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if("kaustav".equals(username.getText().toString())&&"ghosh".equals(password.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Success",
                            Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(Login.this, HomeScreen.class);
                    startActivity(homeIntent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Failed!",
                            Toast.LENGTH_SHORT).show();
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
