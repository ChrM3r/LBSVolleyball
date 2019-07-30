package eu.merscher.lbsvolleyball;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class EditSpielerActivity extends AppCompatActivity implements View.OnClickListener {


    private static String userFotoAlsString = null;
    private Spieler spieler;
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

        spieler = getIntent().getExtras().getParcelable("spieler");

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

        if (spieler.getFoto().equals("avatar_m"))
            spielerBild.setImageResource(R.drawable.avatar_m);

        else if (spieler.getFoto().equals("avatar_f"))
            spielerBild.setImageResource(R.drawable.avatar_f);

        else
            spielerBild.setImageBitmap(BitmapFactory.decodeFile(spieler.getFoto()));
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
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

                    spielerBild.setImageBitmap(BitmapFactory.decodeFile(userFotoAlsString));

                    cursor.close();
                    break;

            }

    }

}
