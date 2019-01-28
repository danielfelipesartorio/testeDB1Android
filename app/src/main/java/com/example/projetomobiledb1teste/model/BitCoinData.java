package com.example.projetomobiledb1teste.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projetomobiledb1teste.presenter.MainActivityPresenter;
import com.example.projetomobiledb1teste.utilities.DbHelper;
import com.example.projetomobiledb1teste.utilities.NetWorkUtilities;
import com.example.projetomobiledb1teste.utilities.bitcoinContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.net.URL;

public class BitCoinData {
    private String timespan = "31days";
    private SQLiteDatabase mDBData;
    private DbHelper dbHelper;
    public Context mContext;


    URL urlBitCoin = NetWorkUtilities.buildUrl("https://api.blockchain.info/charts/market-price?timespan=" + timespan);
    public int[] dia;
    public double[] valor;
    public MainActivityPresenter presenterForMain;

    public void getData (Context context, MainActivityPresenter presenter) {
        mContext = context;
        presenterForMain = presenter;
        DbHelper mDBHelper = new DbHelper(context);
        mDBData = mDBHelper.getReadableDatabase();

        Cursor cursor = mDBData.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+bitcoinContract.bitCoinEntry.TABLE_NAME+"';",null);
        if(cursor.getCount()>1){
            cursor = mDBData.rawQuery("SELECT * FROM " + bitcoinContract.bitCoinEntry.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                presenterForMain.updateData(mContext);
            } else {
                new bitCoinAsyncTask().execute(urlBitCoin);
            }
            cursor.close();
        }else {
            new bitCoinAsyncTask().execute(urlBitCoin);
        }
        cursor.close();
    }

    public double[] getValor(Context context){
        if (valor ==null){
            dbHelper = new DbHelper(context);
            mDBData= dbHelper.getReadableDatabase();
            Cursor cursor = mDBData.query(bitcoinContract.bitCoinEntry.TABLE_NAME,
                    new String[] {bitcoinContract.bitCoinEntry.COLUMN_VALOR},
                    null,
                    null,
                    null,
                    null,null);
            int size = cursor.getCount();
            valor = new double[size];
            cursor.moveToFirst();

            for (int i=0;i<size;i++){
                valor[i] = cursor.getFloat(cursor.getColumnIndex(bitcoinContract.bitCoinEntry.COLUMN_VALOR));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return valor;
    }

    public int[] getDia(Context context){
        if (dia ==null){
            dbHelper = new DbHelper(context);
            mDBData= dbHelper.getReadableDatabase();
            Cursor cursor = mDBData.query(bitcoinContract.bitCoinEntry.TABLE_NAME,
                    new String[] {bitcoinContract.bitCoinEntry.COLUMN_DATA},
                    null,
                    null,
                    null,
                    null,null);
            int size = cursor.getCount();
            dia = new int[size];
            cursor.moveToFirst();

            for (int i=0;i<size;i++){
                dia[i] = cursor.getInt(cursor.getColumnIndex(bitcoinContract.bitCoinEntry.COLUMN_DATA));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return dia;}

    public class bitCoinAsyncTask extends android.os.AsyncTask<URL,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject jsonString = null;
            try {
                jsonString = new JSONObject(NetWorkUtilities.getResponseFromHttpUrl(urls[0]));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(JSONObject jsonString) {
            JSONArray dadosJson = null;
            try {
                dadosJson = jsonString.getJSONArray("values");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                int count = 0;
                int tam = dadosJson.length();
                dia = new int[tam];
                valor = new double[tam];
                for (count= 0;count<tam;count++) {
                    JSONObject jsonLineItem = (JSONObject) dadosJson.getJSONObject(count);
                    dia[count] = jsonLineItem.getInt("x");
                    valor[count] = jsonLineItem.getDouble("y");
                    ContentValues cv = new ContentValues();
                    cv.put(bitcoinContract.bitCoinEntry.COLUMN_DATA,dia[count]);
                    cv.put(bitcoinContract.bitCoinEntry.COLUMN_VALOR,valor[count]);
                    mDBData.insert(bitcoinContract.bitCoinEntry.TABLE_NAME,null,cv);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            presenterForMain.updateData(mContext);
        }
    }
    public void droptable(){
        mDBData.execSQL("DROP TABLE IF EXISTS " + bitcoinContract.bitCoinEntry.TABLE_NAME + ";");
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                bitcoinContract.bitCoinEntry.TABLE_NAME + " ("+
                bitcoinContract.bitCoinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                bitcoinContract.bitCoinEntry.COLUMN_DATA + " INTEGER NOT NULL," +
                bitcoinContract.bitCoinEntry.COLUMN_VALOR + " REAL NOT NULL" + ");";
        mDBData.execSQL(SQL_CREATE_TABLE);
    }
}

