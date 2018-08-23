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
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY, " + COL_1 +" TEXT, " + COL_2 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(int id, String meno, String telefon)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id);
        contentValues.put(COL_1, meno);
        contentValues.put(COL_2, telefon);
        long res = db.insert(TABLE_NAME, null, contentValues);
        if(res == -1)
            return false;
        else
            return true;
    }

}
