package com.example.awake;

/**
 * Created by 지연 on 2016-08-01.
 */
public class APIItem {
    public String lat;
    public String lon;
    public String datentime;
    public String location;
    public String weather;
    public String temperature;
    public String city;
    public String fullName;
    public int airmise;
    public int airchomise;
    public int airno;
    public int airso2;
    public int airo3;
    public int airco;
    public APIItem(){

    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getDatentime() {
        return datentime;
    }

    public void setDatentime(String datentime) {
        this.datentime = datentime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAirmise() {
        return airmise;
    }

    public void setAirmise(int airmise) {
        this.airmise = airmise;
    }

    public int getAirchomise() {
        return airchomise;
    }

    public void setAirchomise(int airchomise) {
        this.airchomise = airchomise;
    }

    public int getAirno() {
        return airno;
    }

    public void setAirno(int airno) {
        this.airno = airno;
    }

    public int getAirso2() {
        return airso2;
    }

    public void setAirso2(int airso2) {
        this.airso2 = airso2;
    }

    public int getAiro3() {
        return airo3;
    }

    public void setAiro3(int airo3) {
        this.airo3 = airo3;
    }

    public int getAirco() {
        return airco;
    }

    public void setAirco(int airco) {
        this.airco = airco;
    }

    public APIItem(String lat, String lon, String datentime, String location, String weather, String temperature, String city, String fullName, int airmise, int airchomise, int airno, int airso2, int airo3, int airco) {
        this.lat = lat;
        this.lon = lon;
        this.datentime = datentime;
        this.location = location;
        this.weather = weather;
        this.temperature = temperature;
        this.city = city;
        this.fullName = fullName;
        this.airmise = airmise;
        this.airchomise = airchomise;
        this.airno = airno;
        this.airso2 = airso2;
        this.airo3 = airo3;
        this.airco = airco;
    }

    @Override
    public String toString() {
        return "APIItem{" +
                "lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", datentime='" + datentime + '\'' +
                ", location='" + location + '\'' +
                ", weather='" + weather + '\'' +
                ", temperature='" + temperature + '\'' +
                ", city='" + city + '\'' +
                ", fullName='" + fullName + '\'' +
                ", airmise=" + airmise +
                ", airchomise=" + airchomise +
                ", airno=" + airno +
                ", airso2=" + airso2 +
                ", airo3=" + airo3 +
                ", airco=" + airco +
                '}';
    }
}
