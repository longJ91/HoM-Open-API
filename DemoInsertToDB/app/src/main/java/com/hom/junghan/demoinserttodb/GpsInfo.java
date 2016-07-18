package com.hom.junghan.demoinserttodb;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class GpsInfo extends Service implements LocationListener {
    private final Context mContext;
    boolean isGPSEnabled = false; // 현재 GPS 사용유무
    boolean isNetworkEnabled = false; // 네트워크 사용유무
    boolean isGetLocation = false; // GPS 상태값

    Location location;
    double lat;
    double lon;

    protected LocationManager locationManager;

    public GpsInfo() {
        this.mContext = null;
    }

    public GpsInfo(Context context) {
        mContext = context;

        getLocation();
    }

    public Location getLocation() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG","NOT PERMISSION");
        }

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                //GPS와 네트워크사용이 가능하지 않을때 소스구현
            } else {
                //네트워크 정보로부터 위치값 가져오기
                this.isGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 60000, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            Log.d("TAG","IN NETWORK1 " + lat + "   " + lon);
                        }
                        Log.d("TAG","IN NETWORK2 " + lat + "   " + lon);
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        //GPS정보로 위치값 가져오기
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 60000, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                                Log.d("TAG","IN GPS1 " + lat + "   " + lon);
                            }
                            Log.d("TAG","IN GPS2 " + lat + "   " + lon);
                        }
                        Log.d("TAG","IN GPS3 " + lat + "   " + lon);
                    }

                }
                Log.d("TAG","IN " + lat + "   " + lon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
            Log.d("TAG","LAT " + lat);
        }
        return lat;
    }

    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
            Log.d("TAG","LON "+ lon);
        }
        return lon;
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
