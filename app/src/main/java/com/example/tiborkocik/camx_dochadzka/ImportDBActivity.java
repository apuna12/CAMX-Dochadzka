package com.example.tiborkocik.camx_dochadzka;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ImportDBActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    TextView time;
    Spinner database;
    Button findDB,submitBtn;
    File file;
    FileReader fileReader;
    Uri uri;
    InputStream is;
    String path;
    String filePath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importdb);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        final ArrayList<String> databaseOptions = new ArrayList<>();
        databaseOptions.add("Zamestnanci");
        databaseOptions.add("Dochádzka");

        database = (Spinner)findViewById(R.id.nameSpinnerimportDB);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, databaseOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        database.setAdapter(adapter);

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


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }


        findDB = (Button)findViewById(R.id.findDB);
        findDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath());
                intent.setDataAndType(uri, "text/csv");
                startActivity(Intent.createChooser(intent, "Select the file"));

            }
        });

        submitBtn = (Button)findViewById(R.id.submitBtnimportDB);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(database.getSelectedItem().toString() == "Dochádzka")
                {



                    file = new File(uri.getPath());
                    if(file != null) {
                        try {
                            DatabaseHelper helper = new DatabaseHelper(ImportDBActivity.this);
                            SQLiteDatabase db = helper.getWritableDatabase();
                            FileReader fr = new FileReader(file.getAbsoluteFile()); //tu pada
                            BufferedReader br = new BufferedReader(fr);
                            ContentValues contentValues = new ContentValues();
                            String line = "";
                            String tableName = "dochadzka_tabulka";
                            db.beginTransaction();
                            while ((line = br.readLine()) != null) {
                                String[] str = line.split("\t");

                                contentValues.put("ID", str[0]);
                                contentValues.put("MENO", str[1]);
                                contentValues.put("PRICHOD", str[2]);
                                contentValues.put("ODCHOD_NA_OBED", str[3]);
                                contentValues.put("PRICHOD_Z_OBEDA", str[4]);
                                contentValues.put("ODCHOD", str[5]);
                                contentValues.put("POZNAMKA", str[6]);
                                contentValues.put("HODINY", str[0]);
                                db.insert(tableName,null,contentValues);
                            }
                            db.setTransactionSuccessful();
                            db.endTransaction();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else if(database.getSelectedItem().toString() == "Zamestnanci")
                {

                }
                else
                {

                }
            }
        });
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column);
        cursor.close();
        return result;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_addData) {
            Intent intent = new Intent(ImportDBActivity.this, MainActivity.class);
            ImportDBActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_viewData) {
            Intent intent = new Intent(ImportDBActivity.this, ViewActivity.class);
            ImportDBActivity.this.startActivity(intent);
            finish();
        } else if (id == R.id.nav_updateData) {

        }
        else if (id == R.id.nav_zamestnanci) {
            Intent intent = new Intent(ImportDBActivity.this, WorkersActivity.class);
            ImportDBActivity.this.startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_odobZamestnanca) {
            Intent intent = new Intent(ImportDBActivity.this, OdobrzamesActivity.class);
            ImportDBActivity.this.startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_sendDB)
        {
            Intent intent = new Intent(ImportDBActivity.this, SendDBActivity.class);
            ImportDBActivity.this.startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
