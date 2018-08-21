package com.luciayanicelli.icsalud_investigador.Api_Json;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.luciayanicelli.icsalud_investigador.Configuraciones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
  */

public class Get_Alerts_index extends AsyncTask<Integer, Void, HashMap<String, String>> {

    private static String _PATIENT_ID, _DATE_TIME, _TYPE, _IGUAL_DISTINTO;
    private final boolean lastDate, alertaVerde;
    private Configuraciones configuraciones;
    private Context context;
    private String ACCESS_TOKEN;
    private int STATUS;

    // get {{url}}/patients/{{patient}}/alerts
    private final String BASE_URL;
    private String BUSQUEDA;
    private String base_url;

    public Get_Alerts_index(Context context, String patient_id, String dateTime, String type, String igual_distinto, boolean lastDate, boolean alertaVerde) throws ExecutionException, InterruptedException {
        this.context = context;
        this.configuraciones = new Configuraciones(context);
      //  this._PATIENT_ID = configuraciones.getID();
        this._PATIENT_ID = patient_id;
        this._DATE_TIME = dateTime;
        this._TYPE = type;
        this._IGUAL_DISTINTO = igual_distinto;
        this.lastDate = lastDate;
        this.alertaVerde = alertaVerde;


        this.BASE_URL = JSON_CONSTANTS.BASE_URL +
                JSON_CONSTANTS.PATIENTS +
                "/" + _PATIENT_ID +
                "/" + JSON_CONSTANTS.ALERTS;



        JSON_functions jsonFunctions = new JSON_functions(context);
        this.ACCESS_TOKEN = jsonFunctions.getAccessTokenPassword();

    }



    @Override
    protected HashMap<String, String> doInBackground(Integer... params) {

        HashMap<String, String> data = new HashMap<String, String>();

        BUSQUEDA = filters(_DATE_TIME, _TYPE, _IGUAL_DISTINTO);

        base_url = BASE_URL + JSON_CONSTANTS.FILTER + BUSQUEDA;


        // 1. Obtener la conexión
        URL url = null;
        try {
            url = new URL(base_url);
        //    url = new URL(BASE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection con = null;

        //Conectando
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            con.setRequestMethod(JSON_CONSTANTS.REQUEST_GET);
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        }
        //Añadiendo request headers
        con.setRequestProperty(JSON_CONSTANTS.HEADER_ACCEPT, JSON_CONSTANTS.HEADER_ACCEPT_VALUE_1);
        con.setRequestProperty(JSON_CONSTANTS.HEADER_CONTENT_TYPE, JSON_CONSTANTS.HEADER_CONTENT_TYPE_VALUE);
        con.setRequestProperty(JSON_CONSTANTS.HEADER_AUTHORIZATION, JSON_CONSTANTS.HEADER_AUTHORIZATION_VALUE + ACCESS_TOKEN);


        InputStream contentStream = null;
        try {
            contentStream = con.getInputStream();

        } catch (IOException e1) {
            e1.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
            return data;
        }


        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(contentStream));

