package com.example.projetomobiledb1teste.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;

import android.view.View;
import android.widget.TextView;

import com.example.projetomobiledb1teste.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MVPInterfaces.MainActivityInterface {
    private static final String formatoData = "dd/MM/yyyy";
    private static Context context;
    public GraphView grafico;
    public TextView mTextViewMainCardDate;
    public TextView mTextViewMainCardValue;
    private MainActivityPresenter presenter;
    private RecyclerView mValuesList;
    private RecyclerView.Adapter mValuesListAdapter;
    private RecyclerView.LayoutManager mValuesListLayoutManager;
    private SwipeRefreshLayout mSwipeRefresh;

    public static Context getContext(){
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        mTextViewMainCardDate = findViewById(R.id.card_principal_data);
        mTextViewMainCardValue = findViewById(R.id.card_principal_valor);
        grafico = findViewById(R.id.grafico);
        mValuesList = findViewById(R.id.values_list);


        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTextViewMainCardDate.setText("");
                mTextViewMainCardValue.setText("");
                mValuesListLayoutManager = new LinearLayoutManager(MainActivity.this);
                mValuesList.setLayoutManager(mValuesListLayoutManager);
                mValuesListAdapter = new ValuesListAdapter(null,null);
                mValuesList.setAdapter(mValuesListAdapter);
                grafico.removeAllSeries();
                presenter.refresh();
            }
        });

        presenter = new MainActivityPresenter(this);
        presenter.pegaDados();
    }

    @Override
    public void updateScreen(int[] data, float[] valor, LineGraphSeries<DataPoint> dataPoint) {
        final String dolarFormat = "US$#.00";
        final DecimalFormat df = new DecimalFormat(dolarFormat);
        int dataSize = data.length;

        //Atualização da lista
        mValuesListLayoutManager = new LinearLayoutManager(this);
        mValuesList.setLayoutManager(mValuesListLayoutManager);
        mValuesListAdapter = new ValuesListAdapter(data,valor);
        mValuesList.setAdapter(mValuesListAdapter);

        //Atualização do card principal
        String cardPrincipalText ="Data: "+DateFormat.format(formatoData,new Date ((long)data[dataSize-1]*1000));
        mTextViewMainCardDate.setText(cardPrincipalText);
        String cardPrincipalValue = "Valor do BitCoin: "+df.format(valor[dataSize-1]);
        mTextViewMainCardValue.setText(cardPrincipalValue);

        //Atualização do grafico
        //label dos eixos
        LabelFormatter mLabelFormater = new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX){
                    return ""+ DateFormat.format(formatoData,new Date((long)value*1000));
                }else{
                    return ""+df.format(value);
                }
            }
            @Override
            public void setViewport(Viewport viewport) {
            }
        };
        //Grid
        GridLabelRenderer mGrid = grafico.getGridLabelRenderer();
        mGrid.setLabelFormatter(mLabelFormater);
        mGrid.setHumanRounding(false,true);
        mGrid.setHorizontalLabelsAngle(135);
        mGrid.setNumVerticalLabels(8);
        mGrid.setNumHorizontalLabels(11);

        grafico.getViewport().setXAxisBoundsManual(true);
        grafico.getViewport().setMaxX(dataPoint.getHighestValueX());
        grafico.getViewport().setMinX(dataPoint.getLowestValueX());
        grafico.addSeries(dataPoint);
        grafico.setVisibility(View.VISIBLE);
        mSwipeRefresh.setRefreshing(false);

        //Formatação da linha
        float var = valor[dataSize-1]/valor[dataSize-2];
        if (var>1){
            dataPoint.setColor(Color.GREEN);
        }else if (var<1){
            dataPoint.setColor(Color.RED);
        }else{
            dataPoint.setColor(Color.WHITE);
        }
        dataPoint.setDrawDataPoints(true);

    }

    public void semConexao(){
        mTextViewMainCardDate.setText(getString(R.string.error_msg_1));
        mTextViewMainCardValue.setText(getString(R.string.error_msg_2));
        mSwipeRefresh.setRefreshing(false);
    }
}
