package eu.merscher.lbsvolleyball.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.BitmapScaler;
import eu.merscher.lbsvolleyball.utilities.Utils;

import static eu.merscher.lbsvolleyball.controller.SpieltagActivity.resources;


public class EditSpielerActivity extends AppCompatActivity implements View.OnClickListener {


    private static String userFotoAlsString = null;
    private ImageView spielerBild;

    public static String getUserFotoAlsString() {
        return userFotoAlsString;
    }

    public static void setUserFotoAlsString(String s) {
        userFotoAlsString = s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spieler);

        setTitle(R.string.button_spieler_aendern);

        bottomNavBarInitialisieren();

        Spieler spieler = getIntent().getExtras().getParcelable("spieler");

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_add_edit);
        Toolbar toolbar = findViewById(R.id.htab_toolbar_add_edit);
        TabLayout tabLayout = findViewById(R.id.htab_tabs_add_edit);
        FloatingActionButton fotoAddButton = findViewById(R.id.activity_add_edit_spieler_foto_button);
        ViewPager viewPager = findViewById(R.id.add_edit_viewpager);
        spielerBild = findViewById(R.id.spielerbild_groß_add_edit);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spielerBild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bildAusGalerieAuswaehlen();
            }
        });

        fotoAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bildAusGalerieAuswaehlen();
            }
        });

        EditSpielerActivityPagerAdapter adapter = new EditSpielerActivityPagerAdapter(this, spieler, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //Displaygröße ermittlen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        //Spielerbild skalieren und setzen
        Bitmap spielerBildOriginal;
        Bitmap spielerBildScaled;
        Uri uri;

        if (spieler.getFoto().equals("avatar_m"))
            spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);

        else if (spieler.getFoto().equals("avatar_f"))
            spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_f);
        else {
            spielerBildOriginal = BitmapFactory.decodeFile(spieler.getFoto());
            try {
                uri = Uri.fromFile(new File(spieler.getFoto()));
                spielerBildOriginal = Utils.handleSamplingAndRotationBitmap(getApplicationContext(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        spielerBildScaled = BitmapScaler.scaleToFitWidth(spielerBildOriginal, width);
        spielerBild.setImageBitmap(spielerBildScaled);
    }

    private void bottomNavBarInitialisieren() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_spieltag_bottom:
                        Intent intent1 = new Intent(EditSpielerActivity.this, SpieltagActivity.class);
                        EditSpielerActivity.this.startActivity(intent1);
                        break;
                    case R.id.action_spielerverwaltung_bottom:
                        Intent intent2 = new Intent(EditSpielerActivity.this, SpielerVerwaltungActivity.class);
                        EditSpielerActivity.this.startActivity(intent2);
                        break;
                }
                return true;
            }
        });
    }

    public void onClick(View v) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Bild aus Galerie auswählen und auf das Ergebnis reagieren

    private void bildAusGalerieAuswaehlen() {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 1:
                    Uri selectedImage = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    userFotoAlsString = cursor.getString(columnIndex);

                    Uri uri;
                    Bitmap spielerBildNeu = BitmapFactory.decodeFile(userFotoAlsString);

                    try {
                        uri = Uri.fromFile(new File(userFotoAlsString));
                        spielerBildNeu = Utils.handleSamplingAndRotationBitmap(getApplicationContext(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    spielerBild.setImageBitmap(spielerBildNeu);
                    cursor.close();
            }

    }

    public class EditSpielerActivityPagerAdapter extends FragmentPagerAdapter {

        private final Context context;
        private final Spieler spieler;


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
            if (position == 0) {
                return context.getString(R.string.tab_grunddaten);
            }
            return null;
        }


    }
}
