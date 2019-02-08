package com.example.projetomobiledb1teste.ui;


import android.util.Log;

import com.example.projetomobiledb1teste.data.db.model.BitCoinData;
import com.example.projetomobiledb1teste.data.db.model.BitCoinManager;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivityPresenter {

    private static final String TAG = MainActivityPresenter.class.getSimpleName();

    private CompositeDisposable compositeDisposable;

    private BitCoinManager bitCoinManager;
    private MainActivityInterface viewInterface;

    MainActivityPresenter(MainActivityInterface viewInterface){
        this.viewInterface = viewInterface;
        this.bitCoinManager = new BitCoinManager();
        this.compositeDisposable = new CompositeDisposable();
    }

    void updateDataFromSource(){
        compositeDisposable.add(
            bitCoinManager.getData(MainActivity.getContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess(), onError())
        );
    }

    private Consumer<BitCoinData> onSuccess() {
        return new Consumer<BitCoinData>() {
            @Override
            public void accept(BitCoinData bitCoinData) throws Exception {
                updateData(bitCoinData);
            }
        };
    }

    private Consumer<Throwable> onError() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG, Log.getStackTraceString(throwable), throwable);
                viewInterface.semConexao();
            }
        };
    }

    private void updateData(BitCoinData bitCoinData){
        float[] valor = bitCoinData.getValue();
        int[] data = bitCoinData.getData();

        int tamanho = data.length;

        DataPoint[] dataPoint =  new DataPoint[tamanho];

        for (int i=0;i<tamanho;i++) {
            dataPoint[i] = new DataPoint(data[i],valor[i]);
        }

        LineGraphSeries<DataPoint> seriesParaDataPoint = new LineGraphSeries<>(dataPoint);
        viewInterface.updateScreen(data,valor,seriesParaDataPoint);
    }

    public void clear() {
        compositeDisposable.clear();
    }

}
