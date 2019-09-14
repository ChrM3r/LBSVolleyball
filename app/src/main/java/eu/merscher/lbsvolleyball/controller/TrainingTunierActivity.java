package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utilities;

public class TrainingTunierActivity extends AppCompatActivity implements TrainingTunierSpielerauswahlFragment.OnSpielerClickListener {

    public static Resources resources;
    public static ArrayList<Buchung> buchungList;
    private static Spieler spieler;
    private static EditSpielerFragment.OnEditFinish onEditFinish;
    public Context context;
    public Fragment fragment;
    public FragmentManager fm;
    private ImageView spielerBild;
    private FloatingActionButton editSpielerButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton exportKontoButton;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TrainingTunierPagerAdapter adapter;
    private boolean shouldExecuteOnResume = false;

    public TrainingTunierActivity() {
    }

    @Override
    public void onSpielerClick() {
        System.out.println("onSpielerClick Activty");
        adapter.trainingFragment.onSpielerClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_tunier);

        context = getApplicationContext();

        resources = getResources();
        spieler = getIntent().getParcelableExtra("spieler");
        //setTitle(spieler.getVname() + " " + spieler.getName());

        findViewsById();

        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);

        final Toolbar toolbar = findViewById(R.id.htab_toolbar_training_tunier);

        //Toolbar

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Spieltag erfassen");

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.spieltag, R.string.spieltag);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
            navigationView.bringToFront();
            navigationView.setCheckedItem(R.id.nav_spieltag);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    navigationView.getMenu().findItem(menuItem.getItemId()).setChecked(false);

                    switch (menuItem.getItemId()) {

                        case R.id.nav_spieltag: {
                            break;
                        }

                        case R.id.nav_spielerverwaltung: {
                            Intent intent = new Intent(TrainingTunierActivity.this, SpielerVerwaltungActivity.class);
                            TrainingTunierActivity.this.startActivity(intent);
                            break;
                        }

                        case R.id.nav_einstellungen: {
                            Intent intent = new Intent(TrainingTunierActivity.this, EinstellungenActivity.class);
                            TrainingTunierActivity.this.startActivity(intent);
                            break;
                        }

                        case R.id.nav_trainingsverwaltung: {
                            Intent intent = new Intent(TrainingTunierActivity.this, TrainingUebersichtActivity.class);
                            TrainingTunierActivity.this.startActivity(intent);
                            break;
                        }

                        case R.id.nav_export: {
                            DataSource dataSource = DataSource.getInstance();
                            dataSource.open();
                            ArrayList<Spieler> spielerList = new ArrayList<>();
                            ArrayList<String> spielerListString = new ArrayList<>();

                            spielerList = dataSource.getAllSpielerAlphabetischName();

                            for (Spieler spieler : spielerList) {
                                spielerListString.add(
                                        spieler.getVname() + ";" +
                                                spieler.getName() + ";" +
                                                spieler.getBdate() + ";" +
                                                spieler.getTeilnahmen() + ";" +
                                                spieler.getFoto() + ";" +
                                                spieler.getMail() + ";");
                            }

                            Utilities.csvExport(spielerListString, context);
                        }

                        case R.id.nav_import: {

                            DataSource dataSource = DataSource.getInstance();
                            dataSource.open();

                            ArrayList<String> spielerListString = Utilities.csvImport(context);

                            for (String s : spielerListString) {

                                String[] spielerArray = s.split(";");

                                String vname = spielerArray[0].replace("{", "");
                                String name = spielerArray[1];
                                String bdate = spielerArray[2];
                                int teilnahmen = Integer.parseInt(spielerArray[3]);
                                String foto = spielerArray[4];
                                String mail = spielerArray[5];

                                dataSource.createSpieler(name, vname, bdate, teilnahmen, foto, mail, null);

                            }

                        }
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
        }


        adapter = new TrainingTunierPagerAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void findViewsById() {

        viewPager = findViewById(R.id.viewpager_training_tunier);
        spielerBild = findViewById(R.id.bild_groß_training_tunier);
        collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_training_tunier);
        tabLayout = findViewById(R.id.htab_tabs_training_tunier);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (shouldExecuteOnResume) {
            navigationView.setCheckedItem(R.id.nav_spieltag);
            //adapter.notifyDataSetChanged();

            //adapter = new TrainingTunierPagerAdapter(this, getSupportFragmentManager());
            //viewPager.setAdapter(adapter);
            //tabLayout.setupWithViewPager(viewPager);
        } else
            shouldExecuteOnResume = true;

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Fokus des Edittexts ändern wenn Touch außerhalb des Edittext-Bereiches
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        //setBetragJeSpieler();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    public class TrainingTunierPagerAdapter extends FragmentPagerAdapter {


        private final Context context;
        public TrainingFragment trainingFragment;
        public TunierFragment tunierFragment;


        public TrainingTunierPagerAdapter(Context context, FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return trainingFragment = new TrainingFragment();
            } else {
                return tunierFragment = new TunierFragment();
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
                    return context.getString(R.string.tab_training);
                case 1:
                    return context.getString(R.string.tab_tunier);
                default:
                    return null;
            }
        }
    }

}
