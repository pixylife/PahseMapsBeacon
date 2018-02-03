package com.phasemaps.azio.phasemapsbeacon;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phasemaps.azio.phasemapsbeacon.model.Advertiser;
import com.phasemaps.azio.phasemapsbeacon.model.Beacon;
import com.phasemaps.azio.phasemapsbeacon.res.CustomBeaconAdapter;
import com.phasemaps.azio.phasemapsbeacon.res.HTTPConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends ListActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    Advertiser advertiser = new Advertiser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        advertiser = (Advertiser) intent.getSerializableExtra("advertiser");

        String name= advertiser.getName();
        String email = advertiser.getEmail();
        String company = advertiser.getCompany();


        setTitle("Phase Beacon");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(MainMenu.this, AddBeaconActivity.class);
                intent.putExtra("advertiser", advertiser);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);

        TextView nameView= (TextView) hView.findViewById(R.id.nametextView);
        nameView.setText(name);

        TextView companyView= (TextView) hView.findViewById(R.id.companytextView);
        companyView.setText(company);

        TextView emailView= (TextView) hView.findViewById(R.id.emailtextView);
        emailView.setText(email);

        new BeaconGetAllTask(this,this.getListView()).execute(advertiser.getIdadvertiser().toString());




        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                String selected =((TextView)view.findViewById(R.id.idtextView)).getText().toString();
                Intent intent = new Intent(MainMenu.this, ViewBeaconActivity.class);
                intent.putExtra("advertiser", advertiser);
                intent.putExtra("id", selected);

                startActivity(intent);


            }

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            Intent i = new Intent(MainMenu.this, LoginActivity.class);
        startActivity(i);
        finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addBeacon) {
            Intent intent = new Intent(MainMenu.this, AddBeaconActivity.class);
            intent.putExtra("advertiser", advertiser);
            startActivity(intent);
        } else if (id == R.id.nav_editProfile) {
            Intent intent = new Intent(MainMenu.this, EditProfileActivity.class);
            intent.putExtra("advertiser", advertiser);
            startActivity(intent);
        }
        else if (id == R.id.nav_logOut) {
            Intent i = new Intent(MainMenu.this, LoginActivity.class);
            startActivity(i);
            finishAffinity();
        }
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    private class BeaconGetAllTask extends AsyncTask<String, Integer, List<Beacon>> {
        private Context mContext;
        ListView view;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }


        public BeaconGetAllTask(Context context, ListView listView) {
            mContext = context.getApplicationContext();
            view =listView;
        }
        @Override
        protected List<Beacon> doInBackground(String... params) {
            URL url = null;
            StringBuilder sb = new StringBuilder();
            List<Beacon> beacons = new ArrayList<>();

            try {

                url = new URL(new HTTPConnection().getURL() + "beacon/getBeaconsByUser");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.connect();

                String json="{\"idadvertiser\": "+params[0]+"}";
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();

                int HttpResult = conn.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();


                        Gson gson = new Gson();

                        Type founderListType = new TypeToken<ArrayList<Beacon>>() {
                        }.getType();

                        beacons = gson.fromJson(sb.toString(), founderListType);
                    }


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return beacons;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(List<Beacon> result) {
             dismissDialog(progress_bar_type);
          CustomBeaconAdapter adpAdapter = new CustomBeaconAdapter(mContext, result);
          view.setAdapter(adpAdapter);

        }
    }
}
