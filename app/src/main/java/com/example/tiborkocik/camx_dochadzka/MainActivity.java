package com.example.tiborkocik.camx_dochadzka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Display;
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
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ZAMESTNANCI> arrayList = new ArrayList<>();


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

        String datetimeString = time.getText().toString();
        SimpleDateFormat curDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        String desiredDate;
        String oldDesiredDate;
        long diffDays = 0;
        long diff = 0;
        Date date;
        Date date2;
        try {
            date = curDateFormat.parse(datetimeString);
            desiredDate = String.valueOf(date.getMonth());
            desiredDate = desiredDate + String.valueOf(date.getDate());
            String olddate = getData(sItemsName.getSelectedItem().toString(), "PRICHOD");
            date2 = curDateFormat.parse(olddate);
            oldDesiredDate = String.valueOf(date2.getMonth());
            oldDesiredDate = oldDesiredDate + String.valueOf(date2.getDate());
            diff = Math.abs(Long.parseLong(desiredDate) - Long.parseLong(oldDesiredDate));
            diffDays = diff;

        } catch (Exception e) {}

        if (getData(sItemsName.getSelectedItem().toString(), "PRICHOD") == null && diffDays == 0)
        {
            sItemsReason.setSelection(0);
        }

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED" ) == null && diffDays == 0)
        {
            sItemsReason.setSelection(1);
        }

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null && diffDays == 0)
        {
            sItemsReason.setSelection(2);
        }

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null && diffDays == 0)
        {
            sItemsReason.setSelection(3);
        }

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") != null && diffDays == 0)
        {
            sItemsReason.setSelection(3);
        }

        else
        {
            sItemsReason.setSelection(0);
        }

        sItemsName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String datetimeString = time.getText().toString();
                SimpleDateFormat curDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                String desiredDate;
                String oldDesiredDate;
                long diffDays = 0;
                long diff = 0;
                Date date;
                Date date2;
                try {
                    date = curDateFormat.parse(datetimeString);
                    desiredDate = String.valueOf(date.getMonth());
                    desiredDate = desiredDate + String.valueOf(date.getDate());
                    String olddate = getData(sItemsName.getSelectedItem().toString(), "PRICHOD");
                    date2 = curDateFormat.parse(olddate);
                    oldDesiredDate = String.valueOf(date2.getMonth());
                    oldDesiredDate = oldDesiredDate + String.valueOf(date2.getDate());
                    diff = Math.abs(Long.parseLong(desiredDate) - Long.parseLong(oldDesiredDate));
                    diffDays = diff;

                } catch (Exception e) {}

                if (getData(sItemsName.getSelectedItem().toString(), "PRICHOD") == null && diffDays == 0)
                {
                    sItemsReason.setSelection(0);
                }

                else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") == null && diffDays == 0)
                {
                    sItemsReason.setSelection(1);
                }

                else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null && diffDays == 0)
                {
                    sItemsReason.setSelection(2);
                }

                else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null && diffDays == 0)
                {
                    sItemsReason.setSelection(3);
                }

                else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") != null && diffDays == 0)
                {
                    sItemsReason.setSelection(3);
                }
                else
                {
                    sItemsReason.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public void AddData()
    {
        updateSpinner();
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
                long diff = 0;
                Date date;
                Date date2;
                try {
                    date = curDateFormat.parse(datetimeString);
                    desiredDate = String.valueOf(date.getMonth());
                    desiredDate = desiredDate + String.valueOf(date.getDate());
                    String olddate = getData(sItemsName.getSelectedItem().toString(), "PRICHOD");
                    date2 = curDateFormat.parse(olddate);
                    oldDesiredDate = String.valueOf(date2.getMonth());
                    oldDesiredDate = oldDesiredDate + String.valueOf(date2.getDate());
                    diff = Math.abs(Long.parseLong(desiredDate) - Long.parseLong(oldDesiredDate));
                    diffDays = diff; /*/ (1000*60*60*24);*/

                } catch (Exception e) {}
                id = getLatestId("ID");
                String menoGetData = sItemsName.getSelectedItem().toString();
                String prichodGetData = getData(sItemsName.getSelectedItem().toString(), "PRICHOD");
                String odchObedGetData = getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED");
                String prichObedGetData = getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA");
                String odchodGetData = getData(sItemsName.getSelectedItem().toString(), "ODCHOD");
                if(prichodGetData == null && diffDays == 0)
                {
                    if(id==1 && myDb.getAllData() == null)
                    {
                        isInserted = myDb.insertData(id, menoGetData, datetimeString, null, null, null, sItemsTransport.getSelectedItem().toString());
                        id++;
                    }
                    else
                    {
                        id++;
                        isInserted = myDb.insertData(id, menoGetData, datetimeString, null, null, null, sItemsTransport.getSelectedItem().toString());
                    }
                }

                else if(prichodGetData != null && diffDays>0)
                {
                    id++;
                    isInserted = myDb.insertData(id, menoGetData, datetimeString, null, null, null, sItemsTransport.getSelectedItem().toString());
                }

                else if(prichodGetData != null && diffDays == 0 && odchObedGetData == null)
                {
                    isInserted = myDb.updateData(id, menoGetData, prichodGetData, datetimeString, null, null, sItemsTransport.getSelectedItem().toString());
                }

                else if(prichodGetData != null && diffDays == 0 && odchObedGetData != null && prichObedGetData == null)
                {
                    isInserted = myDb.updateData(id, menoGetData, prichodGetData, odchObedGetData, datetimeString, null, sItemsTransport.getSelectedItem().toString());
                }

                else if(prichodGetData != null && diffDays == 0 && odchObedGetData != null && prichObedGetData != null && odchodGetData == null)
                {
                    isInserted = myDb.updateData(id, menoGetData, prichodGetData, odchObedGetData, prichObedGetData, datetimeString, sItemsTransport.getSelectedItem().toString());
                }

                else
                {
                    isInserted = true;
                }





                if(isInserted == true) {
                    Toast.makeText(MainActivity.this, "Inserted", Toast.LENGTH_LONG).show();
                    //Cursor res = myDb.getAllData();
                    Cursor cursor = myDb.getAllData();
                    if(cursor.getCount() == 0)
                    {
                        showMessage("Error", "Nothing found");
                        return;
                    }
                    recyclerView = (RecyclerView)findViewById(R.id.dbView);
                    arrayList.clear();
                    layoutManager = new LinearLayoutManager(MainActivity.this);

                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,1,GridLayoutManager.VERTICAL,false));
                    recyclerView.setHasFixedSize(true);
                    //SQLiteDatabase sqLiteDatabase = myDb.getReadableDatabase();
                    cursor.moveToFirst();
                    do {

                        ZAMESTNANCI zamestnanci = new ZAMESTNANCI(cursor.getString(1),cursor.getString(2), cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
                        arrayList.add(zamestnanci);

                    }while (cursor.moveToNext());
                    myDb.close();

                    adapter = new RecyclerAdapter(arrayList);
                    recyclerView.setAdapter(adapter);

                    /*StringBuffer buffer = new StringBuffer();
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
                    showMessage("Data", buffer.toString());*/

                    //startActivity(new Intent(MainActivity.this, DisplayList.class));

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
