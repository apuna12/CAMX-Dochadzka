package com.example.tiborkocik.camx_dochadzka;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.security.Permission;
import java.security.Permissions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class SendDBActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    TextView time;
    Button submitBtn;
    Spinner databaseName;
    ArrayList<String> dbNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senddb);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }

        dbNames = new ArrayList<>();
        dbNames.add("Dochádzka");
        dbNames.add("Zamestnanci");

        submitBtn = (Button)findViewById(R.id.submitBtnsendDB);


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




        ArrayAdapter adapterReason = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, dbNames);
        adapterReason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        databaseName = (Spinner) findViewById(R.id.nameSpinnersendDB);
        databaseName.setAdapter(adapterReason);



        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exportDB(databaseName.getSelectedItem().toString());
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addData) {
            Intent intent = new Intent(SendDBActivity.this, MainActivity.class);
            SendDBActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_viewData) {
            Intent intent = new Intent(SendDBActivity.this, viewActivity.class);
            SendDBActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_updateData) {

        }
        else if (id == R.id.nav_zamestnanci) {
            Intent intent = new Intent(SendDBActivity.this, workersActivity.class);
            SendDBActivity.this.startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_odobZamestnanca) {
            Intent intent = new Intent(SendDBActivity.this, odobrzamesActivity.class);
            SendDBActivity.this.startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void exportDB(String dbName)
    {
        String filename = "";
        if(dbName == "Dochádzka") {
            DatabaseHelper dbhelper = new DatabaseHelper(this);
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if(!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String date = sdf.format(calendar.getTime()).toString();

            filename = dbName + date + ".csv";
            File file = new File(exportDir,filename);
            try
            {
                file.createNewFile();
                CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
                SQLiteDatabase db = dbhelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM " + dbhelper.TABLE_NAME, null);
                csvWriter.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext())
                {
                    String arrStr[] = {curCSV.getString(0), curCSV.getString(1),curCSV.getString(2), curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6), curCSV.getString(7)};
                    csvWriter.writeNext(arrStr);
                }
                csvWriter.close();
                curCSV.close();
            }
            catch (Exception e)
            {
                Log.e(this.toString(), e.toString(), e);
            }
        }
        else if (dbName == "Zamestnanci")
        {
            DatabaseWorkers dbhelper = new DatabaseWorkers(this);
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if(!exportDir.exists())
            {
                exportDir.mkdirs();
            }
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String date = sdf.format(calendar.getTime()).toString();

            filename = dbName + date + ".csv";
            File file = new File(exportDir,filename);
            try
            {
                file.createNewFile();
                CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
                SQLiteDatabase db = dbhelper.getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM " + dbhelper.TABLE_NAME, null);
                csvWriter.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext())
                {
                    String arrStr[] = {curCSV.getString(0), curCSV.getString(1),curCSV.getString(2)};
                    csvWriter.writeNext(arrStr);
                }
                csvWriter.close();
                curCSV.close();
            }
            catch (Exception e)
            {
                Log.e(this.toString(), e.toString(), e);
            }
        }


        File filelocation = new File(Environment.getExternalStorageDirectory(), filename);
        Uri path = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"robert.vilagi@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, dbName + "-" + time.getText().toString());
        startActivity(Intent.createChooser(emailIntent , "Send email..."));



    }
}
