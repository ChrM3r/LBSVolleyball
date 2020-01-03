package eu.merscher.lbsvolleyball.controller;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class AddSpielerActivity extends AppCompatActivity {

    private static String userFotoAlsString = null;
    private ImageView spielerBild;

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private FloatingActionButton fotoAddButton;
    private FloatingActionButton fotoLoeschenButton;
    private ViewPager viewPager;
    private Button spieler_add_button;

    private boolean boolZurueck;
    private AddSpieler addSpieler;

    protected static String getUserFotoAlsString() {
        return userFotoAlsString;
    }

    protected static void setUserFotoAlsString(String s) {
        userFotoAlsString = s;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spieler);

        setTitle(R.string.button_spieler_anlegen);

        findViewsById();

        //Foto hinzufügen und löschen
        spielerBild.setOnClickListener(v -> bildAusGalerieAuswaehlen());
        fotoAddButton.setOnClickListener(v -> bildAusGalerieAuswaehlen());
        fotoLoeschenButton.setOnClickListener(v -> {
            spielerBild.setImageResource(R.drawable.avatar_m);
            setUserFotoAlsString("geloescht");

        });

        //Spielerdaten-Pager laden
        AddSpielerActivityPagerAdapter adapter = new AddSpielerActivityPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        spieler_add_button.setOnClickListener(v -> {

            addSpieler = adapter.getAddSpielerFragment().getAddSpielerFragmentAdapter().getViewHolder();
            addSpieler.onAddSpieler();
        });

        //Zurück-Zähler wieder default setzen
        boolZurueck = false;

        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    @Override
    public void onResume() {
        super.onResume();

        //Berechtigungen
        Utilities.berechtigungenPruefen(this);
    }

    private void findViewsById() {

        collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_add_edit);
        toolbar = findViewById(R.id.htab_toolbar_add_edit);
        tabLayout = findViewById(R.id.htab_tabs_add_edit);
        fotoAddButton = findViewById(R.id.activity_add_edit_spieler_foto_button);
        fotoLoeschenButton = findViewById(R.id.activity_add_edit_spieler_foto_loeschen_button);
        viewPager = findViewById(R.id.add_edit_viewpager);
        spielerBild = findViewById(R.id.spielerbild_groß_add_edit);
        spieler_add_button = findViewById(R.id.fragement_add_spieler_button);


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

                Bitmap spielerBild = BitmapFactory.decodeFile(userFotoAlsString);

                try {
                    spielerBild = Utilities.handleSamplingAndRotationBitmap(getApplicationContext(), selectedImage);
                    new UserFotoUmspeichernAsyncTask(this, spielerBild).execute();

                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File ordner = cw.getDir("profilbilder", Context.MODE_PRIVATE);
                    userFotoAlsString = ordner.getAbsolutePath() + File.separator + "temp.png";
                    Log.d("Foto auswaehlen", userFotoAlsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.spielerBild.setImageBitmap(spielerBild);
                cursor.close();
            }

    }

    private void bildAusGalerieAuswaehlen() {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 1);

    }

    private static class UserFotoUmspeichernAsyncTask extends AsyncTask<Void, Void, Void> {


        private final WeakReference<AddSpielerActivity> activityReference;
        private Bitmap spielerBild;

        private UserFotoUmspeichernAsyncTask(AddSpielerActivity context, Bitmap spielerBild) {
            activityReference = new WeakReference<>(context);
            this.spielerBild = spielerBild;

        }

        @Override
        public Void doInBackground(Void... args) {

            userFotoAlsString = Utilities.bildSpeichern(activityReference.get().getApplicationContext(), spielerBild);
            Log.d("ASyncBild", "Bild gespeichert");
            return null;
        }
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
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    //Pager-Adapter zum Setzen der Inhalte
    public class AddSpielerActivityPagerAdapter extends FragmentPagerAdapter {

        private final Context context;
        private FragmentManager fm;

        AddSpielerFragment getAddSpielerFragment() {
            return (AddSpielerFragment) fm.getFragments().get(0);
        }

        AddSpielerActivityPagerAdapter(Context context, FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fm = fm;
            this.context = context;
        }

        @NotNull
        @Override

        public AddSpielerFragment getItem(int position) {
            return new AddSpielerFragment();
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

    public interface AddSpieler {
        void onAddSpieler();
    }
}
