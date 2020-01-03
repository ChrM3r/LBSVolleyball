package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

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
import eu.merscher.lbsvolleyball.model.Trainingsort;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class EditTrainingsortActivity extends AppCompatActivity implements EditTrainingsortFragment.OnEditFinish {


    private static String trainingsortFotoAlsString = null;
    private static EditTrainingsortFragment.OnEditFinish onEditFinish;
    private boolean boolZurueck;

    public static EditTrainingsortFragment.OnEditFinish getOnEditFinish() {
        return onEditFinish;
    }

    public static String getTrainingsortFotoAlsString() {
        return trainingsortFotoAlsString;
    }

    public static void setTrainingsortFotoAlsString(String s) {
        trainingsortFotoAlsString = s;
    }

    @Override
    public void onEditFinish() {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trainingsort);

        setTitle(R.string.button_trainingsort_aendern);

        onEditFinish = this;

        Trainingsort trainingsort = Objects.requireNonNull(getIntent().getExtras()).getParcelable("trainingsort");

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_edit_trainingsort);
        Toolbar toolbar = findViewById(R.id.htab_toolbar_edit_trainingsort);
        TabLayout tabLayout = findViewById(R.id.htab_tabs_edit_trainingsort);
        ViewPager viewPager = findViewById(R.id.edit_viewpager_trainingsort);
        Button trainingsort_Speichern = findViewById(R.id.fragment_edit_trainigsort_button);
        ImageView trainingsortBild = findViewById(R.id.trainingsortbild_groß_edit);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        EditTrainingsortActivityPagerAdapter adapter = new EditTrainingsortActivityPagerAdapter(this, trainingsort, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //Displaygröße ermittlen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        //Spielerbild skalieren und setzen

        trainingsortFotoAlsString = null;

        Bitmap trainingsortBildOriginal;
        Bitmap trainingsortBildScaled;

        if (Objects.requireNonNull(trainingsort).getFoto().equals("avatar_map"))
            trainingsortBildOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_map);
        else {
            trainingsortBildOriginal = BitmapFactory.decodeFile(trainingsort.getFoto());
        }

        if (trainingsortBildOriginal != null)
            trainingsortBildScaled = Utilities.scaleToFitWidth(trainingsortBildOriginal, width);
        else {
            trainingsortBildOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_map);
            trainingsortBildScaled = Utilities.scaleToFitWidth(trainingsortBildOriginal, width);
        }
        trainingsortBild.setImageBitmap(trainingsortBildScaled);

        trainingsort_Speichern.setOnClickListener(v -> adapter.fragment.adapter.holder.onTrainingsortSpeichernClick());
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

    @Override
    public void onPause() {
        super.onPause();
    }


    public class EditTrainingsortActivityPagerAdapter extends FragmentPagerAdapter {

        private final Context context;
        private final Trainingsort trainingsort;
        EditTrainingsortFragment fragment;


        EditTrainingsortActivityPagerAdapter(Context context, Trainingsort trainingsort, FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.trainingsort = trainingsort;
            this.context = context;
        }


        @NotNull
        @Override
        public Fragment getItem(int position) {
            fragment = new EditTrainingsortFragment(trainingsort);
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
            if (position == 0) {
                return context.getString(R.string.tab_grunddaten);
            }
            return null;
        }


    }
}
