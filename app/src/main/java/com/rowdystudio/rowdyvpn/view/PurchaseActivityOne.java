package com.rowdystudio.rowdyvpn.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rowdystudio.rowdyvpn.api.WebAPI;
import com.rowdystudio.rowdyvpn.speed_meter.activities.HomeActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.utils.Config;
import es.dmoral.toasty.Toasty;


public class PurchaseActivityOne extends AppCompatActivity {

    RelativeLayout backToActivity;

    public static RewardedAd mRewardedAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);


        backToActivity = findViewById(R.id.back);
        backToActivity.setOnClickListener(view -> finish());

        LinearLayout watch_reward = findViewById(R.id.watch_reward);
        watch_reward.setOnClickListener(v -> showRewardedAd());

        LinearLayout purchase_btn = findViewById(R.id.purchase_btn);
        purchase_btn.setOnClickListener(v -> {
            Intent intent = new Intent(PurchaseActivityOne.this, PurchaseActivity.class);
            startActivity(intent);
        });

    }

    public static void loadRewardedAd(final Context context) {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(context, WebAPI.ADMOB_REWARDED_AD,
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mRewardedAd = null;

                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                loadRewardedAd(context);
                                context.startActivity(new Intent(context, HomeActivity.class));

                            }
                        });
                    }
                });
    }

    private void showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd.show(this, rewardItem -> {
                Toasty.success(this, "VIP Servers are Unlocked", Toast.LENGTH_SHORT, true).show();
                Config.vip_subscription = true;
            });
        } else {
            Toasty.error(this, "The ad wasn't ready yet", Toast.LENGTH_SHORT, true).show();

        }

    }

}