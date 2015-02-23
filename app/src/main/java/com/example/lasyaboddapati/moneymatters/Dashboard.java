package com.example.lasyaboddapati.moneymatters;

/**
 * Created by kaustav1992 on 2/23/15.
 */

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

public class Dashboard extends FragmentActivity {
    private ViewPager _mViewPager;
    private DashboardAdapter _adapter;
    String user="kaustav1992";
    int pos=0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setUpView();
        setTab();

        TextView b1=(TextView)findViewById(R.id.textView1);
        TextView b2=(TextView)findViewById(R.id.textView2);
        TextView b3=(TextView)findViewById(R.id.textView3);
        TextView b4=(TextView)findViewById(R.id.textView4);
        TextView b5=(TextView)findViewById(R.id.textView5);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos=0;
                _mViewPager.setCurrentItem(pos);


            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos=1;
                _mViewPager.setCurrentItem(pos);

            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos=2;
                _mViewPager.setCurrentItem(pos);

            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos=3;
                _mViewPager.setCurrentItem(pos);

            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos=4;
                _mViewPager.setCurrentItem(pos);

            }
        });

    }
    private void setUpView(){
        _mViewPager = (ViewPager) findViewById(R.id.viewPager);
        _adapter = new DashboardAdapter(getApplicationContext(),getSupportFragmentManager(),user);
        _mViewPager.setAdapter(_adapter);
        _mViewPager.setCurrentItem(0);
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
                        findViewById(R.id.third_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.fourth_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.fifth_tab).setVisibility(View.INVISIBLE);
                        break;

                    case 1:
                        findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.second_tab).setVisibility(View.VISIBLE);
                        findViewById(R.id.third_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.fourth_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.fifth_tab).setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.third_tab).setVisibility(View.VISIBLE);
                        findViewById(R.id.fourth_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.fifth_tab).setVisibility(View.INVISIBLE);
                        break;

                    case 3:
                        findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.third_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.fourth_tab).setVisibility(View.VISIBLE);
                        findViewById(R.id.fifth_tab).setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.third_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.fourth_tab).setVisibility(View.INVISIBLE);
                        findViewById(R.id.fifth_tab).setVisibility(View.VISIBLE);
                        break;
                }
            }

        });

    }
}