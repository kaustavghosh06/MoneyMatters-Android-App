package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Help extends Activity {
    static final int INTRO_HELP = 0;
    static final int BUDGET_HELP = 1;
    static final int EXPENSES_HELP = 2;
    static final int LOANS_HELP = 3;
    static final int NOTIFICATIONS_HELP = 4;
    static final int FRIENDS_HELP = 5;
    static final int UNINSTALL_HELP = 6;
    int current;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        final TextView next = (TextView) findViewById(R.id.next);
        final TextView prev = (TextView) findViewById(R.id.prev);
        final LinearLayout container = (LinearLayout) findViewById(R.id.containter);
        view = getLayoutInflater().inflate(R.layout.activity_help_intro, null);
        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        current = INTRO_HELP;
        prev.setVisibility(View.GONE);
        next.setText("Get started!");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (current) {
                    case INTRO_HELP:
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_budget, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = BUDGET_HELP;
                        prev.setVisibility(View.VISIBLE);
                        next.setText("Next ->");
                        break;
                    case BUDGET_HELP:
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_expenses, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = EXPENSES_HELP;
                        break;
                    case EXPENSES_HELP:
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_loans, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = LOANS_HELP;
                        break;
                    case LOANS_HELP:
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_notifications, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = NOTIFICATIONS_HELP;
                        break;
                    case NOTIFICATIONS_HELP:
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_friends, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = FRIENDS_HELP;
                        break;
                    case FRIENDS_HELP:
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_uninstall, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = UNINSTALL_HELP;
                        next.setVisibility(View.GONE);
                        break;
                    case UNINSTALL_HELP:
                        //TODO: disable next
                        break;
                }

                //Toast.makeText(Help.this, "changed to "+current, Toast.LENGTH_SHORT).show();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (current) {
                    case BUDGET_HELP :
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_intro, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = INTRO_HELP;
                        prev.setVisibility(View.GONE);
                        next.setText("Get started!");
                        break;
                    case EXPENSES_HELP :
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_budget, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = BUDGET_HELP;
                        break;
                    case LOANS_HELP:
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_expenses, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = EXPENSES_HELP;
                        break;
                    case NOTIFICATIONS_HELP:
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_loans, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = LOANS_HELP;
                        break;
                    case FRIENDS_HELP:
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_notifications, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = NOTIFICATIONS_HELP;
                        break;
                    case UNINSTALL_HELP:
                        container.removeAllViews();
                        view = getLayoutInflater().inflate(R.layout.activity_help_friends, null);
                        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        current = FRIENDS_HELP;
                        next.setVisibility(View.VISIBLE);
                        break;
                }

                //Toast.makeText(Help.this, "changed to "+current, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
