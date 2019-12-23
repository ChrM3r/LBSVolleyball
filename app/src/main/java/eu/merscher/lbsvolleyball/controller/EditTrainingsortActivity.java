//package eu.merscher.lbsvolleyball.controller;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Point;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.Display;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentPagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//
//import com.google.android.material.appbar.CollapsingToolbarLayout;
//import com.google.android.material.tabs.TabLayout;
//
//import eu.merscher.lbsvolleyball.R;
//import eu.merscher.lbsvolleyball.model.Trainingsort;
//import eu.merscher.lbsvolleyball.utilities.BitmapScaler;
//
//import static eu.merscher.lbsvolleyball.controller.TrainingTunierActivity.resources;
//
//
//public class EditTrainingsortActivity extends AppCompatActivity implements View.OnClickListener, EditSpielerFragment.OnEditFinish {
//
//
//    private static String trainingsortFotoAlsString = null;
//    private ImageView spielerBild;
//
//    public static String getTrainingsortFotoAlsString() {
//        return trainingsortFotoAlsString;
//    }
//
//    public static void setTrainingsortFotoAlsString(String s) {
//        trainingsortFotoAlsString = s;
//    }
//
//    @Override
//    public void onEditFinish() {
//        this.finish();
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_edit_trainingsort);
//
//        setTitle(R.string.button_trainingsort_aendern);
//
//        Trainingsort trainingsort = getIntent().getExtras().getParcelable("trainingsort");
//
//        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_add_edit_trainingsort);
//        Toolbar toolbar = findViewById(R.id.htab_toolbar_add_edit_trainingsort);
//        TabLayout tabLayout = findViewById(R.id.htab_tabs_add_edit_trainingsort);
//        ViewPager viewPager = findViewById(R.id.add_edit_viewpager_trainingsort);
//        spielerBild = findViewById(R.id.trainingsortbild_groß_add_edit);
//        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
//        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//
//
//        EditTrainingsortActivityPagerAdapter adapter = new EditTrainingsortActivityPagerAdapter(this, trainingsort, getSupportFragmentManager());
//        viewPager.setAdapter(adapter);
//        tabLayout.setupWithViewPager(viewPager);
//
//        //Displaygröße ermittlen
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//
//        //Spielerbild skalieren und setzen
//
//        trainingsortFotoAlsString = null;
//
//        Bitmap trainingsortBildOriginal;
//        Bitmap trainingsortBildScaled;
//        Uri uri;
//
//        if (trainingsort.getFoto().equals("avatar_map"))
//            trainingsortBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_map);
//        else {
//            trainingsortBildOriginal = BitmapFactory.decodeFile(trainingsort.getFoto());
//        }
//
//        if (trainingsortBildOriginal != null)
//            trainingsortBildScaled = Utilities.scaleToFitWidth(trainingsortBildOriginal, width);
//        else {
//            trainingsortBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_map);
//            trainingsortBildScaled = Utilities.scaleToFitWidth(trainingsortBildOriginal, width);
//        }
//        spielerBild.setImageBitmap(trainingsortBildScaled);
//    }
//
//
//
//    public void onClick(View v) {
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    public class EditTrainingsortActivityPagerAdapter extends FragmentPagerAdapter {
//
//        private final Context context;
//        private final Trainingsort trainingsort;
//
//
//        public EditTrainingsortActivityPagerAdapter(Context context, Trainingsort trainingsort, FragmentManager fm) {
//            super(fm);
//            this.trainingsort = trainingsort;
//            this.context = context;
//        }
//
//
//        @Override
//        public Fragment getItem(int position) {
//             return new EditTrainingsortFragment(trainingsort);
//        }
//
//        // This determines the number of tabs
//        @Override
//        public int getCount() {
//            return 1;
//        }
//
//        // This determines the title for each tab
//        @Override
//        public CharSequence getPageTitle(int position) {
//            // Generate title based on item position
//            if (position == 0) {
//                return context.getString(R.string.tab_grunddaten);
//            }
//            return null;
//        }
//
//
//    }
//}
