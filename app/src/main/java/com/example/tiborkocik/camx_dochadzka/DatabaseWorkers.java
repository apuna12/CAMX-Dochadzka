package com.example.tiborkocik.camx_dochadzka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by tibor.kocik on 11-May-18.
 */

public class DatabaseWorkers extends SQLiteOpenHelper{

    Boolean newVersionChecker = false;

    public static final String DATABASE_NAME = "zamestnanci.db";
    public static final String TABLE_NAME = "zamestnanci_tabulka";
    public static final String COL_ID = "ID";
    public static final String COL_1 = "MENO";
    public static final String COL_2 = "TELEFON";

    public DatabaseWorkers(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        if(newVersionChecker==true)
        {
            sqLiteDatabase.execSQL("CREATE TABLE temporaryWorkerDB AS SELECT * FROM " + TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY, " + COL_1 +" TEXT, " + COL_2 + " TEXT)");
            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_NAME + " SELECT * FROM temporaryWorkerDB");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS temporaryWorkerDB");
            newVersionChecker = false;
        }
        else
        {
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY, " + COL_1 +" TEXT, " + COL_2 + " TEXT)");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(newVersion>oldVersion)
        {
            newVersionChecker = true;
            onCreate(sqLiteDatabase);
        }
        else
        {

            newVersionChecker = false;

        }
    }

    public int getLatestID(String name, String surname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String wholeName = surname + ", " + name;
        Cursor res = db.rawQuery("SELECT ID FROM " + TABLE_NAME + " WHERE MENO='" + wholeName + "' ORDER BY ID DESC LIMIT 1", null);
        if(res.getCount()>0)
        {
            if(res != null)
            {
                res.moveToFirst();
                int result = res.getInt(res.getColumnIndex("ID"));
                return  result;
            }
        }
        return 0; //nedojde tu
    }

    public String getHighestValueOfId()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT ID" + " FROM " + TABLE_NAME + " ORDER BY ID DESC LIMIT 1", null);
        if(res.getCount()>0)
        {
            if (res != null) {
                res.moveToFirst();
                String result = res.getString(res.getColumnIndex("ID"));
                return result;
            }
        }
        else
        {
            return "1";
        }
        return null; // nemal by tu dojst
    }

    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }



    public boolean insertData(int id, String name, String surname, String telephone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id);
        contentValues.put(COL_1, surname + ", " + name);
        contentValues.put(COL_2, telephone);

        long res = db.insert(TABLE_NAME, null, contentValues);
        if(res == -1)
            return false;
        else
            return true;
    }

    public boolean nameChecker(String name, String surname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String wholeName = surname + ", " + name;
        Cursor check = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE MENO = '" + wholeName + "'",null);
        if(check.getCount()>0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
