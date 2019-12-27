package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Trainingsort;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class TrainingsortActivity extends AppCompatActivity implements EditSpielerFragment.OnEditFinish {


    private ImageView trainingsortBild;
    private FloatingActionButton editTrainingsortButton;
    public static Resources resources;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    public void onEditFinish() {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainingsort);

        resources = getResources();
        Trainingsort trainingsort = getIntent().getParcelableExtra("trainingsort");
        setTitle(trainingsort.getName());

        findViewsById();

        DataSource dataSource = DataSource.getInstance();
        dataSource.open();

        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);

        final Toolbar toolbar = findViewById(R.id.htab_toolbar_trainigsort);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Displaygröße ermittlen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        //Spielerbild skalieren und setzen
        Bitmap trainingsortBildOriginal;
        Bitmap trainingsortBildScaled;


        if (trainingsort.getFoto().equals("avatar_map"))
            trainingsortBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_map);
        else {
            trainingsortBildOriginal = BitmapFactory.decodeFile(trainingsort.getFoto());
        }

        if (trainingsortBildOriginal != null)
            trainingsortBildScaled = Utilities.scaleToFitWidth(trainingsortBildOriginal, width);
        else {
            trainingsortBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_map);
            trainingsortBildScaled = Utilities.scaleToFitWidth(trainingsortBildOriginal, width);
        }
        trainingsortBild.setImageBitmap(trainingsortBildScaled);

        //Pager mit Fragmenten erzeugen
        TrainingsortActivityPagerAdapter adapter = new TrainingsortActivityPagerAdapter(this, trainingsort, getSupportFragmentManager());


        editTrainingsortButton.setOnClickListener(v -> {

            Intent intent = new Intent(TrainingsortActivity.this, EditTrainingsortActivity.class);
            intent.putExtra("trainingsort", trainingsort);
            TrainingsortActivity.this.startActivity(intent);
        });


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void findViewsById() {

        viewPager = findViewById(R.id.trainingsort_viewpager);
        trainingsortBild = findViewById(R.id.activity_trainingsort_bild);
        collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_trainigsort);
        editTrainingsortButton = findViewById(R.id.edit_trainingsort_button);
        tabLayout = findViewById(R.id.htab_tabs_trainigsort);


    }

    @Override
    protected void onResume() {
        super.onResume();

        //Berechtigungen
        Utilities.berechtigungenPruefen(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }


    public class TrainingsortActivityPagerAdapter extends FragmentPagerAdapter {

        private final Trainingsort trainingsort;
        private final Context context;

        TrainingsortActivityPagerAdapter(Context context, Trainingsort trainingsort, FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.trainingsort = trainingsort;
            this.context = context;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {

            return new TrainingsortFragment(trainingsort);
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
            return context.getString(R.string.tab_grunddaten);

        }
    }
}


