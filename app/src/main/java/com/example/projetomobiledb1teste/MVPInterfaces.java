package com.example.projetomobiledb1teste;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public interface MVPInterfaces {
    interface MainActivityInterface {
        void updateScreen(int data,double valor, LineGraphSeries<DataPoint> dadosParaGrafico);
        void semConexao();
    }
}
