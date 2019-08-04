package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
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
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static eu.merscher.lbsvolleyball.SpieltagActivity.resources;


public class SpielerseiteActivity extends AppCompatActivity implements View.OnClickListener {


    private static Spieler spieler;
    private ImageView spielerBild;
    private FloatingActionButton editSpielerButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielerseite);

        Context context = getApplicationContext();

        spieler = new Gson().fromJson(getIntent().getStringExtra("spieler"), Spieler.class);
        setTitle(spieler.getVname() + " " + spieler.getName());

        findViewsById();
        bottomNavBarInitialisieren();

        BuchungDataSource buchungDataSource = BuchungDataSource.getInstance();
        buchungDataSource.open();

        ArrayList<Buchung> buchungList = buchungDataSource.getAllBuchungZuSpieler(spieler);
        double kto_saldo_neu;
        if (spieler.getHat_buchung_mm() == null)
            kto_saldo_neu = 0;
        else
            kto_saldo_neu = buchungDataSource.getNeusteBuchungZuSpieler(spieler).getKto_saldo_neu();

        int teilnahmen = spieler.getTeilnahmen();

        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);

        final Toolbar toolbar = findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                spielerBildOriginal = Utils.handleSamplingAndRotationBitmap(context, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        spielerBildScaled = BitmapScaler.scaleToFitWidth(spielerBildOriginal, width);
        spielerBild.setImageBitmap(spielerBildScaled);

        //Pager mit Fragmenten erzeugen
        SpielerseiteActivityPagerAdapter adapter = new SpielerseiteActivityPagerAdapter(this, spieler, buchungList, kto_saldo_neu, teilnahmen, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        editSpielerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SpielerseiteActivity.this, EditSpielerActivity.class);
                intent.putExtra("spieler", spieler);
                SpielerseiteActivity.this.startActivity(intent);
            }
        });
    }

    private void findViewsById() {

        viewPager = findViewById(R.id.spielerseite_viewpager);
        spielerBild = findViewById(R.id.spielerbild_groß);
        collapsingToolbar = findViewById(R.id.htab_collapse_toolbar);
        editSpielerButton = findViewById(R.id.activity_spielerseite_edit_spieler_button);
        tabLayout = findViewById(R.id.htab_tabs);


    }

    private void bottomNavBarInitialisieren() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_spieltag_bottom:
                        Intent intent1 = new Intent(SpielerseiteActivity.this, SpieltagActivity.class);
                        SpielerseiteActivity.this.startActivity(intent1);
                        break;
                    case R.id.action_spielerverwaltung_bottom:
                        Intent intent2 = new Intent(SpielerseiteActivity.this, SpielerVerwaltungActivity.class);
                        SpielerseiteActivity.this.startActivity(intent2);
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
