package eu.merscher.lbsvolleyball.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import eu.merscher.lbsvolleyball.R;
import eu.merscher.lbsvolleyball.database.DataSource;
import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.utilities.Utilities;


public class SpielerseiteActivity extends AppCompatActivity implements EditSpielerFragment.OnEditFinish {


    private static Spieler spieler;
    private ImageView spielerBild;
    private FloatingActionButton editSpielerButton;
    public static Resources resources;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CollapsingToolbarLayout collapsingToolbar;
    public static ArrayList<Buchung> buchungList;
    private static EditSpielerFragment.OnEditFinish onEditFinish;
    private FloatingActionButton exportKontoButton;


    public static void setBuchungList(ArrayList<Buchung> buchungList) {
        SpielerseiteActivity.buchungList = buchungList;
    }

    public static EditSpielerFragment.OnEditFinish getOnEditFinish() {
        return onEditFinish;
    }

    @Override
    public void onEditFinish() {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielerseite);

        onEditFinish = this;
        resources = getResources();
        spieler = getIntent().getParcelableExtra("spieler");
        setTitle(spieler.getVname() + " " + spieler.getName());

        findViewsById();

        DataSource dataSource = DataSource.getInstance();
        dataSource.open();

        buchungList = dataSource.getAllBuchungZuSpieler(spieler);
        double kto_saldo_neu;
        if (spieler.getHat_buchung_mm() == null)
            kto_saldo_neu = 0;
        else
            kto_saldo_neu = dataSource.getNeusteBuchungZuSpieler(spieler).getKto_saldo_neu();

        int teilnahmen = spieler.getTeilnahmen();

        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Widget_Design_AppBarLayout);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.Widget_Design_CollapsingToolbar);

        final Toolbar toolbar = findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Displaygröße ermittlen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        //Spielerbild skalieren und setzen
        Bitmap spielerBildOriginal;
        Bitmap spielerBildScaled;


        if (spieler.getFoto().equals("avatar_m"))
            spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);

        else if (spieler.getFoto().equals("avatar_f"))
            spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_f);
        else {
            spielerBildOriginal = BitmapFactory.decodeFile(spieler.getFoto());
        }

        if (spielerBildOriginal != null)
            spielerBildScaled = Utilities.scaleToFitWidth(spielerBildOriginal, width);
        else {
            spielerBildOriginal = BitmapFactory.decodeResource(resources, R.drawable.avatar_m);
            spielerBildScaled = Utilities.scaleToFitWidth(spielerBildOriginal, width);
        }
        spielerBild.setImageBitmap(spielerBildScaled);

        //Pager mit Fragmenten erzeugen
        SpielerseiteActivityPagerAdapter adapter = new SpielerseiteActivityPagerAdapter(this, spieler, buchungList, kto_saldo_neu, teilnahmen, getSupportFragmentManager());


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                animateFab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        editSpielerButton.setOnClickListener(v -> {

            Intent intent = new Intent(SpielerseiteActivity.this, EditSpielerActivity.class);
            intent.putExtra("spieler", spieler);
            SpielerseiteActivity.this.startActivity(intent);
        });

        exportKontoButton.setOnClickListener(v -> {

            AlertDialog.Builder dialog = new AlertDialog.Builder(SpielerseiteActivity.this);

            dialog.setTitle("Kontoverlauf-Export")
                    .setMessage("Kontoverlauf von "
                            + spieler.getVname() + " " + spieler.getName()
                            + " wird an " + spieler.getMail() + " gesendet.")
                    .setNegativeButton("Abbrechen", (dialog1, which) -> dialog1.cancel())
                    .setPositiveButton("Ok", (dialog12, i) -> {
                        new EMailSendenAsyncTask(spieler, buchungList).execute();
                        Toast toast = Toast.makeText(getApplicationContext(), "Der Kontoverlauf wurde per Mail zugestellt", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }).show();

        });
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void findViewsById() {

        viewPager = findViewById(R.id.spielerseite_viewpager);
        spielerBild = findViewById(R.id.spielerbild_groß);
        collapsingToolbar = findViewById(R.id.htab_collapse_toolbar);
        editSpielerButton = findViewById(R.id.activity_spielerseite_edit_spieler_button);
        tabLayout = findViewById(R.id.htab_tabs);
        exportKontoButton = findViewById(R.id.activity_spielerseite_export_konto);


    }

    private void animateFab(int position) {
        if (position == 1) {
            editSpielerButton.hide();
            exportKontoButton.show();
        } else {
            editSpielerButton.show();
            exportKontoButton.hide();
        }

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


    public class SpielerseiteActivityPagerAdapter extends FragmentPagerAdapter {

        private final Spieler spieler;
        private final ArrayList<Buchung> buchungList;
        private final double kto_saldo_neu;
        private final int teilnahmen;
        private final Context context;

        SpielerseiteActivityPagerAdapter(Context context, Spieler spieler, ArrayList<Buchung> buchungList, double kto_saldo_neu, int teilnahmen, FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.spieler = spieler;
            this.buchungList = buchungList;
            this.kto_saldo_neu = kto_saldo_neu;
            this.teilnahmen = teilnahmen;
            this.context = context;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return SpielerseiteGrunddatenFragment.newInstance(spieler, kto_saldo_neu, teilnahmen);
            } else {
                return new SpielerseiteKontodatenFragment(buchungList, spieler);
            }

        }

        @Override
        public int getItemPosition(@NotNull Object o) {
            return POSITION_NONE;
        }

        //Anzahl der Tabs
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            switch (position) {
                case 0:
                    return context.getString(R.string.tab_grunddaten);
                case 1:
                    return context.getString(R.string.tab_kontodaten);
                default:
                    return null;
            }
        }
    }

    static class EMailSendenAsyncTask extends AsyncTask<Void, Void, Void> {


        private final Spieler spieler;
        private final ArrayList<Buchung> buchungList;

        EMailSendenAsyncTask(Spieler spieler, ArrayList<Buchung> buchungList) {
            this.spieler = spieler;
            this.buchungList = buchungList;
        }


        @Override
        protected Void doInBackground(Void... args) {

            // SMTP Verbindung starten
            final String username = "lbsvolleyball@merscher.eu";
            final String password = "be1gvd1!b685+08787adklasdnl#++13nlandasd2";

            Properties props = new Properties();

            props.put("mail.smtp.host", "mail.merscher.eu");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            // Nachricht:
            String to = spieler.getMail();
            String from = "LBSVolleyball@merscher.eu";
            String subject = "Kontoverlauf";
            Message msg = new MimeMessage(session);

            if (to != null) {
                try {
                    msg.setFrom(new InternetAddress(from));
                    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                    msg.setSubject(subject);

                    StringBuilder stringBuilder = new StringBuilder();

                    for (Buchung b : buchungList) {
                        stringBuilder.append(b).append("\n");
                    }

                    msg.setText("Hi " + spieler.getVname()
                            + "\nanbei erhälst du die Übersicht aller deiner Buchungen im LBS Volleyballteam: \n\n\n"
                            + "Datum       Betrag      Kontostand\n\n"
                            + stringBuilder + "\n"
                            + "Sportliche Grüße\nLBS Volleyball-App");

                    Transport.send(msg);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }


}
