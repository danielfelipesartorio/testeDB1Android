package com.example.projetomobiledb1teste.data.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstanceBitCoin {

    private static final String BASE_URL = "https://api.blockchain.info/charts/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(){
        //Retirado pois causava: java.net.SocketTimeoutException: timeout \\Dead socket
        //if (retrofit ==null){
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        //}
        return retrofit;
    }
}
