package com.luciayanicelli.icsalud_investigador;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class ConexionInternet extends AsyncTask<Void, Void, Boolean> {

    private Context context;

    public ConexionInternet(Context context) {
        this.context = context;
    }


    //ANALIZA QUE EXISTA UNA CONEXIÓN A INTERNET
    private boolean conectadointernet() {
        if(conectadoWifi()){
            //   Toast.makeText(getApplicationContext(), "Tu Dispositivo tiene Conexion Wifi.", Toast.LENGTH_LONG).show();
            return true;
        }else{
            return conectadoRedMovil();
          /*  if(conectadoRedMovil()){

                return true;
            }else{

                return false;
            }*/
        }
    }

    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
         return  conectadointernet();
    }
}
