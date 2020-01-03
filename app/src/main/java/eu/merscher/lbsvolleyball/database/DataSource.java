package eu.merscher.lbsvolleyball.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.model.Training;
import eu.merscher.lbsvolleyball.model.Trainingsort;
import eu.merscher.lbsvolleyball.utilities.Utilities;

public class DataSource {

    private static final String LOG_TAG = DataSource.class.getSimpleName();

    private static DataSource instance;
    private final DbHelper dbHelper;
    //SpielerDB
    private final String[] spieler_data_columns = {
            DbHelper.SPIELER_DATA_COLUMN_S_ID,
            DbHelper.SPIELER_DATA_COLUMN_NAME,
            DbHelper.SPIELER_DATA_COLUMN_VNAME,
            DbHelper.SPIELER_DATA_COLUMN_BDATE,
            DbHelper.SPIELER_DATA_COLUMN_TEILNAHMEN,
            DbHelper.SPIELER_DATA_COLUMN_FOTO,
            DbHelper.SPIELER_DATA_COLUMN_MAIL,
            DbHelper.SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM
    };


    //BuchungDB
    private final String[] buchung_data_columns = {
            DbHelper.BUCHUNG_DATA_COLUMN_BU_ID,
            DbHelper.BUCHUNG_DATA_COLUMN_S_ID,
            DbHelper.BUCHUNG_DATA_COLUMN_BUBTR,
            DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDALT,
            DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDNEU,
            DbHelper.BUCHUNG_DATA_COLUMN_BU_DATE,
            DbHelper.BUCHUNG_DATA_COLUMN_IST_TRAINING_MM,
            DbHelper.BUCHUNG_DATA_COLUMN_TRAINING_ID,
            DbHelper.BUCHUNG_DATA_COLUMN_IST_MANUELL_MM,
            DbHelper.BUCHUNG_DATA_COLUMN_IST_TUNIER_MM,
            DbHelper.BUCHUNG_DATA_COLUMN_TUNIER_ID,
            DbHelper.BUCHUNG_DATA_COLUMN_IST_GELOESCHTER_SPIELER_MM,
            DbHelper.BUCHUNG_DATA_COLUMN_GELOESCHTER_S_ID
    };

    //TrainingDB
    private final String[] training_data_columns = {
            DbHelper.TRAINING_DATA_COLUMN_DBID,
            DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID,
            DbHelper.TRAINING_DATA_COLUMN_TRAININGS_DTM,
            DbHelper.TRAINING_DATA_COLUMN_TRAININGS_ORT_ID,
            DbHelper.TRAINING_DATA_COLUMN_TRAININGS_TN,
            DbHelper.TRAINING_DATA_COLUMN_PLATZKOSTEN,
            DbHelper.TRAINING_DATA_COLUMN_IST_KOSTENLOS_MM
    };

    //TrainingsortDB
    private final String[] trainingsort_data_columns = {
            DbHelper.TRAININGSORT_DATA_COLUMN_DBID,
            DbHelper.TRAININGSORT_DATA_COLUMN_NAME,
            DbHelper.TRAININGSORT_DATA_COLUMN_STRASSE,
            DbHelper.TRAININGSORT_DATA_COLUMN_PLZ,
            DbHelper.TRAININGSORT_DATA_COLUMN_ORT,
            DbHelper.TRAININGSORT_DATA_COLUMN_FOTO,
            DbHelper.TRAININGSORT_DATA_COLUMN_LATITUDE,
            DbHelper.TRAININGSORT_DATA_COLUMN_LONGITUDE,
            DbHelper.TRAININGSORT_DATA_COLUMN_BESUCHE
    };

    private int mOpenCounter;
    private SQLiteDatabase database;


