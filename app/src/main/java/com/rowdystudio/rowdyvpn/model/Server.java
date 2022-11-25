package com.rowdystudio.rowdyvpn.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Server implements Parcelable {
    private String country;
    private String flagUrl;
    private String ovpn;
    private String ovpnUserName;
    private String ovpnUserPassword;


    public Server() {
    }

    public Server(String country, String flagUrl, String ovpn) {
        this.country = country;
        this.flagUrl = flagUrl;
        this.ovpn = ovpn;
    }

    public Server(String country, String flagUrl, String ovpn, String ovpnUserName, String ovpnUserPassword) {
        this.country = country;
        this.flagUrl = flagUrl;
        this.ovpn = ovpn;
        this.ovpnUserName = ovpnUserName;
        this.ovpnUserPassword = ovpnUserPassword;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    public String getOvpn() {
        return ovpn;
    }

    public void setOvpn(String ovpn) {
        this.ovpn = ovpn;
    }

    public String getOvpnUserName() {
        return ovpnUserName;
    }

    public void setOvpnUserName(String ovpnUserName) {
        this.ovpnUserName = ovpnUserName;
    }

    public String getOvpnUserPassword() {
        return ovpnUserPassword;
    }

    public void setOvpnUserPassword(String ovpnUserPassword) {
        this.ovpnUserPassword = ovpnUserPassword;
    }

    public static final Parcelable.Creator<Server> CREATOR
            = new Parcelable.Creator<Server>() {
        public Server createFromParcel(Parcel in) {
            return new Server(in);
        }

        public Server[] newArray(int size) {
            return new Server[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(country);
        dest.writeString(flagUrl);
        dest.writeString(ovpn);
        dest.writeString(ovpnUserName);
        dest.writeString(ovpnUserPassword);
    }

    private Server(Parcel in ) {
        country = in.readString();
        flagUrl = in.readString();
        ovpn = in.readString();
        ovpnUserName = in.readString();
        ovpnUserPassword = in.readString();
    }
}
