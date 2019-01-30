package com.example.projetomobiledb1teste.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;

import com.example.projetomobiledb1teste.MVPInterfaces;
import com.example.projetomobiledb1teste.R;
import com.example.projetomobiledb1teste.ValuesListAdapter;
import com.example.projetomobiledb1teste.presenter.MainActivityPresenter;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements MVPInterfaces.MainActivityInterface {
    private MainActivityPresenter presenter;
    public GraphView grafico;
    public TextView mTextViewMainCardDate;
    public TextView mTextViewMainCardValue;
    private static final String formatoData = "dd/MM/yyyy";
    private static Context context;

    private RecyclerView mValuesList;
    private RecyclerView.Adapter mValuesListAdapter;
    private RecyclerView.LayoutManager mValuesListLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        mTextViewMainCardDate = findViewById(R.id.card_principal_data);
        mTextViewMainCardValue = findViewById(R.id.card_principal_valor);
        grafico = findViewById(R.id.grafico);
        mValuesList = findViewById(R.id.values_list);

        presenter = new MainActivityPresenter(this);
        presenter.pegaDados();
    }

    public static Context getContext(){
        return context;
    }

    @Override
    public void updateScreen(int[] data, float[] valor, LineGraphSeries<DataPoint> dataPoint) {
        final String dolarFormat = "US$#.00";
        final DecimalFormat df = new DecimalFormat(dolarFormat);
        int dataSize = data.length;

        mValuesListLayoutManager = new LinearLayoutManager(this);
        mValuesList.setLayoutManager(mValuesListLayoutManager);

        mValuesListAdapter = new ValuesListAdapter(data,valor);
        mValuesList.setAdapter(mValuesListAdapter);



        // coloca texto no card principal
        String cardPrincipalText ="Data: "+DateFormat.format(formatoData,new Date ((long)data[dataSize-1]*1000));
        mTextViewMainCardDate.setText(cardPrincipalText);
        String cardPrincipalValue = "Valor do BitCoin: "+df.format(valor[dataSize-1]);
        mTextViewMainCardValue.setText(cardPrincipalValue);

        //ajusta formato do texto nos eixos
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

        GridLabelRenderer mGrid = grafico.getGridLabelRenderer();
        mGrid.setLabelFormatter(mLabelFormater);




        mGrid.setHumanRounding(false,true);
        mGrid.setHorizontalLabelsAngle(90);

        mGrid.setNumVerticalLabels(8);
        mGrid.setLabelHorizontalHeight(200);
        mGrid.setLabelVerticalWidth(200);
        mGrid.setNumHorizontalLabels(11);

        //desenha grafico


        dataPoint.setColor(Color.WHITE);
        dataPoint.setDrawDataPoints(true);

        //define limites do eixo x

        grafico.getViewport().setXAxisBoundsManual(true);
        grafico.getViewport().setMaxX(dataPoint.getHighestValueX());
        grafico.getViewport().setMinX(dataPoint.getLowestValueX());
        grafico.addSeries(dataPoint);
        grafico.setSystemUiVisibility(View.GONE);
        grafico.setVisibility(View.VISIBLE);

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


                mTextViewMainCardDate.setText("");
                mTextViewMainCardValue.setText("");
                grafico.removeAllSeries();
                presenter.refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void semConexao(){
        mTextViewMainCardDate.setText(getString(R.string.error_msg_1));
        mTextViewMainCardValue.setText(getString(R.string.error_msg_2));
    }
}
