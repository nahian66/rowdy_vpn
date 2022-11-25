package com.rowdystudio.rowdyvpn.speed_meter.fragments;

import static com.rowdystudio.rowdyvpn.speed_meter.utils.LogUtils.LOGE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.api.WebAPI;
import com.rowdystudio.rowdyvpn.speed_meter.adapters.DataAdapter;
import com.rowdystudio.rowdyvpn.speed_meter.models.DataInfo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DailyDataFragment extends Fragment {

	List<DataInfo> monthData;

	View rootView;
	private DataAdapter dataAdapter;
	private RecyclerView recList;
	private TextView tvDate;
	private TextView tvPing;
	private TextView tvDownload;
	private TextView tvUpload;
	private FrameLayout fLAds;

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState) {
		rootView = inflater.inflate (R.layout.fragment_daily_data, container, false);
		tvDate = rootView.findViewById (R.id.tv_date);
		tvPing = rootView.findViewById (R.id.tv_ping);
		tvDownload = rootView.findViewById (R.id.tv_download);
		tvUpload = rootView.findViewById (R.id.tv_upload);
		fLAds = rootView.findViewById (R.id.v_ad_unified);

		init ();
		LoadBanner();
		Random random = new Random ();
		int l = random.nextInt (2);
		Log.e ("newactvitiy", "newactivity" + l);
		return rootView;
	}

	public void LoadBanner(){
		MobileAds.initialize(requireContext(), initializationStatus -> {

		});
		com.google.android.gms.ads.AdView adView = new AdView(requireContext());
		adView.setAdUnitId(WebAPI.ADMOB_BANNER);
		adView.setAdSize(AdSize.BANNER);
		fLAds.addView(adView);
		AdRequest build = new AdRequest.Builder().build();
		adView.loadAd(build);
	}

	private void init () {
		tvDate.post (() -> {
			int length = tvDate.getMeasuredWidth ();
			float angle = 45;
			Shader textShader = new LinearGradient (0, 0, (int) (Math.sin (Math.PI * angle / 180) * length),
					(int) (Math.cos (Math.PI * angle / 180) * length),
					new int[] {0xFF30E3CA, 0xFFa5dee5},
					null,
					Shader.TileMode.CLAMP);
			tvDate.getPaint ().setShader (textShader);
			tvDate.invalidate ();
		});
		tvPing.post (() -> {
			int length = tvPing.getMeasuredWidth ();
			float angle = 45;
			Shader textShader = new LinearGradient (0, 0, (int) (Math.sin (Math.PI * angle / 180) * length),
					(int) (Math.cos (Math.PI * angle / 180) * length),
					new int[] {0xFF30E3CA, 0xFFa5dee5},
					null,
					Shader.TileMode.CLAMP);
			tvPing.getPaint ().setShader (textShader);
			tvPing.invalidate ();
		});
		tvDownload.post (() -> {
			int length = tvDownload.getMeasuredWidth ();
			float angle = 45;
			Shader textShader = new LinearGradient (0, 0, (int) (Math.sin (Math.PI * angle / 180) * length),
					(int) (Math.cos (Math.PI * angle / 180) * length),
					new int[] {0xFF30E3CA, 0xFFa5dee5},
					null,
					Shader.TileMode.CLAMP);
			tvDownload.getPaint ().setShader (textShader);
			tvDownload.invalidate ();
		});
		tvUpload.post (() -> {
			int length = tvUpload.getMeasuredWidth ();
			float angle = 45;
			Shader textShader = new LinearGradient (0, 0, (int) (Math.sin (Math.PI * angle / 180) * length),
					(int) (Math.cos (Math.PI * angle / 180) * length),
					new int[] {0xFF30E3CA, 0xFFa5dee5},
					null,
					Shader.TileMode.CLAMP);
			tvUpload.getPaint ().setShader (textShader);
			tvUpload.invalidate ();
		});
		recList = rootView.findViewById (R.id.cardList);
		recList.setHasFixedSize (true);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
		recList.setLayoutManager (layoutManager);
		monthData = createList ();
		dataAdapter = new DataAdapter (getActivity (), monthData);
		recList.setAdapter (dataAdapter);
	}


	private List<DataInfo> createList() {
		List<DataInfo> result = new ArrayList<> ();
		SharedPreferences sharedPref = requireActivity().getSharedPreferences (
				"historydata", Context.MODE_PRIVATE);
		String _data = sharedPref.getString ("DATA", "");
		if (!_data.equals ("")) {
			LOGE ("TAG", "1");
			JSONObject js;
			try {
				js = new JSONObject (_data);
				JSONArray array = js.getJSONArray ("History");
				if (array.length () <= 30) {
					LOGE ("TAG", "2");
					for (int i = array.length () - 1 ; i >= 0 ; i--) {
						JSONObject jo = array.getJSONObject (i);
						result.add (new DataInfo (jo.getLong ("date"), jo.getDouble ("ping"), jo.getDouble ("download"), jo.getDouble ("upload")));
					}
					if (array.length () != 30) {
						for (int i = 0; i < (30 - array.length ()) ; i++) {
							result.add (new DataInfo ());
						}
					}
				} else {
					LOGE ("TAG", "3");
					int count = 0;
					while (count <= 30) {
						JSONObject jo = array.getJSONObject ((array.length () - 1) - count);
						result.add (new DataInfo (jo.getLong ("date"), jo.getDouble ("ping"), jo.getDouble ("download"), jo.getDouble ("upload")));
						count++;
					}
				}
			} catch (JSONException e) {
				LOGE ("TAG", "ERROR" + e.getMessage ());
			}
		} else {
			LOGE ("TAG", "4");
			for (int i = 0; i < 30; i++) {
				result.add (new DataInfo ());
			}
		}
		LOGE ("TAG", "list added");
		return result;
	}



	@Override
	public void onResume () {
		super.onResume ();
		LOGE ("TAG", "resume");
		monthData = createList ();
		dataAdapter = new DataAdapter (getActivity (), monthData);
		recList.setAdapter (dataAdapter);
	}



}