        final StringBuilder result = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
            return data;
        }


        // Recibe esto:
        //{"data":[],
        // "meta":{"pagination":{"total":0,
        //                         "count":0,
        //                          "per_page":15,
        //                          "current_page":1,
        //                           "total_pages":0,
        //                             "links":{"first":"http:\/\/api.icsalud.com.ar\/\/patients\/d6c63410-0dd4-11e8-8c3d-c7476782b79a\/practitioners?filter=status+eq+0&page=1","previous":null,"next":null,"last":"http:\/\/api.icsalud.com.ar\/\/patients\/d6c63410-0dd4-11e8-8c3d-c7476782b79a\/practitioners?filter=status+eq+0&page=1"}}}}

        JSONObject jsonObject_response = null;
        try {
            jsonObject_response = new JSONObject(result.toString());

            //OBTENER TOTAL
            JSONObject jsonObjectMETA = jsonObject_response.getJSONObject(JSON_CONSTANTS.RESPONSE_META);
            JSONObject jsonObjectPAGINATION = jsonObjectMETA.getJSONObject(JSON_CONSTANTS.RESPONSE_PAGINATION);
            int total = jsonObjectPAGINATION.getInt(JSON_CONSTANTS.RESPONSE_TOTAL);
            int total_pages = jsonObjectPAGINATION.getInt(JSON_CONSTANTS.RESPONSE_TOTAL_PAGES);
            int per_page = jsonObjectPAGINATION.getInt(JSON_CONSTANTS.RESPONSE_PER_PAGE);
            int current_page = jsonObjectPAGINATION.getInt(JSON_CONSTANTS.RESPONSE_CURRENT_PAGE);

            if(total!=0){

                if(total_pages>1){
                    Log.d("get_alerts_index", "get_alerts_index");
                    //hay varias páginas - cargar todas
                    String date_time ="";
                    String description = "";
                    String level = "";
                    String type = "";
                    String json_string = "";

                    if(lastDate){
                        HashMap<String, String> _data = nuevaConsulta(total_pages);

                        date_time = date_time + _data.get(JSON_CONSTANTS.DATE_TIME);
                        description = description + _data.get(JSON_CONSTANTS.ALERTS_DESCRIPTION);
                        level = level + _data.get(JSON_CONSTANTS.ALERTS_LEVEL);
                        type = type + _data.get(JSON_CONSTANTS.ALERTS_TYPE);
                        json_string = json_string + _data.get(JSON_CONSTANTS.JSON_STRING);

                    }else{
                        for(int i = 1; i <= total_pages; i++){

                            HashMap<String, String> _data = nuevaConsulta(i);

                            date_time = date_time + _data.get(JSON_CONSTANTS.DATE_TIME);
                            description = description + _data.get(JSON_CONSTANTS.ALERTS_DESCRIPTION);
                            level = level + _data.get(JSON_CONSTANTS.ALERTS_LEVEL);
                            type = type + _data.get(JSON_CONSTANTS.ALERTS_TYPE);
                            json_string = json_string + _data.get(JSON_CONSTANTS.JSON_STRING);

                        }
                    }


                    data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.TRUE));
                    data.put(JSON_CONSTANTS.DATE_TIME, date_time);
                    data.put(JSON_CONSTANTS.ALERTS_DESCRIPTION, description);
                    data.put(JSON_CONSTANTS.ALERTS_LEVEL, level);
                    data.put(JSON_CONSTANTS.ALERTS_TYPE, type);

                    data.put(JSON_CONSTANTS.JSON_STRING, result.toString());

                    return data;


                }else{
                    //sólo hay una página
                    return cargarDatos(jsonObject_response, result);
                }


            }else{
                data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
                return  data;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
            return data;
        }

    }

    private String filters(String dateTime, String type, String igual_distinto) {
        String param;
        if(type != null){
            param= JSON_CONSTANTS.DATE_TIME + JSON_CONSTANTS.MAYOR + dateTime +
            "," + JSON_CONSTANTS.ALERTS_TYPE + igual_distinto + type;
        }else{
            param= JSON_CONSTANTS.DATE_TIME + JSON_CONSTANTS.MAYOR + dateTime;
        }

        if(alertaVerde){
            param = param + "," + JSON_CONSTANTS.ALERTS_LEVEL + JSON_CONSTANTS.DISTINTO + String.valueOf(JSON_CONSTANTS.ALERTA_LEVEL_GREEN);
        }

        return param;
    }


    private HashMap<String,String> nuevaConsulta(int page) {
        URL url_page = null;
        JSONArray jsonArr = null;

        try {
         //   url_page = new URL(BASE_URL + "?page=" + String.valueOf(page));
            url_page = new URL(base_url + ",page=" + String.valueOf(page));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection con = null;

        //Conectando
        try {
            con = (HttpURLConnection) url_page.openConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            con.setRequestMethod(JSON_CONSTANTS.REQUEST_GET);
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        }
        //Añadiendo request headers
        con.setRequestProperty(JSON_CONSTANTS.HEADER_ACCEPT, JSON_CONSTANTS.HEADER_ACCEPT_VALUE_1);
        con.setRequestProperty(JSON_CONSTANTS.HEADER_CONTENT_TYPE, JSON_CONSTANTS.HEADER_CONTENT_TYPE_VALUE);
        con.setRequestProperty(JSON_CONSTANTS.HEADER_AUTHORIZATION, JSON_CONSTANTS.HEADER_AUTHORIZATION_VALUE + ACCESS_TOKEN);


        InputStream contentStream = null;
        try {
            contentStream = con.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(contentStream));

        final StringBuilder result = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        JSONObject jsonObject_response = null;
        HashMap<String, String> data = new HashMap<String, String>();
        try {
            jsonObject_response = new JSONObject(result.toString());

            // Getting JSON Array node
            jsonArr = jsonObject_response.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);

            data=cargarDatos(jsonObject_response, result);

        } catch (JSONException e) {
            e.printStackTrace();
            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.FALSE));
        }
        return data;
    }

    private HashMap<String,String> cargarDatos(JSONObject jsonObject_response, StringBuilder result) throws JSONException {
        // Getting JSON Array node
        JSONArray jsonArr = jsonObject_response.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);

        final int numRows = jsonArr.length();

        HashMap<String, String> data = new HashMap<String, String>();

        String date_time = "";
        String description = "";
        String level = "";
        String type = "";

        for (int x = 0; x < numRows; x++) {
            final JSONObject row = jsonArr.getJSONObject(x); //última fila numRows-1

            date_time = date_time + row.getString(JSON_CONSTANTS.DATE_TIME)  + ";";
            description = description + row.getString(JSON_CONSTANTS.ALERTS_DESCRIPTION)  + ";;;";
            level = level + row.getString(JSON_CONSTANTS.ALERTS_LEVEL)  + ";";
            type = type + row.getString(JSON_CONSTANTS.ALERTS_TYPE)  + ";";

        }

            data.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, String.valueOf(Boolean.TRUE));
            data.put(JSON_CONSTANTS.DATE_TIME, date_time);
            data.put(JSON_CONSTANTS.ALERTS_DESCRIPTION, description);
            data.put(JSON_CONSTANTS.ALERTS_LEVEL, level);
            data.put(JSON_CONSTANTS.ALERTS_TYPE, type);

            data.put(JSON_CONSTANTS.JSON_STRING, result.toString());

            return data;

    }


}