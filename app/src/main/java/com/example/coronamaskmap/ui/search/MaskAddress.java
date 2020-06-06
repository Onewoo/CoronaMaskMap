package com.example.coronamaskmap.ui.search;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MaskAddress {
    @GET("corona19-masks/v1/storesByAddr/json")
    Call<Responese> storesByAddr(@Query("address")String address);
}
