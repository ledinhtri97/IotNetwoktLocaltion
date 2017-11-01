package com.example.dimo.updclient;

import java.io.Serializable;

/**
 * Created by dimo on 24/10/2017.
 */

public class Data implements Serializable{


    private static final long serialVersionUID = 8940619353032039735L;
    /**
     *
     */
	
	private double Latitude;
    private double Longitude;


    public Data(double latitude, double longitude, String _ID_ANDROID) {
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.ID_ANDROID = _ID_ANDROID;
    }

    public double getLatitude() {

        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getID_ANDROID() {
        return ID_ANDROID;
    }

    public void setID_ANDROID(String ID_ANDROID) {
        this.ID_ANDROID = ID_ANDROID;
    }

    private String ID_ANDROID;

}
