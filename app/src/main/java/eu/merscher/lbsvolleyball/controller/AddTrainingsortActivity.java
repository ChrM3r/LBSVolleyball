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

import androidx.appcompat.app.AlertDialog;
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


public class AddTrainingsortActivity extends AppCompatActivity {

    private boolean boolZurueck;

    private static String trainingsortFotoAlsString = null;

    protected static String getTrainingsortFotoAlsString() {
        return trainingsortFotoAlsString;
    }

    protected static void setTrainingsortFotoAlsString(String s) {
        trainingsortFotoAlsString = s;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_trainingsort);

        setTitle(R.string.button_trainingsort_anlegen);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_add_edit_trainingsort);
        Toolbar toolbar = findViewById(R.id.htab_toolbar_add_edit_trainingsort);
        TabLayout tabLayout = findViewById(R.id.htab_tabs_add_edit_trainingsort);
        ViewPager viewPager = findViewById(R.id.add_edit_viewpager_trainingsort);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        AddTrainingsortActivityPagerAdapter adapter = new AddTrainingsortActivityPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Zurück-Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolZurueck = false;

        if (item.getItemId() == android.R.id.home) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Achtung")
                    .setMessage(("Alle ungespeicherten Eingaben gehen verloren"))
                    .setNegativeButton("Abbrechen", (dialog1, which) -> {
                        dialog1.cancel();
                        boolZurueck = false;
                    })
                    .setPositiveButton("Ok", (dialog2, i) -> {
                        this.finish();
                        boolZurueck = true;
                    }).show();
            return boolZurueck;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        boolZurueck = false;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Achtung")
                .setMessage(("Alle ungespeicherten Eingaben gehen verloren"))
                .setNegativeButton("Abbrechen", (dialog1, which) -> {
                    dialog1.cancel();
                    boolZurueck = false;
                })
                .setPositiveButton("Ok", (dialog2, i) -> {
                    this.finish();
                    boolZurueck = true;
                    super.onBackPressed();

                }).show();

    }


    //Fokus des Edittexts ändern wenn Touch außerhalb des Edittext-Bereiches
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
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    //Pager-Adapter zum Setzen der Inhalte
    public class AddTrainingsortActivityPagerAdapter extends FragmentPagerAdapter {

        private final Context context;

        private AddTrainingsortActivityPagerAdapter(Context context, FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.context = context;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return new AddTrainingsortFragment();
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
}
