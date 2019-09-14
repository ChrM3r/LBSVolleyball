package eu.merscher.lbsvolleyball.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import eu.merscher.lbsvolleyball.model.Buchung;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.model.Training;
import eu.merscher.lbsvolleyball.utilities.Utilities;

public class DataSource {

    private static final String LOG_TAG = DataSource.class.getSimpleName();
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static DataSource instance;
    private final DbHelper dbHelper;
    //SpielerDB
    private final String[] spieler_data_columns = {
            DbHelper.SPIELER_DATA_COLUMN_UID,
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
            DbHelper.BUCHUNG_DATA_COLUMN_BUID,
            DbHelper.BUCHUNG_DATA_COLUMN_UID,
            DbHelper.BUCHUNG_DATA_COLUMN_BUBTR,
            DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDALT,
            DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDNEU,
            DbHelper.BUCHUNG_DATA_COLUMN_BU_DATE,
            DbHelper.BUCHUNG_DATA_COLUMN_IST_TRAINING_MM,
            DbHelper.BUCHUNG_DATA_COLUMN_IST_MANUELL_MM,
            DbHelper.BUCHUNG_DATA_COLUMN_IST_TUNIER_MM
    };

    //TrainingDB
    private final String[] training_data_columns = {
            DbHelper.TRAINING_DATA_COLUMN_DBID,
            DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID,
            DbHelper.TRAINING_DATA_COLUMN_TRAININGS_DTM,
            DbHelper.TRAINING_DATA_COLUMN_TRAININGS_TN,
            DbHelper.TRAINING_DATA_COLUMN_IST_KOSTENLOS_MM
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

    //Buchung in Datenbank anlegen und gleichzeit als Objekt bereitstellen

    public Buchung createBuchung(long u_id, double bu_btr, double kto_saldo_alt, double kto_saldo_neu, String bu_date, String ist_training_mm, String ist_manuell_mm, String ist_tunier_mm) {

        ContentValues values = new ContentValues();

        values.put(DbHelper.BUCHUNG_DATA_COLUMN_UID, u_id);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_BUBTR, bu_btr);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDALT, kto_saldo_alt);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDNEU, kto_saldo_neu);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_BU_DATE, bu_date);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_TRAINING_MM, ist_training_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_MANUELL_MM, ist_manuell_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_TUNIER_MM, ist_tunier_mm);


        long insertId = database.insert(DbHelper.TABLE_BUCHUNG_DATA, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_BUID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Buchung buchung = cursorToBuchung(cursor);
        cursor.close();

        return buchung;
    }

    //Buchung in Datenbank anlegen und gleichzeit als Objekt bereitstellen

    public Buchung createBuchungAufTeamkonto(double bu_btr, double kto_saldo_alt, double kto_saldo_neu, String bu_date, String ist_training_mm, String ist_manuell_mm, String ist_tunier_mm) {

        ContentValues values = new ContentValues();

        values.put(DbHelper.BUCHUNG_DATA_COLUMN_UID, -1);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_BUBTR, bu_btr);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDALT, kto_saldo_alt);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDNEU, kto_saldo_neu);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_BU_DATE, bu_date);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_TRAINING_MM, ist_training_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_MANUELL_MM, ist_manuell_mm);
        values.put(DbHelper.BUCHUNG_DATA_COLUMN_IST_TUNIER_MM, ist_tunier_mm);

        long insertId = database.insert(DbHelper.TABLE_BUCHUNG_DATA, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_BUID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Buchung buchung = cursorToBuchung(cursor);
        cursor.close();

        return buchung;
    }

    //Cursor-Objekt in ein Buchung-Objekt umwandeln

    private Buchung cursorToBuchung(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_BUID);
        int idUid = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_UID);
        int idBu_btr = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_BUBTR);
        int idKto_saldo_alt = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDALT);
        int idKto_saldo_neu = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDNEU);
        int idBu_date = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_BU_DATE);
        int idIst_training_mm = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_IST_TRAINING_MM);
        int idIst_manuell_mm = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_IST_MANUELL_MM);
        int idIst_tunier_mm = cursor.getColumnIndex(DbHelper.BUCHUNG_DATA_COLUMN_IST_TUNIER_MM);

        long u_id = cursor.getLong(idUid);
        double bu_btr = cursor.getDouble(idBu_btr);
        double kto_saldo_alt = cursor.getDouble(idKto_saldo_alt);
        double kto_saldo_neu = cursor.getDouble(idKto_saldo_neu);
        long bu_id = cursor.getLong(idIndex);
        String bu_date = cursor.getString(idBu_date);
        String ist_training_mm = cursor.getString(idIst_training_mm);
        String ist_manuell_mm = cursor.getString(idIst_manuell_mm);
        String ist_tunier_mm = cursor.getString(idIst_tunier_mm);

        return new Buchung(bu_id, u_id, bu_btr, kto_saldo_alt, kto_saldo_neu, bu_date, ist_training_mm, ist_manuell_mm, ist_tunier_mm);


    }
    //Alle Buchung aus Datenbank in eine Liste

    public ArrayList<Buchung> getAllBuchung() {

        ArrayList<Buchung> buchungList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, null, null, null, null, null);

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
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_UID + "=" + spieler.getU_id(),
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BUID + " DESC");

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
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_UID + "=" + spieler.getU_id(),
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BUID);

        Buchung buchung;
        if (cursor.moveToLast()) {
            cursor.moveToLast();
            buchung = cursorToBuchung(cursor);
        } else
            buchung = new Buchung(-999, -999, null, null, null, null, null, null, null);

        cursor.close();

        return buchung;
    }


    //Alle Buchungen eines Spielers aus Datenbank in eine Liste

    public ArrayList<Buchung> getAllBuchungZuTeamkonto() {


        ArrayList<Buchung> BuchungList = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_UID + "=" + -1,
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BUID + " DESC");

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
                buchung_data_columns, DbHelper.BUCHUNG_DATA_COLUMN_UID + "=" + -1,
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BUID);

        cursor.moveToLast();

        Buchung buchung;
        if (cursor.getCount() > 0)
            buchung = cursorToBuchung(cursor);
        else
            buchung = null;


        cursor.close();

        return buchung;
    }

    //Alle Buchungsbeträge einer Person aus der Datenbank in eine Liste in umgekehrter Reihenfolge (neuste oben)

    public ArrayList<String> getAlleBuchungBetragZuSpieler(Spieler spieler) {

        ArrayList<String> buchungBetragList = new ArrayList<>();
        String[] bu_btr_zu_spieler = {DbHelper.BUCHUNG_DATA_COLUMN_BUID, DbHelper.BUCHUNG_DATA_COLUMN_BUBTR};


        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                bu_btr_zu_spieler, DbHelper.BUCHUNG_DATA_COLUMN_UID + "=" + spieler.getU_id(),
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BUID + " DESC");

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
        String[] bu_datum_zu_spieler = {DbHelper.BUCHUNG_DATA_COLUMN_BUID, DbHelper.BUCHUNG_DATA_COLUMN_BU_DATE};

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                bu_datum_zu_spieler, DbHelper.BUCHUNG_DATA_COLUMN_UID + "=" + spieler.getU_id(),
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BUID + " DESC");

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
        String[] kto_saldo_neu_zu_spieler = {DbHelper.BUCHUNG_DATA_COLUMN_BUID, DbHelper.BUCHUNG_DATA_COLUMN_KTOSLDNEU};

        Cursor cursor = database.query(DbHelper.TABLE_BUCHUNG_DATA,
                kto_saldo_neu_zu_spieler, DbHelper.BUCHUNG_DATA_COLUMN_UID + "=" + spieler.getU_id(),
                null, null, null, DbHelper.BUCHUNG_DATA_COLUMN_BUID + " DESC");

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
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_UID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

        return spieler;
    }

    // Spieler löschen
    public void deleteSpieler(Spieler spieler) {


        long id = spieler.getU_id();

        database.delete(DbHelper.TABLE_SPIELER_DATA,
                DbHelper.SPIELER_DATA_COLUMN_UID + "=" + id,
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
                DbHelper.SPIELER_DATA_COLUMN_UID + "=" + id,
                null);

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_UID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

        return spieler;
    }

    //Spieler Teilnahmen updaten
    public Spieler updateTeilnahmenSpieler(Spieler s) {

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_UID + "=" + s.getU_id(),
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
                DbHelper.SPIELER_DATA_COLUMN_UID + "=" + s.getU_id(),
                null);

        Cursor cursor1 = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_UID + "=" + s.getU_id(),
                null, null, null, null);

        cursor1.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor1);

        cursor1.close();

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
                DbHelper.SPIELER_DATA_COLUMN_UID + "=" + s.getU_id(),
                null);

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_UID + "=" + s.getU_id(),
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

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
                DbHelper.SPIELER_DATA_COLUMN_UID + "=" + s.getU_id(),
                null);

        Cursor cursor = database.query(DbHelper.TABLE_SPIELER_DATA,
                spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_UID + "=" + s.getU_id(),
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

        return spieler;
    }

    //Cursor-Objekt in ein Spieler-Objekt umwandeln

    private Spieler cursorToSpieler(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_UID);
        int idName = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_NAME);
        int idVname = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_VNAME);
        int idBdate = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_BDATE);
        int idTeilnahmen = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_TEILNAHMEN);
        int idFoto = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_FOTO);
        int idMail = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_MAIL);
        int idHatBuchungMM = cursor.getColumnIndex(DbHelper.SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM);

        String name = cursor.getString(idName);
        String vname = cursor.getString(idVname);
        String bdate = cursor.getString(idBdate);
        int teilnahmen = cursor.getInt(idTeilnahmen);
        long u_id = cursor.getLong(idIndex);
        String foto = cursor.getString(idFoto);
        String mail = cursor.getString(idMail);
        String hat_buchung_mm = cursor.getString(idHatBuchungMM);

        return new Spieler(u_id, name, vname, bdate, teilnahmen, foto, mail, hat_buchung_mm);

    }
    //Alle Spieler aus Datenbank in eine Liste

    public ArrayList<Spieler> getAllSpieler() {


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

        return spielerList;
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

    public Training createTraining(long trainings_id, String trainings_dtm, long trainings_tn, String ist_kostenlos_mm) {

        ContentValues values = new ContentValues();

        values.put(DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID, trainings_id);
        values.put(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_DTM, trainings_dtm);
        values.put(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_TN, trainings_tn);
        values.put(DbHelper.TRAINING_DATA_COLUMN_IST_KOSTENLOS_MM, ist_kostenlos_mm);


        long insertId = database.insert(DbHelper.TABLE_TRAINING_DATA, null, values);

        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, DbHelper.TRAINING_DATA_COLUMN_DBID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Training training = cursorToTraining(cursor);
        cursor.close();

        return training;
    }

    //Cursor-Objekt in ein Buchung-Objekt umwandeln

    private Training cursorToTraining(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_DBID);
        int idTrainings_id = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID);
        int idTrainings_dtm = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_DTM);
        int idTrainings_ort = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_ORT);
        int idKTrainings_tn = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_TRAININGS_TN);
        int idIst_kostenlos_mm = cursor.getColumnIndex(DbHelper.TRAINING_DATA_COLUMN_IST_KOSTENLOS_MM);

        long db_id = cursor.getLong(idIndex);
        long trainings_id = cursor.getLong(idTrainings_id);
        String trainings_dtm = cursor.getString(idTrainings_dtm);
        String trainings_ort = cursor.getString(idTrainings_ort);
        long trainings_tn = cursor.getLong(idKTrainings_tn);
        String ist_kostenlos_mm = cursor.getString(idIst_kostenlos_mm);


        return new Training(db_id, trainings_id, trainings_dtm, trainings_ort, trainings_tn, ist_kostenlos_mm);


    }

    public long getNeustesTrainingsID() {


        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, null,
                null, null, null, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID);

        Training training;
        if (cursor.moveToLast()) {
            cursor.moveToLast();
            training = cursorToTraining(cursor);
        } else
            training = new Training(-999, -999, null, null, -999, null);

        cursor.close();

        return training.getTraining_id();
    }

    public ArrayList<Spieler> getSpielerZuTrainingsId(long trainings_Id) {

        String Id = String.valueOf(trainings_Id);
        ArrayList<Spieler> trainings_teilnehmer = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TABLE_TRAINING_DATA,
                training_data_columns, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID + "=" + Id,
                null, null, null, DbHelper.TRAINING_DATA_COLUMN_TRAINING_ID);

        Training training;
        ArrayList<Training> trainingList = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            training = cursorToTraining(cursor);
            trainingList.add(training);
            cursor.moveToNext();
        }

        for (Training t : trainingList) {

            Cursor cursor_spieler = database.query(DbHelper.TABLE_SPIELER_DATA,
                    spieler_data_columns, DbHelper.SPIELER_DATA_COLUMN_UID + "=" + t.getTrainings_tn(),
                    null, null, null, null);

            cursor_spieler.moveToFirst();
            Spieler spieler = cursorToSpieler(cursor_spieler);

            trainings_teilnehmer.add(spieler);
        }

        return trainings_teilnehmer;
    }

}
