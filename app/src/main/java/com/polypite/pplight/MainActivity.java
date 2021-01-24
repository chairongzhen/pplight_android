package com.polypite.pplight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.polypite.pplight.utils.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_index, R.id.navigation_light, R.id.navigation_machine,R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

//    /**
//     * MD5加密
//     * @param byteStr 需要加密的内容
//     * @return 返回 byteStr的md5值
//     */
//    private static String encryptionMD5(byte[] byteStr) {
//        MessageDigest messageDigest = null;
//        StringBuffer md5StrBuff = new StringBuffer();
//        try {
//            messageDigest = MessageDigest.getInstance("MD5");
//            messageDigest.reset();
//            messageDigest.update(byteStr);
//            byte[] byteArray = messageDigest.digest();
////            return Base64.encodeToString(byteArray,Base64.NO_WRAP);
//            for (int i = 0; i < byteArray.length; i++) {
//                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
//                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
//                } else {
//                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
//                }
//            }
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return md5StrBuff.toString();
//    }
//
//    /**
//     * 获取app签名md5值,与“keytool -list -keystore D:\Desktop\app_key”‘keytool -printcert     *file D:\Desktop\CERT.RSA’获取的md5值一样
//     */
//    public String getSignMd5Str() {
//        try {
//            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            android.content.pm.Signature[] signs = packageInfo.signatures;
//            Signature sign = signs[0];
//            String signStr = encryptionMD5(sign.toByteArray());
//            return signStr;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

}