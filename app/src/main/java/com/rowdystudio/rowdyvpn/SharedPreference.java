package com.rowdystudio.rowdyvpn;

import android.content.Context;
import android.content.SharedPreferences;

import com.rowdystudio.rowdyvpn.model.Server;

import static com.rowdystudio.rowdyvpn.Utils.getImgURL;

public class SharedPreference {

    private static final String APP_PREFS_NAME = "VPNPreference";

    private final SharedPreferences mPreference;
    private final SharedPreferences.Editor mPrefEditor;

    private static final String SERVER_COUNTRY = "server_country";
    private static final String SERVER_FLAG = "server_flag";
    private static final String SERVER_OVPN = "server_ovpn";
    private static final String SERVER_OVPN_USER = "server_ovpn_user";
    private static final String SERVER_OVPN_PASSWORD = "server_ovpn_password";

    private static final String SERVER_COUNTRY_VIP = "server_country_vip";
    private static final String SERVER_FLAG_VIP = "server_flag_vip";
    private static final String SERVER_OVPN_VIP = "server_ovpn_vip";
    private static final String SERVER_OVPN_USER_VIP = "server_ovpn_user_vip";
    private static final String SERVER_OVPN_PASSWORD_VIP = "server_ovpn_password_vip";
    public SharedPreference(Context context) {
        this.mPreference = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
        this.mPrefEditor = mPreference.edit();
    }


    public void saveServer(Server server){
        mPrefEditor.putString(SERVER_COUNTRY, server.getCountry());
        mPrefEditor.putString(SERVER_FLAG, server.getFlagUrl());
        mPrefEditor.putString(SERVER_OVPN, server.getOvpn());
        mPrefEditor.putString(SERVER_OVPN_USER, server.getOvpnUserName());
        mPrefEditor.putString(SERVER_OVPN_PASSWORD, server.getOvpnUserPassword());
        mPrefEditor.commit();
    }
    public void saveVipServer(Server server){
        mPrefEditor.putString(SERVER_COUNTRY_VIP, server.getCountry());
        mPrefEditor.putString(SERVER_FLAG_VIP, server.getFlagUrl());
        mPrefEditor.putString(SERVER_OVPN_VIP, server.getOvpn());
        mPrefEditor.putString(SERVER_OVPN_USER_VIP, server.getOvpnUserName());
        mPrefEditor.putString(SERVER_OVPN_PASSWORD_VIP, server.getOvpnUserPassword());
        mPrefEditor.commit();
    }


    public Server getServer() {

        return new Server(
                mPreference.getString(SERVER_COUNTRY,"Japan"),
                mPreference.getString(SERVER_FLAG,getImgURL(R.drawable.japan)),
                mPreference.getString(SERVER_OVPN,"japan.ovpn"),
                mPreference.getString(SERVER_OVPN_USER,"softmaster"),
                mPreference.getString(SERVER_OVPN_PASSWORD,"softmaster")
        );
    }
    public Server getVipServer() {

        return new Server(
                mPreference.getString(SERVER_COUNTRY_VIP,"Japan"),
                mPreference.getString(SERVER_FLAG_VIP,getImgURL(R.drawable.japan)),
                mPreference.getString(SERVER_OVPN_VIP,"japan.ovpn"),
                mPreference.getString(SERVER_OVPN_USER_VIP,"softmaster"),
                mPreference.getString(SERVER_OVPN_PASSWORD_VIP,"softmaster")
        );
    }
}
