package com.example.projetomobiledb1teste.data.db;

import android.provider.BaseColumns;

public class BitCoinDBContract {

    public static final class bitCoinEntry implements BaseColumns{
        public static final String TABLE_NAME = "bitCoin";
        public static final String COLUMN_DATA = "data";
        public static final String COLUMN_VALOR = "valor";
    }
}
