package com.luciayanicelli.icsalud_investigador;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Clase que permite comunicar la App con la Base de Datos
 */

public class PatientDBHelper extends SQLiteOpenHelper {

    //Constructor
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Patients.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + PatientContract.PatientEntry.TABLE_NAME + " ("
            + PatientContract.PatientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PatientContract.PatientEntry.ID_PATIENT + " TEXT NOT NULL,"
            + PatientContract.PatientEntry.EMAIL + " TEXT NOT NULL,"
            + PatientContract.PatientEntry.FIRST_NAME + " TEXT NOT NULL,"
            + PatientContract.PatientEntry.LAST_NAME + " TEXT NOT NULL,"
            + PatientContract.PatientEntry.EMAIL_PRACTITIONERS + " TEXT NOT NULL,"
            + PatientContract.PatientEntry.DATE_TIME_LAST_ALERT_ENVIADA + " TEXT NOT NULL"
            + ")";


    private Context context;

    public PatientDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    //Métodos obligatorios del Helper

   /* Por defecto el archivo de la base de datos será almacenado en:

            /data/data/<paquete>/databases/<nombre-de-la-bd>.db
    */

    @Override
    public void onCreate(SQLiteDatabase db) {

        if(db.isReadOnly()){
            db = getWritableDatabase();
        }

        db.execSQL(SQL_CREATE_ENTRIES);

    }

    //Este es ejecutado si se identificó que el usuario tiene una versión antigua de la base de datos.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
