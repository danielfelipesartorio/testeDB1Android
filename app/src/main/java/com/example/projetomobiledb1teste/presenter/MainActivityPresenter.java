package com.example.projetomobiledb1teste.presenter;


import com.example.projetomobiledb1teste.CallbackInterface;
import com.example.projetomobiledb1teste.MVPInterfaces;
import com.example.projetomobiledb1teste.model.BitCoinData;

import com.example.projetomobiledb1teste.view.MainActivity;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivityPresenter implements CallbackInterface {
    private BitCoinData bitCoinData;
    private MVPInterfaces.MainActivityInterface contrato;

    public MainActivityPresenter(MVPInterfaces.MainActivityInterface contrato){
        this.contrato = contrato;
        this.bitCoinData = new BitCoinData();
    }

    public void pegaDados (){
        bitCoinData.getDataFromAPIUsingRetofit(MainActivity.getContext(), this);
    }

    private void updateData(){
        float[] valor = bitCoinData.getValue();
        int[] data = bitCoinData.getDate();

        int tamanho = data.length;

        DataPoint[] dataPoint =  new DataPoint[tamanho];

        for (int i=0;i<tamanho;i++) {
            dataPoint[i] = new DataPoint(data[i],valor[i]);
        }

        LineGraphSeries<DataPoint> seriesParaDataPoint = new LineGraphSeries<>(dataPoint);
        contrato.updateScreen(data,valor,seriesParaDataPoint);

    }



    public void refresh(){
        bitCoinData.droptable();
        bitCoinData.getDataFromAPIUsingRetofit(MainActivity.getContext(), this);
    }


    @Override
    public void callback(Boolean success) {
        if (success){
            updateData();
        }else {
            contrato.semConexao();
        }
    }
}
