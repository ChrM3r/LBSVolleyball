package eu.merscher.lbsvolleyball.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;


    //SpielerDB
    static final String TABLE_SPIELER_DATA = "spieler_data";
    static final String SPIELER_DATA_COLUMN_S_ID = "s_id";
    static final String SPIELER_DATA_COLUMN_NAME = "name";
    static final String SPIELER_DATA_COLUMN_VNAME = "vname";
    static final String SPIELER_DATA_COLUMN_BDATE = "bdate";
    static final String SPIELER_DATA_COLUMN_TEILNAHMEN = "teilnahmen";
    static final String SPIELER_DATA_COLUMN_FOTO = "foto";
    static final String SPIELER_DATA_COLUMN_MAIL = "mail";
    static final String SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM = "hat_buchung_mm";
    private static final String SPIELER_DATA_SQL_CREATE =
            "CREATE TABLE " + TABLE_SPIELER_DATA +
                    "(" + SPIELER_DATA_COLUMN_S_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SPIELER_DATA_COLUMN_NAME + " TEXT NOT NULL, " +
                    SPIELER_DATA_COLUMN_VNAME + " TEXT NOT NULL, " +
                    SPIELER_DATA_COLUMN_BDATE + " TEXT NOT NULL," +
                    SPIELER_DATA_COLUMN_TEILNAHMEN + " INTEGER NOT NULL," +
                    SPIELER_DATA_COLUMN_FOTO + " TEXT NOT NULL," +
                    SPIELER_DATA_COLUMN_MAIL + " TEXT," +
                    SPIELER_DATA_COLUMN_HAT_BUCHUNG_MM + " CHAR(1));";

    //TrainingDB

    static final String TABLE_TRAINING_DATA = "training_data";
    static final String TRAINING_DATA_COLUMN_DBID = "t_id";
    static final String TRAINING_DATA_COLUMN_TRAINING_ID = "training_id";
    static final String TRAINING_DATA_COLUMN_TRAININGS_DTM = "trainings_dtm";
    static final String TRAINING_DATA_COLUMN_TRAININGS_ORT_ID = "trainings_ort_id";
    static final String TRAINING_DATA_COLUMN_TRAININGS_TN = "trainings_tn";
    static final String TRAINING_DATA_COLUMN_PLATZKOSTEN = "platzkosten";
    static final String TRAINING_DATA_COLUMN_IST_KOSTENLOS_MM = "ist_kostenlos_mm";
    private static final String TRAINING_DATA_SQL_CREATE =
            "CREATE TABLE " + TABLE_TRAINING_DATA +
                    "(" + TRAINING_DATA_COLUMN_DBID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TRAINING_DATA_COLUMN_TRAINING_ID + " INTEGER NOT NULL, " +
                    TRAINING_DATA_COLUMN_TRAININGS_DTM + " TEXT NOT NULL, " +
                    TRAINING_DATA_COLUMN_TRAININGS_ORT_ID + " INTEGER NOT NULL, " +
                    TRAINING_DATA_COLUMN_TRAININGS_TN + " INTEGER NOT NULL," +
                    TRAINING_DATA_COLUMN_PLATZKOSTEN + " DECIMAL(8,2)," +
                    TRAINING_DATA_COLUMN_IST_KOSTENLOS_MM + " CHAR(1)," +
                    "FOREIGN KEY (" + TRAINING_DATA_COLUMN_TRAININGS_TN + ") REFERENCES " + TABLE_SPIELER_DATA + "(" + SPIELER_DATA_COLUMN_S_ID + "));";

    //BuchungDB

    static final String TABLE_BUCHUNG_DATA = "buchung_data";
    static final String BUCHUNG_DATA_COLUMN_BU_ID = "bu_id";
    static final String BUCHUNG_DATA_COLUMN_S_ID = "s_id";
    static final String BUCHUNG_DATA_COLUMN_BUBTR = "bu_btr";
    static final String BUCHUNG_DATA_COLUMN_KTOSLDALT = "kto_saldo_alt";
    static final String BUCHUNG_DATA_COLUMN_KTOSLDNEU = "kto_saldo_neu";
    static final String BUCHUNG_DATA_COLUMN_BU_DATE = "bu_date";
    static final String BUCHUNG_DATA_COLUMN_IST_TRAINING_MM = "ist_training_mm";
    static final String BUCHUNG_DATA_COLUMN_TRAINING_ID = "training_id";
    static final String BUCHUNG_DATA_COLUMN_IST_MANUELL_MM = "ist_manuell_mm";
    static final String BUCHUNG_DATA_COLUMN_IST_TUNIER_MM = "ist_tunier_mm";
    static final String BUCHUNG_DATA_COLUMN_TUNIER_ID = "tunier_id";

    private static final String BUCHUNG_DATA_SQL_CREATE =
            "CREATE TABLE " + TABLE_BUCHUNG_DATA +
                    "(" + BUCHUNG_DATA_COLUMN_BU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BUCHUNG_DATA_COLUMN_S_ID + " INTEGER NOT NULL, " +
                    BUCHUNG_DATA_COLUMN_BUBTR + " DECIMAL(8,2) NOT NULL, " +
                    BUCHUNG_DATA_COLUMN_KTOSLDALT + " DECIMAL(8,2) NOT NULL, " +
                    BUCHUNG_DATA_COLUMN_KTOSLDNEU + " DECIMAL(8,2) NOT NULL," +
                    BUCHUNG_DATA_COLUMN_BU_DATE + " TEXT NOT NULL," +
                    BUCHUNG_DATA_COLUMN_IST_TRAINING_MM + " CHAR(1)," +
                    BUCHUNG_DATA_COLUMN_TRAINING_ID + " INTEGER," +
                    BUCHUNG_DATA_COLUMN_IST_MANUELL_MM + " CHAR(1)," +
                    BUCHUNG_DATA_COLUMN_IST_TUNIER_MM + " CHAR(1)," +
                    BUCHUNG_DATA_COLUMN_TUNIER_ID + " INTEGER," +
                    "FOREIGN KEY (" + BUCHUNG_DATA_COLUMN_S_ID + ") REFERENCES " + TABLE_SPIELER_DATA + "(" + SPIELER_DATA_COLUMN_S_ID + ")," +
                    "FOREIGN KEY (" + BUCHUNG_DATA_COLUMN_TRAINING_ID + ") REFERENCES " + TABLE_TRAINING_DATA + "(" + TRAINING_DATA_COLUMN_TRAINING_ID + "));";


    //TrainingsortDB
    static final String TABLE_TRAININGSORT_DATA = "trainingsort_data";
    static final String TRAININGSORT_DATA_COLUMN_DBID = "to_id";
    static final String TRAININGSORT_DATA_COLUMN_NAME = "name";
    static final String TRAININGSORT_DATA_COLUMN_STRASSE = "strasse";
    static final String TRAININGSORT_DATA_COLUMN_PLZ = "plz";
    static final String TRAININGSORT_DATA_COLUMN_ORT = "ort";
    static final String TRAININGSORT_DATA_COLUMN_FOTO = "foto";
    static final String TRAININGSORT_DATA_COLUMN_LATITUDE = "latitude";
    static final String TRAININGSORT_DATA_COLUMN_LONGITUDE = "longitude";
    static final String TRAININGSORT_DATA_COLUMN_BESUCHE = "besuche";
    private static final String TRAININGSORT_DATA_SQL_CREATE =
            "CREATE TABLE " + TABLE_TRAININGSORT_DATA +
                    "(" + TRAININGSORT_DATA_COLUMN_DBID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TRAININGSORT_DATA_COLUMN_NAME + " TEXT NOT NULL, " +
                    TRAININGSORT_DATA_COLUMN_STRASSE + " TEXT NOT NULL, " +
                    TRAININGSORT_DATA_COLUMN_PLZ + " CHAR(5) NOT NULL," +
                    TRAININGSORT_DATA_COLUMN_ORT + " TEXT NOT NULL," +
                    TRAININGSORT_DATA_COLUMN_FOTO + " TEXT," +
                    TRAININGSORT_DATA_COLUMN_LATITUDE + " DECIMAL(3,8) NOT NULL," +
                    TRAININGSORT_DATA_COLUMN_LONGITUDE + " DECIMAL(3,8) NOT NULL," +
                    TRAININGSORT_DATA_COLUMN_BESUCHE + " INTEGER NOT NULL);";

    private static final String LOG_TAG = DbHelper.class.getSimpleName();


    DbHelper(Context context) {
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
            db.execSQL(TRAININGSORT_DATA_SQL_CREATE);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}


