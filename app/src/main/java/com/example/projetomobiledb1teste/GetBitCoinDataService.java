package com.example.projetomobiledb1teste;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetBitCoinDataService {
    @GET("market-price?timespan=31days")
    Call<BitCoinPairList> getBitCoinData();
}
