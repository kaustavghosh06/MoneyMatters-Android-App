package com.example.lasyaboddapati.moneymatters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Notifications extends FragmentActivity {
    String loginUser="kaustav1992";
    private ViewPager _mViewPager;
    private ViewPagerAdapter2 _adapter;
    final ArrayList<String> userlist=new ArrayList<String>();
    int pos=0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setUpView();
        setTab();
        //For getting UserList

        Firebase.setAndroidContext(this);
        final Firebase userscloud=new Firebase("https://crackling-inferno-5209.firebaseio.com/");
        userscloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> usersmap = (Map<String, Object>) snapshot.getValue();

                for (String key : usersmap.keySet()) {

                    userlist.add(key);
                }
                for(String str: userlist)
                {
                    Log.d("user", str);
                }



            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        TextView b1=(TextView)findViewById(R.id.textView1);
        TextView b2=(TextView)findViewById(R.id.textView2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos=0;
                _mViewPager.setCurrentItem(pos);

                findViewById(R.id.first_tab).setVisibility(View.VISIBLE);
                findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);




            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos=1;
                _mViewPager.setCurrentItem(pos);

                findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                findViewById(R.id.second_tab).setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();


        if(id==R.id.action_notf){

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Notifications.this);
            alertDialog.setTitle("Send Notification");
            alertDialog.setMessage("Enter the Notification you want to send to your friend");

           /* final EditText input = new EditText(Lending.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);*/



            //final EditText user = new EditText(this);

            final EditText message = new EditText(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, userlist);
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

                            String receiver=user.getText().toString();
                            String notf=message.getText().toString();

                            Firebase receivercloud=new Firebase("https://crackling-inferno-5209.firebaseio.com/"+receiver);
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                            receivercloud.child("Notifications").child(timeStamp).setValue(notf+":"+"0"+":"+loginUser);


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

    private void setUpView(){
        _mViewPager = (ViewPager) findViewById(R.id.viewPager);
        _adapter = new ViewPagerAdapter2(getApplicationContext(),getSupportFragmentManager());
        _mViewPager.setAdapter(_adapter);
        _mViewPager.setCurrentItem(0);
    }
    public void setTab(){
        _mViewPager.setOnPageChangeListener(new OnPageChangeListener(){

            @Override
            public void onPageScrollStateChanged(int position) {}
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                switch(position){
                    case 0:
                        findViewById(R.id.first_tab).setVisibility(View.VISIBLE);
                        findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
                        break;

                    case 1:
                        findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.second_tab).setVisibility(View.VISIBLE);
                        break;
                }
            }

        });

    }
}