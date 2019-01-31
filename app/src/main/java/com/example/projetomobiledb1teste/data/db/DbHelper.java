package com.example.projetomobiledb1teste.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bitcoin2.db";
    private static final int DATABASE_VERSION = 1;
    private static DbHelper mInstance = null;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DbHelper getInstance(Context context){
        if (mInstance==null){
            mInstance = new DbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                BitCoinDBContract.bitCoinEntry.TABLE_NAME + " ("+
                BitCoinDBContract.bitCoinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BitCoinDBContract.bitCoinEntry.COLUMN_DATA + " INTEGER NOT NULL," +
                BitCoinDBContract.bitCoinEntry.COLUMN_VALOR + " REAL NOT NULL" + ");";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ BitCoinDBContract.bitCoinEntry.TABLE_NAME);
        onCreate(db);
    }
}
