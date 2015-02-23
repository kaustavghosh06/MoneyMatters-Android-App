package com.example.lasyaboddapati.moneymatters;

/**
 * Created by kaustav1992 on 2/21/15.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context _context;
    String username;
    int pos;

    public ViewPagerAdapter(Context context, FragmentManager fm,String user,int pos) {
        super(fm);
        _context=context;
        username=user;
        this.pos=pos;

    }
    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch(position){
            case 0:
                f=DebitFragment.newInstance(_context,username);
                break;
            case 1:
                f=CreditFragment.newInstance(_context,username);
                break;
        }
        return f;
    }
    @Override
    public int getCount() {
        return 2;
    }

}