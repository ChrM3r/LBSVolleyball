package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;

public class TrainingTunierActivity extends AppCompatActivity implements TrainingTunierSpielerauswahlFragment.OnSpielerClickListener {

    public Fragment fragment;
    public FragmentManager fm;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CollapsingToolbarLayout collapsingToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    public TrainingTunierActivity() {
    }

    @Override
    public void onSpielerClick() {

        for (Fragment f : fm.getFragments()) {
            if (f instanceof TrainingFragment) {
                ((TrainingFragment) f).onSpielerClick();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Lokalität laden
        Locale locale = new Locale("de_DE");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        this.setContentView(R.layout.activity_training_tunier);

        findViewsById();

        //Toolbar
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Spieltag erfassen");

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.spieltag, R.string.spieltag);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
            navigationView.bringToFront();
            navigationView.setCheckedItem(R.id.nav_spieltag);

            navigationView.setNavigationItemSelectedListener(menuItem -> {
                navigationView.getMenu().findItem(menuItem.getItemId()).setChecked(false);

                switch (menuItem.getItemId()) {


                    case R.id.nav_dashboard: {
                        Intent intent = new Intent(TrainingTunierActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent, 0);
                        break;
                    }

                    case R.id.nav_spieltag: {
                        break;
                    }

                    case R.id.nav_spielerverwaltung: {
                        Intent intent = new Intent(TrainingTunierActivity.this, SpielerVerwaltungActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent, 0);
                        break;
                    }

                    case R.id.nav_einstellungen: {
                        Intent intent = new Intent(TrainingTunierActivity.this, EinstellungenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent, 0);
                        break;
                    }

                    case R.id.nav_trainingsortverwatung: {
                        Intent intent = new Intent(TrainingTunierActivity.this, TrainingsortVerwaltungActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent, 0);
                        break;
                    }

                    case R.id.nav_trainingsverwaltung: {
                        Intent intent = new Intent(TrainingTunierActivity.this, TrainingUebersichtActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivityIfNeeded(intent, 0);
                        break;
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }

    }

    private void findViewsById() {

        viewPager = findViewById(R.id.viewpager_training_tunier);
        collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_training_tunier);
        tabLayout = findViewById(R.id.htab_tabs_training_tunier);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.htab_toolbar_training_tunier);
    }


    @Override
    public void onResume() {
        super.onResume();

        navigationView.setCheckedItem(R.id.nav_spieltag);
        //Pager und Fragmente laden
        TrainingTunierPagerAdapter adapter = new TrainingTunierPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Navigation
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
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


    public class TrainingTunierPagerAdapter extends FragmentPagerAdapter {


        private final Context context;


        private TrainingTunierPagerAdapter(Context context, FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.context = context;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new TrainingFragment();
            } else {
                return new TunierFragment();
            }

        }

        @Override
        public int getItemPosition(@NotNull Object o) {
            return POSITION_NONE;
        }

        //Anzahl der Tabs
        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
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
