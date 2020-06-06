package com.example.coronamaskmap;

import com.example.coronamaskmap.ui.search.AddressService;
import com.example.coronamaskmap.ui.search.MaskAddress;
import com.example.coronamaskmap.ui.search.Responese;

import org.junit.Test;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MaskAddressTest {
    @Test
    public void corona_getStoresByAddr() throws IOException {
        AddressService addressService = new AddressService();

        Response<Responese> response = addressService.maskAddress().storesByAddr("경상북도 경산시 하양읍").execute();

        System.out.println(response.body());
        assertNotNull(response.body());
    }
}