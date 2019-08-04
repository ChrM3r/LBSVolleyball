package eu.merscher.lbsvolleyball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static eu.merscher.lbsvolleyball.SpieltagActivity.context;


public class AddSpielerActivity extends AppCompatActivity implements View.OnClickListener {

    private static String userFotoAlsString = null;
    private ImageView spielerFoto;

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

        setTitle(R.string.button_spieler_anlegen);

        bottomNavBarInitialisieren();

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.htab_collapse_toolbar_add_edit);
        Toolbar toolbar = findViewById(R.id.htab_toolbar_add_edit);
        TabLayout tabLayout = findViewById(R.id.htab_tabs_add_edit);
        FloatingActionButton fotoAddButton = findViewById(R.id.activity_add_edit_spieler_foto_button);
        ViewPager viewPager = findViewById(R.id.add_edit_viewpager);
        spielerFoto = findViewById(R.id.spielerbild_groß_add_edit);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        spielerFoto.setOnClickListener(new View.OnClickListener() {
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

        AddSpielerActivityPagerAdapter adapter = new AddSpielerActivityPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void bottomNavBarInitialisieren() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_spieltag_bottom:
                        Intent intent1 = new Intent(AddSpielerActivity.this, SpieltagActivity.class);
                        AddSpielerActivity.this.startActivity(intent1);
                        break;
                    case R.id.action_spielerverwaltung_bottom:
                        Intent intent2 = new Intent(AddSpielerActivity.this, SpielerVerwaltungActivity.class);
                        AddSpielerActivity.this.startActivity(intent2);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            if (requestCode == 1) {
                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                userFotoAlsString = cursor.getString(columnIndex);

                Uri uri;
                Bitmap spielerBild = BitmapFactory.decodeFile(userFotoAlsString);

                try {
                    uri = Uri.fromFile(new File(userFotoAlsString));
                    spielerBild = Utils.handleSamplingAndRotationBitmap(context, uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                spielerFoto.setImageBitmap(spielerBild);
                cursor.close();
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

}
