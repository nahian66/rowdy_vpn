package com.rowdystudio.rowdyvpn.speed_meter.fragments;


import static android.app.Activity.RESULT_OK;
import static com.rowdystudio.rowdyvpn.speed_meter.activities.HomeActivity.tostErr;
import static com.rowdystudio.rowdyvpn.speed_meter.activities.HomeActivity.tostSucc;
import static com.rowdystudio.rowdyvpn.utils.Config.all_subscription;
import static com.rowdystudio.rowdyvpn.view.PurchaseActivityOne.loadRewardedAd;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.rowdystudio.rowdyvpn.CheckInternetConnection;
import com.rowdystudio.rowdyvpn.NowPremiumActivity;
import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.SharedPreference;
import com.rowdystudio.rowdyvpn.api.WebAPI;
import com.rowdystudio.rowdyvpn.databinding.FragmentMainBinding;
import com.rowdystudio.rowdyvpn.interfaces.ChangeServer;
import com.rowdystudio.rowdyvpn.model.Server;
import com.rowdystudio.rowdyvpn.speed_meter.activities.HomeActivity;
import com.rowdystudio.rowdyvpn.utils.Config;
import com.rowdystudio.rowdyvpn.view.PurchaseActivityOne;
import com.rowdystudio.rowdyvpn.view.Servers;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.Objects;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class VpnFragment extends Fragment
		implements View.OnClickListener, ChangeServer
{

	private Server server;
	private InterstitialAd mInterstitialAd;
	private PulsatorLayout pulsator;



	private CheckInternetConnection connection;
	private final OpenVPNThread vpnThread = new OpenVPNThread();
	private final OpenVPNService vpnService = new OpenVPNService();
	boolean vpnStart = false;
	private SharedPreference preference;
	private FragmentMainBinding binding;
	private View mView;
	private static final int REQUEST_CODE = 101;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		if (mView == null) {
			binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
			mView = binding.getRoot();

			if(all_subscription){
				binding.purchasedLayout.setVisibility(View.VISIBLE);
			}else {
				binding.purchaseLayout.setVisibility(View.VISIBLE);
			}

			binding.purchasedLayout.setOnClickListener(view -> {
				if (all_subscription){
					startActivity(new Intent(getActivity(), NowPremiumActivity.class));
				}else {
					startActivity(new Intent(getActivity(), PurchaseActivityOne.class));
				}
			});
			binding.purchaseLayout.setOnClickListener(view -> {
				if (all_subscription){
					startActivity(new Intent(getActivity(), NowPremiumActivity.class));
				}else {
					startActivity(new Intent(getActivity(), PurchaseActivityOne.class));
				}
			});

			initializeAll();
		} else {
			if (mView.getParent() != null) {
				((ViewGroup) mView.getParent()).removeView(mView);
			}
		}
		return mView;

	}

	public void LoadInterAd(){
		AdRequest adRequest = new AdRequest.Builder().build();

		com.google.android.gms.ads.interstitial.InterstitialAd.load(requireContext(),
				WebAPI.ADMOB_INTERSTITIAL,
				adRequest, new InterstitialAdLoadCallback() {
					@Override
					public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
						// The mInterstitialAd reference will be null until
						// an ad is loaded.
						mInterstitialAd = interstitialAd;

					}


					public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
						// Handle the error
						//prepareVpn();
						mInterstitialAd = null;
					}
				});

	}

	@SuppressLint("MissingPermission")
	private void initializeAll() {
		pulsator = binding.pulsatorr;

		LoadInterAd();
		MobileAds.initialize(requireContext(), initializationStatus -> loadRewardedAd(getContext()));

		AdLoader.Builder builder = new AdLoader.Builder(requireActivity(),
				WebAPI.ADMOB_NATIVE);
		builder.forNativeAd(unifiedNativeAd -> {
			FrameLayout frameLayout = binding.flAdplaceholder2;
			frameLayout.setVisibility(View.VISIBLE);

			@SuppressLint("InflateParams") com.google.android.gms.ads.nativead.NativeAdView adView =
					(com.google.android.gms.ads.nativead.NativeAdView)
							getLayoutInflater().inflate(R.layout.admob_native_new, null);

			if ((!Config.all_subscription )) {
				populateUnifiedNativeAdView(unifiedNativeAd, adView);
				frameLayout.removeAllViews();
				frameLayout.addView(adView);
			}

		}).build();
		NativeAdOptions adOptions = new NativeAdOptions.Builder().build();
		builder.withNativeAdOptions(adOptions);
		AdLoader adLoader = builder.withAdListener(new com.google.android.gms.ads.AdListener() {
		}).build();
		adLoader.loadAd(new AdRequest.Builder().build());

		///NEW//


		((HomeActivity) requireActivity()).currentVipServer.observe(requireActivity(), currentServer -> {
			server = currentServer;
			if (vpnStart)
			{
				stopVpn();
			}

			binding.countryName.setText(server.getCountry());
			binding.logTv.setText(getString(R.string.disconnected));
			updateCurrentVipServerIcon(server.getFlagUrl());

			if (((HomeActivity) requireActivity()).isActivateServer()) {
				//if ((!Config.vip_subscription && !Config.all_subscription));
				// mInterstitialAd.loadAd(new AdRequest.Builder().build());
				//else
				prepareVpn();
			}
		});
		connection = new CheckInternetConnection();
		LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));


	}

	private void populateUnifiedNativeAdView(NativeAd nativeAd, com.google.android.gms.ads.nativead.NativeAdView adView) {
		adView.setMediaView(adView.findViewById(R.id.ad_media));
		adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
		adView.setBodyView(adView.findViewById(R.id.ad_body));
		adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
		adView.setIconView(adView.findViewById(R.id.ad_app_icon));
		adView.setPriceView(adView.findViewById(R.id.ad_price));
		adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
		adView.setStoreView(adView.findViewById(R.id.ad_store));
		adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));


		((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
		Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));


		if (nativeAd.getBody() == null) {
			Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);

		} else {
			Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
			((TextView) adView.getBodyView()).setText(nativeAd.getBody());
		}
		if (nativeAd.getCallToAction() == null) {
			Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
		} else {
			Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
			((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
		}
		if (nativeAd.getIcon() == null) {
			Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
		} else {
			((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
			adView.getIconView().setVisibility(View.VISIBLE);
		}

		if (nativeAd.getPrice() == null) {
			Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);

		} else {
			Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
			((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
		}
		if (nativeAd.getStore() == null) {
			Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);
		} else {
			Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
			((TextView) adView.getStoreView()).setText(nativeAd.getStore());
		}
		if (nativeAd.getStarRating() == null) {
			Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
		} else {
			((RatingBar) Objects.requireNonNull(adView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());
			adView.getStarRatingView().setVisibility(View.VISIBLE);
		}

		if (nativeAd.getAdvertiser() == null) {
			Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
		} else
			((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
		adView.getAdvertiserView().setVisibility(View.VISIBLE);


		adView.setNativeAd(nativeAd);


	}




	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.vpnBtn.setOnClickListener(this);
		binding.currentConnectionLayout.setOnClickListener(this);
		// Checking is softmaster already running or not
		isServiceRunning();
		VpnStatus.initLogCache(requireActivity().getCacheDir());
	}


	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.vpnBtn: {
				// Vpn is running, user would like to disconnect current connection.
				if (vpnStart) {
					if (mInterstitialAd != null) {
						mInterstitialAd.show(requireActivity());
						mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
							@Override
							public void onAdDismissedFullScreenContent() {
								super.onAdDismissedFullScreenContent();
								confirmDisconnect();
								mInterstitialAd = null;
								LoadInterAd();
							}});
					}else {
						confirmDisconnect();
					}

				} else {
					if (mInterstitialAd != null) {
						mInterstitialAd.show(requireActivity());
						mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
							@Override
							public void onAdDismissedFullScreenContent() {
								super.onAdDismissedFullScreenContent();
								prepareVpn();
								mInterstitialAd = null;
								LoadInterAd();
							}});
					}else {
						prepareVpn();
					}

				}
				break;
			}

			case R.id.currentConnectionLayout: {
				if (getActivity() != null) {
					Intent mIntent = new Intent(this.getActivity(), Servers.class);
					getActivity().startActivityForResult(mIntent, REQUEST_CODE);
				}
				break;

			}
		}
	}


	public void confirmDisconnect() {
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
		builder.setMessage(requireActivity().getString(R.string.connection_close_confirm));

		builder.setPositiveButton(requireActivity().getString(R.string.yes), (dialog, id) -> stopVpn());
		builder.setNegativeButton(requireActivity().getString(R.string.no), (dialog, id) -> {
			// User cancelled the dialog
		});

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}


	private void prepareVpn() {
		if (!vpnStart) {
			if (getInternetStatus()) {

				// Checking permission for network monitor
				Intent intent = VpnService.prepare(getContext());

				if (intent != null) {
					startActivityForResult(intent, 1);
				} else startVpn();//have already permission

				// Update confection status
				status("connecting");

			} else {
				// No internet connection available
				tostErr(getContext(),"you have no internet connection !!");
			}

		} else if (stopVpn()) {

			// VPN is stopped, show a Toast message.
			tostSucc(getContext(),"Disconnect Successfully");
		}
	}


	public boolean stopVpn() {
		try {
			vpnThread.stop();

			status("connect");
			vpnStart = false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			//Permission granted, start the VPN
			startVpn();
		} else {
			showToast("Permission Deny !! ");
		}
	}



	public boolean getInternetStatus() {
		return connection.netCheck(requireContext());
	}


	public void isServiceRunning() {
		setStatus(OpenVPNService.getStatus());
	}


	@SuppressLint("SetTextI18n")
	private void startVpn() {
		try {
			OpenVpnApi.startVpn(getContext(), server.getOvpn(), server.getCountry(), server.getOvpnUserName(), server.getOvpnUserPassword());

			// Update log
			pulsator.start();
			binding.logTv.setText("Connecting...");
			binding.firstElipse.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.ellipse_3));
			binding.logTv.setTextColor(getResources().getColor(R.color.yellow_color));
			binding.vpnBtn.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.ellipse_con1));
			binding.secondElipse.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.ellipse_con2));

			vpnStart = true;

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}


	@SuppressLint("SetTextI18n")
	public void setStatus(String connectionState) {
		if (connectionState != null)
			switch (connectionState) {
				case "DISCONNECTED":
					status("connect");
					vpnStart = false;
					pulsator.stop();
					OpenVPNService.setDefaultStatus();
					binding.logTv.setText(getString(R.string.disconnected));
					binding.firstElipse.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.ellipse_3));
					binding.logTv.setTextColor(getResources().getColor(R.color.connection_text));
					binding.vpnBtn.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.ellipse_1));
					binding.secondElipse.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.ellipse_2));

					break;
				case "CONNECTED":
					vpnStart = true;// it will use after restart this activity
					status("connected");
					pulsator.stop();
					binding.logTv.setText("Connected");
					binding.logTv.setTextColor(getResources().getColor(R.color.gnt_green));
					binding.vpnBtn.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.ellipse_yes1));
					binding.secondElipse.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.ellipse_yes2));
					break;
				case "WAIT":
					binding.logTv.setText("waiting for server connection..");
					binding.logTv.setTextColor(getResources().getColor(R.color.yellow_color));
					break;
				case "AUTH":
					binding.logTv.setText("Please Wait..");
					binding.logTv.setTextColor(getResources().getColor(R.color.yellow_color));
					break;
				case "RECONNECTING":
					status("connecting");
					binding.logTv.setText("Reconnecting..");
					binding.logTv.setTextColor(getResources().getColor(R.color.yellow_color));
					break;
				case "NONETWORK":
					binding.logTv.setText("No network connection..");
					binding.logTv.setTextColor(getResources().getColor(R.color.yellow_color));
					break;
			}

	}


	public void status(String status) {

		if (status.equals("invalidDevice")) {
			binding.vpnBtn.setBackgroundResource(R.drawable.button_connected);

		} else if (status.equals("authenticationCheck")) {
			binding.vpnBtn.setBackgroundResource(R.drawable.button_connecting);

		}

	}


	final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				setStatus(intent.getStringExtra("state"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {

				String duration = intent.getStringExtra("duration");
				String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
				String byteIn = intent.getStringExtra("byteIn");
				String byteOut = intent.getStringExtra("byteOut");

				if (duration == null) duration = "00:00:00";
				if (lastPacketReceive == null) lastPacketReceive = "0";
				if (byteIn == null) byteIn = " ";
				if (byteOut == null) byteOut = " ";
				updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};


	public void updateConnectionStatus(String duration, String lastPacketReceive, String byteIn, String byteOut) {
		binding.durationTv.setText(duration);
		String byteinKb = byteIn.split("-")[0];
		String byteoutKb = byteOut.split("-")[0];
		binding.byteInTv.setText(byteinKb);
		binding.byteOutTv.setText(byteoutKb);
	}


	public void showToast(String message) {
		Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
	}


	public void updateCurrentVipServerIcon(String serverIcon) {
		Glide.with(requireActivity())
				.load(serverIcon)
				.into(binding.selectedServerIcon);

	}


	@Override
	public void newServer(Server server) {
		this.server = server;

		// Stop previous connection
		if (vpnStart) {
			stopVpn();
		}

		prepareVpn();
	}



}
