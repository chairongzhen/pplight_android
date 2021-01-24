package com.polypite.pplight.ui.light;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.polypite.pplight.R;
import com.polypite.pplight.ui.index.IndexViewModel;
import com.polypite.pplight.utils.Common;
import com.polypite.pplight.utils.WebAppInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class LightFragment extends Fragment {
    private LightViewModel lightViewModel;
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
        lightViewModel = new ViewModelProvider(this).get(LightViewModel.class);
        View root = inflater.inflate(R.layout.fragment_index,container,false);
        lightViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        webView.addJavascriptInterface(new WebAppInterface(),"ppJsbridge");
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebContentsDebuggingEnabled(true);
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(Common.h5Host);
        sbUrl.append("?openid=");
        sbUrl.append(openId);
        sbUrl.append("&nickname=");
        sbUrl.append(nickName);
        sbUrl.append("#/pages/light/index");
        webView.loadUrl(sbUrl.toString());
    }
}
