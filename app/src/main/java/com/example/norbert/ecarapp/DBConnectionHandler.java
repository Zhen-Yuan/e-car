package com.example.norbert.ecarapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * Created by Norbert on 18.05.2017.
 */


//ToDO HttpS Verbindung absichern

//AsyncTask damit die Funktionen im Hintergrund laufen
public class DBConnectionHandler extends AsyncTask {


    @Override
    protected Object doInBackground(Object[] params) {
        String requestBody = (String) params[0];            //JSON Objekt aus dem Übergabeparameter
        System.out.println(requestBody);
        String response = "";
        String url = "http://pi-bo.dd-dns.de:8080/ContextAware/api/v1/" + requestBody;
        response = getData(requestBody, url);                    //gib mir Daten



        return response;
    }


    //selbsterklärend... öffne Verbindung
    private HttpURLConnection openConnection(String urlstring){
        String temp = "";
        HttpURLConnection conn = null;
        URL url=null;
        try {
            url = new URL(urlstring);
        } catch (MalformedURLException e) {e.printStackTrace();}

        try {
            conn = (HttpURLConnection) url.openConnection(); // Aufbauen der URL-Verbindung

        } catch (IOException e) {e.printStackTrace();}

        return conn;
    }


    //selbsterklärend... schließe Verbindung
    private void closeConnection(HttpURLConnection conn){
        try {
            if(conn.getResponseCode()>0) {
                conn.disconnect();
            }
        }catch(Exception e){e.printStackTrace();}
    }



    //gib Daten
    //ToDo requestBody implementieren
    private String getData(String requestBody, String url){
        HttpURLConnection conn = openConnection(url);  //Verbindung Öffnen yds
        try {
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                System.out.println(sb.toString());
                closeConnection(conn);//Verbindung schließen ... obviously
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        closeConnection(conn);
        return "Exception";

    }



    //ein JSON Object an den Server schicken
    //ToDO bla alles
    private void transmitJSON(String requestBody, HttpURLConnection conn){
        String temp = "";
        try {
            conn.setRequestMethod("POST");                                     // Festlegen des HTTP-Request
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");        // Festlegen, dass ein JSON übertragen wird
            OutputStream outputStream = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
            writer.write(requestBody);   // JSON wird übertragen
            writer.flush();
            writer.close();
            outputStream.close();

            InputStream inputStream;
            // Unterscheidung zwischen Stream für Ergebnisausgabe oder Stream für Fehlerausgabe
            if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            // Fehler - und Ergebnisausgabe im Log.d()
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while ((temp = bufferedReader.readLine()) != null) {
                Log.d("Ergebnis: ",temp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
