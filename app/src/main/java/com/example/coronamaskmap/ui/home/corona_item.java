package com.example.coronamaskmap.ui.home;

public class corona_item {
    private String addr;
    private String code;
    private String created_at;
    private String lat;
    private String lng;
    private String name;
    private String remain_stat;
    private String stock_at;
    private String type;


    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemain_stat() {
        return remain_stat;
    }

    public void setRemain_stat(String remain_stat) {
        this.remain_stat = remain_stat;
    }

    public String getStock_at() {
        return stock_at;
    }

    public void setStock_at(String stock_at) {
        this.stock_at = stock_at;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public corona_item(String lat, String lng, String addr, String code, String created_at, String name, String remain_stat, String stock_at, String type) {
        this.addr = addr;
        this.code = code;
        this.created_at = created_at;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.remain_stat = remain_stat;
        this.stock_at = stock_at;
        this.type = type;
    }
}
