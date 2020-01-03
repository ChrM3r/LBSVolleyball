package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import eu.merscher.lbsvolleyball.R;

public class TrainingTunierActivity extends AppCompatActivity implements TrainingTunierSpielerauswahlFragment.OnSpielerClickListener {

    public Fragment fragment;
    public FragmentManager fm;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CollapsingToolbarLayout collapsingToolbar;
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

        this.setContentView(R.layout.activity_training_tunier);

        findViewsById();

        //Toolbar
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Spieltag erfassen");
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViewsById() {

        viewPager = findViewById(R.id.viewpager_training_tunier);
        collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_training_tunier);
        tabLayout = findViewById(R.id.htab_tabs_training_tunier);
        toolbar = findViewById(R.id.htab_toolbar_training_tunier);
    }


    @Override
    public void onResume() {
        super.onResume();

        //Pager und Fragmente laden
        TrainingTunierPagerAdapter adapter = new TrainingTunierPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
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
                return new TrainingFragment();
                //return new TunierFragment();
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
