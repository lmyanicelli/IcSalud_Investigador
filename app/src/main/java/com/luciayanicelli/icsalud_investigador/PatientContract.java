package com.luciayanicelli.icsalud_investigador;

import android.provider.BaseColumns;

import com.luciayanicelli.icsalud_investigador.Api_Json.JSON_CONSTANTS;

/**
 * Created by LuciaYanicelli on 29/6/2017.
 *
 * Determina el "contrato" con la base de datos.
 * La estructura que tendrá la misma. Tabla, columnas etc.
 */

public class PatientContract {


    //Creamos esta clase abstracta para guardar el nombre de las columnas y de la tabla
    public static abstract class PatientEntry implements BaseColumns {

        public static final String TABLE_NAME ="patient";

        public static final String _ID = "_id";
        public static final String EMAIL = JSON_CONSTANTS.EMAIL;
        public static final String FIRST_NAME = JSON_CONSTANTS.FIRST_NAME;
        public static final String LAST_NAME = JSON_CONSTANTS.LAST_NAME;
        public static final String EMAIL_PRACTITIONERS = JSON_CONSTANTS.PRACTITIONER;
        public static final String ID_PATIENT= JSON_CONSTANTS.ID;
        public static final String DATE_TIME_LAST_ALERT_ENVIADA= JSON_CONSTANTS.DATE_TIME;



    }
}
