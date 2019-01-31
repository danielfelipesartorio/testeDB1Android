package com.example.projetomobiledb1teste.data.network;

import com.google.gson.annotations.SerializedName;

public class BitCoinPair {
    @SerializedName("x")
    private int date;
    @SerializedName("y")
    private float value;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public BitCoinPair(int date, float value){
        this.date = date;
        this.value = value;
    }
}
