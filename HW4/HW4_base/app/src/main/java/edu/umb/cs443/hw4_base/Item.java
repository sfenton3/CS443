package edu.umb.cs443.hw4_base;

/**
 * Created by nomad on 12/5/16.
 */

public class Item {
    private String status;
    private long time;
    private Double lat, lng;
    boolean region;
    int radius;

    public Item(){
        status = " ";
        time = 0;
        radius = 0;
        lat = 0.0;
        lng = 0.0;
        region = false;
    }
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean getRegion() {
        return region;
    }

    public void setRegion(boolean region) {
        this.region = region;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLat() {
        return lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLng() {
        return lng;
    }

}