package com.example.coronamaskmap.ui.search;

import java.io.Serializable;
import java.util.List;

public class Responese implements Serializable {
    private Integer count;
    private List<Store> stores;
    private String address;

    @Override
    public String toString() {
        return "Responese{" +
                "count=" + count +
                ", stores=" + stores +
                '}';
    }

    public Responese() {
    }

    public Responese(Integer count, List<Store> stores, String address) {
        this.count = count;
        this.stores = stores;
        this.address = address;
    }

    public static class Store implements Serializable {
        private String code;
        private String name;
        private String addr;
        private String type;
        private Double lat;
        private Double lng;
        private String stock_at;
        private String remain_stat;
        private String created_at;

        public Store() {
        }

        public Store(String code, String name, String addr, String type, Double lat, Double lng, String stock_at, String remain_stat, String created_at) {
            this.code = code;
            this.name = name;
            this.addr = addr;
            this.type = type;
            this.lat = lat;
            this.lng = lng;
            this.stock_at = stock_at;
            this.remain_stat = remain_stat;
            this.created_at = created_at;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        public String getStock_at() {
            return stock_at;
        }

        public void setStock_at(String stock_at) {
            this.stock_at = stock_at;
        }

        public String getRemain_stat() {
            return remain_stat;
        }

        public void setRemain_stat(String remain_stat) {
            this.remain_stat = remain_stat;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        @Override
        public String toString() {
            return "Store{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", addr='" + addr + '\'' +
                    ", type='" + type + '\'' +
                    ", lat=" + lat +
                    ", lng=" + lng +
                    ", stock_at='" + stock_at + '\'' +
                    ", remain_stat='" + remain_stat + '\'' +
                    ", created_at='" + created_at + '\'' +
                    '}';
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }
}
