package com.phasemaps.azio.phasemapsbeacon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.phasemaps.azio.phasemapsbeacon.res.HTTPConnection;
import com.phasemaps.azio.phasemapsbeacon.res.PhaseLogger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
private PhaseLogger phaseLogger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        phaseLogger = new PhaseLogger(getBaseContext());
    }

    public void register(View view) {
        TextView name = (TextView) findViewById(R.id.txtRegName);
        TextView address = (TextView) findViewById(R.id.txtRegAddress);
        TextView email = (TextView) findViewById(R.id.txtRegEmail);
        TextView telNo = (TextView) findViewById(R.id.txtRegContactno);
        TextView password = (TextView) findViewById(R.id.txtRegPassword);
        TextView company = (TextView) findViewById(R.id.txtRegCompany);
        TextView cnf = (TextView) findViewById(R.id.txtRegCnfPass);

        if (password.getText().toString().equals(cnf.getText().toString())) {
            String[] ar = {name.getText().toString(), email.getText().toString(), telNo.getText().toString(), address.getText().toString(), company.getText().toString(), password.getText().toString()};
            new Register().execute(ar);
        }else{
            Toast.makeText(getApplicationContext(), "Password Does not match", Toast.LENGTH_LONG).show();
        }
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    private class Register extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            StringBuilder sb = new StringBuilder();

            try {

                url = new URL(new HTTPConnection().getURL() + "advertiser/add");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("name", params[0]);
                jsonParam.put("email", params[1]);
                jsonParam.put("contactNo", params[2]);
                jsonParam.put("address", params[3]);
                jsonParam.put("company", params[4]);
                jsonParam.put("password", params[5]);
                jsonParam.put("idadvertiser", "");


                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(jsonParam.toString());
                wr.flush();
                wr.close();

                Log.d("doInBackground(Resp)", jsonParam.toString());
                int HttpResult = conn.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    phaseLogger.write(params[1] + "Registerd");
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);

                } else {
                    phaseLogger.write(params[1] + "Registration Faild");

                    Toast.makeText(getApplicationContext(), conn.getResponseMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            dismissDialog(progress_bar_type);

        }
    }
}

