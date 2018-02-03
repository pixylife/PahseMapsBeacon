package com.phasemaps.azio.phasemapsbeacon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.phasemaps.azio.phasemapsbeacon.model.Advertiser;
import com.phasemaps.azio.phasemapsbeacon.model.Beacon;
import com.phasemaps.azio.phasemapsbeacon.res.HTTPConnection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewBeaconActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private Advertiser advertiser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_beacon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        advertiser = (Advertiser) intent.getSerializableExtra("advertiser");
        String id= intent.getStringExtra("id");
        TextView txtId = (TextView) findViewById(R.id.viewIdtextView);
        txtId.setText(id);
        setTitle("View Beacon");
        String[] ar = {id};
        new ViewBeacon().execute(ar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtId = (TextView) findViewById(R.id.viewIdtextView);
                TextView sub = (TextView) findViewById(R.id.viewSubjecttextView);
                TextView desc = (TextView) findViewById(R.id.viewDesctextView);


                Intent i = new Intent(ViewBeaconActivity.this, UpdateBeaconActivity.class);
                i.putExtra("advertiser", advertiser);
                i.putExtra("id",txtId.getText().toString());
                i.putExtra("subject",sub.getText().toString());
                i.putExtra("description",desc.getText().toString());

                startActivity(i);
            }
        });



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

    private class ViewBeacon extends AsyncTask<String, Void, Beacon> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @Override
        protected Beacon doInBackground(String... params) {
            URL url = null;
            StringBuilder sb = new StringBuilder();
            Beacon beacon=new Beacon();
            try {

                url = new URL(new HTTPConnection().getURL() + "beacon/get");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("idBeacon", params[0]);




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


                    if (!sb.toString().equals("")) {
                        Gson gson = new Gson();
                         beacon = gson.fromJson(sb.toString(), Beacon.class);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), conn.getResponseMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return beacon;
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
        protected void onPostExecute(Beacon beacon) {
            TextView subject = (TextView) findViewById(R.id.viewSubjecttextView);
            TextView desc = (TextView) findViewById(R.id.viewDesctextView);
            subject.setText(beacon.getSubject());
            desc.setText(beacon.getDescription());
            dismissDialog(progress_bar_type);

        }
    }

}
