package com.example.projetomobiledb1teste.data.db.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projetomobiledb1teste.data.db.BitCoinDBContract;
import com.example.projetomobiledb1teste.data.network.BitCoinPair;
import com.example.projetomobiledb1teste.data.network.BitCoinPairList;
import com.example.projetomobiledb1teste.ui.CallbackInterface;
import com.example.projetomobiledb1teste.data.db.DbHelper;
import com.example.projetomobiledb1teste.data.network.GetBitCoinDataService;
import com.example.projetomobiledb1teste.data.network.RetrofitInstanceBitCoin;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BitCoinData {
    private SQLiteDatabase mDBData;
    private DbHelper dbHelper;
    private CallbackInterface callback;
    private Context appContext;
    private int[] dia;
    private float[] valor;

    public float[] getValueFromDB(){
        if (valor ==null){
            dbHelper = DbHelper.getInstance(appContext);
            mDBData= dbHelper.getReadableDatabase();
            Cursor cursor = mDBData.query(BitCoinDBContract.bitCoinEntry.TABLE_NAME,
                    new String[] {BitCoinDBContract.bitCoinEntry.COLUMN_VALOR},
                    null,
                    null,
                    null,
                    null,null);
            int size = cursor.getCount();
            valor = new float[size];
            cursor.moveToFirst();

            for (int i=0;i<size;i++){
                valor[i] = cursor.getFloat(cursor.getColumnIndex(BitCoinDBContract.bitCoinEntry.COLUMN_VALOR));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return valor;
    }

    public int[] getDateFromDB(){
        if (dia ==null){
            dbHelper = DbHelper.getInstance(appContext);
            mDBData= dbHelper.getReadableDatabase();
            Cursor cursor = mDBData.query(BitCoinDBContract.bitCoinEntry.TABLE_NAME,
                    new String[] {BitCoinDBContract.bitCoinEntry.COLUMN_DATA},
                    null,
                    null,
                    null,
                    null,null);
            int size = cursor.getCount();
            dia = new int[size];
            cursor.moveToFirst();

            for (int i=0;i<size;i++){
                dia[i] = cursor.getInt(cursor.getColumnIndex(BitCoinDBContract.bitCoinEntry.COLUMN_DATA));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return dia;}

    public void droptable(){
        dbHelper = DbHelper.getInstance(appContext);
        mDBData = dbHelper.getWritableDatabase();
        mDBData.execSQL("DROP TABLE IF EXISTS " + BitCoinDBContract.bitCoinEntry.TABLE_NAME + ";");
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                BitCoinDBContract.bitCoinEntry.TABLE_NAME + " ("+
                BitCoinDBContract.bitCoinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BitCoinDBContract.bitCoinEntry.COLUMN_DATA + " INTEGER NOT NULL," +
                BitCoinDBContract.bitCoinEntry.COLUMN_VALOR + " REAL NOT NULL" + ");";
        mDBData.execSQL(SQL_CREATE_TABLE);
    }

    public void getDataFromAPIUsingRetrofit(Context appContext, CallbackInterface callbackInterface){
        this.appContext = appContext;
        callback = callbackInterface;
        final GetBitCoinDataService bitCoinDataService = RetrofitInstanceBitCoin.getRetrofitInstance().create(GetBitCoinDataService.class);

        Call<BitCoinPairList> call = bitCoinDataService.getBitCoinData();

        call.enqueue(new Callback<BitCoinPairList>() {
            @Override
            public void onResponse(Call<BitCoinPairList> call, Response<BitCoinPairList> response) {
                generateDataBase(response.body().getBitCoinPairList());
                callback.callback(true);
            }

            @Override
            public void onFailure(Call<BitCoinPairList> call, Throwable t) {
                callback.callback(false);
            }
        });

    }
    private void generateDataBase(ArrayList<BitCoinPair> bitCoinPairArrayList){
        int date;
        float value;

        dbHelper = DbHelper.getInstance(appContext);
        mDBData= dbHelper.getWritableDatabase();

        Cursor cursor = mDBData.rawQuery("SELECT * FROM " + BitCoinDBContract.bitCoinEntry.TABLE_NAME, null);
        if (!cursor.moveToFirst()){
            int tam = bitCoinPairArrayList.size();
            for (int count= 0;count<tam;count++) {
                date = bitCoinPairArrayList.get(count).getDate();
                value = bitCoinPairArrayList.get(count).getValue();
                ContentValues cv = new ContentValues();
                cv.put(BitCoinDBContract.bitCoinEntry.COLUMN_DATA,date);
                cv.put(BitCoinDBContract.bitCoinEntry.COLUMN_VALOR,value);
                mDBData.insert(BitCoinDBContract.bitCoinEntry.TABLE_NAME,null,cv);
            }
        }
        cursor.close();
    }

}

