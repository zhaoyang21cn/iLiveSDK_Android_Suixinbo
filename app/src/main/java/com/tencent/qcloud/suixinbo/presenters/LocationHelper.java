package com.tencent.qcloud.suixinbo.presenters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.tencent.qcloud.suixinbo.presenters.viewinface.LocationView;
import com.tencent.qcloud.suixinbo.utils.Constants;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

import java.io.IOException;
import java.util.List;

/**
 * 位置服务类
 */
public class LocationHelper {
    private static String TAG = "LocationHelper";
    private Activity locActivity;

    public LocationHelper(Activity activity) {
        locActivity = activity;
    }

    public boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(locActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(locActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }

    private String getAddressFromLocation(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context);

        try {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            SxbLog.d(TAG, "getAddressFromLocation->lat:" + latitude + ", long:" + longitude);
            List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);
            if (list.size() > 0) {
                Address address = list.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
        }

        return "";
    }

    public boolean getMyLocation(final Context context, final LocationView view) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        }

        if (!checkLocationPermission()) {
            return true;
        }

        Location curLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (null == curLoc) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    String strAddr = getAddressFromLocation(context, location);
                    if (TextUtils.isEmpty(strAddr)) {
                        view.onLocationChanged(-1, 0, 0, strAddr);
                    } else {
                        view.onLocationChanged(0, location.getLatitude(), location.getLongitude(), strAddr);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        } else {
            String strAddr = getAddressFromLocation(context, curLoc);
            if (TextUtils.isEmpty(strAddr)) {
                view.onLocationChanged(-1, 0, 0, strAddr);
            } else {
                view.onLocationChanged(0, curLoc.getLatitude(), curLoc.getLongitude(), strAddr);
            }
        }

        return true;
    }

    public void onDestory() {
        locActivity = null;
    }
}
