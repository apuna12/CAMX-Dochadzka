package com.example.tiborkocik.camx_dochadzka;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    public static final String COL_7 = "ID";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_7 + " INTEGER, " + COL_1 +" TEXT PRIMARY KEY, " + COL_2 + " DATETIME, " + COL_3 + " DATETIME, " + COL_4 + " DATETIME, " + COL_5 + " DATETIME, " + COL_6 + " TEXT)");
        //sqLiteDatabase.execSQL("CREATE TABLE skuska (ID INTEGER, MENO TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}
