package eu.merscher.lbsvolleyball;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class EditSpielerActivityPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private Spieler spieler;


    public EditSpielerActivityPagerAdapter(Context context, Spieler spieler, FragmentManager fm) {
        super(fm);
        this.spieler = spieler;
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        EditSpielerFragment fragment = new EditSpielerFragment(spieler);
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
