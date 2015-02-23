package com.example.lasyaboddapati.moneymatters;

/**
 * Created by kaustav1992 on 2/23/15.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DashboardAdapter extends FragmentPagerAdapter {
    private Context _context;
    String user;

    public DashboardAdapter(Context context, FragmentManager fm,String user) {
        super(fm);
        _context=context;
        this.user=user;
    }
    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch(position){
            case 0:
                f=DebitFragment.newInstance(_context,user);
                break;
            case 1:
                f=CreditFragment.newInstance(_context,user);
                break;
            case 2:
                f=SystemNotificationFragment.newInstance(_context);
                break;
            case 3:
                f=UserNotificationFragment.newInstance(_context);
                break;
            case 4:
                f=CreditFragment.newInstance(_context,user);
                break;
        }
        return f;
    }
    @Override
    public int getCount() {
        return 5;
    }

}
