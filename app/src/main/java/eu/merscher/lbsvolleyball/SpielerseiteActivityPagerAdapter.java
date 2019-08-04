package eu.merscher.lbsvolleyball;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SpielerseiteActivityPagerAdapter extends FragmentPagerAdapter {

    private final Spieler spieler;
    private final ArrayList<Buchung> buchungList;
    private final double kto_saldo_neu;
    private final int teilnahmen;
    private final Context context;

    public SpielerseiteActivityPagerAdapter(Context context, Spieler spieler, ArrayList<Buchung> buchungList, double kto_saldo_neu, int teilnahmen, FragmentManager fm) {
        super(fm);
        this.spieler = spieler;
        this.buchungList = buchungList;
        this.kto_saldo_neu = kto_saldo_neu;
        this.teilnahmen = teilnahmen;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            SpielerseiteGrunddatenFragment spielerseiteGrunddatenFragment = new SpielerseiteGrunddatenFragment().newInstance(spieler, kto_saldo_neu, teilnahmen);
            return spielerseiteGrunddatenFragment;
        } else {
            SpielerseiteKontodatenFragment spielerseiteKontodatenFragment = new SpielerseiteKontodatenFragment(buchungList, spieler);
            return spielerseiteKontodatenFragment;
        }

    }

    @Override
    public int getItemPosition(Object o) {
        return POSITION_NONE;
    }

    //Anzahl der Tabs
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return context.getString(R.string.tab_grunddaten);
            case 1:
                return context.getString(R.string.tab_kontodaten);
            default:
                return null;
        }
    }
}
