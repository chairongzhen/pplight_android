package com.polypite.pplight.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.polypite.pplight.R;
import com.polypite.pplight.ui.login.LoginActivity;
import com.polypite.pplight.utils.Common;
import com.polypite.pplight.utils.Util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class SettingFragment extends Fragment {
    private SettingViewModel settingViewModel;
    private Context mContext;
    private SharedPreferences sp;
    private WebView webView;
    private WebSettings webSettings;
    private String openId;
    private String nickName;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_index,container,false);
        settingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        initView(root);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initView(View rootView) {
        sp = this.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        openId = sp.getString("openid","");
        nickName = sp.getString("nickname","");

        webView = rootView.findViewById(R.id.wbIndex);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //webView.addJavascriptInterface(new WebAppInterface(),"ppJsbridge");
        webView.addJavascriptInterface(this,"ppjsbridge");

        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebContentsDebuggingEnabled(true);
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(Common.h5Host);
        sbUrl.append("?openid=");
        sbUrl.append(openId);
        sbUrl.append("&nickname=");
        sbUrl.append(nickName);
        sbUrl.append("#/pages/setting/index");
        webView.loadUrl(sbUrl.toString());
    }

    @JavascriptInterface
    public void logout() {
        SharedPreferences userinfo = getContext().getSharedPreferences("userinfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userinfo.edit();
        editor.clear();
        editor.commit();
        Intent loginActivity = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginActivity);
    }

    @JavascriptInterface
    public String getssid() {
        String ssid = Util.getSsid(this.getActivity());
        return ssid;
    }


}
