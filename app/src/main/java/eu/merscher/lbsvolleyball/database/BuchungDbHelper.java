package eu.merscher.lbsvolleyball.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BuchungDbHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "buchung_data.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_BUCHUNG_DATA = "buchung_data";
    public static final String COLUMN_BUID = "_id";
    public static final String COLUMN_UID = "u_id";
    public static final String COLUMN_BUBTR = "bu_btr";
    public static final String COLUMN_KTOSLDALT = "kto_saldo_alt";
    public static final String COLUMN_KTOSLDNEU = "kto_saldo_neu";
    public static final String COLUMN_BU_DATE = "bu_date";
    public static final String COLUMN_IST_TRAINING_MM = "ist_training_mm";
    public static final String COLUMN_IST_MANUELL_MM = "ist_manuell_mm";
    public static final String COLUMN_IST_TUNIER_MM = "ist_tunier_mm";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_BUCHUNG_DATA +
                    "(" + COLUMN_BUID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_UID + " INTEGER NOT NULL, " +
                    COLUMN_BUBTR + " INTEGER NOT NULL, " +
                    COLUMN_KTOSLDALT + " INTEGER NOT NULL, " +
                    COLUMN_KTOSLDNEU + " INTEGER NOT NULL," +
                    COLUMN_BU_DATE + " TEXT NOT NULL," +
                    COLUMN_IST_TRAINING_MM + " TEXT," +
                    COLUMN_IST_MANUELL_MM + " TEXT," +
                    COLUMN_IST_TUNIER_MM + " TEXT);";
    private static final String LOG_TAG = BuchungDbHelper.class.getSimpleName();


    public BuchungDbHelper(Context context) {
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