    public DataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new DbHelper(context);
    }

    public static synchronized void initializeInstance(Context context) {
        if (instance == null) {
            instance = new DataSource(context);
        }
    }

    public static synchronized DataSource getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DataSource.class.getSimpleName() +
                    " is not initialized, call initialize(..) method first.");
        }
        return instance;
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

    //#############################################################################################################################################################################--BUCHUNG--

    //Buchung in Datenbank anlegen und gleichzeit als Objekt bereitstellen

    public Buchung createBuchung(long s_id, double bu_btr, double kto_saldo_alt, double kto_saldo_neu, String bu_date, String ist_training_mm, long training_id, String ist_manuell_mm, String ist_tunier_mm, long tunier_id, String ist_geloeschter_spieler_mm, long geloeschter_s_id) {

        ContentValues values = new ContentValues();

        values.put(DbHelper.BUCHUNG_DATA_COLUMN_S_ID, s_id);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_BUBTR, bu_btr);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDALT, kto_saldo_alt);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDNEU, kto_saldo_neu);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_BU_DATE, bu_date);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_TRAINING_MM, ist_training_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_TRAINING_ID, training_id);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_MANUELL_MM, ist_manuell_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_TUNIER_MM, ist_tunier_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_TUNIER_ID, tunier_id);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_GELOESCHTER_SPIELER_MM, ist_geloeschter_spieler_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_GELOESCHTER_S_ID, geloeschter_s_id);


        long insertId = database.insert(DbHelper.TABLE_BUCHUNG_DATA, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_BU_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Buchung buchung = cursorToBuchung(cursor);
        cursor.close();
        Log.d(LOG_TAG, "DataSource: " + buchung.toString() + " erzeugt");

        return buchung;
    }

    //Buchung in Datenbank anlegen und gleichzeit als Objekt bereitstellen

    public Buchung createBuchungAufTeamkonto(double bu_btr, double kto_saldo_alt, double kto_saldo_neu, String bu_date, String ist_training_mm, long training_id, String ist_manuell_mm, String ist_tunier_mm, long tunier_id, String ist_geloeschter_spieler_mm, long geloeschter_s_id) {

        ContentValues values = new ContentValues();

        values.put(DbHelper.BUCHUNG_DATA_COLUMN_S_ID, -1);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_BUBTR, bu_btr);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDALT, kto_saldo_alt);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDNEU, kto_saldo_neu);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_BU_DATE, bu_date);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_TRAINING_MM, ist_training_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_TRAINING_ID, training_id);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_MANUELL_MM, ist_manuell_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_TUNIER_MM, ist_tunier_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_TUNIER_ID, tunier_id);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_GELOESCHTER_SPIELER_MM, ist_geloeschter_spieler_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_GELOESCHTER_S_ID, geloeschter_s_id);

        long insertId = database.insert(DbHelper.TABLE_BUCHUNG_DATA, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_BU_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Buchung buchung = cursorToBuchung(cursor);
        cursor.close();

        Log.d(LOG_TAG, "DataSource: " + buchung.toString() + " erzeugt");
        return buchung;
    }

    //Cursor-Objekt in ein Buchung-Objekt umwandeln

    private Buchung cursorToBuchung(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_BU_ID);
        int idUid = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_S_ID);
        int idBu_btr = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_BUBTR);
        int idKto_saldo_alt = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDALT);
        int idKto_saldo_neu = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDNEU);
        int idBu_date = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_BU_DATE);
        int idIst_training_mm = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_IST_TRAINING_MM);
        int idTrainings_ID = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_TRAINING_ID);
        int idIst_manuell_mm = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_IST_MANUELL_MM);
        int idIst_tunier_mm = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_IST_TUNIER_MM);
        int idTunier_ID = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_TUNIER_ID);
        int idIst_geloeschter_spieler_mm = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_IST_GELOESCHTER_SPIELER_MM);
        int idGeloeschter_s_ID = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_GELOESCHTER_S_ID);


        long u_id = cursor.getLong(idUid);
        double bu_btr = cursor.getDouble(idBu_btr);
        double kto_saldo_alt = cursor.getDouble(idKto_saldo_alt);
        double kto_saldo_neu = cursor.getDouble(idKto_saldo_neu);
        long bu_id = cursor.getLong(idIndex);
        String bu_date = cursor.getString(idBu_date);
        String ist_training_mm = cursor.getString(idIst_training_mm);
        long training_id = cursor.getLong(idTrainings_ID);
        String ist_manuell_mm = cursor.getString(idIst_manuell_mm);
        String ist_tunier_mm = cursor.getString(idIst_tunier_mm);
        long tunier_id = cursor.getLong(idTunier_ID);
        String ist_geloeschter_spieler_mm = cursor.getString(idIst_geloeschter_spieler_mm);
        long geloeschter_s_id = cursor.getLong(idGeloeschter_s_ID);

        return new Buchung(bu_id, u_id, bu_btr, kto_saldo_alt, kto_saldo_neu, bu_date, ist_training_mm, training_id, ist_manuell_mm, ist_tunier_mm, tunier_id, ist_geloeschter_spieler_mm, geloeschter_s_id);


    }

    //Alle Buchungen aus Datenbank in eine Liste

    public ArrayList<Buchung> getAllBuchungen() {


        ArrayList<Buchung> buchungList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, null,
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BU_ID + " DESC");

        cursor.moveToFirst();
        Buchung Buchung;

        while (!cursor.isAfterLast()) {
            Buchung = cursorToBuchung(cursor);
            buchungList.add(Buchung);
            cursor.moveToNext();
        }

        cursor.close();

        return buchungList;
    }
    //Alle Buchungen eines Spielers aus Datenbank in eine Liste

    public ArrayList<Buchung> getAllBuchungZuSpieler(Spieler spieler) {


        ArrayList<Buchung> buchungList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_S_ID + "=" + spieler.getS_id(),
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BU_ID + " DESC");

        cursor.moveToFirst();
        Buchung Buchung;

        while (!cursor.isAfterLast()) {
            Buchung = cursorToBuchung(cursor);
            buchungList.add(Buchung);
            cursor.moveToNext();
        }

        cursor.close();

        return buchungList;
    }

    //Neuste Buchung aus Datenbank in eine Liste

    public Buchung getNeusteBuchungZuSpieler(Spieler spieler) {


        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_S_ID + "=" + spieler.getS_id(),
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BU_ID);

        Buchung buchung;
        if (cursor.moveToLast()) {
            cursor.moveToLast();
            buchung = cursorToBuchung(cursor);
        } else
            buchung = new Buchung(-999, -999, 0, 0, 0, null, null, -999, null, null, -999, null, -999);

        cursor.close();

        return buchung;
    }

    //Alle Buchungen eines Spielers aus Datenbank in eine Liste

    public ArrayList<Buchung> getAllBuchungZuTeamkonto() {


        ArrayList<Buchung> BuchungList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_S_ID + "= -1",
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BU_ID + " DESC");

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

    //Neuste Buchung zum teamkonto aus Datenbank in eine Liste

    public Buchung getNeusteBuchungZuTeamkonto() {


        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_S_ID + "= -1",
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BU_ID);

        cursor.moveToLast();

        Buchung buchung;
        if (cursor.getCount() > 0)
            buchung = cursorToBuchung(cursor);
        else
            buchung = null;


        cursor.close();

        return buchung;
    }


    //#############################################################################################################################################################################--SPIELER--

    //Spieler in Datenbank anlegen und gleichzeit als Objekt bereitstellen

    public Spieler createSpieler(String name, String vname, String bdate, int teilnahmen, String foto, String mail, String hat_buchung_mm) {


        ContentValues values = new ContentValues();

        values.put(DbHelper.SPIELER_DATA_COLUMN_NAME, name);
        values.put(DbHelper.SPIELER_DATA_COLUMN_VNAME, vname);
        values.put(DbHelper.SPIELER_DATA_COLUMN_BDATE, bdate);
        values.put(DbHelper.SPIELER_DATA_COLUMN_TEILNAHMEN, teilnahmen);
        values.put(DbHelper.SPIELER_DATA_COLUMN_FOTO, foto);
        values.put(DbHelper.SPIELER_DATA_COLUMN_MAIL, mail);
        values.put(DbHelper.SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM, hat_buchung_mm);

        long insertId = database.insert(DbHelper.TABLE_SPIELER_DATA, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

        Log.d(LOG_TAG, "DataSource: " + spieler.toString() + " erzeugt");

        return spieler;
    }

    // Spieler löschen
    public void deleteSpieler(Spieler spieler) {


        long id = spieler.getS_id();

        database.delete(DbHelper.TABLE_SPIELER_DATA,
                DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + spieler.toString());
    }

    //Spieler updaten gesamt
    public Spieler updateSpieler(long id, String newName, String newVname, String newBdate, int newTeilnahmen, String newFoto, String newMail, String hat_buchung_mm) {


        ContentValues values = new ContentValues();
        values.put(DbHelper.SPIELER_DATA_COLUMN_NAME, newName);
        values.put(DbHelper.SPIELER_DATA_COLUMN_VNAME, newVname);
        values.put(DbHelper.SPIELER_DATA_COLUMN_BDATE, newBdate);
        values.put(DbHelper.SPIELER_DATA_COLUMN_TEILNAHMEN, newTeilnahmen);
        values.put(DbHelper.SPIELER_DATA_COLUMN_FOTO, newFoto);
        values.put(DbHelper.SPIELER_DATA_COLUMN_MAIL, newMail);
        values.put(DbHelper.SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM, hat_buchung_mm);


        database.update(DbHelper.TABLE_SPIELER_DATA,
                values,
                DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + id,
                null);

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

        Log.d(LOG_TAG, "DataSource: " + spieler.toString() + " Update");

        return spieler;
    }

    //Spieler Teilnahmen updaten
    public Spieler updateTeilnahmenSpieler(Spieler s) {

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + s.getS_id(),
                null, null, null, null);

        cursor.moveToFirst();

        int idTeilnahmen = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_TEILNAHMEN);
        int teilnahmen = cursor.getInt(idTeilnahmen);
        cursor.close();

        teilnahmen++;

        ContentValues values = new ContentValues();
        values.put(DbHelper.SPIELER_DATA_COLUMN_NAME, s.getName());
        values.put(DbHelper.SPIELER_DATA_COLUMN_VNAME, s.getVname());
        values.put(DbHelper.SPIELER_DATA_COLUMN_BDATE, s.getBdate());
        values.put(DbHelper.SPIELER_DATA_COLUMN_TEILNAHMEN, teilnahmen);
        values.put(DbHelper.SPIELER_DATA_COLUMN_FOTO, s.getFoto());
        values.put(DbHelper.SPIELER_DATA_COLUMN_MAIL, s.getMail());
        values.put(DbHelper.SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM, s.getHat_buchung_mm());


        database.update(DbHelper.TABLE_SPIELER_DATA,
                values,
                DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + s.getS_id(),
                null);

        Cursor cursor1 = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + s.getS_id(),
                null, null, null, null);

        cursor1.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor1);

        cursor1.close();

        Log.d(LOG_TAG, "DataSource: " + spieler.toString() + " Update");


        return spieler;
    }

    //Spieler Foto updaten
    public Spieler updateFotoSpieler(Spieler s, String newFoto) {


        ContentValues values = new ContentValues();
        values.put(DbHelper.SPIELER_DATA_COLUMN_NAME, s.getName());
        values.put(DbHelper.SPIELER_DATA_COLUMN_VNAME, s.getVname());
        values.put(DbHelper.SPIELER_DATA_COLUMN_BDATE, s.getBdate());
        values.put(DbHelper.SPIELER_DATA_COLUMN_TEILNAHMEN, s.getTeilnahmen());
        values.put(DbHelper.SPIELER_DATA_COLUMN_FOTO, newFoto);
        values.put(DbHelper.SPIELER_DATA_COLUMN_MAIL, s.getMail());
        values.put(DbHelper.SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM, s.getHat_buchung_mm());

        database.update(DbHelper.TABLE_SPIELER_DATA,
                values,
                DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + s.getS_id(),
                null);

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + s.getS_id(),
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();


        Log.d(LOG_TAG, "DataSource: " + spieler.toString() + " Update");


        return spieler;
    }

    //Spieler Hat_Buchung_Merkmal updaten
    public Spieler updateHatBuchungenMM(Spieler s) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.SPIELER_DATA_COLUMN_NAME, s.getName());
        values.put(DbHelper.SPIELER_DATA_COLUMN_VNAME, s.getVname());
        values.put(DbHelper.SPIELER_DATA_COLUMN_BDATE, s.getBdate());
        values.put(DbHelper.SPIELER_DATA_COLUMN_TEILNAHMEN, s.getTeilnahmen());
        values.put(DbHelper.SPIELER_DATA_COLUMN_FOTO, s.getFoto());
        values.put(DbHelper.SPIELER_DATA_COLUMN_MAIL, s.getMail());
        values.put(DbHelper.SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM, "X");


        database.update(DbHelper.TABLE_SPIELER_DATA,
                values,
                DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + s.getS_id(),
                null);

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + s.getS_id(),
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

        Log.d(LOG_TAG, "DataSource: " + spieler.toString() + " Update");


        return spieler;
    }

    //Cursor-Objekt in ein Spieler-Objekt umwandeln

    private Spieler cursorToSpieler(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_S_ID);
        int idName = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_NAME);
        int idVname = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_VNAME);
        int idBdate = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_BDATE);
        int idTeilnahmen = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_TEILNAHMEN);
        int idFoto = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_FOTO);
        int idMail = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_MAIL);
        int idHatBuchungMM = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM);

        long u_id = cursor.getLong(idIndex);
        String name = cursor.getString(idName);
        String vname = cursor.getString(idVname);
        String bdate = cursor.getString(idBdate);
        int teilnahmen = cursor.getInt(idTeilnahmen);
        String foto = cursor.getString(idFoto);
        String mail = cursor.getString(idMail);
        String hat_buchung_mm = cursor.getString(idHatBuchungMM);

        return new Spieler(u_id, name, vname, bdate, teilnahmen, foto, mail, hat_buchung_mm);

    }
    //Alle Spieler aus Datenbank in eine Liste

    public ArrayList<Spieler> getAllSpieler() {


        ArrayList<Spieler> spielerList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, null, null, null, null, DbHelper.SPIELER_DATA_COLUMN_S_ID);

        cursor.moveToFirst();
        Spieler spieler;

        while (!cursor.isAfterLast()) {
            spieler = cursorToSpieler(cursor);
            spielerList.add(spieler);
            cursor.moveToNext();
        }

        cursor.close();

        return spielerList;
    }

    //Beste Spieler (Teilnahmen) aus Datenbank in eine Liste

    public ArrayList<Spieler> getBesteSpieler(int anzahl) {


        ArrayList<Spieler> spielerList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, null, null, null, null, DbHelper.SPIELER_DATA_COLUMN_TEILNAHMEN + " DESC");

        cursor.moveToFirst();
        Spieler spieler;

        while (!cursor.isAfterLast()) {
            spieler = cursorToSpieler(cursor);
            if (spielerList.size() < anzahl)
                spielerList.add(spieler);
            cursor.moveToNext();
        }

        cursor.close();

        return spielerList;
    }

    //Alle Geburttage zu Spielern aus einer Liste
    public ArrayList<String> getGeburtstageZuSpieler(ArrayList<Spieler> spielerList) {


        ArrayList<String> geburtstagsList = new ArrayList<>();

        for (Spieler s : spielerList) {

            Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                    spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + s.getS_id(), null, null, null, null);

            cursor.moveToFirst();
            Spieler spieler;

            spieler = cursorToSpieler(cursor);
            geburtstagsList.add(spieler.getBdate());

            cursor.close();
        }

        return geburtstagsList;
    }


    public ArrayList<Spieler> getAllSpielerAlphabetischName() {

        ArrayList<Spieler> spielerList = new ArrayList<>();


        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, null, null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler;

        while (!cursor.isAfterLast()) {
            spieler = cursorToSpieler(cursor);
            spielerList.add(spieler);
            cursor.moveToNext();
        }
        cursor.close();

        Collections.sort(spielerList, new Utilities().new SortName());

        return spielerList;
    }

    public ArrayList<Spieler> getAllSpielerAlphabetischVname() {

        ArrayList<Spieler> spielerList = new ArrayList<>();


        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, null, null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler;

        while (!cursor.isAfterLast()) {
            spieler = cursorToSpieler(cursor);
            spielerList.add(spieler);
            cursor.moveToNext();
        }
        cursor.close();

        Collections.sort(spielerList, new Utilities().new SortVname());

        return spielerList;
    }

    public ArrayList<Spieler> getAllSpielerAbsteigendTeilnahme() {

        ArrayList<Spieler> spielerList = new ArrayList<>();


        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, null, null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler;

        while (!cursor.isAfterLast()) {
            spieler = cursorToSpieler(cursor);
            spielerList.add(spieler);
            cursor.moveToNext();
        }
        cursor.close();

        Collections.sort(spielerList, new Utilities().new SortTeilnahmen());
        Collections.reverse(spielerList);

        return spielerList;
    }

    //#############################################################################################################################################################################--TRAINING--

    public Training createTraining(long trainings_id, String trainings_dtm, long trainings_ort_id, long trainings_tn, double platzkosten, String ist_kostenlos_mm) {

        ContentValues values = new ContentValues();

        values.put(DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID, trainings_id);
        values.put(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_DTM, trainings_dtm);
        values.put(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_ORT_ID, trainings_ort_id);
        values.put(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_TN, trainings_tn);
        values.put(DbHelper.TRAINING_DATA_COLUMN_PLATZKOSTEN, platzkosten);
        values.put(DbHelper.TRAINING_DATA_COLUMN_IST_KOSTENLOS_MM, ist_kostenlos_mm);


        long insertId = database.insert(DbHelper.TABLE_TRAINING_DATA, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, DbHelper.TRAINING_DATA_COLUMN_DBID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Training training = cursorToTraining(cursor);
        cursor.close();

        Log.d(LOG_TAG, "Training: " + training.getDb_id() + " erzeugt.");


        return training;
    }

    //Cursor-Objekt in ein Buchung-Objekt umwandeln

    private Training cursorToTraining(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_DBID);
        int idTrainings_id = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID);
        int idTrainings_dtm = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_DTM);
        int idTrainings_ort_id = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_ORT_ID);
        int idTrainings_tn = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_TN);
        int idPlatzkosten = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_PLATZKOSTEN);
        int idIst_kostenlos_mm = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_IST_KOSTENLOS_MM);

        long db_id = cursor.getLong(idIndex);
        long trainings_id = cursor.getLong(idTrainings_id);
        String trainings_dtm = cursor.getString(idTrainings_dtm);
        long trainings_ort_id = cursor.getLong(idTrainings_ort_id);
        long trainings_tn = cursor.getLong(idTrainings_tn);
        double platzkosten = cursor.getDouble(idPlatzkosten);
        String ist_kostenlos_mm = cursor.getString(idIst_kostenlos_mm);


        return new Training(db_id, trainings_id, trainings_dtm, trainings_ort_id, trainings_tn, platzkosten, ist_kostenlos_mm);
    }

    public long getNeusteTrainingsID() {


        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, null,
                null, null, null, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID);

        Training training;
        if (cursor.moveToLast()) {
            cursor.moveToLast();
            training = cursorToTraining(cursor);
        } else
            training = new Training(-999, -999, null, -999, -999, -999, null);

        cursor.close();

        return training.getTraining_id();
    }


    public ArrayList<Long> getTrainingsIDzuDatum(int jahr, int monat, int tag) {

        String monatString = Integer.toString(monat);
        String tagString = Integer.toString(tag);

        if (monatString.length() == 1)
            monatString = "0" + monat;

        if (tagString.length() == 1)
            tagString = "0" + tag;

        String datum = tagString + "." + monatString + "." + jahr;

        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, DbHelper.TRAINING_DATA_COLUMN_TRAININGS_DTM + "=" + "'" + datum + "'",
                null, null, null, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID);

        Training training;
        ArrayList<Long> trainingIDList = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            training = cursorToTraining(cursor);

            if (!trainingIDList.contains(training.getTraining_id()))
                trainingIDList.add(training.getTraining_id());

            cursor.moveToNext();
        }

        cursor.close();

        return trainingIDList;
    }

    public ArrayList<Long> getAllTrainingsID() {

        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, null,
                null, null, null, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID + " DESC");

        Training training;
        ArrayList<Long> trainingIDList = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            training = cursorToTraining(cursor);

            if (!trainingIDList.contains(training.getTraining_id()))
                trainingIDList.add(training.getTraining_id());

            cursor.moveToNext();
        }

        cursor.close();

        return trainingIDList;
    }

    //ArrayList mit allen Trainings
    public ArrayList<Training> getAllTraining() {


        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, null,
                null, null, null, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID);

        Training training;
        ArrayList<Training> trainingList = new ArrayList<>();

        cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                training = cursorToTraining(cursor);
                trainingList.add(training);
                cursor.moveToNext();
            }


        cursor.close();

        return trainingList;
    }

    //Arraylist aller Spieler zu einer Trainings ID

    public ArrayList<Spieler> getSpielerZuTrainingsId(long trainings_Id) {

        ArrayList<Spieler> trainings_teilnehmer = new ArrayList<>();
        ArrayList<Training> trainingList = new ArrayList<>();


        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID + "=" + trainings_Id,
                null, null, null, null);

        cursor.moveToFirst();
        Training training;

        while (!cursor.isAfterLast()) {
            training = cursorToTraining(cursor);
            trainingList.add(training);
            cursor.moveToNext();
        }

        for (Training t : trainingList) {

            Cursor cursor_spieler = database.query(DbHelper.TABLE_SPIELER_DATA,
                    spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_S_ID + "=" + t.getTrainings_tn(),
                    null, null, null, null);

            if (!cursor_spieler.isAfterLast()) {
                cursor_spieler.moveToFirst();
                Spieler spieler = cursorToSpieler(cursor_spieler);
                trainings_teilnehmer.add(spieler);
            } else
                trainings_teilnehmer.add(new Spieler(-999, "gelöscht", "Bereits", null, 0, "avatar_m", null, null));
        }

        return trainings_teilnehmer;
    }

    //Platzkosten zu TrainingsID
    public double getPlatzkostenZuTrainingsId(long trainings_Id) {

        Training training;
        double platzkosten;

        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID + "=" + trainings_Id,
                null, null, null, null);


        cursor.moveToFirst();
        training = cursorToTraining(cursor);
        platzkosten = training.getPlatzkosten();

        return platzkosten;
    }

    public Trainingsort getTrainingsortZuTrainingsId(long trainings_Id) {

        Training training;
        Trainingsort trainingsort;

        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID + "=" + trainings_Id,
                null, null, null, null);


        cursor.moveToFirst();
        training = cursorToTraining(cursor);
        long trainings_ort_id = training.getTrainings_ort_id();

        Cursor cursor1 = database.query(DbHelper.TABLE_TRAININGSORT_DATA,
                trainingsort_data_columns, DbHelper.TRAININGSORT_DATA_COLUMN_DBID + "=" + trainings_ort_id,
                null, null, null, null);

        cursor1.moveToFirst();

        if (!cursor1.isAfterLast())
            trainingsort = cursorToTrainingsort(cursor1);
        else
            trainingsort = new Trainingsort(-999, "Bereits gelöscht", null, null, null, null, -999, -999, 0);
        cursor1.close();

        return trainingsort;
    }

    //Platzkosten zu TrainingsID
    public String getDatumZuTrainingsId(long trainings_Id) {

        Training training;
        String datum;

        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID + "=" + trainings_Id,
                null, null, null, null);


        cursor.moveToFirst();
        training = cursorToTraining(cursor);
        datum = training.getTraining_dtm();

        return datum;
    }


    //Trainingobjekt zu TrainingsID
    public Training getTrainingZuTrainingsId(long trainings_Id) {

        Training training;

        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID + "=" + trainings_Id,
                null, null, null, null);


        cursor.moveToFirst();
        training = cursorToTraining(cursor);

        return training;
    }


