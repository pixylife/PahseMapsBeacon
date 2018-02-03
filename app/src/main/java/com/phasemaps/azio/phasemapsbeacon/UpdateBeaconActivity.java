package com.phasemaps.azio.phasemapsbeacon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.phasemaps.azio.phasemapsbeacon.model.Advertiser;
import com.phasemaps.azio.phasemapsbeacon.res.HTTPConnection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateBeaconActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private Advertiser advertiser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_beacon);

        Intent intent = getIntent();
        advertiser = (Advertiser) intent.getSerializableExtra("advertiser");
        String id= intent.getStringExtra("id");
        String subject= intent.getStringExtra("subject");
        String desc= intent.getStringExtra("description");

        TextView txtId = (TextView) findViewById(R.id.serialNoTextView);
        TextView txtsubject = (TextView) findViewById(R.id.subjectEditText);
        TextView txtdescription = (TextView) findViewById(R.id.descriptionEditText);
        txtId.setText(id);
        txtsubject.setText(subject);
        txtdescription.setText(desc);
        setTitle("Update Beacon");
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
    public void updateBeacon(View view){
        TextView id = (TextView) findViewById(R.id.serialNoTextView);
        TextView subject = (TextView) findViewById(R.id.subjectEditText);
        TextView description = (TextView) findViewById(R.id.descriptionEditText);
        String[] ar = {id.getText().toString(), subject.getText().toString(), description.getText().toString(), advertiser.getIdadvertiser().toString()};
        new UpdateBeacon().execute(ar);
    }

    private class UpdateBeacon extends AsyncTask<String, Void, String> {


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
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(UpdateBeaconActivity.this, MainMenu.class);
                            i.putExtra("advertiser", advertiser);
                            startActivity(i);
                        }
                    });

                } else {
                    Handler h1 = new Handler(Looper.getMainLooper());
                    h1.post(new Runnable() {
                        public void run() {
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
