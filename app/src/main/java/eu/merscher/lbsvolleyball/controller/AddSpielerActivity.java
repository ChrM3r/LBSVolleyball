package eu.merscher.lbsvolleyball.controller;

import android.app.Activity;
import android.app.ProgressDialog;
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

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class AddSpielerActivity extends AppCompatActivity {

    private static String userFotoAlsString = null;
    private ImageView spielerBild;

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
        setContentView(R.layout.activity_add_edit_spieler);

        setTitle(R.string.button_spieler_anlegen);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_add_edit);
        Toolbar toolbar = findViewById(R.id.htab_toolbar_add_edit);
        TabLayout tabLayout = findViewById(R.id.htab_tabs_add_edit);
        FloatingActionButton fotoAddButton = findViewById(R.id.activity_add_edit_spieler_foto_button);
        FloatingActionButton fotoLoeschenButton = findViewById(R.id.activity_add_edit_spieler_foto_loeschen_button);
        ViewPager viewPager = findViewById(R.id.add_edit_viewpager);
        spielerBild = findViewById(R.id.spielerbild_groß_add_edit);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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

        fotoLoeschenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spielerBild.setImageResource(R.drawable.avatar_m);
                setUserFotoAlsString("geloescht");

            }
        });

        AddSpielerActivityPagerAdapter adapter = new AddSpielerActivityPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Zurück-Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        ProgressDialog progressDialog = ProgressDialog.show(this,
                "Einen kleinen Augenblick",
                "");

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 1:
                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

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
        progressDialog.dismiss();

    }


    //Bild aus Galerie auswählen und auf das Ergebnis reagieren

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

        public UserFotoUmspeichernAsyncTask(AddSpielerActivity context, Bitmap spielerBild) {
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
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    //Pager-Adapter zum Setzen der Inhalte
    public class AddSpielerActivityPagerAdapter extends FragmentPagerAdapter {

        private final Context context;

        public AddSpielerActivityPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            AddSpielerFragment fragment = new AddSpielerFragment();
            return fragment;
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
