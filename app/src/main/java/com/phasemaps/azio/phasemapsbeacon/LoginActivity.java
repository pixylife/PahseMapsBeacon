package com.phasemaps.azio.phasemapsbeacon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.phasemaps.azio.phasemapsbeacon.model.Advertiser;
import com.phasemaps.azio.phasemapsbeacon.res.HTTPConnection;
import com.phasemaps.azio.phasemapsbeacon.res.PhaseLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
private PhaseLogger phaseLogger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        phaseLogger = new PhaseLogger(getBaseContext());
    }

    public void signIn(View view) {
        TextView email = (TextView) findViewById(R.id.txtLogInEmail);
        TextView password = (TextView) findViewById(R.id.txtLogInPassword);
        String[] ar = {email.getText().toString(), password.getText().toString()};
        new Login().execute(ar);
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

    private class Login extends AsyncTask<String, Void, String> {


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

                url = new URL(new HTTPConnection().getURL() + "advertiser/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", params[0]);
                jsonParam.put("password", params[1]);


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
                        Gson gson =new Gson();
                        Advertiser advertiser = gson.fromJson(sb.toString(), Advertiser.class);

                        phaseLogger.write(advertiser.getEmail() +" Login Success");
                        Intent i = new Intent(LoginActivity.this, MainMenu.class);
                        i.putExtra("advertiser", advertiser);
                        startActivity(i);
                        finish();
                    } else {
                        Handler h = new Handler(Looper.getMainLooper());
                        h.post(new Runnable() {
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Invalid email or password")
                                        .setCancelable(false)
                                        .setTitle("Error")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }
                } else {
                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            phaseLogger.write("Error in Login");

                            builder.setMessage("Invalid email or password")
                                    .setTitle("Error")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });

                }

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
