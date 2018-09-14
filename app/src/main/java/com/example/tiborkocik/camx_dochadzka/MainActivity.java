package com.example.tiborkocik.camx_dochadzka;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
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
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    List<String> spinnerName;
    List<String> spinnerReason;
    List<String> spinnerTransport;
    TextView time, dovodTW, dopravaTW, odpracovaneHod;
    DatabaseHelper myDb;
    Button submit;
    Spinner sItemsName, sItemsReason, sItemsTransport;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ZAMESTNANCI> arrayList = new ArrayList<>();
    RadioButton addDataCheckBox, viewDataCheckbox;
    ArrayAdapter<String> adapterReason;
    ArrayAdapter<String> adapterTransport;
    ArrayAdapter<String> adapterTransportGray, adapterReasonGray;
    Thread countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myDb = new DatabaseHelper(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //region Time
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

        //endregion

        odpracovaneHod = (TextView)findViewById(R.id.textView7);






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






        adapterReason = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, spinnerReason);
        adapterReason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItemsReason = (Spinner) findViewById(R.id.reasonSpinner);
        sItemsReason.setAdapter(adapterReason);

        adapterTransport = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, spinnerTransport);
        adapterTransport.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItemsTransport = (Spinner) findViewById(R.id.transportSpinner);
        sItemsTransport.setAdapter(adapterTransport);


        adapterReasonGray = new ArrayAdapter<String>(
                this, R.layout.spinner_item_gray, spinnerReason);
        adapterTransportGray = new ArrayAdapter<String>(
                this, R.layout.spinner_item_gray, spinnerTransport);

        submit = (Button)findViewById(R.id.submitBtn);

        //endregion

        DatabaseWorkers workersDb = new DatabaseWorkers(this);
        Cursor allWorkers = workersDb.getAllData();


        spinnerName =  new ArrayList<String>();
        if(allWorkers.getCount()>0)
        {
            for (int i = 0; i < allWorkers.getCount(); i++) {
                allWorkers.moveToPosition(i);
                spinnerName.add(allWorkers.getString(allWorkers.getColumnIndex("MENO")));
            }

            ArrayAdapter<String> adapterName = new ArrayAdapter<String>(
                    this, R.layout.spinner_layout, spinnerName);
            adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sItemsName = (Spinner) findViewById(R.id.nameSpinner);
            sItemsName.setAdapter(adapterName);
            updateSpinner();
            AddData();
        }
        else
        {
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("V databáze sa nenachádzajú žiadny zamestnanci")
                            .setPositiveButton("Pridať zamestnancov", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this, workersActivity.class);
                                    MainActivity.this.startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Upozornenie")
                            .show();
                }
            });

        }



    }


    public void cleardbView()
    {
        if(countdown == null) {
            countdown = new Thread() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(20000);
                        if (!Thread.interrupted()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView = (RecyclerView) findViewById(R.id.dbView);
                                    odpracovaneHod.setText("");
                                    arrayList.clear();
                                    layoutManager = new LinearLayoutManager(MainActivity.this);
                                    adapter.notifyDataSetChanged();
                                    return;
                                }
                            });
                        } else {
                            return;
                        }


                    } catch (InterruptedException e) {
                    }
                    return;
                }
            };
        }
        else
        {
            countdown.interrupt();
            countdown = new Thread() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(15000);
                        if (!Thread.interrupted()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView = (RecyclerView) findViewById(R.id.dbView);
                                    odpracovaneHod.setText("");
                                    arrayList.clear();
                                    layoutManager = new LinearLayoutManager(MainActivity.this);
                                    adapter.notifyDataSetChanged();
                                    return;
                                }
                            });
                        } else {
                            return;
                        }


                    } catch (InterruptedException e) {
                    }
                    return;
                }
            };
        }
            countdown.start();
    }

    public void updateSpinner()
    {


        SimpleDateFormat curDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formatDate = df.format(c.getTime());
        String strDate = mdformat.format(calendar.getTime()) + "   " + formatDate;
        String datetimeString = strDate;
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

        } catch (Exception e) {
            Log.i("Exception",e.toString());
        }

        //region conditions
        if (getData(sItemsName.getSelectedItem().toString(), "PRICHOD") == null && diffDays == 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
        {
            sItemsReason.setSelection(0);
        }

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED" ) == null && diffDays == 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
        {
            sItemsReason.setSelection(1);
        }

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null && diffDays == 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
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

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") == null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") != null && diffDays == 0)
        {
            sItemsReason.setSelection(3);
        }

        else if (getData(sItemsName.getSelectedItem().toString(), "PRICHOD") == null && diffDays > 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
        {
            sItemsReason.setSelection(0);
        }

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED" ) == null && diffDays > 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
        {
            sItemsReason.setSelection(1);
        }

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null && diffDays > 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
        {
            sItemsReason.setSelection(2);
        }

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null && diffDays > 0)
        {
            sItemsReason.setSelection(3);
        }

        else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") == null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") != null && diffDays > 0)
        {
            sItemsReason.setSelection(3);
        }

        else
        {
            sItemsReason.setSelection(0);
        }
        //endregion

        sItemsName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SimpleDateFormat curDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formatDate = df.format(c.getTime());
                String strDate = mdformat.format(calendar.getTime()) + "   " + formatDate;
                String datetimeString = strDate;
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

                //region Conditions

                if (getData(sItemsName.getSelectedItem().toString(), "PRICHOD") == null && diffDays == 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
                {
                    sItemsReason.setSelection(0);
                }

                else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") == null && diffDays == 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
                {
                    sItemsReason.setSelection(1);
                }

                else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null && diffDays == 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
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

                else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") == null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") != null && diffDays == 0)
                {
                    sItemsReason.setSelection(3);
                }

                else if (getData(sItemsName.getSelectedItem().toString(), "PRICHOD") == null && diffDays > 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
                {
                    sItemsReason.setSelection(0);
                }

                else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED" ) == null && diffDays > 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
                {
                    sItemsReason.setSelection(1);
                }

                else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") == null && diffDays > 0 && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null)
                {
                    sItemsReason.setSelection(2);
                }

                else if(getData(sItemsName.getSelectedItem().toString(), "PRICHOD") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED") != null && getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA") != null && getData(sItemsName.getSelectedItem().toString(), "ODCHOD") == null && diffDays > 0)
                {
                    sItemsReason.setSelection(3);
                }

                else
                {
                    sItemsReason.setSelection(0);
                }
                //endregion
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
                String menoGetData = sItemsName.getSelectedItem().toString();

                Boolean isInserted = false;
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
                id = getLatestId("ID");
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

                } catch (Exception e) {
                }

                String prichodGetData = getData(sItemsName.getSelectedItem().toString(), "PRICHOD");
                String odchObedGetData = getData(sItemsName.getSelectedItem().toString(), "ODCHOD_NA_OBED");
                String prichObedGetData = getData(sItemsName.getSelectedItem().toString(), "PRICHOD_Z_OBEDA");
                String odchodGetData = getData(sItemsName.getSelectedItem().toString(), "ODCHOD");
                if (prichodGetData == null && diffDays == 0 && sItemsReason.getSelectedItem().toString() == "Príchod") {
                    Cursor check = myDb.getDataByIDequalsOne();
                    if (id == 1 && check.getCount() == 0)
                    {
                        isInserted = myDb.insertData(id, menoGetData, datetimeString, null, null, null, sItemsTransport.getSelectedItem().toString());
                        id++;
                    } else {
                        id++;
                        isInserted = myDb.insertData(id, menoGetData, datetimeString, null, null, null, sItemsTransport.getSelectedItem().toString());
                    }
                    aktualizeHours();
                } else if (prichodGetData != null && diffDays > 0) {
                    SQLiteDatabase db = myDb.getWritableDatabase();
                    Cursor res = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE MENO = '" + sItemsName.getSelectedItem().toString() + "'" + " ORDER BY ID DESC LIMIT 1", null);
                    res.moveToFirst();
                    if(res.getString(res.getColumnIndex("ODCHOD_NA_OBED")) == null && sItemsReason.getSelectedItem().toString() == "Odchod na obed") {
                       id = myDb.getLatestID(menoGetData);
                       isInserted = myDb.updateData(id, menoGetData, prichodGetData, datetimeString, null, null, sItemsTransport.getSelectedItem().toString());
                    }
                    else if(res.getString(res.getColumnIndex("PRICHOD_Z_OBEDA")) == null && sItemsReason.getSelectedItem().toString() == "Príchod z obeda")
                    {
                        id = myDb.getLatestID(menoGetData);
                        isInserted = myDb.updateData(id, menoGetData, prichodGetData, odchObedGetData, datetimeString, null, sItemsTransport.getSelectedItem().toString());
                    }
                    else if(res.getString(res.getColumnIndex("ODCHOD")) == null && sItemsReason.getSelectedItem().toString() == "Odchod")
                    {
                        id = myDb.getLatestID(menoGetData);
                        isInserted = myDb.updateData(id, menoGetData, prichodGetData, odchObedGetData, prichObedGetData, datetimeString, sItemsTransport.getSelectedItem().toString());
                    }
                    else
                    {
                        id++;
                        isInserted = myDb.insertData(id, menoGetData, datetimeString, null, null, null, sItemsTransport.getSelectedItem().toString());
                    }
                    aktualizeHours();
                } else if (prichodGetData != null && diffDays == 0 && odchObedGetData == null && sItemsReason.getSelectedItem().toString() == "Odchod na obed") {
                    id = myDb.getLatestID(menoGetData);
                    isInserted = myDb.updateData(id, menoGetData, prichodGetData, datetimeString, null, null, sItemsTransport.getSelectedItem().toString());
                    aktualizeHours();
                } else if (prichodGetData != null && diffDays == 0 && odchObedGetData != null && prichObedGetData == null && sItemsReason.getSelectedItem().toString() == "Príchod z obeda") {
                    id = myDb.getLatestID(menoGetData);
                    isInserted = myDb.updateData(id, menoGetData, prichodGetData, odchObedGetData, datetimeString, null, sItemsTransport.getSelectedItem().toString());
                    aktualizeHours();
                } else if (prichodGetData != null && diffDays == 0 && odchObedGetData != null && prichObedGetData != null && odchodGetData == null && sItemsReason.getSelectedItem().toString() == "Odchod") {
                    id = myDb.getLatestID(menoGetData);
                    isInserted = myDb.updateData(id, menoGetData, prichodGetData, odchObedGetData, prichObedGetData, datetimeString, sItemsTransport.getSelectedItem().toString());
                    aktualizeHours();
                } else if(prichodGetData != null && odchObedGetData == null && prichObedGetData == null && odchodGetData == null && diffDays == 0 && sItemsReason.getSelectedItem().toString() == "Odchod") {
                    id = myDb.getLatestID(menoGetData);
                    isInserted = myDb.updateData(id, menoGetData, prichodGetData, null, null, datetimeString, sItemsTransport.getSelectedItem().toString());
                    aktualizeHours();
                }


                if (isInserted == true) {
                    Toast.makeText(MainActivity.this, "Inserted", Toast.LENGTH_LONG).show();
                    Cursor cursor = myDb.getAllData();
                    if (cursor.getCount() == 0) {
                        showMessage("Error", "Nothing found");
                        return;
                    }
                    recyclerView = (RecyclerView) findViewById(R.id.dbView);
                    arrayList.clear();
                    layoutManager = new LinearLayoutManager(MainActivity.this);




                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1, GridLayoutManager.VERTICAL, false));
                    recyclerView.setHasFixedSize(true);
                    cursor.moveToFirst();
                    do {

                        ZAMESTNANCI zamestnanci = new ZAMESTNANCI(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
                        arrayList.add(zamestnanci);

                    } while (cursor.moveToNext());




                    myDb.close();

                    adapter = new RecyclerAdapter(arrayList);
                    recyclerView.setAdapter(adapter);


                    cleardbView();
                    isInserted = false;
                } else
                    Toast.makeText(MainActivity.this, "Záznam nepridaný alebo už bol vyplnený", Toast.LENGTH_LONG).show();


                updateSpinner();
            }
        });

    }

    public void aktualizeHours()
    {
        String newPrichData = getData(sItemsName.getSelectedItem().toString(), "PRICHOD");
        String workingTime;
        if(newPrichData != null) {
            workingTime = substrTime(newPrichData, sItemsName.getSelectedItem().toString());
            odpracovaneHod.setText(workingTime);
        }
    }

    public String getData(String meno, String stlpec)
    {
        return myDb.getValue(meno, stlpec);
    }

    public String getDataWithID(int id, String meno, String stlpec)
    {
        return myDb.getValueWithID(id, meno, stlpec);
    }

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public String substrTime(String prichTime, String name)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        String actualTime;
        actualTime = sdf.format(c.getTime());
        SQLiteDatabase db = myDb.getWritableDatabase();
        int id = myDb.getLatestID(sItemsName.getSelectedItem().toString());
        Cursor data = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE MENO = '" + sItemsName.getSelectedItem().toString() + "' AND ID = '" + id + "' ORDER BY ID DESC", null);
        Date date1 = null;
        Date date2 = null;
        int days;
        int hours;
        int minutes;
        data.moveToFirst();
        if(data.getString(data.getColumnIndex("ODCHOD_NA_OBED")) != null && data.getString(data.getColumnIndex("PRICHOD_Z_OBEDA")) == null)
        {
            try
            {
                date1 = sdf.parse(prichTime);
                date2 = sdf.parse(actualTime);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            long resultTime = 0;
            resultTime = date2.getTime() - date1.getTime();
            days = (int) TimeUnit.MILLISECONDS.toDays(resultTime);
            hours = (int) (TimeUnit.MILLISECONDS.toHours(resultTime) - TimeUnit.DAYS.toHours(days) + days*24);
            minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(resultTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(resultTime)));
            minutes = minutes / 6;

        }
        else if(data.getString(data.getColumnIndex("PRICHOD_Z_OBEDA")) != null && data.getString(data.getColumnIndex("ODCHOD")) == null)
        {
            actualTime = data.getString(data.getColumnIndex("ODCHOD_NA_OBED"));
            try
            {
                date1 = sdf.parse(prichTime);
                date2 = sdf.parse(actualTime);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            long resultTime = 0;
            resultTime = date2.getTime() - date1.getTime();
            days = (int) TimeUnit.MILLISECONDS.toDays(resultTime);
            hours = (int) (TimeUnit.MILLISECONDS.toHours(resultTime) - TimeUnit.DAYS.toHours(days) + days*24);
            minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(resultTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(resultTime)));
            minutes = minutes / 6;

        }
        else if(data.getString(data.getColumnIndex("ODCHOD")) != null)
        {
            if(data.getString(data.getColumnIndex("ODCHOD_NA_OBED")) == null && data.getString(data.getColumnIndex("PRICHOD_Z_OBEDA")) == null)
            {
                try {
                    date1 = sdf.parse(prichTime);
                    date2 = sdf.parse(actualTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long resultTime = 0;
                resultTime = date2.getTime() - date1.getTime();
                days = (int) TimeUnit.MILLISECONDS.toDays(resultTime);
                hours = (int) (TimeUnit.MILLISECONDS.toHours(resultTime) - TimeUnit.DAYS.toHours(days) + days*24);
                minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(resultTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(resultTime)));
                minutes = minutes / 6;

            }
            else
            {
                Date odchodnaobed = null, prichodzobeda = null;

                try {
                    date1 = sdf.parse(prichTime);
                    date2 = sdf.parse(actualTime);
                    odchodnaobed = sdf.parse(data.getString(data.getColumnIndex("ODCHOD_NA_OBED")));
                    prichodzobeda = sdf.parse(data.getString(data.getColumnIndex("PRICHOD_Z_OBEDA")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long resultTime = 0;
                long breakTime = prichodzobeda.getTime() - odchodnaobed.getTime();
                resultTime = date2.getTime() - date1.getTime() - breakTime;
                days = (int) TimeUnit.MILLISECONDS.toDays(resultTime);
                hours = (int) (TimeUnit.MILLISECONDS.toHours(resultTime) - TimeUnit.DAYS.toHours(days) + days*24);
                minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(resultTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(resultTime)));
                minutes = minutes / 6;

            }
        }
        else
        {
            try
            {
                date1 = sdf.parse(prichTime);
                date2 = sdf.parse(actualTime);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            long resultTime = 0;
            resultTime = date2.getTime() - date1.getTime();
            days = (int) TimeUnit.MILLISECONDS.toDays(resultTime);
            hours = (int) (TimeUnit.MILLISECONDS.toHours(resultTime) - TimeUnit.DAYS.toHours(days) + days*24);
            minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(resultTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(resultTime)));
            minutes = minutes / 6;

        }
        db.execSQL("UPDATE " + DatabaseHelper.TABLE_NAME + " SET " + DatabaseHelper.COL_ODPR + " = '" + hours + "." + minutes + "' WHERE " + DatabaseHelper.COL_ID + "=" + id + " AND " + DatabaseHelper.COL_1 + "='" + sItemsName.getSelectedItem().toString() + "'");
        return hours + "," + minutes;
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
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
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

        if (id == R.id.nav_addData) {
            // Handle the camera action
        } else if (id == R.id.nav_viewData) {
            Intent intent = new Intent(MainActivity.this, viewActivity.class);
            MainActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_updateData) {

        }
        else if (id == R.id.nav_zamestnanci) {
            Intent intent = new Intent(MainActivity.this, workersActivity.class);
            MainActivity.this.startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_odobZamestnanca) {
            Intent intent = new Intent(MainActivity.this, odobrzamesActivity.class);
            MainActivity.this.startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
