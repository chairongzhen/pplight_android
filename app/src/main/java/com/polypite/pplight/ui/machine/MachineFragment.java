package com.polypite.pplight.ui.machine;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.polypite.pplight.R;
import com.polypite.pplight.ui.light.LightViewModel;
import com.polypite.pplight.utils.Common;
import com.polypite.pplight.utils.Util;
import com.polypite.pplight.utils.WebAppInterface;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MachineFragment extends Fragment {
    private MachineViewModel machineViewModel;
    private Context mContext;
    private SharedPreferences sp;
    private WebView webView;
    private WebSettings webSettings;
    private String openId;
    private String nickName;
    private final int REQUEST_CODE_SCAN = 100;
    private final int RESULT_OK = 200;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        machineViewModel = new ViewModelProvider(this).get(MachineViewModel.class);
        View root = inflater.inflate(R.layout.fragment_index,container,false);
        machineViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        initView(root);
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetJavaScriptEnabled")
    private void initView(View rootView) {
        sp = this.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        openId = sp.getString("openid","");
        nickName = sp.getString("nickname","");

        webView = rootView.findViewById(R.id.wbIndex);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(this,"ppjsbridge");
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebContentsDebuggingEnabled(true);
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(Common.h5Host);
        sbUrl.append("?openid=");
        sbUrl.append(openId);
        sbUrl.append("&nickname=");
        sbUrl.append(nickName);
        sbUrl.append("#/pages/machine/index");
        webView.loadUrl(sbUrl.toString());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if(requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            String content = data.getStringExtra(Constant.CODED_CONTENT);
            Log.d("qrcode",content);
            String jsUrl = "javascript:qrcodeCallback('" + content + "')";
            webView.loadUrl(jsUrl);
        //}
    }

    @JavascriptInterface
    public void gotoWifiSetting() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    @JavascriptInterface
    public void scanQrCode() {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivityForResult(intent,REQUEST_CODE_SCAN);
    }

    @JavascriptInterface
    public String getssid() {
        String ssid = Util.getSsid(this.getActivity());
        return ssid;
    }
}
