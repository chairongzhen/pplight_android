package com.polypite.pplight.ui.index;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.polypite.pplight.R;
import com.polypite.pplight.utils.Common;
import com.polypite.pplight.utils.WebAppInterface;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class IndexFragment extends Fragment {

    private IndexViewModel indexViewModel;
    private Context mContext;
    private SharedPreferences sp;
    private WebView webView;
    private WebSettings webSettings;
    private String openId;
    private String nickName;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        indexViewModel = new ViewModelProvider(this).get(IndexViewModel.class);
        View root = inflater.inflate(R.layout.fragment_index,container,false);
        indexViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        initView(root);
        String type = "hello";
        return root;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        mContext = getContext();
//        SharedPreferences sp = mContext.getSharedPreferences("data",Context.MODE_PRIVATE);
//        String responseInfo = sp.getString("responseInfo","");
//        Log.d("the response is: ", responseInfo);
//    }

    private void initListerner(View rootView) {
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                    if(keyEvent.getAction() == keyEvent.ACTION_DOWN) {
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });


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
        webView.addJavascriptInterface(new WebAppInterface(),"ppJsbridge");
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebContentsDebuggingEnabled(true);
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(Common.h5Host);
        sbUrl.append("?openid=");
        sbUrl.append(openId);
        sbUrl.append("&nickname=");
        sbUrl.append(nickName);
        sbUrl.append("#/pages/index/index");
        webView.loadUrl(sbUrl.toString());
        //webView.loadUrl("http://192.168.1.11:10086/?openid=1234&nickname=chai#/pages/index/index");
        Log.d("url",sbUrl.toString());
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                //super.onPageStarted(view, url, favicon);
//                if(progressDialog == null) {
//                    progressDialog = new ProgressDialog()
//                }
//            }
//        });

//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
//                b.setTitle("Alert");
//                b.setMessage(message);
//                b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        result.confirm();
//                    }
//                });
//                b.setCancelable(false);
//                b.create().show();
//                return true;
//            }
//        });
    }
}
