package com.example.tiborkocik.camx_dochadzka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
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
    DatabaseHelper myDb;
    Button submit;
    Spinner sItemsName, sItemsReason, sItemsTransport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myDb = new DatabaseHelper(this);

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
        sItemsName = (Spinner) findViewById(R.id.nameSpinner);
        sItemsName.setAdapter(adapterName);


        ArrayAdapter<String> adapterReason = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, spinnerReason);
        adapterReason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItemsReason = (Spinner) findViewById(R.id.reasonSpinner);
        sItemsReason.setAdapter(adapterReason);

        ArrayAdapter<String> adapterTransport = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, spinnerTransport);
        adapterReason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItemsTransport = (Spinner) findViewById(R.id.transportSpinner);
        sItemsTransport.setAdapter(adapterTransport);

        submit = (Button)findViewById(R.id.submitBtn);

        updateSpinner();


        AddData();

    }


    public void updateSpinner()
    {
        if (getData(sItemsName.getSelectedItem().toString(), "PRICHOD") == null)
        {
            sItemsReason.setSelection(0);
        }

        if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") == null)
        {
            sItemsReason.setSelection(1);
        }

        if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null)
        {
            sItemsReason.setSelection(2);
        }

        if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
        {
            sItemsReason.setSelection(3);
        }

        if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") != null)
        {
            sItemsReason.setSelection(3);
        }

        sItemsName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (getData(sItemsName.getSelectedItem().toString(), "PRICHOD") == null)
                {
                    sItemsReason.setSelection(0);
                }

                if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") == null)
                {
                    sItemsReason.setSelection(1);
                }

                if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null)
                {
                    sItemsReason.setSelection(2);
                }

                if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
                {
                    sItemsReason.setSelection(3);
                }

                if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") != null)
                {
                    sItemsReason.setSelection(3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    public void AddData()
    {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isInserted = null;
                int id = 0;
                String datetimeString = time.getText().toString();
                SimpleDateFormat curDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                SimpleDateFormat desiredDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String desiredDate = null;
                String oldDesiredDate = null;
                long diffDays = 0;
                try {
                    Date date = curDateFormat.parse(datetimeString);
                    desiredDate = desiredDateFormat.format(date);
                    String olddate = getData(sItemsName.getSelectedItem().toString(), "PRICHOD");
                    Date date2 = curDateFormat.parse(olddate);
                    oldDesiredDate = desiredDateFormat.format(date2);
                    long diff = Math.abs(date.getTime() - date2.getTime());
                    diffDays = diff / (24 * 60 * 60 * 1000);

                } catch (Exception e) {}
                id = getLatestId("ID");
                String prichodGetData = getData(sItemsName.getSelectedItem().toString(), "PRICHOD");
                String odchObedGetData = getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED");
                String prichObedGetData = getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA");
                String odchodGetData = getData(sItemsName.getSelectedItem().toString(), "ODCHOD");

                if(prichodGetData == null && diffDays == 0)
                {
                    id++;
                    isInserted = myDb.insertData(id, sItemsName.getSelectedItem().toString(), datetimeString, null, null, null, sItemsTransport.getSelectedItem().toString());
                }

                else if(prichodGetData != null && diffDays>0)
                {
                    id++;
                    isInserted = myDb.insertData(id, sItemsName.getSelectedItem().toString(), datetimeString, null, null, null, sItemsTransport.getSelectedItem().toString());
                }

                else if(prichodGetData != null && diffDays == 0 && odchObedGetData == null)
                {
                    isInserted = myDb.updateData(sItemsName.getSelectedItem().toString(), prichodGetData, datetimeString, null, null, sItemsTransport.getSelectedItem().toString());
                }

                else if(prichodGetData != null && diffDays == 0 && odchObedGetData != null && prichObedGetData == null)
                {
                    isInserted = myDb.updateData(sItemsName.getSelectedItem().toString(), prichodGetData, odchObedGetData, datetimeString, null, sItemsTransport.getSelectedItem().toString());
                }

                else if(prichodGetData != null && diffDays == 0 && odchObedGetData != null && prichObedGetData != null && odchodGetData == null)
                {
                    isInserted = myDb.updateData(sItemsName.getSelectedItem().toString(), prichodGetData, odchObedGetData, prichObedGetData, datetimeString, sItemsTransport.getSelectedItem().toString());
                }

                else if(prichodGetData != null && diffDays == 0 && odchObedGetData != null && prichObedGetData != null && odchodGetData != null)
                {
                    isInserted = myDb.updateData(sItemsName.getSelectedItem().toString(), prichodGetData, odchObedGetData, prichObedGetData, odchodGetData, sItemsTransport.getSelectedItem().toString());
                }



                if(isInserted == true) {
                    Toast.makeText(MainActivity.this, "Inserted", Toast.LENGTH_LONG).show();
                    Cursor res = myDb.getAllData();

                    //if(myDb)
                    if(res.getCount() == 0)
                    {
                        showMessage("Error", "Nothing found");
                        return;
                    }

                    StringBuffer buffer = new StringBuffer();
                    while(res.moveToNext())
                    {
                        buffer.append("ID: " + res.getString(0) + "\n");
                        buffer.append("MENO: " + res.getString(1) + "\n");
                        buffer.append("PRICHOD: " + res.getString(2) + "\n");
                        buffer.append("ODCHOD_NA_OBED: " + res.getString(3) + "\n");
                        buffer.append("PRICHOD_Z_OBEDA: " + res.getString(4) + "\n");
                        buffer.append("ODCHOD: " + res.getString(5) + "\n");
                        buffer.append("POZNAMKA: " + res.getString(6) + "\n\n");
                    }
                    showMessage("Data", buffer.toString());
                    isInserted = false;
                }
                else
                    Toast.makeText(MainActivity.this, "Not Inserted", Toast.LENGTH_LONG).show();



                updateSpinner();
            }

        });

    }

    public String getData(String meno, String stlpec)
    {
        return myDb.getValue(meno, stlpec);
    }

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public int getLatestId(String id)
    {
        String idString = myDb.getHighestValueOfId();
        int res = 0;
        try
        {
            res = Integer.parseInt(idString);
            return res;
        }
        catch (Exception e)
        {
            return 0;
        }
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
