package com.example.coronamaskmap.ui.search;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddressService {
    public AddressService() {
    }

    public MaskAddress maskAddress(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://8oi9s0nnth.apigw.ntruss.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MaskAddress.class);
    }
}
