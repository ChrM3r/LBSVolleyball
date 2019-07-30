package eu.merscher.lbsvolleyball;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class AddSpielerActivityPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public AddSpielerActivityPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        AddSpielerFragment fragment = new AddSpielerFragment();
        return fragment;
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 1;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return context.getString(R.string.tab_grunddaten);
            default:
                return null;
        }
    }


}
