package com.example.denis.ecar.datenbank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
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
            EcarDbHelper.COLUMN_SESSION_NAME,
            EcarDbHelper.COLUMN_SESSION_EVALUATION
    };
    private String[] columnsuser = {
            EcarDbHelper.COLUMN_USER_ID,
            EcarDbHelper.COLUMN_USER_NAME,
            EcarDbHelper.COLUMN_USER_EMAIL,
            EcarDbHelper.COLUMN_USER_PICTURE,
            EcarDbHelper.COLUMN_SETTINGS_ID
    };
    private String[] columnssettings = {
            EcarDbHelper.COLUMN_SETTINGS_ID
    };
    private String[] columnscar = {
            EcarDbHelper.COLUMN_CAR_ID,
            EcarDbHelper.COLUMN_CAR_NAME,
            EcarDbHelper.COLUMN_CAR_MANUFACTURER,
            EcarDbHelper.COLUMN_CAR_DESC,
            EcarDbHelper.COLUMN_CAR_EMISSIONS,
            EcarDbHelper.COLUMN_CAR_CONSUMPTION,
            EcarDbHelper.COLUMN_CAR_PICTURE,
            EcarDbHelper.COLUMN_FUEL_ID
    };

    public EcarDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new EcarDbHelper(context);
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

    public EcarUser createEcarUser(String name, String mail, int settingid){
        ContentValues values = new ContentValues();
        values.put(EcarDbHelper.COLUMN_USER_NAME, name);
        values.put(EcarDbHelper.COLUMN_USER_EMAIL, mail);
        values.put(EcarDbHelper.COLUMN_SETTINGS_ID, settingid);

        long insertId = database.insert(EcarDbHelper.TABLE_USER, null, values);

        Cursor cursor = database.query(EcarDbHelper.TABLE_USER,
                columnsuser, EcarDbHelper.COLUMN_USER_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        EcarUser ecaruser = cursorToEcarUser(cursor);
        cursor.close();
        return ecaruser;
    }
    private EcarUser cursorToEcarUser(Cursor cursor){
        int idIndex = cursor.getColumnIndex(EcarDbHelper.COLUMN_USER_ID);
        int idName = cursor.getColumnIndex(EcarDbHelper.COLUMN_USER_NAME);
        int idMail = cursor.getColumnIndex(EcarDbHelper.COLUMN_USER_EMAIL);
        int idBild = cursor.getColumnIndex(EcarDbHelper.COLUMN_USER_PICTURE);
        int idSettingsIndex = cursor.getColumnIndex(EcarDbHelper.COLUMN_SETTINGS_ID);

        long id = cursor.getLong(idIndex);
        int settingsid = cursor.getInt(idSettingsIndex);
        String name = cursor.getString(idName);
        String mail = cursor.getString(idMail);
        byte[] picture = cursor.getBlob(idBild);

        EcarUser ecaruser = new EcarUser(name, mail,settingsid);
        ecaruser.setUid((int)id);
        if(picture != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            ecaruser.setPic(bitmap);
        }
        return ecaruser;
    }
    public void deleteEcarUser(EcarUser usr){
        List<EcarSession> ecarSessionList;
        ecarSessionList = getUsersSession(usr);
        for(int i=0;i < ecarSessionList.size();i++){
            deleteEcarSession(ecarSessionList.get(i));
        }
        database.delete(EcarDbHelper.TABLE_USER,
                EcarDbHelper.COLUMN_USER_ID + "=" + usr.getUid(),
                null);
        Log.d(LOG_TAG, "Eintrag gelöscht! Inhalt: " + usr.toString());
    }
    public EcarUser addUserPic(EcarUser usr, Bitmap bild){
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bild.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
        byte[] barry = blob.toByteArray();

        ContentValues values = new ContentValues();
        values.put(EcarDbHelper.COLUMN_USER_PICTURE, barry);
        database.update(EcarDbHelper.TABLE_USER,values,EcarDbHelper.COLUMN_USER_ID+"="+usr.getUid(), null);

        Cursor cursor = database.query(EcarDbHelper.TABLE_USER,
                columnsuser, EcarDbHelper.COLUMN_USER_ID + "=" + usr.getUid(),
                null, null, null, null);

        cursor.moveToFirst();
        EcarUser ecaruser = cursorToEcarUser(cursor);
        cursor.close();
        return ecaruser;
    }
    public List<EcarUser> getAllUser(){
        List<EcarUser> ecarUserList = new ArrayList<>();

        Cursor cursor = database.query(EcarDbHelper.TABLE_USER,
                columnsuser, null, null, null, null, null);

        cursor.moveToFirst();
        EcarUser ecaruser;

        while(!cursor.isAfterLast()) {
            ecaruser = cursorToEcarUser(cursor);
            ecarUserList.add(ecaruser);
            Log.d(LOG_TAG, ecaruser.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return ecarUserList;
    }
    public EcarUser getUser(int id){
        String whereClause = null;
        String[] whereArgs = null;
        whereClause = EcarDbHelper.COLUMN_USER_ID + " = ?";
        whereArgs = new String[]{
                "" + id
        };
        Cursor cursor = database.query(
                EcarDbHelper.TABLE_USER,
                columnsuser,
                whereClause, whereArgs, null, null, null);

        if (cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        EcarUser ecarUser;
        ecarUser = cursorToEcarUser(cursor);
        Log.d(LOG_TAG, ecarUser.toString());
        cursor.close();

        return ecarUser;
    }
    public EcarUser checkUser(String name, String email){
        String whereClause = null;
        String[] whereArgs = null;
        whereClause = EcarDbHelper.COLUMN_USER_NAME + " = ? AND "+EcarDbHelper.COLUMN_USER_EMAIL+" = ?";
        whereArgs = new String[]{
                "" + name,
                "" + email
        };
        Cursor cursor = database.query(EcarDbHelper.TABLE_USER,
                columnsuser, whereClause, whereArgs, null, null, null);
        if (cursor.getCount() == 0){
            return null;
        }
        cursor.moveToFirst();
        EcarUser ecarUser;
        ecarUser = cursorToEcarUser(cursor);
        Log.d(LOG_TAG, ecarUser.toString());
        cursor.close();

        return ecarUser;
    }

    public EcarSettings createEcarSettings(){
        ContentValues values = new ContentValues();

        long insertId = database.insert(EcarDbHelper.TABLE_SETTINGS, null, values);

        Cursor cursor = database.query(EcarDbHelper.TABLE_SETTINGS,
                columnssettings, EcarDbHelper.COLUMN_SETTINGS_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        EcarSettings ecarsettings = cursorToEcarSettings(cursor);
        cursor.close();
        return ecarsettings;
    }
    public EcarSettings cursorToEcarSettings(Cursor cursor){
        int idIndex = cursor.getColumnIndex(EcarDbHelper.COLUMN_SETTINGS_ID);

        long id = cursor.getLong(idIndex);

        EcarSettings ecarset = new EcarSettings();
        ecarset.setSid((int)id);

        return ecarset;
    }
    public void deleteEcarSettings(EcarSettings eset){
        int id = eset.getSid();

        database.delete(EcarDbHelper.TABLE_SETTINGS,
                EcarDbHelper.COLUMN_SETTINGS_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! Inhalt: " + eset.toString());
    }

    public EcarSession createEcarSession(int uid, String name){
        ContentValues values = new ContentValues();
        values.put(EcarDbHelper.COLUMN_USER_ID, uid);
        values.put(EcarDbHelper.COLUMN_SESSION_NAME, name);
        values.put(EcarDbHelper.COLUMN_SESSION_EVALUATION, 0);

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
        int idAuswertung = cursor.getColumnIndex(EcarDbHelper.COLUMN_SESSION_EVALUATION);

        long id = cursor.getLong(idIndex);
        int userid = cursor.getInt(idUserIndex);
        String name = cursor.getString(idName);
        int iasw = cursor.getInt(idAuswertung);
        boolean basw = true;
        if (iasw == 0){ basw = false; }

        EcarSession ecarses = new EcarSession(userid, name);
        ecarses.setSesid((int)id);
        ecarses.setbAuswertung(basw);
        return ecarses;
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
    public EcarSession auswertungEintragen (EcarSession eses){
        ContentValues values = new ContentValues();
        values.put(EcarDbHelper.COLUMN_SESSION_EVALUATION, 1);
        database.update(EcarDbHelper.TABLE_SESSION,values,EcarDbHelper.COLUMN_SESSION_ID+"="+eses.getSesid(), null);
        //String strSQL = "UPDATE "+EcarDbHelper.TABLE_SESSION+" SET "+EcarDbHelper.COLUMN_SESSION_EVALUATION+" = 1 WHERE "+EcarDbHelper.COLUMN_SESSION_ID+" = "+ eses.getSesid();
        Cursor cursor = database.query(EcarDbHelper.TABLE_SESSION,
                columnssession, EcarDbHelper.COLUMN_SESSION_ID + "=" + eses.getSesid(),
                null, null, null, null);

        cursor.moveToFirst();
        EcarSession ecarsession = cursorToEcarSession(cursor);
        cursor.close();
        return ecarsession;
    }
    public List<EcarSession> getAllEcarSession() {
        List<EcarSession> ecarSessionList = new ArrayList<>();

        Cursor cursor = database.query(
                EcarDbHelper.TABLE_SESSION,
                columnssession,
                null, null, null, null, null);
        if (cursor.getCount() == 0){
            return null;
        }
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
    public List<EcarSession> getUsersSession(EcarUser euser) {
        List<EcarSession> ecarSessionList = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;

        whereClause = EcarDbHelper.COLUMN_USER_ID + " = ?";
        whereArgs = new String[]{
                "" + euser.getUid()
        };
        Cursor cursor = database.query(
                EcarDbHelper.TABLE_SESSION,
                columnssession,
                whereClause,
                whereArgs,
                null, null, null);

        if (cursor.getCount() == 0){
            return null;
        }

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
        if (cursor.getCount() == 0){
            return null;
        }
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
        if (cursor.getCount() == 0){
            return null;
        }
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

    public EcarCar createEcarCar(String name, String hersteller, String beschreibung, double emissionen, double verbrauch, Bitmap bild, int treibstoffid){

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        if(bild != null) {
            bild.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
        }
        byte[] barry = blob.toByteArray();


        ContentValues values = new ContentValues();
        values.put(EcarDbHelper.COLUMN_CAR_NAME, name);
        values.put(EcarDbHelper.COLUMN_CAR_MANUFACTURER, hersteller);
        values.put(EcarDbHelper.COLUMN_CAR_DESC, beschreibung);
        values.put(EcarDbHelper.COLUMN_CAR_EMISSIONS, emissionen);
        values.put(EcarDbHelper.COLUMN_CAR_CONSUMPTION, verbrauch);
        values.put(EcarDbHelper.COLUMN_CAR_PICTURE, barry);
        values.put(EcarDbHelper.COLUMN_FUEL_ID, treibstoffid);

        long insertId = database.insert(EcarDbHelper.TABLE_CAR, null, values);

        Cursor cursor = database.query(EcarDbHelper.TABLE_CAR,
                columnscar, EcarDbHelper.COLUMN_CAR_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        EcarCar ecarauto = cursorToEcarCar(cursor);
        cursor.close();

        return ecarauto;
    }
    private EcarCar cursorToEcarCar(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(EcarDbHelper.COLUMN_CAR_ID);
        int idName = cursor.getColumnIndex(EcarDbHelper.COLUMN_CAR_NAME);
        int idHersteller = cursor.getColumnIndex(EcarDbHelper.COLUMN_CAR_MANUFACTURER);
        int idBeschreibung = cursor.getColumnIndex(EcarDbHelper.COLUMN_CAR_DESC);
        int idEmissionen = cursor.getColumnIndex(EcarDbHelper.COLUMN_CAR_EMISSIONS);
        int idVerbrauch = cursor.getColumnIndex(EcarDbHelper.COLUMN_CAR_CONSUMPTION);
        int idBild = cursor.getColumnIndex(EcarDbHelper.COLUMN_CAR_PICTURE);
        int idTreibstoff = cursor.getColumnIndex(EcarDbHelper.COLUMN_FUEL_ID);

        long id = cursor.getLong(idIndex);
        String name = cursor.getString(idName);
        String hersteller = cursor.getString(idHersteller);
        String beschreibung = cursor.getString(idBeschreibung);
        double emissionen = cursor.getDouble(idEmissionen);
        double verbrauch = cursor.getDouble(idVerbrauch);
        byte[] picture = cursor.getBlob(idBild);
        int treibstoff = cursor.getInt(idTreibstoff);

        EcarCar ecarauto = new EcarCar(
                name,
                hersteller,
                beschreibung,
                emissionen,
                verbrauch,
                null,
                treibstoff);

        if(picture != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            ecarauto.setCarpic(bitmap);
        }
        ecarauto.setCid((int)id);
        return ecarauto;
    }
    public void deleteEcarCar(EcarCar ecarcar) {
        int id = ecarcar.getCid();

        database.delete(EcarDbHelper.TABLE_CAR,
                EcarDbHelper.COLUMN_CAR_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! Inhalt: " + ecarcar.toString());
    }
    public List<EcarCar> getCarType(int treibstoff) {
        List<EcarCar> ecarCarList = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;

        whereClause = EcarDbHelper.COLUMN_FUEL_ID + " = ?";
        whereArgs = new String[]{
                "" + treibstoff
        };
        Cursor cursor = database.query(
                EcarDbHelper.TABLE_CAR,
                columnscar,
                whereClause,
                whereArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        EcarCar ecarcar;

        while (!cursor.isAfterLast()) {
            ecarcar = cursorToEcarCar(cursor);
            ecarCarList.add(ecarcar);
            Log.d(LOG_TAG, "Inhalt: " + ecarcar.toString());
            cursor.moveToNext();
        }
        cursor.close();

        return ecarCarList;
    }
    public List<EcarCar> getAllCar() {
        List<EcarCar> ecarCarList = new ArrayList<>();

        Cursor cursor = database.query(
                EcarDbHelper.TABLE_CAR,
                columnscar,
                null,
                null,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        EcarCar ecarcar;

        while (!cursor.isAfterLast()) {
            ecarcar = cursorToEcarCar(cursor);
            ecarCarList.add(ecarcar);
            Log.d(LOG_TAG, "Inhalt: " + ecarcar.toString());
            cursor.moveToNext();
        }
        cursor.close();

        return ecarCarList;
    }
    public Cursor SqlQuarry(String com, String[] vars){
        // Anwendungs beispiel: rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        try {
            return database.rawQuery(com, vars);
        }
        catch (Exception ex){
            Log.d(LOG_TAG,"Fehler: " +ex);
            return null;
        }
    }
    public void SqlExec(String com){
        try {
            database.execSQL(com);
        }
        catch (Exception ex){
            Log.d(LOG_TAG,"Fehler: " +ex);
        }
    }
}
