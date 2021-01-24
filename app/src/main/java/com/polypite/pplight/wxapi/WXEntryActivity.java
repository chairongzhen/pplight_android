package com.polypite.pplight.wxapi;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.polypite.pplight.MainActivity;
import com.polypite.pplight.ui.login.LoginActivity;
import com.polypite.pplight.utils.Common;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        api = WXAPIFactory.createWXAPI(this, Common.APP_ID,false);
        api.handleIntent(getIntent(),this);
        sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) baseResp).code;
                getAccessToken(code);
                Log.d("wechat login", code.toString() + "");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finish();
                break;
            default:
                finish();
                break;
        }
    }


    private void createProgressDialog() {
        mContext = this;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("登录中,请稍后");
        mProgressDialog.show();
    }

    private void getUserInfo(String access, String openid) {
        String getUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access + "&openid=" + openid;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getUserInfoUrl)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("network error","get userinfo failed");
                mProgressDialog.dismiss();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseInfo = response.body().string();
                Log.d("network ok","onResponse: " + responseInfo);
                try {
                    JSONObject jsonobj = new JSONObject(responseInfo);
                    String openid = jsonobj.getString("unionid");
                    String nickname = jsonobj.getString("nickname");
                    wxLogin(openid,nickname);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("openid",openid);
                    editor.putString("nickname",nickname);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                finish();
                mProgressDialog.dismiss();
            }
        });

    }

    private void wxLogin(String openid,String nickname) throws JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = Common.apiWxLogin;
        JSONObject json = new JSONObject();
        json.put("openid",openid);
        json.put("nickname",nickname);
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
                    if(isSuccess) {
                        Log.d("result","wx initial success");
                    } else {
                        Toast.makeText(WXEntryActivity.this,message,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getAccessToken(String code) {
        createProgressDialog();
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=")
                .append(Common.APP_ID)
                .append("&secret=")
                .append(Common.APP_SECRET)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");
        Log.d("url", loginUrl.toString());

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(loginUrl.toString())
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("network error","login failed");
                mProgressDialog.dismiss();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseInfo = response.body().string();
                Log.d("network ok","onResponse: " + responseInfo);
                String access = null;
                String openId = null;
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo);
                    access = jsonObject.getString("access_token");
                    openId = jsonObject.getString("unionid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getUserInfo(access,openId);
            }
        });

    }
}
