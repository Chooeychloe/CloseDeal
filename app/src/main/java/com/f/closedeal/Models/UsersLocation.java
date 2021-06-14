package com.f.closedeal.Models;

public class UsersLocation {

    private String latitude, longitude,country, locality,address, name, uid;

    public UsersLocation() {
    }

    public UsersLocation(String latitude, String longitude, String country, String locality, String address, String name, String uid) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.locality = locality;
        this.address = address;
        this.name = name;
        this.uid = uid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
