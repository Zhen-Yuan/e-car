package com.example.denis.ecar.datenbank;//..

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EcarDbHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = EcarDbHelper.class.getSimpleName();

    public static final String DB_NAME = "nr55.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_USER = "nutzer";
    public static final String TABLE_SETTINGS = "einstellung";
    public static final String TABLE_SESSION = "sitzung";
    public static final String TABLE_DATA = "daten";
    public static final String TABLE_VALUES = "werte";
    public static final String TABLE_FUEL = "treibstoffe";
    public static final String TABLE_CAR = "autos";

    public static final String COLUMN_USER_ID = "uid";
    public static final String COLUMN_USER_FIREBASE = "firebaseid";

    public static final String COLUMN_SETTINGS_ID = "setid";
    public static final String COLUMN_SETTINGS_CONSUMPTION = "verbrauch";

    public static final String COLUMN_SESSION_ID = "sesid";
    public static final String COLUMN_SESSION_NAME = "name";
    public static final String COLUMN_SESSION_EVALUATION = "ausgewertet";

    public static final String COLUMN_DATA_ID = "did";
    public static final String COLUMN_DATA_DATA = "daten";
    public static final String COLUMN_DATA_TIME = "zeit";

    public static final String COLUMN_VALUES_ID = "vid";
    public static final String COLUMN_VALUES_NAME = "name";

    public static final String COLUMN_CAR_ID = "cid";
    public static final String COLUMN_CAR_NAME = "autoname";
    public static final String COLUMN_CAR_MANUFACTURER = "hersteller";
    public static final String COLUMN_CAR_DESC = "beschreibung";
    public static final String COLUMN_CAR_EMISSIONS = "emissionen";
    public static final String COLUMN_CAR_CONSUMPTION = "verbrauch";
    public static final String COLUMN_CAR_RANGE = "reichweite";
    public static final String COLUMN_CAR_POWERSTOREAGE = "batterie";
    public static final String COLUMN_CAR_PICTURE = "autobild";

    public static final String COLUMN_FUEL_ID = "fid";
    public static final String COLUMN_FUEL_NAME = "treibstoffart";

    public static final String SQL_CREATE_USER =
            "CREATE TABLE " + TABLE_USER +
                    "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_SETTINGS_ID + " INTEGER, " +
                    COLUMN_USER_FIREBASE + " TEXT NOT NULL, " +
					"FOREIGN KEY("+COLUMN_SETTINGS_ID+") REFERENCES "+TABLE_SETTINGS+"("+COLUMN_SETTINGS_ID+"));";

    public static final String SQL_CREATE_SETTINGS =
            "CREATE TABLE " + TABLE_SETTINGS+
                    "(" + COLUMN_SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SETTINGS_CONSUMPTION + " REAL NOT NULL);";

    public static final String SQL_CREATE_SESSION =
            "CREATE TABLE " + TABLE_SESSION +
                    "(" + COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_SESSION_NAME + " TEXT NOT NULL, " +
                    COLUMN_SESSION_EVALUATION + " INTEGER, " +
                    "FOREIGN KEY("+COLUMN_USER_ID+") REFERENCES "+TABLE_USER+"("+COLUMN_USER_ID+"));";

    public static final String SQL_CREATE_DATA =
            "CREATE TABLE " + TABLE_DATA +
                    "(" + COLUMN_DATA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATA_DATA + " REAL NOT NULL, " +
                    COLUMN_DATA_TIME + " TIMESTAMP DEFAULT (strftime('%s','now')), " +
                    COLUMN_SESSION_ID + " INTEGER, " +
                    COLUMN_VALUES_ID + " INTEGER, " +
                    "FOREIGN KEY("+COLUMN_SESSION_ID+") REFERENCES "+TABLE_SESSION+"("+COLUMN_SESSION_ID+"), "+
                    "FOREIGN KEY("+COLUMN_VALUES_ID+") REFERENCES "+TABLE_VALUES+"("+COLUMN_VALUES_ID+"));";

    public static final String SQL_CREATE_VALUES =
            "CREATE TABLE " + TABLE_VALUES +
                    "(" + COLUMN_VALUES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_VALUES_NAME + " TEXT NOT NULL);";

    public static final String SQL_CREATE_FUEL =
            "CREATE TABLE " + TABLE_FUEL+
                    "(" + COLUMN_FUEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FUEL_NAME + " TEXT NOT NULL);";

    public static final String SQL_CREATE_CAR =
            "CREATE TABLE " + TABLE_CAR +
                    "(" + COLUMN_CAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CAR_NAME + " TEXT NOT NULL, " +
                    COLUMN_CAR_MANUFACTURER + " TEXT, " +
                    COLUMN_CAR_DESC + " TEXT, " +
                    COLUMN_CAR_EMISSIONS + " REAL, " +
                    COLUMN_CAR_CONSUMPTION + " REAL, " +
                    COLUMN_CAR_RANGE + " REAL, " +
                    COLUMN_CAR_POWERSTOREAGE + " REAL, "+
                    COLUMN_CAR_PICTURE + " BLOB, " +
                    COLUMN_FUEL_ID + " INTEGER, " +
                    "FOREIGN KEY("+COLUMN_FUEL_ID+") REFERENCES "+TABLE_FUEL+"("+COLUMN_FUEL_ID+"));";

    public EcarDbHelper(Context context) {
        //super(context, "PLATZHALTER_DATENBANKNAME", null, 1);
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_USER + " angelegt.");
            db.execSQL(SQL_CREATE_USER);
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_SETTINGS + " angelegt.");
            db.execSQL(SQL_CREATE_SETTINGS);
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_SESSION + " angelegt.");
            db.execSQL(SQL_CREATE_SESSION);
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_VALUES + " angelegt.");
            db.execSQL(SQL_CREATE_VALUES);
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_DATA + " angelegt.");
            db.execSQL(SQL_CREATE_DATA);
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_FUEL + " angelegt.");
            db.execSQL(SQL_CREATE_FUEL);
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_CAR + " angelegt.");
            db.execSQL(SQL_CREATE_CAR);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
        //Kurzes Einfügen von Daten um die tieferen Tabellen testen zu können
        try {
            Log.d(LOG_TAG, "Datensatz wird zum Testen in " + TABLE_SETTINGS + " angelegt.");
            db.execSQL("INSERT INTO "+TABLE_SETTINGS+" ("+COLUMN_SETTINGS_ID+","+COLUMN_SETTINGS_CONSUMPTION+")\n" +
                    "VALUES (1, 5);");
            Log.d(LOG_TAG, "Datensatz wird zum Testen in " + TABLE_USER + " angelegt.");
            db.execSQL("INSERT INTO "+TABLE_USER+" ("+COLUMN_USER_ID+","+COLUMN_SESSION_ID+","+COLUMN_USER_FIREBASE+")\n" +
                    "VALUES (1, 1,'Platzhalter');");

            Log.d(LOG_TAG, "Datensätze werden zum Testen in " + TABLE_VALUES + " angelegt.");
            db.execSQL("INSERT INTO "+TABLE_VALUES+" ("+COLUMN_VALUES_ID+","+COLUMN_VALUES_NAME+")\n" +
                    "VALUES (1, 'Latitude');");
            db.execSQL("INSERT INTO "+TABLE_VALUES+" ("+COLUMN_VALUES_ID+","+COLUMN_VALUES_NAME+")\n" +
                    "VALUES (2, 'Longitude');");
            Log.d(LOG_TAG, "Datensätze werden zum Testen in " + TABLE_FUEL + " angelegt.");
            db.execSQL("INSERT INTO "+TABLE_FUEL+" ("+COLUMN_FUEL_ID+","+COLUMN_FUEL_NAME+")\n" +
                    "VALUES (1, 'Benzin');");
            db.execSQL("INSERT INTO "+TABLE_FUEL+" ("+COLUMN_FUEL_ID+","+COLUMN_FUEL_NAME+")\n" +
                    "VALUES (2, 'Diesel');");
            db.execSQL("INSERT INTO "+TABLE_FUEL+" ("+COLUMN_FUEL_ID+","+COLUMN_FUEL_NAME+")\n" +
                    "VALUES (3, 'Elektro');");
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Befehl: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}