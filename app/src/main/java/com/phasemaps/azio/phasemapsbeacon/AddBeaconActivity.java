package com.phasemaps.azio.phasemapsbeacon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.phasemaps.azio.phasemapsbeacon.model.Advertiser;
import com.phasemaps.azio.phasemapsbeacon.res.HTTPConnection;
import com.phasemaps.azio.phasemapsbeacon.res.PhaseLogger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddBeaconActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private Advertiser advertiser;
    private PhaseLogger phaseLogger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_beacon);
        Intent intent = getIntent();
        phaseLogger = new PhaseLogger(getBaseContext());
        advertiser = (Advertiser) intent.getSerializableExtra("advertiser");
        setTitle("Add New Beacon");

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
public void addBeacon(View view){
    TextView id = (TextView) findViewById(R.id.idEditText);
    TextView subject = (TextView) findViewById(R.id.subjectEditText);
    TextView description = (TextView) findViewById(R.id.descriptionEditText);
    String[] ar = {id.getText().toString(), subject.getText().toString(), description.getText().toString(), advertiser.getIdadvertiser().toString()};
    new AddNewBeacon().execute(ar);
}

    private class AddNewBeacon extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @Override
        protected String doInBackground(final String... params) {
            URL url = null;
            StringBuilder sb = new StringBuilder();

            try {

                url = new URL(new HTTPConnection().getURL() + "beacon/update");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("idBeacon", params[0]);
                jsonParam.put("subject", params[1]);
                jsonParam.put("description", params[2]);
                JSONObject jparam = new JSONObject();
                jparam.put("idadvertiser", params[3]);
                jsonParam.put("advertiseridadvertiser", jparam);



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
                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {
                            phaseLogger.write(params[0] + "Added");
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(AddBeaconActivity.this, MainMenu.class);
                            i.putExtra("advertiser", advertiser);
                            startActivity(i);
                        }
                    });

                } else {
                    Handler h1 = new Handler(Looper.getMainLooper());
                    h1.post(new Runnable() {
                        public void run() {
                            phaseLogger.write(params[0] + "Error");

                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    });
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
