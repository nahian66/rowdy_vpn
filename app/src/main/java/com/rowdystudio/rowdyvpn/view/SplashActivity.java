package com.rowdystudio.rowdyvpn.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.api.WebAPI;
import com.rowdystudio.rowdyvpn.speed_meter.activities.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init_variables ();
        new Handler().postDelayed(() -> {
            new Thread(() -> {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?freeServers").build();
                    Response response = okHttpClient.newCall(request).execute();
                    WebAPI.FREE_SERVERS = Objects.requireNonNull(response.body()).string();

                    request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?proServers").build();
                    response = okHttpClient.newCall(request).execute();
                    WebAPI.PREMIUM_SERVERS = Objects.requireNonNull(response.body()).string();

                    request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?admob").build();
                    response = okHttpClient.newCall(request).execute();
                    String body = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONArray jsonArray = new JSONArray(body);
                        for (int i=0; i < jsonArray.length();i++){
                            JSONObject object = (JSONObject) jsonArray.get(0);
                            WebAPI.ADMOB_ID = object.getString("admobID");
                            WebAPI.ADMOB_BANNER = object.getString("bannerID");
                            WebAPI.ADMOB_INTERSTITIAL = object.getString("interstitialID");
                            WebAPI.ADMOB_NATIVE = object.getString("nativeID");
                            WebAPI.ADMOB_REWARDED_AD = object.getString("RewardedAdID");
                        }
                        try {
                            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                            Bundle bundle = applicationInfo.metaData;
                            applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID",WebAPI.ADMOB_ID);
                            String apiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                            Log.d("AppID","The saved id is "+WebAPI.ADMOB_ID);
                            Log.d("AppID","The saved id is "+apiKey);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    Log.v("Kabila",e.toString());
                    e.printStackTrace();
                }


            }).start();
            try {
                Log.v("SERVER_API",WebAPI.FREE_SERVERS);
                Thread.sleep(3000);
                Log.v("SERVER_API","after "+WebAPI.FREE_SERVERS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        },1000);

        }

    private void init_variables () {
        SharedPreferences sharedPref = getSharedPreferences (
                "setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit ();
        editor.putString ("UNIT", "Mbps");
        editor.apply ();

    }


}
