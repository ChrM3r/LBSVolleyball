package eu.merscher.lbsvolleyball;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.google.gson.Gson;

import java.util.ArrayList;


public class SpielerseiteActivity extends AppCompatActivity implements View.OnClickListener {


    private BuchungDataSource buchungDataSource;

    private static Spieler spieler;
    private static double kto_saldo_neu;
    private static int teilnahmen;
    private ArrayList<Spieler> spielerList = new ArrayList<>();
    private ArrayList<Buchung> buchungList = new ArrayList<>();
    private ImageView spielerBild;
    private FloatingActionButton editSpielerButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private androidx.appcompat.widget.Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private SpielerseiteActivityPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielerseite);

        spieler = new Gson().fromJson(getIntent().getStringExtra("spieler"), Spieler.class);
        setTitle(spieler.getVname() + " " + spieler.getName());

        findViewsById();
        bottomNavBarInitialisieren();

        buchungDataSource = new BuchungDataSource(this);

        buchungDataSource.open();
        buchungList = buchungDataSource.getAllBuchungZuSpieler(spieler);
        if (spieler.getHat_buchung_mm() == null)
            kto_saldo_neu = 0;
        else
            kto_saldo_neu = buchungDataSource.getNeusteBuchungZuSpieler(spieler).getKto_saldo_neu();

        buchungDataSource.close();
        teilnahmen = spieler.getTeilnahmen();

        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);

        final Toolbar toolbar = findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (spieler.getFoto().equals("avatar_m"))
            spielerBild.setImageResource(R.drawable.avatar_m);

        else if (spieler.getFoto().equals("avatar_f"))
            spielerBild.setImageResource(R.drawable.avatar_f);

        else
            spielerBild.setImageBitmap(BitmapFactory.decodeFile(spieler.getFoto()));

        adapter = new SpielerseiteActivityPagerAdapter(this, spieler, buchungList, kto_saldo_neu, teilnahmen, getSupportFragmentManager());
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
}
