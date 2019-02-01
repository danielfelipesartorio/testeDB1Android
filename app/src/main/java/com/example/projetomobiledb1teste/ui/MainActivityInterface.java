package com.example.projetomobiledb1teste.ui;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public interface MainActivityInterface {
    void updateScreen(int[] data,float[] valor, LineGraphSeries<DataPoint> dadosParaGrafico);
    void semConexao();
}
