package com.example.lasyaboddapati.moneymatters;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class Home extends Activity implements OnItemClickListener {

    //static final String EXTRA_MAP = "map";
    static final int ADD_EXPENSE_REQUEST = 1;
    String username;

    static final LauncherIcon[] ICONS = {
            new LauncherIcon(R.drawable.ic_action_action_note_add, "Add Expense"),
            new LauncherIcon(R.drawable.ic_action_av_my_library_books, "Budget"/*, "budget.png"*/),
            new LauncherIcon(R.drawable.ic_action_action_receipt, "Expenses"/*, "expenses.png"*/),
            new LauncherIcon(R.drawable.ic_action_action_thumbs_up_down, "Credits/Debits"/*, "loans.png"*/),
            new LauncherIcon(R.drawable.ic_action_action_question_answer, "Notifications"/*, "notifications.png"*/),
            new LauncherIcon(R.drawable.ic_action_social_people, "Friends"/*, "friends.png"*/),
            //new LauncherIcon(R.drawable.ic_action_action_settings, "Settings"/*, "settings.png"*/)
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //username= getIntent().getExtras().getString("Username");
        SharedPreferences sharedPref = getSharedPreferences("Credentials",Context.MODE_PRIVATE);
        username = sharedPref.getString("Username",null);

        GridView gridview = (GridView) findViewById(R.id.dashboard_grid);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(this);

        // Hack to disable GridView scrolling
        gridview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        Firebase.setAndroidContext(getApplicationContext());
        final Firebase myFirebaseRef = new Firebase("https://crackling-inferno-5209.firebaseio.com/"+username);
        Firebase debtcloud=myFirebaseRef.child("Debts");
        Firebase notifcloud=myFirebaseRef.child("Notifications");

        debtcloud.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String timestamp;
                String info=null;
                Log.d("snapshotvalue", dataSnapshot.getValue().toString());
                String[] parts=(dataSnapshot.getValue().toString()).split(":");

                Intent resultIntent = new Intent(getApplicationContext(), LendStatus.class);
                resultIntent.putExtra("Username",username);
                //TODO : set tab to System Notifications
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);

                Notification notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Money Matters")
                        .setContentText("You owe "+parts[1]+" $"+parts[2]+" for " + parts[0])
                        .setSmallIcon(R.drawable.ic_social_notifications_on)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
                Log.d("NOTIFY", "user notified");



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        notifcloud.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String timestamp;
                String info=null;
                Log.d("snapshotvalue", dataSnapshot.getValue().toString());
                String[] parts=(dataSnapshot.getValue().toString()).split(":");

                Intent resultIntent = new Intent(getApplicationContext(),   Notifications.class);
                resultIntent.putExtra("Username",username);
                //TODO : set tab to System Notifications
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);

                Notification notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Money Matters")
                        .setContentText("Notification from "+parts[2]+":"+parts[0])
                        .setSmallIcon(R.drawable.ic_social_notifications_on)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(2, notification);
                Log.d("NOTIFYusernotif", "user notified");



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if(position == 0) {
            Intent intent = new Intent(this, AddExpense.class);
            startActivityForResult(intent, ADD_EXPENSE_REQUEST);
        }
        else if(position==1)
        {
            Intent intent = new Intent(this, Budget.class);
            startActivity(intent);
        }
        else if(position==2)
        {
            Intent intent = new Intent(this, Expenses.class);
            startActivity(intent);
        }
        else if(position==3)
        {
            Intent intent = new Intent(this, LendStatus.class);
            intent.putExtra("Username",username);
            startActivity(intent);
        }
        else if(position==4)
        {
            Intent intent = new Intent(this, Notifications.class);
            intent.putExtra("Username",username);
            startActivity(intent);
        }
        else if(position==5)
        {
            Toast.makeText(getApplicationContext(),"Not yet implemented",Toast.LENGTH_LONG).show();

        }
        /*else if(position==6)
        {
            Toast.makeText(getApplicationContext(),"Not yet implemented",Toast.LENGTH_LONG).show();
        }*/
        //intent.putExtra(EXTRA_MAP, ICONS[position].map);

    }

    static class LauncherIcon {
        final String text;
        final int imgId;
        //final String map;

        public LauncherIcon(int imgId, String text/*, String map*/) {
            super();
            this.imgId = imgId;
            this.text = text;
            //this.map = map;
        }

    }

    static class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return ICONS.length;
        }

        @Override
        public LauncherIcon getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        static class ViewHolder {
            public ImageView icon;
            public TextView text;
        }

        // Create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                v = vi.inflate(R.layout.dashboard_icon, null);
                holder = new ViewHolder();
                holder.text = (TextView) v.findViewById(R.id.dashboard_icon_text);
                holder.icon = (ImageView) v.findViewById(R.id.dashboard_icon_img);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.icon.setImageResource(ICONS[position].imgId);
            holder.text.setText(ICONS[position].text);

            return v;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_uninstall) {
            Intent uninstall = new Intent(Intent.ACTION_DELETE);
            uninstall.setData(Uri.parse("package:" + this.getPackageName()));
            startActivity(uninstall);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ADD_EXPENSE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "New Expense added", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
