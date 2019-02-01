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
import android.widget.Toast;

import com.example.projetomobiledb1teste.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DecimalFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MainActivityInterface {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
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
        final String DOLAR_FORMAT = "US$#.00";
        final DecimalFormat df = new DecimalFormat(DOLAR_FORMAT);
        int dataSize = data.length;

        //Atualização da lista
        mValuesListLayoutManager = new LinearLayoutManager(this);
        mValuesList.setLayoutManager(mValuesListLayoutManager);
        mValuesListAdapter = new ValuesListAdapter(data,valor);
        mValuesList.setAdapter(mValuesListAdapter);

        //Atualização do card principal
        //String cardPrincipalText ="Data: "+DateFormat.format(DATE_FORMAT,new Date ((long)data[dataSize-1]*1000));
        mTextViewMainCardDate.setText(getResources()
                .getString(R.string.main_card_date,
                        DateFormat.format(DATE_FORMAT,new Date ((long)data[dataSize-1]*1000))));
        //String cardPrincipalValue = "Valor do BitCoin: "+df.format(valor[dataSize-1]);
        mTextViewMainCardValue.setText(getResources()
                .getString(R.string.main_card_value,
                        df.format(valor[dataSize-1])));
        //Atualização do grafico
        //label dos eixos
        LabelFormatter mLabelFormater = new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX){
                    return ""+ DateFormat.format(DATE_FORMAT,new Date((long)value*1000));
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
        mGrid.setNumVerticalLabels(9);
        mGrid.setNumHorizontalLabels(11);
        mGrid.setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        mGrid.setHorizontalLabelsVisible(false);

        grafico.getViewport().setXAxisBoundsManual(true);
        grafico.getViewport().setMaxX(dataPoint.getHighestValueX());
        grafico.getViewport().setMinX(dataPoint.getLowestValueX());
        grafico.addSeries(dataPoint);
        grafico.setVisibility(View.VISIBLE);
        mSwipeRefresh.setRefreshing(false);

        //Formatação da linha
        float var = valor[dataSize-1]/valor[dataSize-2];
        if (var>1){
            dataPoint.setColor(0xFF3AECFC);
        }else if (var<1){
            dataPoint.setColor(0xFFB8110B);
        }else{
            dataPoint.setColor(Color.WHITE);
        }
        //dataPoint.setDrawDataPoints(true);
        //dataPoint.setDataPointsRadius(6);
        dataPoint.setThickness(5);

        dataPoint.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String toastText = "" + DateFormat.format(DATE_FORMAT,new Date ((long)dataPoint.getX()*1000))+": " + df.format(dataPoint.getY());
                Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void semConexao(){
        mTextViewMainCardDate.setText(getString(R.string.error_msg_1));
        mTextViewMainCardValue.setText(getString(R.string.error_msg_2));
        mSwipeRefresh.setRefreshing(false);
    }
}
