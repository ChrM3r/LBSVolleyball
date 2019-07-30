package eu.merscher.lbsvolleyball;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;

public class SpielerDataSource {

    private static final String LOG_TAG = SpielerDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private SpielerDbHelper dbHelper;

    //Array mit allen Spaltennamen der Tabelle
    private String[] columns = {
            SpielerDbHelper.COLUMN_UID,
            SpielerDbHelper.COLUMN_NAME,
            SpielerDbHelper.COLUMN_VNAME,
            SpielerDbHelper.COLUMN_BDATE,
            SpielerDbHelper.COLUMN_TEILNAHMEN,
            SpielerDbHelper.COLUMN_FOTO,
            SpielerDbHelper.COLUMN_HAT_BUCHUNG_MM
    };

    public SpielerDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new SpielerDbHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    //Spieler in Datenbank anlegen und gleichzeit als Objekt bereitstellen

    public Spieler createSpieler(String name, String vname, String bdate, int teilnahmen, String foto, String hat_buchung_mm) {

        open();

        ContentValues values = new ContentValues();

        values.put(SpielerDbHelper.COLUMN_NAME, name);
        values.put(SpielerDbHelper.COLUMN_VNAME, vname);
        values.put(SpielerDbHelper.COLUMN_BDATE, bdate);
        values.put(SpielerDbHelper.COLUMN_TEILNAHMEN, teilnahmen);
        values.put(SpielerDbHelper.COLUMN_FOTO, foto);
        values.put(SpielerDbHelper.COLUMN_HAT_BUCHUNG_MM, hat_buchung_mm);

        long insertId = database.insert(SpielerDbHelper.TABLE_SPIELER_DATA, null, values);

        Cursor cursor = database.query(SpielerDbHelper.TABLE_SPIELER_DATA,
                columns, SpielerDbHelper.COLUMN_UID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

        return spieler;
    }

    // Spieler löschen
    public void deleteSpieler(Spieler spieler) {

        open();

        long id = spieler.getU_id();

        database.delete(SpielerDbHelper.TABLE_SPIELER_DATA,
                SpielerDbHelper.COLUMN_UID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + spieler.toString());
    }

    //Spieler updaten gesamt
    public Spieler updateSpieler(long id, String newName, String newVname, String newBdate, int newTeilnahmen, String foto, String hat_buchung_mm) {

        open();

        ContentValues values = new ContentValues();
        values.put(SpielerDbHelper.COLUMN_NAME, newName);
        values.put(SpielerDbHelper.COLUMN_VNAME, newVname);
        values.put(SpielerDbHelper.COLUMN_BDATE, newBdate);
        values.put(SpielerDbHelper.COLUMN_TEILNAHMEN, newTeilnahmen);
        values.put(SpielerDbHelper.COLUMN_FOTO, foto);
        values.put(SpielerDbHelper.COLUMN_HAT_BUCHUNG_MM, hat_buchung_mm);


        database.update(SpielerDbHelper.TABLE_SPIELER_DATA,
                values,
                SpielerDbHelper.COLUMN_UID + "=" + id,
                null);

        Cursor cursor = database.query(SpielerDbHelper.TABLE_SPIELER_DATA,
                columns, SpielerDbHelper.COLUMN_UID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

        return spieler;
    }
    //Spieler Teilnahmen updaten
    public Spieler updateTeilnahmenSpieler(Spieler s, int newTeilnahmen) {

        open();

        ContentValues values = new ContentValues();
        values.put(SpielerDbHelper.COLUMN_NAME, s.getName());
        values.put(SpielerDbHelper.COLUMN_VNAME, s.getVname());
        values.put(SpielerDbHelper.COLUMN_BDATE, s.getBdate());
        values.put(SpielerDbHelper.COLUMN_FOTO, s.getFoto());
        values.put(SpielerDbHelper.COLUMN_HAT_BUCHUNG_MM, s.getHat_buchung_mm());
        values.put(SpielerDbHelper.COLUMN_TEILNAHMEN, newTeilnahmen);


        database.update(SpielerDbHelper.TABLE_SPIELER_DATA,
                values,
                SpielerDbHelper.COLUMN_UID + "=" + s.getU_id(),
                null);

        Cursor cursor = database.query(SpielerDbHelper.TABLE_SPIELER_DATA,
                columns, SpielerDbHelper.COLUMN_UID + "=" + s.getU_id(),
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

        return spieler;
    }

    //Spieler Foto updaten
    public Spieler updateFotoSpieler(Spieler s, String newFoto) {

        open();

        ContentValues values = new ContentValues();
        values.put(SpielerDbHelper.COLUMN_NAME, s.getName());
        values.put(SpielerDbHelper.COLUMN_VNAME, s.getVname());
        values.put(SpielerDbHelper.COLUMN_BDATE, s.getBdate());
        values.put(SpielerDbHelper.COLUMN_TEILNAHMEN, s.getTeilnahmen());
        values.put(SpielerDbHelper.COLUMN_HAT_BUCHUNG_MM, s.getHat_buchung_mm());
        values.put(SpielerDbHelper.COLUMN_FOTO, newFoto);


        database.update(SpielerDbHelper.TABLE_SPIELER_DATA,
                values,
                SpielerDbHelper.COLUMN_UID + "=" + s.getU_id(),
                null);

        Cursor cursor = database.query(SpielerDbHelper.TABLE_SPIELER_DATA,
                columns, SpielerDbHelper.COLUMN_UID + "=" + s.getU_id(),
                null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler = cursorToSpieler(cursor);
        cursor.close();

        return spieler;
    }
    //Cursor-Objekt in ein Spieler-Objekt umwandeln

    private Spieler cursorToSpieler(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(SpielerDbHelper.COLUMN_UID);
        int idName = cursor.getColumnIndex(SpielerDbHelper.COLUMN_NAME);
        int idVname = cursor.getColumnIndex(SpielerDbHelper.COLUMN_VNAME);
        int idBdate = cursor.getColumnIndex(SpielerDbHelper.COLUMN_BDATE);
        int idTeilnahmen = cursor.getColumnIndex(SpielerDbHelper.COLUMN_TEILNAHMEN);
        int idFoto = cursor.getColumnIndex(SpielerDbHelper.COLUMN_FOTO);
        int idHatBuchungMM = cursor.getColumnIndex(SpielerDbHelper.COLUMN_HAT_BUCHUNG_MM);

        String name = cursor.getString(idName);
        String vname = cursor.getString(idVname);
        String bdate = cursor.getString(idBdate);
        int teinahmen = cursor.getInt(idTeilnahmen);
        long u_id = cursor.getLong(idIndex);
        String foto = cursor.getString(idFoto);
        String hat_buchung_mm = cursor.getString(idHatBuchungMM);

        return new Spieler(u_id, name, vname, bdate, teinahmen, foto, hat_buchung_mm);

    }
    //Alle Spieler aus Datenbank in eine Liste

    public ArrayList<Spieler> getAllSpieler() {

        open();

        ArrayList<Spieler> spielerList = new ArrayList<>();

        Cursor cursor = database.query(SpielerDbHelper.TABLE_SPIELER_DATA,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        Spieler spieler;

        while(!cursor.isAfterLast()) {
            spieler = cursorToSpieler(cursor);
            spielerList.add(spieler);
            cursor.moveToNext();
        }

        cursor.close();

        return spielerList;
    }
}
