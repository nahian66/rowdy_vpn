package com.rowdystudio.rowdyvpn.speed_meter.activities;

import static com.rowdystudio.rowdyvpn.speed_meter.utils.LogUtils.LOGE;
import static com.rowdystudio.rowdyvpn.utils.Config.all_subscription;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.rowdystudio.rowdyvpn.BuildConfig;
import com.rowdystudio.rowdyvpn.NowPremiumActivity;
import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.SharedPreference;
import com.rowdystudio.rowdyvpn.adapter.ServerListRVAdapter;
import com.rowdystudio.rowdyvpn.interfaces.ChangeServer;
import com.rowdystudio.rowdyvpn.model.Server;
import com.rowdystudio.rowdyvpn.speed_meter.fragments.DailyDataFragment;
import com.rowdystudio.rowdyvpn.speed_meter.fragments.SpeedTestFragment;
import com.rowdystudio.rowdyvpn.speed_meter.fragments.VpnFragment;
import com.rowdystudio.rowdyvpn.utils.Config;
import com.rowdystudio.rowdyvpn.view.Faq;
import com.rowdystudio.rowdyvpn.view.PurchaseActivityOne;
import com.rowdystudio.rowdyvpn.view.Servers;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * The type Home activity.
 */
public class HomeActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener,
		PurchasesUpdatedListener, BillingClientStateListener {

	private TextView tvSubTitle;
	private ViewPager viewPager;
	private BottomNavigationView bottomNavigationView;

	private RadioButton rbMBps;
	private RadioButton rbkBps;
	private RadioButton rbMbps;
	private RadioButton rbkbps;




	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	private RecyclerView serverListRv;
	private ArrayList<Server> serverLists;
	private DrawerLayout drawer;
	private SharedPreference preference;


	protected Toolbar toolbar;

	public final MutableLiveData<Server> currentVipServer = new MutableLiveData<>();

	public static final String TAG = "Open VPN";

	private BillingClient billingClient;
	private final Map<String, SkuDetails> skusWithSkuDetails = new HashMap<>();
	private final List<String> allSubs = new ArrayList<>(Arrays.asList(
			Config.all_month_id,
			Config.all_threemonths_id,
			Config.all_sixmonths_id,
			Config.all_yearly_id));

	private boolean activateServer;

	private static final int REQUEST_CODE = 101;
	private Dialog RateDialog;
	public ImageView menudr,rateBtn,cursor;

	@Override
	protected void onCreate (@Nullable Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_main);
		find_views_by_id ();
		init_variables ();
//
		billingClient = BillingClient
				.newBuilder(this)
				.setListener(this)
				.enablePendingPurchases()
				.build();

		connectToBillingService();
		MobileAds.initialize(this, initializationStatus -> {
		});
//
		ButterKnife.bind(this);
		preference = new SharedPreference(this);
		currentVipServer.setValue(preference.getVipServer());

		initializeAll();

		//openScreen(fragment);

		if (serverLists != null) {
			ServerListRVAdapter serverListRVAdapter = new ServerListRVAdapter(serverLists, this);
			serverListRv.setAdapter(serverListRVAdapter);
		}

	}

	private void find_views_by_id () {
		Toolbar toolbar = findViewById(R.id.toolbar);
		menudr = toolbar.findViewById (R.id.menudr);
		rateBtn = toolbar.findViewById (R.id.rateBtn);
		tvSubTitle = toolbar.findViewById (R.id.toolbar_subtitle);
		viewPager = findViewById (R.id.viewpager);
		bottomNavigationView = findViewById (R.id.bottom_navigation);
	}





		@SuppressLint("NonConstantResourceId")
		private void init_variables () {

		menudr.setOnClickListener(view -> openCloseDrawer());
		InitiaterateDialog();
		rateBtn.setOnClickListener(view -> {
			if(cursor!=null){
				cursor.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in_out));
			}
			RateDialog.show();
		});


		Intent intentBC = new Intent ();
		intentBC.setAction ("com.maxcloud.softmaster.speed_meter");
		sendBroadcast (intentBC);
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		if (viewPager != null)
			viewPager.setAdapter (adapter);
		if (viewPager != null)
			viewPager.setCurrentItem (1);
		tvSubTitle.setText (getString (R.string.vpn));
		bottomNavigationView.setSelectedItemId (R.id.action_vpn);
		bottomNavigationView.setOnNavigationItemSelectedListener (item -> {
			switch (item.getItemId ()) {
				case R.id.action_history:
					viewPager.setCurrentItem (0);
					tvSubTitle.setText (getString (R.string.history));
					break;
				case R.id.action_vpn:
					//showAlertDialogButtonClicked ();
					viewPager.setCurrentItem (1);
					tvSubTitle.setText(R.string.vpn);
					break;
				case R.id.action_speed:
					viewPager.setCurrentItem (2);
					tvSubTitle.setText (getString (R.string.speed));
					break;
				default:
					break;
			}
			return true;
		});
		if (viewPager != null)
			viewPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener () {
				@Override
				public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
					LOGE ("TAG", "onPageScrolled");
				}

				@Override
				public void onPageSelected (int position) {
					switch (position) {
						case 0:
							bottomNavigationView.getMenu ().findItem (R.id.action_history).setChecked (true);
							tvSubTitle.setText (getString (R.string.history));
							break;
						case 1:
							bottomNavigationView.getMenu ().findItem (R.id.action_vpn).setChecked (true);
							tvSubTitle.setText(R.string.vpn);
							break;
						case 2:
							bottomNavigationView.getMenu ().findItem (R.id.action_speed).setChecked (true);
							tvSubTitle.setText (getString (R.string.speed));
							break;
						default:
							break;
					}
				}

				@Override
				public void onPageScrollStateChanged (int state) {
					LOGE ("TAG", "onPageScrollStateChanged");
				}
			});
	}


	private void showAlertDialogButtonClicked () {
		AlertDialog.Builder builder = new AlertDialog.Builder (this, R.style.MyDialogTheme);
		final View customLayout
				= getLayoutInflater ()
				.inflate (R.layout.dialog_datarate_units, null);
		builder.setView (customLayout);
		rbMBps = customLayout.findViewById (R.id.rb_MBps);
		rbkBps = customLayout.findViewById (R.id.rb_kBps);
		rbMbps = customLayout.findViewById (R.id.rb_Mbps);
		rbkbps = customLayout.findViewById (R.id.rb_kbps);
		SharedPreferences sharedPref = this.getSharedPreferences (
				"setting", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit ();
		switch (sharedPref.getString ("UNIT", "Mbps")) {
			case "MBps":
				rbMBps.setChecked (true);
				rbkBps.setChecked (false);
				rbMbps.setChecked (false);
				rbkbps.setChecked (false);
				break;
			case "kBps":
				rbMBps.setChecked (false);
				rbkBps.setChecked (true);
				rbMbps.setChecked (false);
				rbkbps.setChecked (false);
				break;
			case "Mbps":
				rbMBps.setChecked (false);
				rbkBps.setChecked (false);
				rbMbps.setChecked (true);
				rbkbps.setChecked (false);
				break;
			case "kbps":
				rbMBps.setChecked (false);
				rbkBps.setChecked (false);
				rbMbps.setChecked (false);
				rbkbps.setChecked (true);
				break;
			default:
				LOGE ("TAG", "ERROR");
				break;
		}
		rbMBps.setOnCheckedChangeListener ((buttonView, isChecked) -> {
			if (isChecked) {
				editor.remove ("UNIT");
				editor.putString ("UNIT", "MBps");
				editor.apply ();
				rbkBps.setChecked (false);
				rbMbps.setChecked (false);
				rbkbps.setChecked (false);
			}
		});
		rbkBps.setOnCheckedChangeListener ((buttonView, isChecked) -> {
			if (isChecked) {
				editor.remove ("UNIT");
				editor.putString ("UNIT", "kBps");
				editor.apply ();
				rbMBps.setChecked (false);
				rbMbps.setChecked (false);
				rbkbps.setChecked (false);
			}
		});
		rbMbps.setOnCheckedChangeListener ((buttonView, isChecked) -> {
			if (isChecked) {
				editor.remove ("UNIT");
				editor.putString ("UNIT", "Mbps");
				editor.apply ();
				rbkBps.setChecked (false);
				rbMBps.setChecked (false);
				rbkbps.setChecked (false);
			}
		});
		rbkbps.setOnCheckedChangeListener ((buttonView, isChecked) -> {
			if (isChecked) {
				editor.remove ("UNIT");
				editor.putString ("UNIT", "kbps");
				editor.apply ();
				rbkBps.setChecked (false);
				rbMbps.setChecked (false);
				rbMBps.setChecked (false);
			}
		});
		builder
				.setPositiveButton (
						"OK",
						(dialog, which) -> {
						});
		builder.setNegativeButton ("Cancel",
				(dialog, which) -> {
				});
		AlertDialog dialog
				= builder.create ();
		dialog.show ();
	}


	@Override
	protected void onResume () {
		super.onResume ();
	}

	@Override
	protected void onPause () {
		super.onPause ();
	}

	@Override
	protected void onDestroy () {
		super.onDestroy ();
	}



	private class ViewPagerAdapter extends FragmentStatePagerAdapter {
		/**
		 * The Page count.
		 */
		final int PAGE_COUNT = 3;
		private final String[] mTabsTitle = {getString (R.string.history), getString (R.string.vpn), getString (R.string.speed)};

		/**
		 * Instantiates a new View pager adapter.
		 *
		 * @param fm the fm
		 */
		ViewPagerAdapter (FragmentManager fm) {
			super (fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		}

		/**
		 * Gets tab view.
		 *
		 * @param position the position
		 * @return the tab view
		 */
		public View getTabView (int position) {
			@SuppressLint("InflateParams") View view = LayoutInflater.from (getApplicationContext ()).inflate (R.layout.custom_tab, null);
			TextView title = view.findViewById (R.id.title);
			title.setText (mTabsTitle[ position ]);
			return view;
		}

		@NonNull
		@Override
		public Fragment getItem (int pos) {
			switch (pos) {
				case 0:
					return new DailyDataFragment ();
				case 1:
					return new VpnFragment();
					case 2:
					return new SpeedTestFragment ();
				default:
					break;
			}
			return null;
		}

		@Override
		public void destroyItem (ViewGroup viewPager, int position, @NonNull Object object) {
			LOGE ("TAG", "destroyItem");
		}

		@Override
		public int getCount () {
			return PAGE_COUNT;
		}

		@Override
		public CharSequence getPageTitle (int position) {
			return null;
		}
	}





	//////////////////

	private void initializeAll() {
		drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		NavigationView navigationView = findViewById(R.id.navigation_view);
		navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

		Fragment fragment = new VpnFragment();
		ChangeServer changeServer = (ChangeServer) fragment;

	}


	public void openCloseDrawer() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START, true);
		} else {
			drawer.openDrawer(GravityCompat.START, true);
		}
	}


	void openScreen(Fragment fragmentClass) {
		FragmentManager manager = getSupportFragmentManager();
		try {
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.container, fragmentClass);
			transaction.commitAllowingStateLoss();
			manager.executePendingTransactions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onStop() {
		if (currentVipServer.getValue() != null) {
			preference.saveVipServer(currentVipServer.getValue());
		}
		super.onStop();
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@SuppressLint("NonConstantResourceId")
	public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
		// Handle navigation view item clicks here.
		switch (menuitem.getItemId()) {
			case R.id.nav_upgrade:
				startActivity(new Intent(this, Servers.class));
				break;
			case R.id.nav_unlock:
				if (all_subscription){
					startActivity(new Intent(this, NowPremiumActivity.class));
				}else {
					startActivity(new Intent(this, PurchaseActivityOne.class));
				}
				break;
			case R.id.nav_helpus:
//            find help about the application
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.setData(Uri.parse("mailto:"));
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_email)});
				intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.help_to_improve_us_email_subject));
				intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.help_to_improve_us_body));

				try {
					startActivity(Intent.createChooser(intent, "send mail"));
				} catch (ActivityNotFoundException ex) {
					Toast.makeText(this, "No mail app found!!!", Toast.LENGTH_SHORT).show();
				} catch (Exception ex) {
					Toast.makeText(this, "Unexpected Error!!!", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.nav_rate:
				rateUs();
				break;

			case R.id.nav_share:
//            share the application...
				try {
					Intent shareIntent = new Intent(Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app");
					shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm using this" + getResources().getString(R.string.app_name) + "App, it's provide all fastest servers for free https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
					startActivity(Intent.createChooser(shareIntent, "choose one"));
				} catch (Exception e) {
					Toasty.success(this, "Error..", Toast.LENGTH_SHORT, true).show();
				}
				break;
			case R.id.nav_faq:
				startActivity(new Intent(this, Faq.class));
				break;

			case R.id.nav_about:
				showAboutDialog();
				break;

			case R.id.nav_policy:
				try {
					Uri uri = Uri.parse(getResources().getString(R.string.privacy_policy_link)); // missing 'http://' will cause crashed
					Intent intent_policy = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent_policy);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					tostErr(this, "Please give a valid privacy policy URL.");
				}
				break;
			case R.id.exit:
				finish();
				break;
		}
		return true;
	}

	private void showAboutDialog() {

		Dialog about_dialog = new Dialog(this);
		about_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
		about_dialog.setContentView(R.layout.dialog_about);
		about_dialog.setCancelable(true);
		about_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(about_dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		about_dialog.findViewById(R.id.bt_close).setOnClickListener(v -> about_dialog.dismiss());

		about_dialog.show();
		about_dialog.getWindow().setAttributes(lp);
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	protected void rateUs() {
		Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

		goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
				Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
				Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName())));
		}
	}

	private void connectToBillingService() {
		if (!billingClient.isReady()) {
			billingClient.startConnection(this);
		}
	}

	@Override
	public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
		if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
			querySkuDetailsAsync(
					allSubs
			);
			queryPurchases();
		}
	}

	@Override
	public void onBillingServiceDisconnected() {
		connectToBillingService();
	}

	@Override
	public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {

	}

	private void queryPurchases() {
		Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
		List<Purchase> purchases = result.getPurchasesList();
		List<ArrayList<String>> skus = new ArrayList<>();

		if (purchases != null) {
			for (Purchase purchase : purchases) {
				skus.add(purchase.getSkus());

			}

			if (skus.contains(Config.all_month_id) ||
					skus.contains(Config.all_threemonths_id) ||
					skus.contains(Config.all_sixmonths_id) ||
					skus.contains(Config.all_yearly_id)) {
				all_subscription = true;
			}
		}
	}

	private void querySkuDetailsAsync(List<String> skuList) {
		SkuDetailsParams params = SkuDetailsParams
				.newBuilder()
				.setSkusList(skuList)
				.setType(BillingClient.SkuType.SUBS)
				.build();

		billingClient.querySkuDetailsAsync(
				params, (billingResult, skuDetailsList) -> {
					if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
						for (SkuDetails details : skuDetailsList) {
							skusWithSkuDetails.put(details.getSku(), details);
						}
					}
				}
		);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE && data != null) {
				activateServer = true;
				Server server = data.getParcelableExtra("server");
				currentVipServer.postValue(server);
			}
		}
	}

	public boolean isActivateServer() {
		return activateServer;
	}


	public static void tostSucc(final Context context,String tt ){

		Toasty.success(context, tt, Toast.LENGTH_SHORT, true).show();

	}
	public static void tostErr(final Context context,String tt ){

		Toasty.error(context, tt, Toast.LENGTH_SHORT, true).show();

	}

	public void InitiaterateDialog(){
		RateDialog = new Dialog(this);
		RateDialog.setContentView(R.layout.rating_window);
		Button rateButton = RateDialog.findViewById(R.id.btn_rt);
		ImageView laterButton = RateDialog.findViewById(R.id.laterButton);
		cursor = RateDialog.findViewById(R.id.ic_rate_us);
		rateButton.setOnClickListener(view -> {

			RateDialog.dismiss();
			if(cursor!=null)
				cursor.clearAnimation();
			Rate();
		});


		laterButton.setOnClickListener(view -> RateDialog.dismiss());


		RateDialog.setOnCancelListener(dialog -> {
			if(cursor!=null)
				cursor.clearAnimation();
		});
		RateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	public void Rate(){
		final String appPackageName = BuildConfig.APPLICATION_ID;
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
		}
	}

}
