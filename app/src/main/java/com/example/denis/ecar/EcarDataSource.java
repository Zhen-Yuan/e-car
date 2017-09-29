package com.example.denis.ecar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raja on 25.06.2017.
 */

public class EcarDataSource {

    private static final String LOG_TAG = EcarDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private EcarDbHelper dbHelper;

    private String[] columnsdata = {
            EcarDbHelper.COLUMN_DATA_ID,
            EcarDbHelper.COLUMN_DATA_DATA,
            EcarDbHelper.COLUMN_DATA_TIME,
            EcarDbHelper.COLUMN_SESSION_ID,
            EcarDbHelper.COLUMN_VALUES_ID
    };
    private String[] columnssession = {
            EcarDbHelper.COLUMN_SESSION_ID,
            EcarDbHelper.COLUMN_USER_ID,
            EcarDbHelper.COLUMN_SESSION_NAME
    };

    public EcarDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new EcarDbHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
        //Kurzes Einfügen von Daten um die tieferen Tabellen testen zu können
        //Diese Einträge sollten in der finalen Version im EcarDbHelper OnCreate stehen.
        /*try {
            database.execSQL("INSERT INTO "+dbHelper.TABLE_USER+" ("+dbHelper.COLUMN_USER_ID+","+dbHelper.COLUMN_USER_NAME+")\n" +
                    "VALUES (1, 'TestUser');");
            database.execSQL("INSERT INTO "+dbHelper.TABLE_VALUES+" ("+dbHelper.COLUMN_VALUES_ID+","+dbHelper.COLUMN_VALUES_NAME+")\n" +
                    "VALUES (1, 'Latitude');");
            database.execSQL("INSERT INTO "+dbHelper.TABLE_VALUES+" ("+dbHelper.COLUMN_VALUES_ID+","+dbHelper.COLUMN_VALUES_NAME+")\n" +
                    "VALUES (2, 'Longitude');");
        }
        catch (Exception ex) {
            Log.e("Fehler", "Fehler beim Befehl: " + ex.getMessage());
        }*/
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    public EcarSession createEcarSession(int uid, String name){
        ContentValues values = new ContentValues();
        values.put(EcarDbHelper.COLUMN_USER_ID, uid);
        values.put(EcarDbHelper.COLUMN_SESSION_NAME, name);

        long insertId = database.insert(EcarDbHelper.TABLE_SESSION, null, values);

        Cursor cursor = database.query(EcarDbHelper.TABLE_SESSION,
                columnssession, EcarDbHelper.COLUMN_SESSION_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        EcarSession ecarsession = cursorToEcarSession(cursor);
        cursor.close();
        return ecarsession;
    }

    private EcarSession cursorToEcarSession(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(EcarDbHelper.COLUMN_SESSION_ID);
        int idUserIndex = cursor.getColumnIndex(EcarDbHelper.COLUMN_USER_ID);
        int idName = cursor.getColumnIndex(EcarDbHelper.COLUMN_SESSION_NAME);

        long id = cursor.getLong(idIndex);
        int userid = cursor.getInt(idUserIndex);
        String name = cursor.getString(idName);

        EcarSession ecarses = new EcarSession(userid, name);
        ecarses.setSesid((int)id);

        return ecarses;
    }

    public EcarData createEcarData(double data, int session, int value) {
        ContentValues values = new ContentValues();
        values.put(EcarDbHelper.COLUMN_DATA_DATA, data);
        values.put(EcarDbHelper.COLUMN_SESSION_ID, session);
        values.put(EcarDbHelper.COLUMN_VALUES_ID, value);

        long insertId = database.insert(EcarDbHelper.TABLE_DATA, null, values);

        Cursor cursor = database.query(EcarDbHelper.TABLE_DATA,
                columnsdata, EcarDbHelper.COLUMN_DATA_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        EcarData ecardata = cursorToEcarData(cursor);
        cursor.close();

        return ecardata;
    }

    public void deleteEcarSession(EcarSession ecarSes) {
        int id = ecarSes.getSesid();

        database.delete(EcarDbHelper.TABLE_DATA,
                EcarDbHelper.COLUMN_SESSION_ID + "=" + id,
                null);

        database.delete(EcarDbHelper.TABLE_SESSION,
                EcarDbHelper.COLUMN_SESSION_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! Inhalt: " + ecarSes.toString());
    }

    private EcarData cursorToEcarData(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(EcarDbHelper.COLUMN_DATA_ID);
        int idData = cursor.getColumnIndex(EcarDbHelper.COLUMN_DATA_DATA);
        int idTime = cursor.getColumnIndex(EcarDbHelper.COLUMN_DATA_TIME);
        int idSession = cursor.getColumnIndex(EcarDbHelper.COLUMN_SESSION_ID);
        int idValues = cursor.getColumnIndex(EcarDbHelper.COLUMN_VALUES_ID);

        long id = cursor.getLong(idIndex);
        double data = cursor.getDouble(idData);
        int time = cursor.getInt(idTime);
        int session = cursor.getInt(idSession);
        int values = cursor.getInt(idValues);

        EcarData ecardata = new EcarData(data, session , values);
        ecardata.setDid((int)id);
        ecardata.setTime(time);

        return ecardata;
    }

    public void deleteEcarData(EcarData ecarData) {
        int id = ecarData.getDid();

        database.delete(EcarDbHelper.TABLE_DATA,
                EcarDbHelper.COLUMN_DATA_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! Inhalt: " + ecarData.toString());
    }

    public List<EcarData> getAllEcarData() {
        List<EcarData> ecarDataList = new ArrayList<>();

        Cursor cursor = database.query(EcarDbHelper.TABLE_DATA,
                columnsdata, null, null, null, null, null);

        cursor.moveToFirst();
        EcarData ecardata;

        while(!cursor.isAfterLast()) {
            ecardata = cursorToEcarData(cursor);
            ecarDataList.add(ecardata);
            Log.d(LOG_TAG, "Inhalt: " + ecardata.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return ecarDataList;
    }
    public List<EcarData> getSpecificEcarData(int session,int value) {
        List<EcarData> ecarDataList = new ArrayList<>();

        String whereClause = null;
        String[] whereArgs = null;
        if(session == 0 && value != 0){
            whereClause = EcarDbHelper.COLUMN_VALUES_ID + " = ?";
            whereArgs = new String[]{
                    "" + value
            };
        }
        if(session != 0 && value == 0){
            whereClause = EcarDbHelper.COLUMN_SESSION_ID + " = ?";
            whereArgs = new String[]{
                    "" + session
            };
        }
        if(session !=0 && value != 0) {
            whereClause = EcarDbHelper.COLUMN_SESSION_ID + " = ? AND " + EcarDbHelper.COLUMN_VALUES_ID + " = ?";
            whereArgs = new String[]{
                    "" + session,
                    "" + value
            };
        }

        Cursor cursor = database.query(EcarDbHelper.TABLE_DATA,
                columnsdata, whereClause, whereArgs, null, null, null);

        cursor.moveToFirst();
        EcarData ecardata;

        while(!cursor.isAfterLast()) {
            ecardata = cursorToEcarData(cursor);
            ecarDataList.add(ecardata);
            Log.d(LOG_TAG, "Inhalt: " + ecardata.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return ecarDataList;
    }

    public List<EcarSession> getAllEcarSession() {
        List<EcarSession> ecarSessionList = new ArrayList<>();

        Cursor cursor = database.query(EcarDbHelper.TABLE_SESSION,
                columnssession, null, null, null, null, null);

        cursor.moveToFirst();
        EcarSession ecarsession;

        while (!cursor.isAfterLast()) {
            ecarsession = cursorToEcarSession(cursor);
            ecarSessionList.add(ecarsession);
            Log.d(LOG_TAG, "Inhalt: " + ecarsession.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return ecarSessionList;
    }
}
