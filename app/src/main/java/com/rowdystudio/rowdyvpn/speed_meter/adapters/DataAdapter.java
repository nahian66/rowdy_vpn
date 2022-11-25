package com.rowdystudio.rowdyvpn.speed_meter.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.speed_meter.models.DataInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * The type Data adapter.
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {
	/**
	 * The Data list.
	 */
	public List<DataInfo> dataList;
	/**
	 * The Context.
	 */
	final Context context;

	/**
	 * Instantiates a new Data adapter.
	 *
	 * @param context  the context
	 * @param dataList the data list
	 */
	public DataAdapter(Context context, List<DataInfo> dataList) {
		this.dataList = dataList;
		this.context = context;
	}

	@NonNull
	@Override
	public DataViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
		View itemView = LayoutInflater
				.from (parent.getContext ())
				.inflate (R.layout.card_layout, parent, false);
		return new DataViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder (DataViewHolder holder, int position) {
		DataInfo di = dataList.get (position);
		@SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat (context.getString (R.string.date_format));
		Calendar calendar = Calendar.getInstance ();
		calendar.setTimeInMillis (di.getDate ());
		holder.vDate.setText (formatter.format (calendar.getTime ()));
		holder.vWifi.setText (String.valueOf (di.getPing ()));
		holder.vMobile.setText (String.valueOf (di.getDownload ()));
		holder.vTotal.setText (String.valueOf (di.getUpload ()));
		if (position % 2 == 0) {
			holder.card_view.setBackgroundColor (context.getResources ().getColor (R.color.white_10));
		} else {
			holder.card_view.setBackgroundColor (context.getResources ().getColor (android.R.color.transparent));
		}
	}

	/**
	 * Update data.
	 *
	 * @param temp the temp
	 */
	@SuppressLint("NotifyDataSetChanged")
	public void updateData (List<DataInfo> temp) {
		this.dataList = temp;
		notifyDataSetChanged ();
	}

	@Override
	public int getItemCount () {
		return dataList.size ();
	}

	/**
	 * The type Data view holder.
	 */
	static class DataViewHolder extends RecyclerView.ViewHolder {
		/**
		 * The V date.
		 */
		final TextView vDate;
		/**
		 * The V wifi.
		 */
		final TextView vWifi;
		/**
		 * The V mobile.
		 */
		final TextView vMobile;
		/**
		 * The V total.
		 */
		final TextView vTotal;
		/**
		 * The Card view.
		 */
		final ConstraintLayout card_view;

		/**
		 * Instantiates a new Data view holder.
		 *
		 * @param itemView the item view
		 */
		public DataViewHolder (View itemView) {
			super (itemView);
			vDate = itemView.findViewById (R.id.id_date);
			vWifi = itemView.findViewById (R.id.id_wifi);
			vMobile = itemView.findViewById (R.id.mobile);
			vTotal = itemView.findViewById (R.id.total);
			card_view = itemView.findViewById (R.id.card_view);
		}
	}
}
