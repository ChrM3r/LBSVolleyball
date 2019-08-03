package eu.merscher.lbsvolleyball;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BuchungDataSource {

    private static final String LOG_TAG = BuchungDataSource.class.getSimpleName();

    private static BuchungDataSource instance;
    private int mOpenCounter;
    private SQLiteDatabase database;
    private BuchungDbHelper dbHelper;

    public static synchronized void initializeInstance(Context context) {
        if (instance == null) {
            instance = new BuchungDataSource(context);
        }
    }

    public static synchronized BuchungDataSource getInstance() {
        if (instance == null) {
            throw new IllegalStateException(BuchungDataSource.class.getSimpleName() +
                    " is not initialized, call initialize(..) method first.");
        }
        return instance;
    }

    //Array mit allen Spaltennamen der Tabelle
    private String[] columns = {
            BuchungDbHelper.COLUMN_BUID,
            BuchungDbHelper.COLUMN_UID,
            BuchungDbHelper.COLUMN_BUBTR,
            BuchungDbHelper.COLUMN_KTOSLDALT,
            BuchungDbHelper.COLUMN_KTOSLDNEU,
            BuchungDbHelper.COLUMN_BU_DATE
    };

    private static DecimalFormat df = new DecimalFormat("0.00");


    public BuchungDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new BuchungDbHelper(context);
    }

    public void open() {
        mOpenCounter++;
        if (mOpenCounter == 1) {
            Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
            database = dbHelper.getWritableDatabase();
            Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
        }
    }

    public void close() {
        mOpenCounter--;
        if (mOpenCounter == 1) {
            dbHelper.close();
            Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
        }
    }

    //Buchung in Datenbank anlegen und gleichzeit als Objekt bereitstellen

    public Buchung createBuchung(long u_id, double bu_btr, double kto_saldo_alt, double kto_saldo_neu, String bu_date) {

        ContentValues values = new ContentValues();

        values.put(BuchungDbHelper.COLUMN_UID, u_id);
        values.put(BuchungDbHelper.COLUMN_BUBTR, bu_btr);
        values.put(BuchungDbHelper.COLUMN_KTOSLDALT, kto_saldo_alt);
        values.put(BuchungDbHelper.COLUMN_KTOSLDNEU, kto_saldo_neu);
        values.put(BuchungDbHelper.COLUMN_BU_DATE, bu_date);

        long insertId = database.insert(BuchungDbHelper.TABLE_BUCHUNG_DATA, null, values);

        Cursor cursor = database.query(BuchungDbHelper.TABLE_BUCHUNG_DATA,
                columns, BuchungDbHelper.COLUMN_BUID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Buchung Buchung = cursorToBuchung(cursor);
        cursor.close();

        return Buchung;
    }

    //Cursor-Objekt in ein Buchung-Objekt umwandeln

    private Buchung cursorToBuchung(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(BuchungDbHelper.COLUMN_BUID);
        int idUid = cursor.getColumnIndex(BuchungDbHelper.COLUMN_UID);
        int idBu_btr = cursor.getColumnIndex(BuchungDbHelper.COLUMN_BUBTR);
        int idKto_saldo_alt = cursor.getColumnIndex(BuchungDbHelper.COLUMN_KTOSLDALT);
        int idKto_saldo_neu = cursor.getColumnIndex(BuchungDbHelper.COLUMN_KTOSLDNEU);
        int idBu_date = cursor.getColumnIndex(BuchungDbHelper.COLUMN_BU_DATE);


        long u_id = cursor.getLong(idUid);
        double bu_btr = cursor.getDouble(idBu_btr);
        double kto_saldo_alt = cursor.getDouble(idKto_saldo_alt);
        double kto_saldo_neu = cursor.getDouble(idKto_saldo_neu);
        long bu_id = cursor.getLong(idIndex);
        String bu_date = cursor.getString(idBu_date);


        return new Buchung(bu_id, u_id, bu_btr, kto_saldo_alt, kto_saldo_neu, bu_date);

    }
    //Alle Buchung aus Datenbank in eine Liste

    public ArrayList<Buchung> getAllBuchung() {

        ArrayList<Buchung> BuchungList = new ArrayList<>();

        Cursor cursor = database.query(BuchungDbHelper.TABLE_BUCHUNG_DATA,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        Buchung Buchung;

        while (!cursor.isAfterLast()) {
            Buchung = cursorToBuchung(cursor);
            BuchungList.add(Buchung);
            cursor.moveToNext();
        }

        cursor.close();

        return BuchungList;
    }

    //Alle Buchungen eines Spielers aus Datenbank in eine Liste

    public ArrayList<Buchung> getAllBuchungZuSpieler(Spieler spieler) {


        ArrayList<Buchung> BuchungList = new ArrayList<>();

        Cursor cursor = database.query(BuchungDbHelper.TABLE_BUCHUNG_DATA,
                columns, BuchungDbHelper.COLUMN_UID + "=" + spieler.getU_id(), null, null, null, BuchungDbHelper.COLUMN_BUID + " DESC");

        cursor.moveToFirst();
        Buchung Buchung;

        while (!cursor.isAfterLast()) {
            Buchung = cursorToBuchung(cursor);
            BuchungList.add(Buchung);
            cursor.moveToNext();
        }

        cursor.close();

        return BuchungList;
    }

    //Neuste Buchung aus Datenbank in eine Liste

    public Buchung getNeusteBuchungZuSpieler(Spieler spieler) {


        Cursor cursor = database.query(BuchungDbHelper.TABLE_BUCHUNG_DATA,
                columns, BuchungDbHelper.COLUMN_UID + "=" + spieler.getU_id(),
                null, null, null, BuchungDbHelper.COLUMN_BUID);

        cursor.moveToLast();
        Buchung buchung = cursorToBuchung(cursor);

        cursor.close();

        return buchung;
    }

    //Alle Buchungsbeträge einer Person aus der Datenbank in eine Liste in umgekehrter Reihenfolge (neuste oben)

    public ArrayList<String> getAlleBuchungBetragZuSpieler(Spieler spieler) {

        ArrayList<String> buchungBetragList = new ArrayList<>();
        String[] bu_btr_zu_spieler = {BuchungDbHelper.COLUMN_BUID, BuchungDbHelper.COLUMN_BUBTR};


        Cursor cursor = database.query(BuchungDbHelper.TABLE_BUCHUNG_DATA,
                bu_btr_zu_spieler, BuchungDbHelper.COLUMN_UID + "=" + spieler.getU_id(),
                null, null, null, BuchungDbHelper.COLUMN_BUID + " DESC");

        cursor.moveToFirst();
        double buchungsbetrag;

        while (!cursor.isAfterLast()) {
            buchungsbetrag = cursor.getDouble(1);
            buchungBetragList.add(String.valueOf(df.format(buchungsbetrag)));
            cursor.moveToNext();
        }

        cursor.close();

        return buchungBetragList;
    }

    //Alle Buchungsdatümer einer Person aus der Datenbank in eine Liste in umgekehrter Reihenfolge (neuste oben)

    public ArrayList<String> getAlleBuchungDatumZuSpieler(Spieler spieler) {

        ArrayList<String> buchungDatumList = new ArrayList<>();
        String[] bu_datum_zu_spieler = {BuchungDbHelper.COLUMN_BUID, BuchungDbHelper.COLUMN_BU_DATE};

        Cursor cursor = database.query(BuchungDbHelper.TABLE_BUCHUNG_DATA,
                bu_datum_zu_spieler, BuchungDbHelper.COLUMN_UID + "=" + spieler.getU_id(),
                null, null, null, BuchungDbHelper.COLUMN_BUID + " DESC");

        cursor.moveToFirst();
        String buchungdatum;

        while (!cursor.isAfterLast()) {
            buchungdatum = cursor.getString(1);
            buchungDatumList.add(buchungdatum);
            cursor.moveToNext();
        }

        cursor.close();

        return buchungDatumList;
    }

    //Alle neuen Kontostände einer Person aus der Datenbank in eine Liste in umgekehrter Reihenfolge (neuste oben)

    public ArrayList<String> getAlleKtoSaldoNeuZuSpieler(Spieler spieler) {


        ArrayList<String> kontosaldoNeuList = new ArrayList<>();
        String[] kto_saldo_neu_zu_spieler = {BuchungDbHelper.COLUMN_BUID, BuchungDbHelper.COLUMN_KTOSLDNEU};

        Cursor cursor = database.query(BuchungDbHelper.TABLE_BUCHUNG_DATA,
                kto_saldo_neu_zu_spieler, BuchungDbHelper.COLUMN_UID + "=" + spieler.getU_id(),
                null, null, null, BuchungDbHelper.COLUMN_BUID + " DESC");

        cursor.moveToFirst();
        double kto_saldo_neu;

        while (!cursor.isAfterLast()) {
            kto_saldo_neu = cursor.getDouble(1);
            kontosaldoNeuList.add(String.valueOf(df.format(kto_saldo_neu)));
            cursor.moveToNext();
        }

        cursor.close();

        return kontosaldoNeuList;
    }
}
