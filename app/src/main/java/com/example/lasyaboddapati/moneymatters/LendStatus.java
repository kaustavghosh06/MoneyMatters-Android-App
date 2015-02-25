package com.example.lasyaboddapati.moneymatters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

public class LendStatus extends FragmentActivity {
    private ViewPager _mViewPager;
    private ViewPagerAdapter _adapter;
    String lender;
    String receiver;
    String descr;
    String amt;
    Firebase lendercloud=null;
    Firebase receivercloud=null;
    String user;
    int pos=0;
    ArrayList<String> userlist;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_status);
        user= getIntent().getExtras().getString("Username");

        Firebase.setAndroidContext(this);
        final Firebase userscloud=new Firebase("https://crackling-inferno-5209.firebaseio.com/");

        //For getting UserList


        userscloud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> usersmap = (Map<String, Object>) snapshot.getValue();
                userlist=new ArrayList<String>();

                for (String key : usersmap.keySet()) {

                    userlist.add(key);
                }
                userlist.remove(user);
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




        setUpView();
        setTab();
        TextView b1=(TextView)findViewById(R.id.textView1);
        TextView b2=(TextView)findViewById(R.id.textView2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos=0;
                _mViewPager.setCurrentItem(pos);

                //findViewById(R.id.first_tab).setVisibility(View.VISIBLE);
                //findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);




            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos=1;
                _mViewPager.setCurrentItem(pos);

                //findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                //findViewById(R.id.second_tab).setVisibility(View.VISIBLE);

            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lending, menu);
        return true;
    }
    private void setUpView(){
        _mViewPager = (ViewPager) findViewById(R.id.viewPager);
        _adapter = new ViewPagerAdapter(getApplicationContext(),getSupportFragmentManager(),user,pos);
        _mViewPager.setAdapter(_adapter);
        /*_mViewPager.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });*/
        _mViewPager.setCurrentItem(0);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if(id==R.id.action_lend)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LendStatus.this);
            alertDialog.setTitle("Lend Money");
            alertDialog.setMessage("Enter the Amount you want to lend to your friend");

            final EditText amount = new EditText(this);
            final EditText description = new EditText(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, userlist);
            final AutoCompleteTextView user1 = new AutoCompleteTextView(this);
            user1.setHint("Enter User ID");
            user1.setAdapter(adapter);

            user1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!userlist.contains(s.toString()))
                    {
                        user1.setError("User doesn't exist");
                    }
                    else
                    {
                        user1.setError(null);
                    }

                }
            });



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

                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                            lendercloud.child("Credit").child(timeStamp).setValue(descr+":"+receiver+":"+amt);
                            receivercloud.child("Debts").child(timeStamp).setValue(descr + ":" + lender + ":" + amt);





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
    private void setTab(){
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