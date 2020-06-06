package com.example.coronamaskmap.ui.home;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CoronaApi extends AsyncTask<String, String, String> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d("Task3", "POST");
        String temp = "Not Gained";
        temp = GET(strings[0], strings[1]);
        Log.d("REST", temp);
        return temp;
    }

    private String GET(String x, String y) {
        String corona_API = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat="+ x +"&lng="+ y +"&m=1000";

        String data =" ";
        String myUrl3 = String.format(corona_API, x);

        try {
            URL url = new URL(myUrl3);
            Log.d("CoronaApi", "The response is :" + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            String line;
            String result = "";

            BufferedReader bf;
            bf = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = bf.readLine()) != null) {
                result = result.concat(line);

            }
            Log.d("CoronaApi", "The response is :" + result);
            JSONObject root = new JSONObject(result);

            JSONArray coronaArray = root.getJSONArray("stores");
            for(int i = 0; i< coronaArray.length() ; i++){
                JSONObject item = coronaArray.getJSONObject(i);
                Log.d("corona",item.getString("name"));
                corona_item corona_item = new corona_item(
                        item.getString("lat"),
                        item.getString("lng"),
                        item.getString("addr"),
                        item.getString("code"),
                        item.getString("created_at"),
                        item.getString("name"),
                        item.getString("remain_stat"),
                        item.getString("stock_at"),
                        item.getString("type")
                );
                home_fragment.corona_list.add(corona_item);
            }
            home_fragment.startFlagForCoronaApi=false;



        } catch (NullPointerException | JsonSyntaxException | JSONException | IOException e) {
            e.printStackTrace();
        }


        return data;
    }

}
