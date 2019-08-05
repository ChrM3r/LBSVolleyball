package eu.merscher.lbsvolleyball.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SpielerDbHelper extends SQLiteOpenHelper {


    protected static final String DB_NAME = "spieler_data.db";
    protected static final int DB_VERSION = 1;
    protected static final String TABLE_SPIELER_DATA = "spieler_data";
    protected static final String COLUMN_UID = "_id";
    protected static final String COLUMN_NAME = "name";
    protected static final String COLUMN_VNAME = "vname";
    protected static final String COLUMN_BDATE = "bdate";
    protected static final String COLUMN_TEILNAHMEN = "teilnahmen";
    protected static final String COLUMN_FOTO = "foto";
    protected static final String COLUMN_MAIL = "mail";
    protected static final String COLUMN_HAT_BUCHUNG_MM = "hat_buchung_mm";
    protected static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_SPIELER_DATA +
                    "(" + COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_VNAME + " TEXT NOT NULL, " +
                    COLUMN_BDATE + " TEXT NOT NULL," +
                    COLUMN_TEILNAHMEN + " INTEGER NOT NULL," +
                    COLUMN_FOTO + " TEXT NOT NULL," +
                    COLUMN_MAIL + " TEXT," +
                    COLUMN_HAT_BUCHUNG_MM + " TEXT);";
    private static final String LOG_TAG = SpielerDbHelper.class.getSimpleName();


    public SpielerDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    // Die onCreate-Methode wird nur aufgerufen, falls die Datenbank noch nicht existiert
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
