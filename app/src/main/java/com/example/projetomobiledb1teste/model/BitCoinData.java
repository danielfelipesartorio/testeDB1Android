package com.example.projetomobiledb1teste.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.projetomobiledb1teste.BitCoinPair;
import com.example.projetomobiledb1teste.BitCoinPairList;
import com.example.projetomobiledb1teste.GetBitCoinDataService;
import com.example.projetomobiledb1teste.RetrofitInstanceBitCoin;
import com.example.projetomobiledb1teste.presenter.MainActivityPresenter;
import com.example.projetomobiledb1teste.utilities.DbHelper;
import com.example.projetomobiledb1teste.utilities.NetWorkUtilities;
import com.example.projetomobiledb1teste.utilities.BitCoinContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BitCoinData {
    private String timespan = "31days";
    private SQLiteDatabase mDBData;
    private DbHelper dbHelper;
    private Context mContext;
    private boolean sucsses;

    private URL urlBitCoin = NetWorkUtilities.buildUrl("https://api.blockchain.info/charts/market-price?timespan=" + timespan);
    private int[] dia;
    private float[] valor;
    private MainActivityPresenter presenterForMain;


    public boolean getDataFromAPI(Context context, MainActivityPresenter presenter) {
        mContext = context;
        presenterForMain = presenter;
        DbHelper mDBHelper = new DbHelper(context);
        mDBData = mDBHelper.getReadableDatabase();

        Cursor cursor = mDBData.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+ BitCoinContract.bitCoinEntry.TABLE_NAME+"';",null);
        if(cursor.getCount()>0){
            cursor = mDBData.rawQuery("SELECT * FROM " + BitCoinContract.bitCoinEntry.TABLE_NAME, null);
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

        return sucsses;
    }

    public float[] getValor(Context context){
        if (valor ==null){
            dbHelper = new DbHelper(context);
            mDBData= dbHelper.getReadableDatabase();
            Cursor cursor = mDBData.query(BitCoinContract.bitCoinEntry.TABLE_NAME,
                    new String[] {BitCoinContract.bitCoinEntry.COLUMN_VALOR},
                    null,
                    null,
                    null,
                    null,null);
            int size = cursor.getCount();
            valor = new float[size];
            cursor.moveToFirst();

            for (int i=0;i<size;i++){
                valor[i] = cursor.getFloat(cursor.getColumnIndex(BitCoinContract.bitCoinEntry.COLUMN_VALOR));
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
            Cursor cursor = mDBData.query(BitCoinContract.bitCoinEntry.TABLE_NAME,
                    new String[] {BitCoinContract.bitCoinEntry.COLUMN_DATA},
                    null,
                    null,
                    null,
                    null,null);
            int size = cursor.getCount();
            dia = new int[size];
            cursor.moveToFirst();

            for (int i=0;i<size;i++){
                dia[i] = cursor.getInt(cursor.getColumnIndex(BitCoinContract.bitCoinEntry.COLUMN_DATA));
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

            if (jsonString ==null){
                sucsses = false;
                return;
            }else{
                sucsses =true;
            }
            try {
                dadosJson = jsonString.getJSONArray("values");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                int count = 0;
                int tam = dadosJson.length();
                dia = new int[tam];
                valor = new float[tam];
                for (count= 0;count<tam;count++) {
                    JSONObject jsonLineItem = (JSONObject) dadosJson.getJSONObject(count);
                    dia[count] = jsonLineItem.getInt("x");
                    valor[count] = Float.valueOf(jsonLineItem.getString("y"));
                    ContentValues cv = new ContentValues();
                    cv.put(BitCoinContract.bitCoinEntry.COLUMN_DATA,dia[count]);
                    cv.put(BitCoinContract.bitCoinEntry.COLUMN_VALOR,valor[count]);
                    mDBData.insert(BitCoinContract.bitCoinEntry.TABLE_NAME,null,cv);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            presenterForMain.updateData(mContext);
            Log.v("teste","acesso a internet");

        }
    }
    public void droptable(){
        mDBData.execSQL("DROP TABLE IF EXISTS " + BitCoinContract.bitCoinEntry.TABLE_NAME + ";");
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                BitCoinContract.bitCoinEntry.TABLE_NAME + " ("+
                BitCoinContract.bitCoinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BitCoinContract.bitCoinEntry.COLUMN_DATA + " INTEGER NOT NULL," +
                BitCoinContract.bitCoinEntry.COLUMN_VALOR + " REAL NOT NULL" + ");";
        mDBData.execSQL(SQL_CREATE_TABLE);
    }

    public void getDataFromAPIUsingRetofit (){
        GetBitCoinDataService bitCoinDataService = RetrofitInstanceBitCoin.getRetrofitInstance().create(GetBitCoinDataService.class);

        Call<BitCoinPairList> call = bitCoinDataService.getBitCoinData();

        call.enqueue(new Callback<BitCoinPairList>() {
            @Override
            public void onResponse(Call<BitCoinPairList> call, Response<BitCoinPairList> response) {
                generateDataBase(response.body().getBitCoinPairList());
            }

            @Override
            public void onFailure(Call<BitCoinPairList> call, Throwable t) {

            }
        });

    }
    private void generateDataBase(ArrayList<BitCoinPair> bitCoinPairArrayList){
        int date;
        float value;

        dbHelper = new DbHelper(null);
        mDBData= dbHelper.getWritableDatabase();

        int tam = bitCoinPairArrayList.size();

        for (int count= 0;count<tam;count++) {
            date = bitCoinPairArrayList.get(count).getDate();
            value = bitCoinPairArrayList.get(count).getValue();
            ContentValues cv = new ContentValues();
            cv.put(BitCoinContract.bitCoinEntry.COLUMN_DATA,date);
            cv.put(BitCoinContract.bitCoinEntry.COLUMN_VALOR,value);
            mDBData.insert(BitCoinContract.bitCoinEntry.TABLE_NAME,null,cv);
        }
    }

}

