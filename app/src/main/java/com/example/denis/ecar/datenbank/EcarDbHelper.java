package com.example.denis.ecar.datenbank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EcarDbHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = EcarDbHelper.class.getSimpleName();

    public static final String DB_NAME = "ecaRnn.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_USER = "nutzer";
    public static final String TABLE_SETTINGS = "einstellung";
    public static final String TABLE_SESSION = "sitzung";
    public static final String TABLE_DATA = "daten";
    public static final String TABLE_VALUES = "werte";

    public static final String COLUMN_USER_ID = "uid";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PICTURE = "profilbild";

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

    public static final String SQL_CREATE_USER =
            "CREATE TABLE " + TABLE_USER +
                    "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_SETTINGS_ID + " INTEGER, " +
                    COLUMN_USER_NAME + " TEXT NOT NULL, " +
                    COLUMN_USER_EMAIL + " TEXT NOT NULL, " +
                    COLUMN_USER_PICTURE + " BLOB, " +
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
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}