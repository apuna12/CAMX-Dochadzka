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

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "dochadzka.db";
    public static final String TABLE_NAME = "dochadzka_tabulka";
    public static final String COL_1 = "MENO";
    public static final String COL_2 = "PRICHOD";
    public static final String COL_3 = "ODCHOD_NA_OBED";
    public static final String COL_4 = "PRICHOD_Z_OBEDA";
    public static final String COL_5 = "ODCHOD";
    public static final String COL_6 = "POZNAMKA";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " ("+ COL_1 +" TEXT PRIMARY KEY, " + COL_2 + " DATETIME, " + COL_3 + " DATETIME, " + COL_4 + " DATETIME, " + COL_5 + " DATETIME, " + COL_6 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String meno, String prichod, String odchObed, String prichObed, String odchod, String poznamka)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, meno);
        contentValues.put(COL_2, prichod);
        contentValues.put(COL_3, odchObed);
        contentValues.put(COL_4, prichObed);
        contentValues.put(COL_5, odchod);
        contentValues.put(COL_6, poznamka);
        long res = db.insert(TABLE_NAME, null, contentValues);
        if(res == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }


    public Boolean updateData(String meno, String prichod, String odchObed, String prichObed, String odchod, String poznamka)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, meno);
        contentValues.put(COL_2, prichod);
        contentValues.put(COL_3, odchObed);
        contentValues.put(COL_4, prichObed);
        contentValues.put(COL_5, odchod);
        contentValues.put(COL_6, poznamka);
        db.update(TABLE_NAME, contentValues, "MENO = ?", new String[] {meno});
        return true;
    }

    public String getValue(String meno , String stlpec)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT " + stlpec + " FROM " + TABLE_NAME + " WHERE MENO = '" + meno + "'" + " ORDER BY PRICHOD DESC LIMIT 1", null);
        if(res.getCount()>0)
        {
            if (res != null) {
                res.moveToFirst();
                String result = res.getString(res.getColumnIndex(stlpec));
                return result;
            }
        }
        else
        {
            return null;
        }
        return null; // nemal by tu dojst
    }
    public void setValue(String stlpec, String hodnota)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(stlpec, hodnota);
        db.insert(TABLE_NAME, null, contentValues);
    }

}
