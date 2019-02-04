package com.example.projetomobiledb1teste.data.network;

import com.example.projetomobiledb1teste.data.network.BitCoinPairList;


import io.reactivex.Observable;
import retrofit2.http.GET;

public interface GetBitCoinDataService {
    @GET("market-price?timespan=31days")
    Observable<BitCoinPairList> getBitCoinData();
}
