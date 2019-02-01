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

    public float[] getValueFromDB(){
        dbHelper = DbHelper.getInstance(appContext);
        return dbHelper.getValueFromDB();
    }

    public int[] getDateFromDB(){
        dbHelper = DbHelper.getInstance(appContext);
        return dbHelper.getDateFromDB();
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
        dbHelper.droptable();
        mDBData= dbHelper.getWritableDatabase();

        Cursor cursor = mDBData.rawQuery("SELECT * FROM " + BitCoinDBContract.BitCoinEntry.TABLE_NAME, null);
        if (!cursor.moveToFirst()){
            int tam = bitCoinPairArrayList.size();
            for (int count= 0;count<tam;count++) {
                date = bitCoinPairArrayList.get(count).getDate();
                value = bitCoinPairArrayList.get(count).getValue();
                ContentValues cv = new ContentValues();
                cv.put(BitCoinDBContract.BitCoinEntry.COLUMN_DATA,date);
                cv.put(BitCoinDBContract.BitCoinEntry.COLUMN_VALOR,value);
                mDBData.insert(BitCoinDBContract.BitCoinEntry.TABLE_NAME,null,cv);
            }
        }
        cursor.close();
    }
}