package com.example.coronamaskmap.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coronamaskmap.R;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class search_fragment extends Fragment {
    private static final String TAG = search_fragment.class.getSimpleName();
    View view;
    RecyclerView recyclerView;
    Adapter adapter;
    AddressService addressService = new AddressService();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fragment, container, false);
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("약국 검색");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


            addressService.maskAddress().storesByAddr("서울특별시 강남구").enqueue(new Callback<Responese>() {
                @Override
                public void onResponse(Call<Responese> call, Response<Responese> response) {
                    Responese body = response.body();
                    adapter = new Adapter(body.getStores());
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onFailure(Call<Responese> call, Throwable t) {
                 t.printStackTrace();
                }
            });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "onQueryTextSubmit: " + query);
                addressService.maskAddress().storesByAddr(query).enqueue(new Callback<Responese>() {
                    @Override
                    public void onResponse(Call<Responese> call, Response<Responese> response) {
                        Responese body = response.body();
                        adapter = new Adapter(body.getStores());
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<Responese> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
        alert_confirm.setMessage("예시 : '서울특별시 강남구' \n또는,  '서울특별시 강남구 논현동'\n" +
                "('서울특별시'와 같이 '시'단위만\n  입력하는 것은 불가능합니다.)");
        alert_confirm.setPositiveButton("확인", null);
        AlertDialog alert = alert_confirm.create();
        alert.setTitle("검색 시 입력 유의사항");
        alert.show();    }
}