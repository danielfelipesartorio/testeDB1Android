package com.example.projetomobiledb1teste;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BitCoinPairList {
    public ArrayList<BitCoinPair> getBitCoinPairList() {
        return bitCoinPairList;
    }

    public void setBitCoinPairList(ArrayList<BitCoinPair> bitCoinPairList) {
        this.bitCoinPairList = bitCoinPairList;
    }

    @SerializedName("values")
    private ArrayList<BitCoinPair> bitCoinPairList;

}
