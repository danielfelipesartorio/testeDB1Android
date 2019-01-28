package com.example.projetomobiledb1teste.view;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.projetomobiledb1teste.R;
import com.example.projetomobiledb1teste.presenter.MainActivityPresenter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements MainActivityPresenter.Contrato {
    private MainActivityPresenter presenter;
    public GraphView grafico;
    public TextView cardPrincipal;
    private static final String formatoData = "dd/MM/yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardPrincipal = (TextView) findViewById(R.id.card_principal);
        grafico = (GraphView) findViewById(R.id.grafico);

        presenter = new MainActivityPresenter(this);

        presenter.pegaDados(this);

    }

    @Override
    public void updateScreen(int data, double valor, LineGraphSeries<DataPoint> dataPoint) {
        final String dolarFormat = "US$#.00";
        final DecimalFormat df = new DecimalFormat(dolarFormat);
        final DateFormat dateFormat = new DateFormat();

        //define limites do eixo x
        grafico.getViewport().setXAxisBoundsManual(true);
        grafico.getViewport().setMaxX(data);
        grafico.getViewport().setMinX(data -30*24*60*60);

        // coloca texto no card principal

        String cardPrincipalText =""+dateFormat.format(formatoData,new Date ((long)data*1000))+"\n"+df.format(valor);
        cardPrincipal.setText(cardPrincipalText);

        //ajusta formato do texto nos eixos
        GridLabelRenderer mGrid = grafico.getGridLabelRenderer();

        mGrid.setHumanRounding(false,true);
        mGrid.setHorizontalLabelsAngle(90);
        mGrid.setNumHorizontalLabels(11);
        mGrid.setNumVerticalLabels(8);
        mGrid.setLabelHorizontalHeight(200);
        mGrid.setLabelVerticalWidth(200);

        LabelFormatter mLabelFormater = new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX){
                    return ""+ dateFormat.format(formatoData,new Date((long)value*1000));
                }else{
                    return ""+df.format(value);
                }
            }

            @Override
            public void setViewport(Viewport viewport) {
            }
        };
        mGrid.setLabelFormatter(mLabelFormater);

        //desenha grafico

        grafico.addSeries(dataPoint);
        dataPoint.setColor(Color.DKGRAY);
        dataPoint.setDrawDataPoints(true);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_refresh:
                cardPrincipal.setText(R.string.wait_message);
                grafico.removeAllSeries();
                presenter.refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
