package com.example.tiborkocik.camx_dochadzka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    List<String> spinnerName;
    List<String> spinnerReason;
    List<String> spinnerTransport;
    TextView time;
    BroadcastReceiver _broadcastReceiver;
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        time = (TextView)findViewById(R.id.timeText);
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");

                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                                String formatDate = df.format(c.getTime());

                                String strDate = mdformat.format(calendar.getTime()) + "   " + formatDate;
                                time.setText(strDate);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();



        spinnerName =  new ArrayList<String>();
        spinnerName.add("Martin Mrazko");
        spinnerName.add("Tomas Granat");
        spinnerName.add("Jakub Grega");
        spinnerName.add("Tibor Kocik");

        spinnerReason = new ArrayList<String>();
        spinnerReason.add("Príchod");
        spinnerReason.add("Odchod na obed");
        spinnerReason.add("Príchod z obeda");
        spinnerReason.add("Odchod");

        spinnerTransport = new ArrayList<String>();
        spinnerTransport.add("Neplatena");
        spinnerTransport.add("Auto");
        spinnerTransport.add("1x Autobus");
        spinnerTransport.add("2x Autobus");




        ArrayAdapter<String> adapterName = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, spinnerName);
        adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItemsName = (Spinner) findViewById(R.id.nameSpinner);
        sItemsName.setAdapter(adapterName);


        ArrayAdapter<String> adapterReason = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, spinnerReason);
        adapterReason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItemsReason = (Spinner) findViewById(R.id.reasonSpinner);
        sItemsReason.setAdapter(adapterReason);

        ArrayAdapter<String> adapterTransport = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, spinnerTransport);
        adapterReason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItemsTransport = (Spinner) findViewById(R.id.transportSpinner);
        sItemsTransport.setAdapter(adapterTransport);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
