package com.example.tiborkocik.camx_dochadzka;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OdobrzamesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView time;
    EditText name,surname,telephone;
    List<String> spinnerName;
    Spinner sItemsName;
    Button button;
    DatabaseWorkers workDb;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ZOZNAM_ZAMESTNANCOV> arrayList = new ArrayList<>();;
    Thread countdown;
    Boolean isInserted = false;
    ArrayAdapter<String> adapterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odobrzamest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_odobrZam);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        workDb = new DatabaseWorkers(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
        final DatabaseWorkers workDB = new DatabaseWorkers(this);
        final Cursor allData = workDB.getAllData();
        final SQLiteDatabase db = workDB.getWritableDatabase();
        spinnerName =  new ArrayList<String>();

        if(allData.getCount()>0)
        {
            for (int i = 0; i < allData.getCount(); i++) {
                allData.moveToPosition(i);
                spinnerName.add(allData.getString(allData.getColumnIndex("MENO")));
            }


            sItemsName = (Spinner) findViewById(R.id.nameSpinnerodobr);
            adapterName = new ArrayAdapter<String>(
                    this, R.layout.spinner_layout, spinnerName);
            adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sItemsName.setAdapter(adapterName);
        }


        button = (Button) findViewById(R.id.submitBtnodobr);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(allData.getCount()>0)
                {
                    Cursor checkName = db.rawQuery("SELECT * FROM " + DatabaseWorkers.TABLE_NAME + " WHERE MENO='" + sItemsName.getSelectedItem().toString() + "'",null);
                    checkName.moveToFirst();
                    if(checkName.getCount()>0)
                    {


                        AlertDialog.Builder builder = new AlertDialog.Builder(OdobrzamesActivity.this);
                        builder.setMessage("Naozaj si prajete vymazať zamestnanca '" + sItemsName.getSelectedItem().toString() + "' z databázy?")
                                .setPositiveButton("Áno", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        db.execSQL("DELETE FROM " + DatabaseWorkers.TABLE_NAME + " WHERE MENO='" + sItemsName.getSelectedItem().toString() + "'");
                                        isInserted = true;
                                        Intent intent = new Intent(OdobrzamesActivity.this, OdobrzamesActivity.class);
                                        OdobrzamesActivity.this.startActivity(intent);
                                        finish();
                                        Toast.makeText(OdobrzamesActivity.this, "Zamestnanec '" + sItemsName.getSelectedItem().toString() + "' bol z databázy vymazaný", Toast.LENGTH_LONG).show();
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
                    else
                    {
                        Toast.makeText(OdobrzamesActivity.this,"Meno sa v databáze nenachádza",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OdobrzamesActivity.this);
                    builder.setMessage("V databáze sa nenachádzajú žiadny zamestnanci")
                            .setPositiveButton("Pridať zamestnancov", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(OdobrzamesActivity.this, WorkersActivity.class);
                                    OdobrzamesActivity.this.startActivity(intent);
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

                if(isInserted)
                {
                    Toast.makeText(OdobrzamesActivity.this,"Zamestnanec " + sItemsName.getSelectedItem().toString() + " bol z databázy odobratý",Toast.LENGTH_LONG).show();
                    spinnerName.clear();
                    for (int i = 0; i < allData.getCount(); i++) {
                        allData.moveToPosition(i);
                        spinnerName.add(allData.getString(allData.getColumnIndex("MENO")));
                    }

                    sItemsName.setAdapter(adapterName);
                }

            }
        });


    }


    public void cleardbView()
    {
        if(countdown == null) {
            countdown = new Thread() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(60000);
                        if (!Thread.interrupted()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView = (RecyclerView) findViewById(R.id.dbView);
                                    arrayList.clear();
                                    layoutManager = new LinearLayoutManager(OdobrzamesActivity.this);
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
                                    arrayList.clear();
                                    layoutManager = new LinearLayoutManager(OdobrzamesActivity.this);
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


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addData) {
            Intent intent = new Intent(OdobrzamesActivity.this, MainActivity.class);
            OdobrzamesActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_viewData) {
            Intent intent = new Intent(OdobrzamesActivity.this, ViewActivity.class);
            OdobrzamesActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_updateData) {

        } else if (id == R.id.nav_zamestnanci) {
            Intent intent = new Intent(OdobrzamesActivity.this, WorkersActivity.class);
            OdobrzamesActivity.this.startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_importDB) {
            Intent intent = new Intent(OdobrzamesActivity.this, ImportDBActivity.class);
            OdobrzamesActivity.this.startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
