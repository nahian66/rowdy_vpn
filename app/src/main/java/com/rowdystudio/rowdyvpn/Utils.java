package com.rowdystudio.rowdyvpn;

import android.net.Uri;

public class Utils {


    public static String getImgURL(int resourceId) {

        // Use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" +  BuildConfig.APPLICATION_ID + "/" + resourceId).toString();
    }
}