//#################################################################################################################################################################--TRAININGSORT--
    //Trainingsort in Datenbank anlegen und gleichzeit als Objekt bereitstellen

    public Trainingsort createTrainingsort(String name, String strasse, String plz, String ort, String foto, double latitude, double longitude, int besuche) {


        ContentValues values = new ContentValues();

        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_NAME, name);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_STRASSE, strasse);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_PLZ, plz);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_ORT, ort);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_FOTO, foto);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_LATITUDE, latitude);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_LONGITUDE, longitude);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_BESUCHE, besuche);

        long insertId = database.insert(DbHelper.TABLE_TRAININGSORT_DATA, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_TRAININGSORT_DATA,
                trainingsort_data_columns, DbHelper.TRAININGSORT_DATA_COLUMN_DBID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Trainingsort trainingsort = cursorToTrainingsort(cursor);
        cursor.close();

        Log.d(LOG_TAG, "DataSource: " + trainingsort.toString() + " erzeugt.");


        return trainingsort;
    }

    // Trainingsort löschen
    public void deleteTrainingsort(Trainingsort trainingsort) {


        long id = trainingsort.getTo_id();

        database.delete(DbHelper.TABLE_TRAININGSORT_DATA,
                DbHelper.TRAININGSORT_DATA_COLUMN_DBID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + trainingsort.toString());
    }

    //Trainingsort updaten gesamt
    public Trainingsort updateTrainingsort(long id, String newName, String newStrasse, String newPlz, String newOrt, String newFoto, double newLatitude, double newLongitude, int newBesuche) {


        ContentValues values = new ContentValues();
        values.put(DbHelper.SPIELER_DATA_COLUMN_NAME, newName);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_STRASSE, newStrasse);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_PLZ, newPlz);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_ORT, newOrt);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_FOTO, newFoto);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_LATITUDE, newLatitude);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_LONGITUDE, newLongitude);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_BESUCHE, newBesuche);


        database.update(DbHelper.TABLE_TRAININGSORT_DATA,
                values,
                DbHelper.TRAININGSORT_DATA_COLUMN_DBID + "=" + id,
                null);

        Cursor cursor = database.query(DbHelper.TABLE_TRAININGSORT_DATA,
                trainingsort_data_columns, DbHelper.TRAININGSORT_DATA_COLUMN_DBID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        Trainingsort trainingsort = cursorToTrainingsort(cursor);
        cursor.close();

        Log.d(LOG_TAG, "DataSource: " + trainingsort.toString() + " Update");

        return trainingsort;
    }

    //Trainingsort Besuche updaten
    public Trainingsort updateBesucheTrainingsort(Trainingsort t) {

        Cursor cursor = database.query(DbHelper.TABLE_TRAININGSORT_DATA,
                trainingsort_data_columns, DbHelper.TRAININGSORT_DATA_COLUMN_DBID + "=" + t.getTo_id(),
                null, null, null, null);

        cursor.moveToFirst();

        int idBesuche = cursor.getColumnIndex(DbHelper.TRAININGSORT_DATA_COLUMN_BESUCHE);
        int besuche = cursor.getInt(idBesuche);
        cursor.close();

        besuche++;

        ContentValues values = new ContentValues();
        values.put(DbHelper.SPIELER_DATA_COLUMN_NAME, t.getName());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_STRASSE, t.getStrasse());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_PLZ, t.getPlz());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_ORT, t.getOrt());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_FOTO, t.getFoto());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_LATITUDE, t.getLatitude());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_LONGITUDE, t.getLongitude());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_BESUCHE, besuche);


        database.update(DbHelper.TABLE_TRAININGSORT_DATA,
                values,
                DbHelper.TRAININGSORT_DATA_COLUMN_DBID + "=" + t.getTo_id(),
                null);

        Cursor cursor1 = database.query(DbHelper.TABLE_TRAININGSORT_DATA,
                trainingsort_data_columns, DbHelper.TRAININGSORT_DATA_COLUMN_DBID + "=" + t.getTo_id(),
                null, null, null, null);

        cursor1.moveToFirst();
        Trainingsort trainingsort = cursorToTrainingsort(cursor1);

        cursor1.close();

        Log.d(LOG_TAG, "DataSource: " + trainingsort.toString() + " Update");


        return trainingsort;
    }

    //Trainingsort Foto updaten
    public Trainingsort updateFotoTrainingsort(Trainingsort t, String newFoto) {

        ContentValues values = new ContentValues();
        values.put(DbHelper.SPIELER_DATA_COLUMN_NAME, t.getName());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_STRASSE, t.getStrasse());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_PLZ, t.getPlz());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_ORT, t.getOrt());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_FOTO, newFoto);
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_LATITUDE, t.getLatitude());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_LONGITUDE, t.getLongitude());
        values.put(DbHelper.TRAININGSORT_DATA_COLUMN_BESUCHE, t.getBesuche());

        database.update(DbHelper.TABLE_TRAININGSORT_DATA,
                values,
                DbHelper.TRAININGSORT_DATA_COLUMN_DBID + "=" + t.getTo_id(),
                null);

        Cursor cursor = database.query(DbHelper.TABLE_TRAININGSORT_DATA,
                trainingsort_data_columns, DbHelper.TRAININGSORT_DATA_COLUMN_DBID + "=" + t.getTo_id(),
                null, null, null, null);

        cursor.moveToFirst();
        Trainingsort trainingsort = cursorToTrainingsort(cursor);
        cursor.close();

        Log.d(LOG_TAG, "DataSource: " + trainingsort.toString() + " Update");

        return trainingsort;
    }
    //Cursor-Objekt in ein Trainingsort-Objekt umwandeln

    private Trainingsort cursorToTrainingsort(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(DbHelper.TRAININGSORT_DATA_COLUMN_DBID);
        int idName = cursor.getColumnIndex(DbHelper.TRAININGSORT_DATA_COLUMN_NAME);
        int idStrasse = cursor.getColumnIndex(DbHelper.TRAININGSORT_DATA_COLUMN_STRASSE);
        int idPlz = cursor.getColumnIndex(DbHelper.TRAININGSORT_DATA_COLUMN_PLZ);
        int idOrt = cursor.getColumnIndex(DbHelper.TRAININGSORT_DATA_COLUMN_ORT);
        int idFoto = cursor.getColumnIndex(DbHelper.TRAININGSORT_DATA_COLUMN_FOTO);
        int idLatitude = cursor.getColumnIndex(DbHelper.TRAININGSORT_DATA_COLUMN_LATITUDE);
        int idLongitude = cursor.getColumnIndex(DbHelper.TRAININGSORT_DATA_COLUMN_LONGITUDE);
        int idBesuche = cursor.getColumnIndex(DbHelper.TRAININGSORT_DATA_COLUMN_BESUCHE);


        long db_id = cursor.getLong(idIndex);
        String name = cursor.getString(idName);
        String strasse = cursor.getString(idStrasse);
        String plz = cursor.getString(idPlz);
        String ort = cursor.getString(idOrt);
        String foto = cursor.getString(idFoto);
        double latitude = cursor.getDouble(idLatitude);
        double longitude = cursor.getDouble(idLongitude);
        int besuche = cursor.getInt(idBesuche);


        return new Trainingsort(db_id, name, strasse, plz, ort, foto, latitude, longitude, besuche);

    }

    public ArrayList<Trainingsort> getBesteTrainingsorte(int anzahl) {


        ArrayList<Trainingsort> trainingsortList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_TRAININGSORT_DATA,
                trainingsort_data_columns, null, null, null, null, DbHelper.TRAININGSORT_DATA_COLUMN_BESUCHE + " DESC");

        cursor.moveToFirst();
        Trainingsort trainingsort;

        while (!cursor.isAfterLast()) {
            trainingsort = cursorToTrainingsort(cursor);
            if (trainingsortList.size() < anzahl)
                trainingsortList.add(trainingsort);
            cursor.moveToNext();
        }

        cursor.close();

        return trainingsortList;
    }

    public ArrayList<Trainingsort> getAllTrainingsort() {


        ArrayList<Trainingsort> trainingsortList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_TRAININGSORT_DATA,
                trainingsort_data_columns, null, null, null, null, null);

        cursor.moveToFirst();
        Trainingsort trainingsort;

        while (!cursor.isAfterLast()) {
            trainingsort = cursorToTrainingsort(cursor);
            trainingsortList.add(trainingsort);
            cursor.moveToNext();
        }

        cursor.close();

        return trainingsortList;
    }

}
