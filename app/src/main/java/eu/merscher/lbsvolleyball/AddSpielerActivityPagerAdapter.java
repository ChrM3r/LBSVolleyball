package eu.merscher.lbsvolleyball;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class AddSpielerActivityPagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    public AddSpielerActivityPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        AddSpielerFragment fragment = new AddSpielerFragment();
        return fragment;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(R.string.tab_grunddaten);

    }


}
