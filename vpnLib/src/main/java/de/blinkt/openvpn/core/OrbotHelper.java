


package de.blinkt.openvpn.core;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.blinkt.openvpn.core.OpenVPNService.ORBOT_PACKAGE_NAME;

public class OrbotHelper {


    public final static String ACTION_STATUS = "org.torproject.android.intent.action.STATUS";
    public final static String STATUS_ON = "ON";
    public final static String STATUS_STARTS_DISABLED = "STARTS_DISABLED";

    public final static String STATUS_STARTING = "STARTING";
    public final static String STATUS_STOPPING = "STOPPING";
    public final static String EXTRA_STATUS = "org.torproject.android.intent.extra.STATUS";

    public final static String ACTION_START = "org.torproject.android.intent.action.START";
    public final static String EXTRA_PACKAGE_NAME = "org.torproject.android.intent.extra.PACKAGE_NAME";
    public static final int SOCKS_PROXY_PORT_DEFAULT = 9050;
    @SuppressLint("StaticFieldLeak")
    private static OrbotHelper mInstance;

    final String EXTRA_SOCKS_PROXY_HOST = "org.torproject.android.intent.extra.SOCKS_PROXY_HOST";
    final String EXTRA_SOCKS_PROXY_PORT = "org.torproject.android.intent.extra.SOCKS_PROXY_PORT";
    private Context mContext;
    private final Set<StatusCallback> statusCallbacks = new HashSet<>();
    private final BroadcastReceiver orbotStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (TextUtils.equals(intent.getAction(),
                    OrbotHelper.ACTION_STATUS)) {
                for (StatusCallback cb : statusCallbacks) {
                    cb.onStatus(intent);
                }

                String status = intent.getStringExtra(EXTRA_STATUS);
                if (TextUtils.equals(status, STATUS_ON)) {
                    int socksPort = intent.getIntExtra(EXTRA_SOCKS_PROXY_PORT, SOCKS_PROXY_PORT_DEFAULT);
                    String socksHost = intent.getStringExtra(EXTRA_SOCKS_PROXY_HOST);
                    if (TextUtils.isEmpty(socksHost))
                        socksHost = "127.0.0.1";
                    for (StatusCallback cb : statusCallbacks) {
                        cb.onOrbotReady(intent, socksHost, socksPort);
                    }
                } else if (TextUtils.equals(status, STATUS_STARTS_DISABLED)) {
                    for (StatusCallback cb : statusCallbacks)
                        cb.onDisabled(intent);
                }

            }
        }
    };

    private OrbotHelper() {

    }

    public static OrbotHelper get(OpenVPNService mOpenVPNService) {
        if (mInstance == null)
            mInstance = new OrbotHelper();
        return mInstance;
    }


    public static Intent getOrbotStartIntent(Context context) {
        Intent intent = new Intent(ACTION_START);
        intent.setPackage(ORBOT_PACKAGE_NAME);
        intent.putExtra(EXTRA_PACKAGE_NAME, context.getPackageName());
        return intent;
    }

    public static boolean checkTorReceier(Context c) {
        Intent startOrbot = getOrbotStartIntent(c);
        PackageManager pm = c.getPackageManager();
        Intent result = null;
        List<ResolveInfo> receivers =
                pm.queryBroadcastReceivers(startOrbot, 0);

        return receivers == null || receivers.size() <= 0;
    }


    public synchronized void addStatusCallback(Context c, StatusCallback cb) {
        if (statusCallbacks.size() == 0) {
            c.getApplicationContext().registerReceiver(orbotStatusReceiver,
                    new IntentFilter(OrbotHelper.ACTION_STATUS));
            mContext = c.getApplicationContext();
        }
        if (checkTorReceier(c))
            cb.onNotYetInstalled();
        statusCallbacks.add(cb);
    }


    public synchronized void removeStatusCallback(StatusCallback cb) {
        statusCallbacks.remove(cb);
        if (statusCallbacks.size() == 0)
            mContext.unregisterReceiver(orbotStatusReceiver);
    }

    public void sendOrbotStartAndStatusBroadcast() {
        mContext.sendBroadcast(getOrbotStartIntent(mContext));
    }

    private void startOrbotService(String action) {
        Intent clearVPNMode = new Intent();
        clearVPNMode.setComponent(new ComponentName(ORBOT_PACKAGE_NAME, ".service.TorService"));
        clearVPNMode.setAction(action);
        mContext.startService(clearVPNMode);
    }

    public interface StatusCallback {

        void onStatus(Intent statusIntent);



        void onNotYetInstalled();

        void onOrbotReady(Intent intent, String socksHost, int socksPort);


        void onDisabled(Intent intent);
    }
}
