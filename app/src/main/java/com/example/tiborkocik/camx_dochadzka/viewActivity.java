package com.example.tiborkocik.camx_dochadzka;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


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
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        fromDay.setText(sdf.format(myCalendar.getTime()));
        myDb = new DatabaseHelper(this);

        spinnerName =  new ArrayList<String>();
        spinnerName.add("Martin Mrazko");
        spinnerName.add("Tomas Granat");
        spinnerName.add("Jakub Grega");
        spinnerName.add("Tibor Kocik");

        ArrayAdapter<String> adapterName = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, spinnerName);
        adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItemsName = (Spinner) findViewById(R.id.nameSpinnerView);
        sItemsName.setAdapter(adapterName);



        //region Submit btn
        submit = (Button) findViewById(R.id.submitBtnView);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = myDb.getWritableDatabase();
                Cursor viewDataCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE MENO='" + sItemsName.getSelectedItem().toString() + "' ORDER BY ID DESC", null);
                if(viewDataCursor.getCount()>0)
                {
                    recyclerView = (RecyclerView) findViewById(R.id.dbView);
                    arrayList.clear();
                    layoutManager = new LinearLayoutManager(viewActivity.this);

                    recyclerView.setLayoutManager(new GridLayoutManager(viewActivity.this, 1, GridLayoutManager.VERTICAL, false));
                    recyclerView.setHasFixedSize(true);
                    //SQLiteDatabase sqLiteDatabase = myDb.getReadableDatabase();
                    viewDataCursor.moveToFirst();
                    do {

                        ZAMESTNANCI zamestnanci = new ZAMESTNANCI(viewDataCursor.getString(1), viewDataCursor.getString(2), viewDataCursor.getString(3), viewDataCursor.getString(4), viewDataCursor.getString(5), viewDataCursor.getString(6));
                        arrayList.add(zamestnanci);

                    } while (viewDataCursor.moveToNext());
                    myDb.close();

                    adapter = new RecyclerAdapter(arrayList);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
        //endregion

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
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMAN);

        fromDay.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
