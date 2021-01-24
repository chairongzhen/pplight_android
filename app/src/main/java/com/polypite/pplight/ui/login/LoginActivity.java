package com.polypite.pplight.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.polypite.pplight.MainActivity;
import com.polypite.pplight.R;
import com.polypite.pplight.utils.Common;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUserName;
    private EditText txtPwd;
    private Button btnLogin;
    private ImageButton btnWx;
    private SharedPreferences sp;
    private IWXAPI api;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("resume","i am back");
        sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String openId = sp.getString("openid","");

        if(!TextUtils.isEmpty(openId)) {
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        } else {
            initView();
        }
    }

    private void initView() {
        txtUserName = (EditText)findViewById(R.id.txtUserName);
        txtPwd = (EditText)findViewById(R.id.txtPwd);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUserName.getText().toString();
                String pwd = txtPwd.getText().toString();
                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
                    Log.d("login message","here we go");
                    try {
                        loginRequest(username,pwd);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,"帐号或者密码有误",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnWx = (ImageButton)findViewById(R.id.btnWx);
        btnWx.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                api = WXAPIFactory.createWXAPI(LoginActivity.this, Common.APP_ID,false);
                api.registerApp(Common.APP_ID);
                if(!api.isWXAppInstalled()) {
                    Toast.makeText(LoginActivity.this,"您的设备未安装微信客户端",Toast.LENGTH_LONG).show();
                } else {
                    final SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "wechat";
                    api.sendReq(req);
                }
            }
        });
    }

    private void loginRequest(String username, String pwd) throws JSONException {
        boolean isLoginOk = false;
        String url = Common.apiLogin;
        final String isLogingmsg = "未知错误";
        OkHttpClient okHttpClient = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("username",username);
        json.put("pwd",pwd);
        RequestBody requestBody = RequestBody.create(Common.MEDIA_TYPE_JSON,String.valueOf(json));
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("error",e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    JSONObject jsonobj = new JSONObject(response.body().string());
                    boolean isSuccess = jsonobj.getBoolean("isSuccess");
                    String message = jsonobj.getString("message");
                    JSONObject content = jsonobj.getJSONObject("content");
                    if(isSuccess) {
                        JSONObject jsoncontent = new JSONObject(String.valueOf(content));
                        String nickname = jsoncontent.getString("nickname");
                        String openid = jsoncontent.getString("openid");
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("openid",openid);
                        editor.putString("nickname",nickname);
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this,message,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }




    ;}
