package com.luciayanicelli.icsalud_investigador;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.luciayanicelli.icsalud_investigador.Api_Json.Buscar_Profesional;
import com.luciayanicelli.icsalud_investigador.Api_Json.Get_Alerts_index;
import com.luciayanicelli.icsalud_investigador.Api_Json.Get_Patient_index;
import com.luciayanicelli.icsalud_investigador.Api_Json.Get_Practitioner_index;
import com.luciayanicelli.icsalud_investigador.Api_Json.Get_Profesional_Practitioner_index;
import com.luciayanicelli.icsalud_investigador.Api_Json.JSON_CONSTANTS;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private String username = "icsalud.adm@gmail.com";
    private String password = "lucia0701201";

    private Configuraciones configuraciones;
    private String id_profesional;

    private Button button, btn_todos, btn_1, btn_cero, btn_indice;
    private int indice = -1;
    private Button btn_10, btn_okfecha, btn_un_mail;
    private EditText editText, editText_email;
    private boolean fechaOk = false;
    private ToggleButton toggleButton, toggleButton_alerta_verde;
    private boolean fecha_automatica;
    private boolean alertaVerde = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configuraciones = new Configuraciones(getApplicationContext());


        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarICSALUD();
            }


        });


        btn_todos = findViewById(R.id.btn_todos);
        btn_todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTODOS();
            }


        });


        btn_1 = findViewById(R.id.btn_1);
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarDEAUNO();
            }


        });

        btn_cero = findViewById(R.id.btn_cero);
        btn_cero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indice = -1;
                Toast.makeText(getApplicationContext(),
                        "Indice= " + indice,
                        Toast.LENGTH_LONG).show();
            }


        });

        btn_10 = findViewById(R.id.btn_10);
        btn_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarDEA10();
            }


        });

        btn_indice = findViewById(R.id.btn_indice);
        btn_indice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "Indice= " + indice,
                        Toast.LENGTH_LONG).show();
            }


        });

        editText = findViewById(R.id.editText_fecha);
        editText_email = findViewById(R.id.editText_email);

        btn_okfecha = findViewById(R.id.btn_OK_FECHA);
        btn_okfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fecha_automatica = toggleButton.isChecked();
                fechaOk = true;
                alertaVerde = toggleButton_alerta_verde.isChecked();
            }
        });

        toggleButton = findViewById(R.id.toggleButton);
        toggleButton_alerta_verde = findViewById(R.id.toggleButton_alertaVerde);

        btn_un_mail = findViewById(R.id.btn_todos_pacientes_un_mail);
        btn_un_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_email.getText().toString()!= ""){
                    iniciarUnMail(editText_email.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Ingrese el mail del profesional al que desea enviarle un mail",
                            Toast.LENGTH_LONG).show();
                }

            }
        });



    }

    private void iniciarUnMail(String s) {
        Toast.makeText(getApplicationContext(),
                "iniciarUnMail",
                Toast.LENGTH_LONG).show();


        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());
        try {
            if(conexionInternet.execute().get()){
                try {
                    if(autenticar()){
                        searchIdProfesional(s);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "CORROBORE CONEXION A INTERNET", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void searchIdProfesional(String email) throws ExecutionException, InterruptedException {

        Buscar_Profesional buscar_profesional = new Buscar_Profesional(getApplicationContext(), email);
        HashMap<String, String> data = buscar_profesional.execute().get();

        if(data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(valueOf(Boolean.TRUE))) {
            //Autorizado
            id_profesional = data.get(JSON_CONSTANTS.ID);
            configuraciones.setID(id_profesional);

            searchPatientsProfesional(email);


        }else{
            Toast.makeText(getApplicationContext(), "EMAIL INCORRECTOS", Toast.LENGTH_LONG).show();

        }


    }

    private void searchPatientsProfesional(String email_profesional) throws ExecutionException, InterruptedException {

        Get_Profesional_Practitioner_index get_profesional_practitioner_index = new Get_Profesional_Practitioner_index(getApplicationContext(), JSON_CONSTANTS.PRACTITIONER_STATUS_FRIEND);
        HashMap<String, String> data = get_profesional_practitioner_index.execute().get();

        if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(valueOf(Boolean.TRUE))) {

            if (data.get(JSON_CONSTANTS.RESPONSE_TOTAL).equalsIgnoreCase("0")) {
                //No hay patients en el listado
                finalizarBusqueda();

            } else {

                String patients = data.get(JSON_CONSTANTS.PROFESSIONALS);
                String id_patients = data.get(JSON_CONSTANTS.ID);
                String email_patients = data.get(JSON_CONSTANTS.EMAIL);

                String[] pacientes = patients.split(";");
                String[] id_pacientes = id_patients.split(";");
                String[] email_pacientes = email_patients.split(",");

                //Recupera los datos en un HashMap para luego poder ordenarlos alfabéticamente
                // HashMap<String, Lista_entrada_profesionales> listado = new HashMap<>();

                String textMail = "";

                for (int i = 0; i < pacientes.length; i++) {
                    //  listado.put(pacientes[i], new Lista_entrada_profesionales(id_pacientes[i], pacientes[i], email_pacientes[i]));

                    String dateTime = searchLastDateTime(id_pacientes[i], pacientes[i], email_pacientes[i]);
                    HashMap<String, String> searchAlert = searchAlerts(id_pacientes[i], dateTime);



                    if(searchAlert.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(Boolean.TRUE.toString())){


                        String alertasPaciente = getAlerts(id_pacientes[i], dateTime, String.valueOf(JSON_CONSTANTS.ALERTA_TYPE_HEART_RATE), JSON_CONSTANTS.DISTINTO);

                        if(alertasPaciente.equalsIgnoreCase("0")){
                           alertasPaciente = "NO TIENE ALERTAS";
                        }

                        textMail = textMail + "<br></br>" + pacientes[i].toUpperCase() + " - " + email_pacientes[i].toUpperCase() +  "<br></br>"
                        + alertasPaciente +  "<br></br>" + "------------------------------------------" +  "<br></br>";



                    }

                }

                EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano(getApplicationContext(),
                        "ALERTAS PACIENTES",
                        textMail,
                        email_profesional + ","+ configuraciones.getEmailAdministrator());
                if(enviarMailSegundoPlano.execute().get()){
                    Toast.makeText(getApplicationContext(),
                            "MAIL ENVIADO",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "ERROR AL ENVIAR EL MAIL",
                            Toast.LENGTH_LONG).show();
                }


            }

        }
    }

    private void iniciarDEA10() {
        Toast.makeText(getApplicationContext(),
                "DE A 10",
                Toast.LENGTH_LONG).show();


        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());
        try {
            if(conexionInternet.execute().get()){
                try {
                    if(autenticar()){
                        searchDea10Patients();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "CORROBORE CONEXION A INTERNET", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    private void searchDea10Patients() throws ExecutionException, InterruptedException {
        Toast.makeText(getApplicationContext(),
                "DE A 10",
                Toast.LENGTH_LONG).show();

        indice = indice + 1;

        Toast.makeText(getApplicationContext(),
                "Indice= " + indice,
                Toast.LENGTH_LONG).show();

        Get_Patient_index get_patient_index = new Get_Patient_index();
        HashMap<String, String> data = get_patient_index.execute().get();

        if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(valueOf(Boolean.TRUE))) {
            if (data.get(JSON_CONSTANTS.RESPONSE_TOTAL).equalsIgnoreCase("0")) {
                //No hay patients en el listado
                Toast.makeText(getApplicationContext(),
                        "No hay patients en el listado",
                        Toast.LENGTH_LONG).show();

            } else {

                String patients = data.get(JSON_CONSTANTS.PROFESSIONALS);
                String id_patients = data.get(JSON_CONSTANTS.ID);
                String email_patients = data.get(JSON_CONSTANTS.EMAIL);

                String[] pacientes = patients.split(";");
                String[] id_pacientes = id_patients.split(";");
                String[] email_pacientes = email_patients.split(",");

                //Recupera los datos en un HashMap para luego poder ordenarlos alfabéticamente
                // HashMap<String, Lista_entrada_profesionales> listado = new HashMap<>();

                //     for (int i = 0; i < pacientes.length; i++) {

                if(indice<pacientes.length) {

                    int fin, inicio;
                    inicio = indice;

                    if(indice + 10 <pacientes.length) {
                        fin = indice + 10;
                    }else {
                        fin = pacientes.length;
                    }


                    for (int i = inicio; i < fin; i++) {
                    //  listado.put(pacientes[i], new Lista_entrada_profesionales(id_pacientes[i], pacientes[i], email_pacientes[i]));

                        indice = i;
                    String dateTime = searchLastDateTime(id_pacientes[i], pacientes[i], email_pacientes[i]);
                    HashMap<String, String> searchAlert = searchAlerts(id_pacientes[i], dateTime);

                    if (searchAlert.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(Boolean.TRUE.toString())) {

                        boolean send_practitioner = sendAlertsPractitioner(id_pacientes[i], dateTime, JSON_CONSTANTS.ALERTA_TYPE_HEART_RATE, JSON_CONSTANTS.DISTINTO, pacientes[i], email_pacientes[i]);
                        boolean send_administrator = sendAlertsAdministrator(id_pacientes[i], dateTime, JSON_CONSTANTS.ALERTA_TYPE_HEART_RATE, JSON_CONSTANTS.IGUAL, pacientes[i], email_pacientes[i]);

                        if (send_practitioner & send_administrator) {

                            //modificar %20
                            String dateTime_server = searchAlert.get(JSON_CONSTANTS.DATE_TIME);
                           /*  String[] dateTime_array = dateTime_server.split(" ");
                             String dateTime_BD = dateTime_array[0] + "%20" + dateTime_array[1];
*/
                            String palabra = dateTime_server.replace(" ", "%20");

                            updateDB(id_pacientes[i], palabra);

                            Toast.makeText(getApplicationContext(),
                                    "Alertas enviadas - " + email_pacientes[i],
                                    Toast.LENGTH_LONG).show();

                        } else {
                            finalizarBusqueda();
                        }


                    } else {
                        Toast.makeText(getApplicationContext(),
                                "No tiene alertas - " + email_pacientes[i],
                                Toast.LENGTH_LONG).show();
                    }
                }

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Indice= " + indice + "supera la cantidad de pacientes existentes. Presione volver a cero",
                            Toast.LENGTH_LONG).show();
                }


            }

        }
    }

    private void iniciarDEAUNO() {
        Toast.makeText(getApplicationContext(),
                "DE A UNO",
                Toast.LENGTH_LONG).show();



        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());
        try {
            if(conexionInternet.execute().get()){
                try {
                    if(autenticar()){
                        searchDeaUnPatients();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "CORROBORE CONEXION A INTERNET", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



    }

    private void searchDeaUnPatients() throws ExecutionException, InterruptedException {
        Toast.makeText(getApplicationContext(),
                "DE A UNO",
                Toast.LENGTH_LONG).show();

        indice = indice + 1;

        Toast.makeText(getApplicationContext(),
                "Indice= " + indice,
                Toast.LENGTH_LONG).show();

        Get_Patient_index get_patient_index = new Get_Patient_index();
        HashMap<String, String> data = get_patient_index.execute().get();

        if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(valueOf(Boolean.TRUE))) {
            if (data.get(JSON_CONSTANTS.RESPONSE_TOTAL).equalsIgnoreCase("0")) {
                //No hay patients en el listado
                Toast.makeText(getApplicationContext(),
                        "No hay patients en el listado",
                        Toast.LENGTH_LONG).show();

            } else {

                String patients = data.get(JSON_CONSTANTS.PROFESSIONALS);
                String id_patients = data.get(JSON_CONSTANTS.ID);
                String email_patients = data.get(JSON_CONSTANTS.EMAIL);

                String[] pacientes = patients.split(";");
                String[] id_pacientes = id_patients.split(";");
                String[] email_pacientes = email_patients.split(",");

                //Recupera los datos en un HashMap para luego poder ordenarlos alfabéticamente
                // HashMap<String, Lista_entrada_profesionales> listado = new HashMap<>();

           //     for (int i = 0; i < pacientes.length; i++) {
                int i;
                if(indice<pacientes.length){
                    i = indice;
                    //  listado.put(pacientes[i], new Lista_entrada_profesionales(id_pacientes[i], pacientes[i], email_pacientes[i]));

                    String dateTime = searchLastDateTime(id_pacientes[i], pacientes[i], email_pacientes[i]);
                    HashMap<String, String> searchAlert = searchAlerts(id_pacientes[i], dateTime);

                    if(searchAlert.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(Boolean.TRUE.toString())){

                        boolean send_practitioner = sendAlertsPractitioner(id_pacientes[i], dateTime, JSON_CONSTANTS.ALERTA_TYPE_HEART_RATE, JSON_CONSTANTS.DISTINTO, pacientes[i], email_pacientes[i]);
                        boolean send_administrator = sendAlertsAdministrator(id_pacientes[i], dateTime, JSON_CONSTANTS.ALERTA_TYPE_HEART_RATE, JSON_CONSTANTS.IGUAL, pacientes[i], email_pacientes[i]);

                        if(send_practitioner & send_administrator){

                            //modificar %20
                            String dateTime_server = searchAlert.get(JSON_CONSTANTS.DATE_TIME);
                           /*  String[] dateTime_array = dateTime_server.split(" ");
                             String dateTime_BD = dateTime_array[0] + "%20" + dateTime_array[1];
*/
                            String palabra = dateTime_server.replace(" ", "%20");

                            updateDB(id_pacientes[i], palabra);

                            Toast.makeText(getApplicationContext(),
                                    "Alertas enviadas - " + email_pacientes[i],
                                    Toast.LENGTH_LONG).show();

                        }else {
                            finalizarBusqueda();
                        }


                    }else{
                        Toast.makeText(getApplicationContext(),
                                "No tiene alertas - " + email_pacientes[i],
                                Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Indice= " + indice + "supera la cantidad de pacientes existentes. Presione volver a cero",
                            Toast.LENGTH_LONG).show();
                }


            }

        }





    }

    private void iniciarTODOS() {
        Toast.makeText(getApplicationContext(),
                "TODOS",
                Toast.LENGTH_LONG).show();

        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());
        try {
            if(conexionInternet.execute().get()){
                try {
                    if(autenticar()){
                        searchAllPatients();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "CORROBORE CONEXION A INTERNET", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void searchAllPatients() throws ExecutionException, InterruptedException {
        Get_Patient_index get_patient_index = new Get_Patient_index();
        HashMap<String, String> data = get_patient_index.execute().get();

        if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(valueOf(Boolean.TRUE))) {
            if (data.get(JSON_CONSTANTS.RESPONSE_TOTAL).equalsIgnoreCase("0")) {
                //No hay patients en el listado
                finalizarBusqueda();

            } else {

                String patients = data.get(JSON_CONSTANTS.PROFESSIONALS);
                String id_patients = data.get(JSON_CONSTANTS.ID);
                String email_patients = data.get(JSON_CONSTANTS.EMAIL);

                String[] pacientes = patients.split(";");
                String[] id_pacientes = id_patients.split(";");
                String[] email_pacientes = email_patients.split(",");

                //Recupera los datos en un HashMap para luego poder ordenarlos alfabéticamente
                // HashMap<String, Lista_entrada_profesionales> listado = new HashMap<>();

                for (int i = 0; i < pacientes.length; i++) {
                    //  listado.put(pacientes[i], new Lista_entrada_profesionales(id_pacientes[i], pacientes[i], email_pacientes[i]));

                    String dateTime = searchLastDateTime(id_pacientes[i], pacientes[i], email_pacientes[i]);
                    HashMap<String, String> searchAlert = searchAlerts(id_pacientes[i], dateTime);

                    if(searchAlert.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(Boolean.TRUE.toString())){

                        boolean send_practitioner = sendAlertsPractitioner(id_pacientes[i], dateTime, JSON_CONSTANTS.ALERTA_TYPE_HEART_RATE, JSON_CONSTANTS.DISTINTO, pacientes[i], email_pacientes[i]);
                        boolean send_administrator = sendAlertsAdministrator(id_pacientes[i], dateTime, JSON_CONSTANTS.ALERTA_TYPE_HEART_RATE, JSON_CONSTANTS.IGUAL, pacientes[i], email_pacientes[i]);

                        if(send_practitioner & send_administrator){

                            //modificar %20
                            String dateTime_server = searchAlert.get(JSON_CONSTANTS.DATE_TIME);
                           /*  String[] dateTime_array = dateTime_server.split(" ");
                             String dateTime_BD = dateTime_array[0] + "%20" + dateTime_array[1];
*/
                            String palabra = dateTime_server.replace(" ", "%20");

                            updateDB(id_pacientes[i], palabra);
                        }else {
                            finalizarBusqueda();
                        }


                    }else{
                        finalizarBusqueda();
                    }

                }


            }

        }



    }


    private void iniciarICSALUD() {

        ConexionInternet conexionInternet = new ConexionInternet(getApplicationContext());
        try {
            if(conexionInternet.execute().get()){
                try {
                    if(autenticar()){
                        searchPatients();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "CORROBORE CONEXION A INTERNET", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void searchPatients() throws ExecutionException, InterruptedException {

        Get_Profesional_Practitioner_index get_profesional_practitioner_index = new Get_Profesional_Practitioner_index(getApplicationContext(), JSON_CONSTANTS.PRACTITIONER_STATUS_FRIEND);
        HashMap<String, String> data = get_profesional_practitioner_index.execute().get();

        if (data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(valueOf(Boolean.TRUE))) {

            if (data.get(JSON_CONSTANTS.RESPONSE_TOTAL).equalsIgnoreCase("0")) {
                //No hay patients en el listado
                finalizarBusqueda();

            } else {

                String patients = data.get(JSON_CONSTANTS.PROFESSIONALS);
                String id_patients = data.get(JSON_CONSTANTS.ID);
                String email_patients = data.get(JSON_CONSTANTS.EMAIL);

                String[] pacientes = patients.split(";");
                String[] id_pacientes = id_patients.split(";");
                String[] email_pacientes = email_patients.split(",");

                //Recupera los datos en un HashMap para luego poder ordenarlos alfabéticamente
               // HashMap<String, Lista_entrada_profesionales> listado = new HashMap<>();

                for (int i = 0; i < pacientes.length; i++) {
                  //  listado.put(pacientes[i], new Lista_entrada_profesionales(id_pacientes[i], pacientes[i], email_pacientes[i]));

                    String dateTime = searchLastDateTime(id_pacientes[i], pacientes[i], email_pacientes[i]);
                    HashMap<String, String> searchAlert = searchAlerts(id_pacientes[i], dateTime);

                    if(searchAlert.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(Boolean.TRUE.toString())){

                        boolean send_practitioner = sendAlertsPractitioner(id_pacientes[i], dateTime, JSON_CONSTANTS.ALERTA_TYPE_HEART_RATE, JSON_CONSTANTS.DISTINTO, pacientes[i], email_pacientes[i]);
                        boolean send_administrator = sendAlertsAdministrator(id_pacientes[i], dateTime, JSON_CONSTANTS.ALERTA_TYPE_HEART_RATE, JSON_CONSTANTS.IGUAL, pacientes[i], email_pacientes[i]);

                         if(send_practitioner & send_administrator){

                             //modificar %20
                             String dateTime_server = searchAlert.get(JSON_CONSTANTS.DATE_TIME);
                           /*  String[] dateTime_array = dateTime_server.split(" ");
                             String dateTime_BD = dateTime_array[0] + "%20" + dateTime_array[1];
*/
                             String palabra = dateTime_server.replace(" ", "%20");

                             updateDB(id_pacientes[i], palabra);
                         }else {
                             finalizarBusqueda();
                         }


                     }else{
                        finalizarBusqueda();
                    }

                }


            }

        }
    }

    private void updateDB(String id_paciente, String s) {

        PatientDBHelper patientDBHelper = new PatientDBHelper(getApplicationContext());
        SQLiteDatabase db = patientDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(PatientContract.PatientEntry.DATE_TIME_LAST_ALERT_ENVIADA, s);

        String whereClause = PatientContract.PatientEntry.ID_PATIENT + "= ? ";

        String[] whereArgs = new String[]{id_paciente};

        int number = db.update(PatientContract.PatientEntry.TABLE_NAME, values, whereClause, whereArgs);

        db.close();

    }

    private boolean sendAlertsAdministrator(String id_paciente, String dateTime, int alertaTypeHeartRate, String igual, String paciente, String email_paciente) throws ExecutionException, InterruptedException {

        String textMail = getAlerts(id_paciente, dateTime, String.valueOf(alertaTypeHeartRate), igual);

        if(!textMail.equalsIgnoreCase("0")){
            EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano(getApplicationContext(),
                    "ALERTAS - " + paciente + " - " + email_paciente,
                    textMail, configuraciones.getEmailAdministrator());

            return enviarMailSegundoPlano.execute().get();
        }else{
            return true;
        }




    }

    private String getEmailPractitioner(String id_paciente) throws ExecutionException, InterruptedException {

        Get_Practitioner_index get_practitioner_index = new Get_Practitioner_index(getApplicationContext(),
                JSON_CONSTANTS.PRACTITIONER_STATUS_FRIEND, id_paciente);
        HashMap<String, String> dataPractitioner = get_practitioner_index.execute().get();

        if(dataPractitioner.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(Boolean.TRUE.toString())
                & !dataPractitioner.get(JSON_CONSTANTS.RESPONSE_TOTAL).equalsIgnoreCase("0")){
            //si hay practitioners
            return dataPractitioner.get(JSON_CONSTANTS.EMAIL);

        }else{
            return configuraciones.getEmailAdministrator();
        }


     /*   PatientDBHelper patientDBHelper = new PatientDBHelper(getApplicationContext());
        SQLiteDatabase db = patientDBHelper.getWritableDatabase();

        String[] campos = new String[]{PatientContract.PatientEntry.EMAIL_PRACTITIONERS};
        String selection = PatientContract.PatientEntry.ID_PATIENT + "= ? ";

        String[] args = new String[]{id_paciente};

        Cursor busqueda = db.query(true, PatientContract.PatientEntry.TABLE_NAME,
                campos, selection, args, null, null, null, null);

        //Si existen alertas del tipo verde con ese parámetro y fecha sale, en caso contrario ingresa a buscar alertas
        if (busqueda != null & busqueda.moveToFirst()) {
            //existe el patient
            String emailPractitioner = busqueda.getString(0);
            db.close();
            return emailPractitioner;

        }else{

            db.close();
            return configuraciones.getEmailAdministrator();
        }
        */
    }

    private boolean sendAlertsPractitioner(String id_paciente, String dateTime, int alertaTypeHeartRate, String distinto, String paciente, String email_paciente) throws ExecutionException, InterruptedException {

        String textMail = getAlerts(id_paciente, dateTime, String.valueOf(alertaTypeHeartRate), distinto);

        if(!textMail.equalsIgnoreCase("0")){
            String emailPractitioners = getEmailPractitioner(id_paciente);


            EnviarMailSegundoPlano enviarMailSegundoPlano = new EnviarMailSegundoPlano(getApplicationContext(),
                    "ALERTAS - " + paciente + " - " + email_paciente,
                    textMail, emailPractitioners);

            return enviarMailSegundoPlano.execute().get();
        }else{
            return true;
        }



    }

    private String getAlerts(String id_paciente, String dateTime, String type, String igual) throws ExecutionException, InterruptedException {

        Get_Alerts_index get_alerts_index = new Get_Alerts_index(getApplicationContext(), id_paciente, dateTime, type, igual, false, alertaVerde);
        HashMap<String, String> data = get_alerts_index.execute().get();

        if(data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(Boolean.TRUE.toString())){

            String description = data.get(JSON_CONSTANTS.ALERTS_DESCRIPTION);
            String fecha = data.get(JSON_CONSTANTS.DATE_TIME);
            String alert_type = data.get(JSON_CONSTANTS.ALERTS_TYPE);
            String alert_level = data.get(JSON_CONSTANTS.ALERTS_LEVEL);

            String[] description_array = description.split(";;;");
            String[] fecha_array = fecha.split(";");
            String[] alert_type_array = alert_type.split(";");
            String[] alert_level_array = alert_level.split(";");

            String textMail = "";

            for(int i = description_array.length - 1; i>=0; i--){

                String alertType = alert_type_array[i];

                String linea = convertirLevel(alert_level_array[i]) +
                        " - " + fecha_array[i] +
                        ": " + description_array[i];

                textMail = textMail + "<br></br>" + linea + "<br></br>";

            }

                return textMail;

        }else{

            return "0";
        }

        }

    private String convertirLevel(String alert_level) {

         if(alert_level.equalsIgnoreCase(String.valueOf(JSON_CONSTANTS.ALERTA_LEVEL_GREEN))) {
            return "ALERTA VERDE";
        }else if(alert_level.equalsIgnoreCase(String.valueOf(JSON_CONSTANTS.ALERTA_LEVEL_YELLOW))) {
            return "ALERTA AMARILLA";
        }else if(alert_level.equalsIgnoreCase(String.valueOf(JSON_CONSTANTS.ALERTA_LEVEL_RED))) {
            return "ALERTA ROJA";
        }else{
             return "ALERTA VERDE";
         }

    }

    private HashMap<String, String> searchAlerts(String id_paciente, String dateTime) throws ExecutionException, InterruptedException {

        HashMap<String, String> datos = new HashMap<>();

        Get_Alerts_index get_alerts_index = new Get_Alerts_index(getApplicationContext(), id_paciente, dateTime, null, null, true, alertaVerde);
        HashMap<String, String> data = get_alerts_index.execute().get();

        if(data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(Boolean.TRUE.toString())){



            String dateTime_string = data.get(JSON_CONSTANTS.DATE_TIME);
            String[] dateTime_array = dateTime_string.split(";");
            int length_date = dateTime_array.length - 1;

            String lastDateTime = dateTime_array[length_date];

            datos.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, Boolean.TRUE.toString());
            datos.put(JSON_CONSTANTS.DATE_TIME, lastDateTime);

        }else{
            datos.put(JSON_CONSTANTS.HEADER_AUTHORIZATION, Boolean.FALSE.toString());
        }

        return  datos;

    }



    private String searchLastDateTime(String id_paciente, String paciente, String email) throws ExecutionException, InterruptedException {

        PatientDBHelper patientDBHelper = new PatientDBHelper(getApplicationContext());
        SQLiteDatabase db = patientDBHelper.getWritableDatabase();

        String[] campos = new String[]{PatientContract.PatientEntry.DATE_TIME_LAST_ALERT_ENVIADA};
        String selection = PatientContract.PatientEntry.ID_PATIENT + "= ? ";
         //       + "and " + AlertasContract.AlertasEntry.FECHA + ">= ?"+ " and " +  AlertasContract.AlertasEntry.FECHA + "<= ?"
         //       + "and " + AlertasContract.AlertasEntry.PARAMETRO +"= ? ";

        String[] args = new String[] {id_paciente};

        Cursor busqueda = db.query(true, PatientContract.PatientEntry.TABLE_NAME,
                campos, selection, args,null,null,null,null);

        //Si existen alertas del tipo verde con ese parámetro y fecha sale, en caso contrario ingresa a buscar alertas
        if (busqueda != null & busqueda.moveToFirst()) {
            //existe el patient
            String fecha = busqueda.getString(0);

            db.close();

            if(!fecha_automatica){
                //toggle off
                editText.setText(fecha);
                return fecha;
            }else{
                //toggle on
                fecha = editText.getText().toString();
                return fecha;
            }



        }else {
            //No existe el patient
            //creo el patient con fecha vieja
            String[] patient = paciente.split(",");
            String firstName = patient[1];
            String lastName = patient[0];


            String emailPractitioners = getEmailPractitioner(id_paciente);




            ContentValues values = new ContentValues();
            values.put(PatientContract.PatientEntry.ID_PATIENT, id_paciente);
            values.put(PatientContract.PatientEntry.EMAIL, email);
            values.put(PatientContract.PatientEntry.FIRST_NAME, firstName);
            values.put(PatientContract.PatientEntry.LAST_NAME, lastName);
            values.put(PatientContract.PatientEntry.EMAIL_PRACTITIONERS, emailPractitioners);
            values.put(PatientContract.PatientEntry.DATE_TIME_LAST_ALERT_ENVIADA, JSON_CONSTANTS.DEFAULT_DATE_TIME_LAST_ALERT_ENVIADA);

            long longd = db.insert(PatientContract.PatientEntry.TABLE_NAME, null, values);

            db.close();

            if(!fecha_automatica){
                //toggle off
                editText.setText(JSON_CONSTANTS.DEFAULT_DATE_TIME_LAST_ALERT_ENVIADA);
                return JSON_CONSTANTS.DEFAULT_DATE_TIME_LAST_ALERT_ENVIADA;
            }else{
                //toggle on
                String fecha = editText.getText().toString();
                return fecha;
            }

            //return JSON_CONSTANTS.DEFAULT_DATE_TIME_LAST_ALERT_ENVIADA;

        }


        }


    private void finalizarBusqueda() {

            Toast.makeText(getApplicationContext(), "fin de la búsqueda", Toast.LENGTH_LONG).show();
    }

    private boolean autenticar() throws ExecutionException, InterruptedException {
        Log.d(this.getClass().getSimpleName(), "autenticar");

        Buscar_Profesional buscar_profesional = new Buscar_Profesional(getApplicationContext(), configuraciones.getEmailAdministrator());
        HashMap<String, String> data = buscar_profesional.execute().get();

        if(data.get(JSON_CONSTANTS.HEADER_AUTHORIZATION).equalsIgnoreCase(valueOf(Boolean.TRUE))) {
            //Autorizado
            id_profesional = data.get(JSON_CONSTANTS.ID);
            configuraciones.setID(id_profesional);

            return true;
        }else{
            Toast.makeText(getApplicationContext(), "USERNAME O PASSWORD INCORRECTOS", Toast.LENGTH_LONG).show();
            return false;
        }


    }
}
