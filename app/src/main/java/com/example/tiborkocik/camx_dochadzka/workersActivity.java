package com.example.tiborkocik.camx_dochadzka;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
import java.util.concurrent.TimeUnit;

public class workersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView time;
    EditText name,surname,telephone;
    Button button;
    DatabaseWorkers workDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        workDb = new DatabaseWorkers(this);

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

        button = (Button) findViewById(R.id.submitBtnworkers);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = (EditText) findViewById(R.id.editTextworkers1);
                surname = (EditText) findViewById(R.id.editTextworkers2);
                telephone = (EditText) findViewById(R.id.editTextworkers3);
                SQLiteDatabase sqLiteDatabase = workDb.getWritableDatabase();
                Cursor allData = workDb.getAllData();
                int id;
                boolean isInserted=false;
                allData.moveToFirst();
                if(allData.getCount()>0)
                {
                    boolean checker = workDb.nameChecker(name.getText().toString(), surname.getText().toString());
                    id = Integer.parseInt(workDb.getHighestValueOfId());

                    if(checker == false)
                    {
                        id++;
                        isInserted = workDb.insertData(id,name.getText().toString(), surname.getText().toString(), telephone.getText().toString());
                    }
                    else
                    {
                        Toast.makeText(workersActivity.this, "Uvedené meno sa v databáze už nachádza", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    isInserted = workDb.insertData(1,name.getText().toString(), surname.getText().toString(), telephone.getText().toString());
                }

                if(isInserted)
                {
                    Toast.makeText(workersActivity.this, "Zamestnanec pridaný", Toast.LENGTH_LONG).show();
                }
                workDb.close();
            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addData) {
            Intent intent = new Intent(workersActivity.this, MainActivity.class);
            workersActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_viewData) {
            Intent intent = new Intent(workersActivity.this, viewActivity.class);
            workersActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_updateData) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
