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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class EditSpielerActivity extends AppCompatActivity implements EditSpielerFragment.OnEditFinish {


    private static String userFotoAlsString = null;
    private ImageView spielerBild;
    private boolean boolZurueck;
    public static String getUserFotoAlsString() {
        return userFotoAlsString;
    }

    public static void setUserFotoAlsString(String s) {
        userFotoAlsString = s;
    }

    @Override
    public void onEditFinish() {
        this.finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_spieler);

        setTitle(R.string.button_spieler_aendern);

        Spieler spieler = Objects.requireNonNull(getIntent().getExtras()).getParcelable("spieler");

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_edit);
        Toolbar toolbar = findViewById(R.id.htab_toolbar_edit);
        TabLayout tabLayout = findViewById(R.id.htab_tabs_edit);
        FloatingActionButton fotoAddButton = findViewById(R.id.activity_edit_spieler_foto_button);
        FloatingActionButton fotoLoeschenButton = findViewById(R.id.activity_edit_spieler_foto_loeschen_button);
        ViewPager viewPager = findViewById(R.id.add_edit_viewpager);
        spielerBild = findViewById(R.id.spielerbild_groß_edit);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button buttonSpielerAendern = findViewById(R.id.fragment_edit_spieler_button);

        buttonSpielerAendern.setOnClickListener((View v) -> ((EditSpielerFragment) getSupportFragmentManager().getFragments().get(0)).getAdapter().getHolder().onSpielerSpeichernClick());

        spielerBild.setOnClickListener(v -> bildAusGalerieAuswaehlen());

        fotoAddButton.setOnClickListener(v -> bildAusGalerieAuswaehlen());

        fotoLoeschenButton.setOnClickListener(v -> {
            spielerBild.setImageResource(R.drawable.avatar_m);
            setUserFotoAlsString("geloescht");

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

        userFotoAlsString = null;

        Bitmap spielerBildOriginal;
        Bitmap spielerBildScaled;

        if (Objects.requireNonNull(spieler).getFoto().equals("avatar_m"))
            spielerBildOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_m);
        else {
            spielerBildOriginal = BitmapFactory.decodeFile(spieler.getFoto());
        }

        if (spielerBildOriginal != null)
            spielerBildScaled = Utilities.scaleToFitWidth(spielerBildOriginal, width);
        else {
            spielerBildOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_m);
            spielerBildScaled = Utilities.scaleToFitWidth(spielerBildOriginal, width);
        }
        spielerBild.setImageBitmap(spielerBildScaled);

        //Berechtigungen
        Utilities.berechtigungenPruefen(this);
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

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            if (requestCode == 1) {
                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media._ID};
                Cursor cursor = getContentResolver().query(Objects.requireNonNull(selectedImage), filePathColumn, null, null, null);
                Objects.requireNonNull(cursor).moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                userFotoAlsString = cursor.getString(columnIndex);


                try {
                    Bitmap spielerBildNeu = Utilities.handleSamplingAndRotationBitmap(getApplicationContext(), selectedImage);
                    userFotoAlsString = Utilities.bildSpeichern(getApplicationContext(), spielerBildNeu);
                    spielerBild.setImageBitmap(spielerBildNeu);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                cursor.close();
            }

    }

    public class EditSpielerActivityPagerAdapter extends FragmentPagerAdapter {

        private final Context context;
        private final Spieler spieler;


        EditSpielerActivityPagerAdapter(Context context, Spieler spieler, FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.spieler = spieler;
            this.context = context;
        }


        @NotNull
        @Override
        public Fragment getItem(int position) {
            return new EditSpielerFragment(spieler);
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
