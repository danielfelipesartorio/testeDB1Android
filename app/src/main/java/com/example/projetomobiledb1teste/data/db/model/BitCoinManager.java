package com.example.projetomobiledb1teste.data.db.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projetomobiledb1teste.data.db.BitCoinDBContract;
import com.example.projetomobiledb1teste.data.network.BitCoinPair;
import com.example.projetomobiledb1teste.data.network.BitCoinPairList;
import com.example.projetomobiledb1teste.data.db.DbHelper;
import com.example.projetomobiledb1teste.data.network.GetBitCoinDataService;
import com.example.projetomobiledb1teste.data.network.RetrofitInstanceBitCoin;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class BitCoinManager {
    private DbHelper dbHelper;
    private Context appContext;

    public Observable<BitCoinData> getData(Context appContext){
        this.appContext = appContext;

        final GetBitCoinDataService bitCoinDataService =
                RetrofitInstanceBitCoin.getRetrofitInstance().create(GetBitCoinDataService.class);

        return bitCoinDataService.getBitCoinData()
                .map(new Function<BitCoinPairList, BitCoinData>() {
                    @Override
                    public BitCoinData apply(BitCoinPairList bitCoinPairList) throws Exception {
                        generateDataBase(bitCoinPairList.getBitCoinPairList());
                        return new BitCoinData(getValueFromDB(), getDateFromDB());
                    }
                });
    }

    private float[] getValueFromDB(){
        dbHelper = DbHelper.getInstance(appContext);
        return dbHelper.getValueFromDB();
    }

    private int[] getDateFromDB(){
        dbHelper = DbHelper.getInstance(appContext);
        return dbHelper.getDateFromDB();
    }

    private void generateDataBase(ArrayList<BitCoinPair> bitCoinPairArrayList){
        int date;
        float value;

        dbHelper = DbHelper.getInstance(appContext);
        dbHelper.dropTable(DbHelper.getInstance(appContext).getWritableDatabase());
        dbHelper.createTable(DbHelper.getInstance(appContext).getWritableDatabase());
        SQLiteDatabase mDBData = dbHelper.getWritableDatabase();

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