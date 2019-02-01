package com.example.projetomobiledb1teste.data.utils;

import android.text.format.DateFormat;

import java.text.DecimalFormat;
import java.util.Date;

public class TextFormatter {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String DOLAR_FORMAT = "US$#.00";
    private static final DecimalFormat df = new DecimalFormat(DOLAR_FORMAT);
    private static TextFormatter mTextFormatterInstance;

    private TextFormatter(){    }

    public static TextFormatter getInstance(){
        if (mTextFormatterInstance ==null){
            mTextFormatterInstance = new TextFormatter();
        }
        return mTextFormatterInstance;
    }

    public String FormatDate (int unix){
        return DateFormat.format(DATE_FORMAT,new Date((long)unix*1000)).toString();
    }

    public String FormatPrice (float raw){
        return df.format(raw);
    }
}
