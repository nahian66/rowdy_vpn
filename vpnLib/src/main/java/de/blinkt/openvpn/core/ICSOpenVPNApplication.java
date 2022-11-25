

package de.blinkt.openvpn.core;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import android.os.StrictMode;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.api.AppRestrictions;

public class ICSOpenVPNApplication extends Application {

    @Override
    public void onCreate() {
        if("robolectric".equals(Build.FINGERPRINT))
            return;

        super.onCreate();
        PRNGFixes.apply();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannels();
        StatusListener mStatus = new StatusListener();
        mStatus.init(getApplicationContext());

        enableStrictModes();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppRestrictions.getInstance(this).checkRestrictions(this);
        }
    }

    private void enableStrictModes() {
        StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build();
        StrictMode.setVmPolicy(policy);

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence name = getString(R.string.channel_name_background);
        NotificationChannel mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_BG_ID,
                name, NotificationManager.IMPORTANCE_MIN);

        mChannel.setDescription(getString(R.string.channel_description_background));
        mChannel.enableLights(false);

        mChannel.setLightColor(Color.DKGRAY);
        mNotificationManager.createNotificationChannel(mChannel);


        name = getString(R.string.channel_name_status);
        mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_NEWSTATUS_ID,
                name, NotificationManager.IMPORTANCE_LOW);

        mChannel.setDescription(getString(R.string.channel_description_status));
        mChannel.enableLights(true);

        mChannel.setLightColor(Color.BLUE);
        mNotificationManager.createNotificationChannel(mChannel);


        name = getString(R.string.channel_name_userreq);
        mChannel = new NotificationChannel(OpenVPNService.NOTIFICATION_CHANNEL_USERREQ_ID,
                name, NotificationManager.IMPORTANCE_HIGH);
        mChannel.setDescription(getString(R.string.channel_description_userreq));
        mChannel.enableVibration(true);
        mChannel.setLightColor(Color.CYAN);
        mNotificationManager.createNotificationChannel(mChannel);
    }
}
