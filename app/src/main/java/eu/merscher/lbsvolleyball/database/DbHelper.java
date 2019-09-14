package eu.merscher.lbsvolleyball.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "data.db";
    public static final int DB_VERSION = 1;
    public static final String BUCHUNG_DATA_COLUMN_BUID = "_id";


    //BuchungDB

    public static final String TABLE_BUCHUNG_DATA = "buchung_data";
    public static final String BUCHUNG_DATA_COLUMN_UID = "u_id";
    public static final String BUCHUNG_DATA_COLUMN_BUBTR = "bu_btr";
    public static final String BUCHUNG_DATA_COLUMN_KTOSLDALT = "kto_saldo_alt";
    public static final String BUCHUNG_DATA_COLUMN_KTOSLDNEU = "kto_saldo_neu";
    public static final String BUCHUNG_DATA_COLUMN_BU_DATE = "bu_date";
    public static final String BUCHUNG_DATA_COLUMN_IST_TRAINING_MM = "ist_training_mm";
    public static final String BUCHUNG_DATA_COLUMN_IST_MANUELL_MM = "ist_manuell_mm";
    public static final String BUCHUNG_DATA_COLUMN_IST_TUNIER_MM = "ist_tunier_mm";
    public static final String BUCHUNG_DATA_SQL_CREATE =
            "CREATE TABLE " + TABLE_BUCHUNG_DATA +
                    "(" + BUCHUNG_DATA_COLUMN_BUID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BUCHUNG_DATA_COLUMN_UID + " INTEGER NOT NULL, " +
                    BUCHUNG_DATA_COLUMN_BUBTR + " INTEGER NOT NULL, " +
                    BUCHUNG_DATA_COLUMN_KTOSLDALT + " INTEGER NOT NULL, " +
                    BUCHUNG_DATA_COLUMN_KTOSLDNEU + " INTEGER NOT NULL," +
                    BUCHUNG_DATA_COLUMN_BU_DATE + " TEXT NOT NULL," +
                    BUCHUNG_DATA_COLUMN_IST_TRAINING_MM + " TEXT," +
                    BUCHUNG_DATA_COLUMN_IST_MANUELL_MM + " TEXT," +
                    BUCHUNG_DATA_COLUMN_IST_TUNIER_MM + " TEXT);";
    public static final String TABLE_TRAINING_DATA = "training_data";


    //SpielerDB
    public static final String TRAINING_DATA_COLUMN_DBID = "_id";
    public static final String TRAINING_DATA_COLUMN_TRAINING_ID = "training_id";
    public static final String TRAINING_DATA_COLUMN_TRAININGS_DTM = "trainings_dtm";
    public static final String TRAINING_DATA_COLUMN_TRAININGS_ORT = "trainings_ort";
    public static final String TRAINING_DATA_COLUMN_TRAININGS_TN = "trainings_tn";
    public static final String TRAINING_DATA_COLUMN_IST_KOSTENLOS_MM = "ist_kostenlos_mm";
    protected static final String TABLE_SPIELER_DATA = "spieler_data";
    protected static final String SPIELER_DATA_COLUMN_UID = "_id";
    protected static final String SPIELER_DATA_COLUMN_NAME = "name";
    protected static final String SPIELER_DATA_COLUMN_VNAME = "vname";

    //TrainingDB
    protected static final String SPIELER_DATA_COLUMN_BDATE = "bdate";
    protected static final String SPIELER_DATA_COLUMN_TEILNAHMEN = "teilnahmen";
    protected static final String SPIELER_DATA_COLUMN_FOTO = "foto";
    protected static final String SPIELER_DATA_COLUMN_MAIL = "mail";
    protected static final String SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM = "hat_buchung_mm";
    protected static final String SPIELER_DATA_SQL_CREATE =
            "CREATE TABLE " + TABLE_SPIELER_DATA +
                    "(" + SPIELER_DATA_COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SPIELER_DATA_COLUMN_NAME + " TEXT NOT NULL, " +
                    SPIELER_DATA_COLUMN_VNAME + " TEXT NOT NULL, " +
                    SPIELER_DATA_COLUMN_BDATE + " TEXT NOT NULL," +
                    SPIELER_DATA_COLUMN_TEILNAHMEN + " INTEGER NOT NULL," +
                    SPIELER_DATA_COLUMN_FOTO + " TEXT NOT NULL," +
                    SPIELER_DATA_COLUMN_MAIL + " TEXT," +
                    SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM + " TEXT);";
    protected static final String TRAINING_DATA_SQL_CREATE =
            "CREATE TABLE " + TABLE_TRAINING_DATA +
                    "(" + TRAINING_DATA_COLUMN_DBID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TRAINING_DATA_COLUMN_TRAINING_ID + " INTEGER NOT NULL, " +
                    TRAINING_DATA_COLUMN_TRAININGS_DTM + " TEXT NOT NULL, " +
                    TRAINING_DATA_COLUMN_TRAININGS_ORT + " TEXT NOT NULL, " +
                    TRAINING_DATA_COLUMN_TRAININGS_TN + " INTEGER NOT NULL," +
                    TRAINING_DATA_COLUMN_IST_KOSTENLOS_MM + " TEXT);";
    private static final String LOG_TAG = DbHelper.class.getSimpleName();


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    // Die onCreate-Methode wird nur aufgerufen, falls die Datenbank noch nicht existiert
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(BUCHUNG_DATA_SQL_CREATE);
            db.execSQL(SPIELER_DATA_SQL_CREATE);
            db.execSQL(TRAINING_DATA_SQL_CREATE);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}


