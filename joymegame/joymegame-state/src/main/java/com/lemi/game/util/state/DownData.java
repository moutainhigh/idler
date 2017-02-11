package com.lemi.game.util.state;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DownData {
    private String url;
    private String appkey;
    private String distro;
    private String ip;
    private String ua;
    private String date;
    private String refer;
    private int    op;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getDistro() {
        return distro;
    }

    public void setDistro(String distro) {
        this.distro = distro;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString( this );
        } catch (Exception e) {
        }
        return null;
    }
}
