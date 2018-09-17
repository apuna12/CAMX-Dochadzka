package com.example.tiborkocik.camx_dochadzka;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class viewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView time;
    EditText fromDay;
    Button submit;
    DatabaseHelper myDb;
    RecyclerView recyclerView;
    ArrayList arrayList;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    Spinner sItemsName;
    List<String> spinnerName;
    Thread countdown;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fromDay = (EditText)findViewById(R.id.editTextView1);
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        fromDay.setText(sdf.format(myCalendar.getTime()));
        myDb = new DatabaseHelper(this);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        fromDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(viewActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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


        DatabaseWorkers workersDb = new DatabaseWorkers(this);
        Cursor allWorkers = workersDb.getAllData();


        spinnerName =  new ArrayList<String>();
        spinnerName.add("Všetko");
        if(allWorkers.getCount()>0)
        {
            for (int i = 0; i < allWorkers.getCount(); i++) {
                allWorkers.moveToPosition(i);
                spinnerName.add(allWorkers.getString(allWorkers.getColumnIndex("MENO")));
            }

            sItemsName = (Spinner) findViewById(R.id.nameSpinnerView);
            ArrayAdapter<String> adapterName = new ArrayAdapter<String>(
                    this, R.layout.spinner_layout, spinnerName);
            adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sItemsName.setAdapter(adapterName);
            //region Submit btn
            submit = (Button) findViewById(R.id.submitBtnView);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SQLiteDatabase db = myDb.getWritableDatabase();
                    Cursor viewDataCursor;
                    if(sItemsName.getSelectedItem().toString() == "Všetko")
                    {
                        viewDataCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME + " ORDER BY ID DESC", null);
                    }
                    else
                    {
                        viewDataCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE MENO='" + sItemsName.getSelectedItem().toString() + "' ORDER BY ID DESC", null);
                    }
                    if(viewDataCursor.getCount()>0)
                    {
                        recyclerView = (RecyclerView) findViewById(R.id.dbViewView);
                        if(arrayList != null)
                            arrayList.clear();
                        else
                            arrayList = new ArrayList();
                        layoutManager = new LinearLayoutManager(viewActivity.this);

                        recyclerView.setLayoutManager(new GridLayoutManager(viewActivity.this, 1, GridLayoutManager.VERTICAL, false));
                        recyclerView.setHasFixedSize(true);
                        //SQLiteDatabase sqLiteDatabase = myDb.getReadableDatabase();
                        viewDataCursor.moveToFirst();
                        Date tableDate;
                        Date givenDate;
                        do {

                            ZAMESTNANCI zamestnanci = new ZAMESTNANCI(viewDataCursor.getString(1), viewDataCursor.getString(2), viewDataCursor.getString(3), viewDataCursor.getString(4), viewDataCursor.getString(5), viewDataCursor.getString(6), viewDataCursor.getString(7));
                            SimpleDateFormat dateComparer = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                            try {
                                givenDate = dateComparer.parse(fromDay.getText().toString());
                                tableDate = dateFormat.parse(zamestnanci.getPrichod().toString());
                                String tempDate = dateComparer.format(tableDate);
                                tableDate = dateComparer.parse(tempDate);
                                if (TimeUnit.MILLISECONDS.toMillis(tableDate.getTime())>=TimeUnit.MILLISECONDS.toMillis(givenDate.getTime()))
                                    arrayList.add(zamestnanci);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        } while (viewDataCursor.moveToNext());
                        myDb.close();

                        if(arrayList.size() == 0)
                        {
                            Toast.makeText(viewActivity.this, "Záznam nenájdený", Toast.LENGTH_LONG).show();
                        }
                        adapter = new RecyclerAdapter(arrayList);
                        recyclerView.setAdapter(adapter);
                        cleardbView();
                    }
                    else
                    {
                        Toast.makeText(viewActivity.this, "Záznam nenájdený", Toast.LENGTH_LONG).show();
                    }

                }
            });
            //endregion
        }

        else
        {
            submit = (Button) findViewById(R.id.submitBtnView);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(viewActivity.this);
                    builder.setMessage("V databáze sa nenachádzajú žiadny zamestnanci")
                            .setPositiveButton("Pridať zamestnancov", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(viewActivity.this, workersActivity.class);
                                    viewActivity.this.startActivity(intent);
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

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMAN);

        fromDay.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addData) {
            Intent intent = new Intent(viewActivity.this, MainActivity.class);
            viewActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_viewData) {

        } else if (id == R.id.nav_updateData) {

        } else if (id == R.id.nav_zamestnanci) {
            Intent intent = new Intent(viewActivity.this, workersActivity.class);
            viewActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_odobZamestnanca) {
            Intent intent = new Intent(viewActivity.this, odobrzamesActivity.class);
            viewActivity.this.startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void cleardbView()
    {
        if(countdown == null) {
            countdown = new Thread() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(15000);
                        if (!Thread.interrupted()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView = (RecyclerView) findViewById(R.id.dbViewView);
                                    arrayList.clear();
                                    layoutManager = new LinearLayoutManager(viewActivity.this);
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
                                    recyclerView = (RecyclerView) findViewById(R.id.dbViewView);
                                    arrayList.clear();
                                    layoutManager = new LinearLayoutManager(viewActivity.this);
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
}
