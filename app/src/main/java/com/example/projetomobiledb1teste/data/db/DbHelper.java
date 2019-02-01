package com.example.projetomobiledb1teste.data.db;

import android.content.Context;
import android.database.Cursor;
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
                BitCoinDBContract.BitCoinEntry.TABLE_NAME + " ("+
                BitCoinDBContract.BitCoinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BitCoinDBContract.BitCoinEntry.COLUMN_DATA + " INTEGER NOT NULL," +
                BitCoinDBContract.BitCoinEntry.COLUMN_VALOR + " REAL NOT NULL" + ");";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ BitCoinDBContract.BitCoinEntry.TABLE_NAME);
        onCreate(db);
    }

    public float[] getValueFromDB(){
        float[] valor;
            SQLiteDatabase mDBData= mInstance.getReadableDatabase();
            Cursor cursor = mDBData.query(BitCoinDBContract.BitCoinEntry.TABLE_NAME,
                    new String[] {BitCoinDBContract.BitCoinEntry.COLUMN_VALOR},
                    null,
                    null,
                    null,
                    null,null);
            int size = cursor.getCount();
            valor = new float[size];
            cursor.moveToFirst();

            for (int i=0;i<size;i++){
                valor[i] = cursor.getFloat(cursor.getColumnIndex(BitCoinDBContract.BitCoinEntry.COLUMN_VALOR));
                cursor.moveToNext();
            }
            cursor.close();
        return valor;
    }

    public int[] getDateFromDB(){
        int[] dia;
            SQLiteDatabase mDBData= mInstance.getReadableDatabase();
            Cursor cursor = mDBData.query(BitCoinDBContract.BitCoinEntry.TABLE_NAME,
                    new String[] {BitCoinDBContract.BitCoinEntry.COLUMN_DATA},
                    null,
                    null,
                    null,
                    null,null);
            int size = cursor.getCount();
            dia = new int[size];
            cursor.moveToFirst();

            for (int i=0;i<size;i++){
                dia[i] = cursor.getInt(cursor.getColumnIndex(BitCoinDBContract.BitCoinEntry.COLUMN_DATA));
                cursor.moveToNext();
            }
            cursor.close();

        return dia;
    }

        public void droptable(){
            SQLiteDatabase mDBData = mInstance.getWritableDatabase();
            mDBData.execSQL("DROP TABLE IF EXISTS " + BitCoinDBContract.BitCoinEntry.TABLE_NAME + ";");
            final String SQL_CREATE_TABLE = "CREATE TABLE " +
                    BitCoinDBContract.BitCoinEntry.TABLE_NAME + " ("+
                    BitCoinDBContract.BitCoinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BitCoinDBContract.BitCoinEntry.COLUMN_DATA + " INTEGER NOT NULL," +
                    BitCoinDBContract.BitCoinEntry.COLUMN_VALOR + " REAL NOT NULL" + ");";
            mDBData.execSQL(SQL_CREATE_TABLE);
        }
}