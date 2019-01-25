package com.example.projetomobiledb1teste.presenter;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;

import com.example.projetomobiledb1teste.model.BitCoinData;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivityPresenter {
    private BitCoinData bitCoinData;
    private Contrato contrato;
    public SQLiteDatabase mDB;
    Context mContext;

    public MainActivityPresenter(Contrato contrato){
        this.contrato = contrato;
        this.bitCoinData = new BitCoinData();
    }

    public void pegaDados (Context context){
        mContext = context;
        bitCoinData.getData(context, this);
    }

    public LineGraphSeries<DataPoint> updateData(Context context){
        double[] valor = bitCoinData.getValor(context);
        int[] data = bitCoinData.getDia(context);

        int tamanho = valor.length;

        DataPoint[] dataPoint =  new DataPoint[tamanho];

        for (int i=0;i<tamanho;i++) {
            dataPoint[i] = new DataPoint(data[i],valor[i]);
        }

        LineGraphSeries<DataPoint> seriesParaDataPoint = new LineGraphSeries<>(dataPoint);
        contrato.updateScreen(data[tamanho-1],valor[tamanho-1],seriesParaDataPoint);
        return null;
    }

    public interface Contrato{
        void updateScreen(int data,double valor, LineGraphSeries<DataPoint> dadosParaGrafico);
    }

    public void refresh(){
        bitCoinData.droptable();
        bitCoinData.getData(mContext,this);
    }
}
