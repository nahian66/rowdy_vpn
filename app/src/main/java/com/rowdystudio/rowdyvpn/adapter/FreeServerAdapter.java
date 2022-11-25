package com.rowdystudio.rowdyvpn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rowdystudio.rowdyvpn.api.WebAPI;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.model.Server;

import java.util.ArrayList;
import java.util.List;

public class FreeServerAdapter extends RecyclerView.Adapter<FreeServerAdapter.MyViewHolder> {

    private final ArrayList<Server> serverLists;
    private final Context mContext;
    private OnSelectListener selectListener;
    private final int AD_TYPE = 0;
    private final int CONTENT_TYPE = 1;

    public FreeServerAdapter(Context context) {
        this.mContext = context;
        serverLists = new ArrayList<>();
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdView adview;

        if (viewType == AD_TYPE) {
            adview = new AdView(mContext);
            adview.setAdSize(AdSize.BANNER);
            adview.setAdUnitId(WebAPI.ADMOB_BANNER);
            float density = mContext.getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.BANNER.getHeight() * density);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
            adview.setLayoutParams(params);
            AdRequest request = new AdRequest.Builder().build();
            adview.loadAd(request);
            return new MyViewHolder(adview);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_server, parent, false);
            return new MyViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(getItemViewType(position) == CONTENT_TYPE){
            holder.serverCountry.setText(serverLists.get(position).getCountry());
            Glide.with(mContext)
                    .load(serverLists.get(position).getFlagUrl())
                    .into(holder.serverIcon);

            holder.itemView.setOnClickListener(v -> {
                selectListener.onSelected(serverLists.get(position));
                Log.v("Kabila",serverLists.get(position).getCountry());
            });
        }

    }

    @Override
    public int getItemCount() {
        return serverLists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView serverIcon;
        final TextView serverCountry;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            serverIcon = itemView.findViewById(R.id.flag);
            serverCountry = itemView.findViewById(R.id.countryName);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Server> servers) {
        serverLists.clear();
        serverLists.addAll(servers);
        notifyDataSetChanged();
    }

    public interface OnSelectListener {
        void onSelected(Server server);
    }

    public void setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    @Override
    public int getItemViewType(int position) {
        return serverLists.get(position) ==null? AD_TYPE:CONTENT_TYPE;
    }

    public interface ServerSelected {
        void onServerSelected(Server server);
    }
}
