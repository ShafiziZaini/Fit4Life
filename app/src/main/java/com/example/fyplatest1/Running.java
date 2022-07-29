package com.example.fyplatest1;

public class Running {

    String Latitude, Longitude, Latitude1, Longitude1, Time, Pace;
    Double Distance;

    public Running(){}

    public Running(String latitude, String longitude,String latitude1, String longitude1, Double distance, String time, String pace) {
        Latitude = latitude;
        Longitude = longitude;
        Latitude1 = latitude1;
        Longitude1 = longitude1;
        Distance = distance;
        Time = time;
        Pace = pace;
    }

    public String getPace() {
        return Pace;
    }

    public void setPace(String pace) {
        Pace = pace;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public Double getDistance() {
        return Distance;
    }

    public void setDistance(Double distance) {
        Distance = distance;
    }

    public String getLatitude1() {
        return Latitude1;
    }

    public void setLatitude1(String latitude1) {
        Latitude1 = latitude1;
    }

    public String getLongitude1() {
        return Longitude1;
    }

    public void setLongitude1(String longitude1) {
        Longitude1 = longitude1;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
