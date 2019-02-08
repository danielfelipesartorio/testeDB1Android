package com.example.projetomobiledb1teste.data.db.model;

public class BitCoinData {

    float[] value;
    int[] data;

    BitCoinData(float[] value, int[] data) {
        this.value = value;
        this.data = data;
    }

    public float[] getValue() {
        return value;
    }

    public int[] getData() {
        return data;
    }

}
