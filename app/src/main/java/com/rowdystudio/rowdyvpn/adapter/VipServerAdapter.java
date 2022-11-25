package com.rowdystudio.rowdyvpn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.model.Server;

import java.util.ArrayList;
import java.util.List;

public class VipServerAdapter extends RecyclerView.Adapter<VipServerAdapter.MyViewHolder> {

    private final ArrayList<Server> serverLists;
    private final Context mContext;
    private OnSelectListener selectListener;
    public VipServerAdapter(Context context) {
        this.mContext = context;
        serverLists= new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_servers_premium, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.serverCountry.setText(serverLists.get(position).getCountry());
        Glide.with(mContext)
                .load(serverLists.get(position).getFlagUrl())
                .into(holder.serverIcon);

        holder.itemView.setOnClickListener(v -> selectListener.onSelected(serverLists.get(position)));
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
    public void setData(List<Server> servers){
        serverLists.clear();
        serverLists.addAll(servers);
        notifyDataSetChanged();
    }
    public interface OnSelectListener{
        void onSelected(Server server);
    }
    public void setOnSelectListener(OnSelectListener selectListener){
        this.selectListener = selectListener;
    }

}
