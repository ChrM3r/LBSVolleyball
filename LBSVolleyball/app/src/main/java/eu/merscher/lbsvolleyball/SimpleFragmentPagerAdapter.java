package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Spieler> spielerList = new ArrayList<>();
    private ArrayList<Buchung> buchungList = new ArrayList<>();
    private double kto_saldo_neu;
    private int teilnahmen;
    private Context context;

    private static DecimalFormat df = new DecimalFormat("0.00");



    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        this.context = context;
    }

    public SimpleFragmentPagerAdapter(Context context, ArrayList<Spieler> spielerList, ArrayList<Buchung> buchungList, double kto_saldo_neu, int teilnahmen, FragmentManager fm){
        super(fm);
        this.spielerList = spielerList;
        this.buchungList = buchungList;
        this.kto_saldo_neu = kto_saldo_neu;
        this.teilnahmen = teilnahmen;
        this.context = context;
    }
    // This determines the fragment for each tab


    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            SpielerseiteGrunddatenFragment fragment = new SpielerseiteGrunddatenFragment().newInstance(spielerList.get(position), kto_saldo_neu, teilnahmen);
            return fragment;
        } else {
            SpielerseiteKontodatenFragment fragment = new SpielerseiteKontodatenFragment().newInstance(buchungList);
            return fragment;
        }

    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
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
