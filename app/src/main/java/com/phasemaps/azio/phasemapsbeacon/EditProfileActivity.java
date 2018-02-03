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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditProfileActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private Advertiser advertiser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        advertiser = (Advertiser) intent.getSerializableExtra("advertiser");

        TextView email = (TextView) findViewById(R.id.profileEmailtextView);
        TextView name = (TextView) findViewById(R.id.profileNameeditText);
        TextView company = (TextView) findViewById(R.id.profileCompanyeditText);
        TextView address = (TextView) findViewById(R.id.profileAddresseditText);
        TextView contactNo = (TextView) findViewById(R.id.profileContactNoeditText);

        email.setText(advertiser.getEmail());
        name.setText(advertiser.getName());
        company.setText(advertiser.getCompany());
        address.setText(advertiser.getAddress());
        contactNo.setText(advertiser.getContactNo());
        setTitle("Edit User profile");
    }

    public void editProfile(View view) {
        TextView email = (TextView) findViewById(R.id.profileEmailtextView);
        TextView name = (TextView) findViewById(R.id.profileNameeditText);
        TextView company = (TextView) findViewById(R.id.profileCompanyeditText);
        TextView address = (TextView) findViewById(R.id.profileAddresseditText);
        TextView contactNo = (TextView) findViewById(R.id.profileContactNoeditText);

        TextView oldpass = (TextView) findViewById(R.id.profilePasswordeditText);
        TextView newpass = (TextView) findViewById(R.id.profileNewPasswordeditText);
        TextView cnfPass = (TextView) findViewById(R.id.profileCnfPasswordeditText);
        String password;

        if(oldpass.getText().toString().equals(advertiser.getPassword())) {
            password=advertiser.getPassword();
            if (!newpass.getText().toString().equals("") || !cnfPass.getText().toString().equals("")) {
                if (newpass.getText().toString().equals(cnfPass.getText().toString())) {
                    password = newpass.getText().toString();
                    String[] ar = {advertiser.getIdadvertiser().toString(), email.getText().toString(), name.getText().toString(), contactNo.getText().toString(), address.getText().toString(), company.getText().toString(), password};
                    new UpdateProfile().execute(ar);
                } else {
                    Toast.makeText(getApplicationContext(), "Password Does not match", Toast.LENGTH_LONG).show();
                }
            }else{
                String[] ar = {advertiser.getIdadvertiser().toString(), email.getText().toString(), name.getText().toString(), contactNo.getText().toString(), address.getText().toString(), company.getText().toString(), password};
                new UpdateProfile().execute(ar);
            }

        }else {
            Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_LONG).show();

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

    private class UpdateProfile extends AsyncTask<String, Void, String> {


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

                url = new URL(new HTTPConnection().getURL() + "advertiser/update");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("idadvertiser", params[0]);
                jsonParam.put("email", params[1]);
                jsonParam.put("name", params[2]);
                jsonParam.put("contactNo", params[3]);
                jsonParam.put("address", params[4]);
                jsonParam.put("company", params[5]);
                jsonParam.put("password", params[6]);




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
                            Intent i = new Intent(EditProfileActivity.this, LoginActivity.class);
                            startActivity(i);
                            finishAffinity();
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
