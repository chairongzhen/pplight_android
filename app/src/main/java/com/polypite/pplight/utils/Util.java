package com.polypite.pplight.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Util {
    public static String  getSsid(Activity activity) {
        @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager)activity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID().replaceAll("\"","");
        return ssid;
    }
}
