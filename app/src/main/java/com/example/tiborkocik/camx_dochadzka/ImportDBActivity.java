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
import android.os.Build;
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
import android.widget.Toast;

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
    final int SELECT_FOLDER = 1;
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
                intent.setDataAndType(uri, "text/csv");
                startActivityForResult(Intent.createChooser(intent, "Select the file"),
                        SELECT_FOLDER);


            }
        });

        submitBtn = (Button)findViewById(R.id.submitBtnimportDB);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(database.getSelectedItem().toString() == "Dochádzka")
                {



                    file = new File(filePath);
                    if(file != null) {
                        try {
                            DatabaseHelper helper = new DatabaseHelper(ImportDBActivity.this);
                            SQLiteDatabase db = helper.getWritableDatabase();
                            FileReader fr = new FileReader(file.getAbsoluteFile()); //tu pada
                            BufferedReader br = new BufferedReader(fr);
                            ContentValues contentValues = new ContentValues();
                            String line = "";
                            String tableName = "dochadzka_tabulka";
                            ArrayList<String> inner = new ArrayList<String>();
                            ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
                            StringBuilder sb = new StringBuilder();

                            db.beginTransaction();
                            int checker = 0; //urobit to tak ako cvicenia z javy... arraylist arraylistu
                            while ((line = br.readLine()) != null) {
                                for (int i = 0; i < line.length(); i++)
                                {

                                    if (line.charAt(i) != ',') {
                                        if (line.charAt(i) != '"') {
                                            sb.append(line.charAt(i));
                                        }

                                    } else {
                                        inner.add(sb.toString());
                                        sb = new StringBuilder();
                                    }
                                    if(i == line.length()-1)
                                    {
                                        inner.add(sb.toString());
                                        sb = new StringBuilder();
                                    }
                                }

                                data.add(inner);
                                inner = new ArrayList<String>();
                            }
                            ArrayList<ArrayList<String>> unitedData = new ArrayList<ArrayList<String>>();
                            ArrayList<String> rowData = new ArrayList<>();

                            for (int i = 0; i < data.size(); i++) {
                                for (int j = 0; j < data.get(0).size(); j++) {
                                    if(i != 0)
                                    {
                                        if(j == 1)
                                        {
                                            sb = new StringBuilder();
                                            sb.append(data.get(i).get(j) + "," + data.get(i).get(j+1));
                                            rowData.add(sb.toString());
                                            ++j;
                                        }
                                        else
                                        {
                                            if(j == 0)
                                                rowData.add(data.get(i).get(j));
                                            else if(j==data.get(0).size()-1) {
                                                rowData.add(data.get(i).get(j));
                                                rowData.add(data.get(i).get(j+1));
                                            }
                                            else
                                                rowData.add(data.get(i).get(j));
                                        }
                                        /*if(j == data.get(0).size()-1)
                                        {
                                            rowData.add(data.get(i).get(j));
                                        }*/
                                    }
                                    else
                                    {
                                        rowData.add(data.get(0).get(j));
                                    }


                                }
                                unitedData.add(rowData);
                                rowData = new ArrayList<>();
                            }
                            for (int i = 1; i < unitedData.size(); i++) {
                                boolean check = helper.insertData(Integer.parseInt(unitedData.get(i).get(0)), unitedData.get(i).get(1).toString(), unitedData.get(i).get(2).toString(), unitedData.get(i).get(3).toString(), unitedData.get(i).get(4).toString(), unitedData.get(i).get(5).toString(), unitedData.get(i).get(6).toString(), unitedData.get(i).get(7).toString());
                                if(!check) {
                                    Toast.makeText(ImportDBActivity.this, "Niekde nastala chyba", Toast.LENGTH_LONG).show();
                                    break;
                                }
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case SELECT_FOLDER:
                if(requestCode == 1 && data != null)
                {
                    uri = data.getData();
                    filePath = W_FilePathUtil.getPath(this, uri);

                }
                break;
            default:
                break;
        }
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
