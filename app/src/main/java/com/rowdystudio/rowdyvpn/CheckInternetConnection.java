package com.rowdystudio.rowdyvpn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class CheckInternetConnection {


    public boolean netCheck(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo nInfo = cm.getActiveNetworkInfo();

        return nInfo != null && nInfo.isConnectedOrConnecting();
    }
}
