package com.rowdystudio.rowdyvpn.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.adapter.FreeServerAdapter;
import com.rowdystudio.rowdyvpn.api.WebAPI;
import com.rowdystudio.rowdyvpn.model.Server;
import com.rowdystudio.rowdyvpn.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FreeServersFragmentAdMob extends Fragment
        implements FreeServerAdapter.OnSelectListener {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rcv_servers)
    RecyclerView rcvServers;

    private FreeServerAdapter serverAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_free_server, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        serverAdapter = new FreeServerAdapter(getActivity());
        serverAdapter.setOnSelectListener(this);
        rcvServers.setLayoutManager(layoutManager);
        rcvServers.setAdapter(serverAdapter);
        loadServers();
    }

    private void loadServers() {
        ArrayList<Server> servers = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(WebAPI.FREE_SERVERS);
            for (int i=0; i < jsonArray.length();i++){
                JSONObject object = (JSONObject) jsonArray.get(i);
                servers.add(new Server(object.getString("serverName"),
                        object.getString("flagURL"),
                        object.getString("ovpnConfiguration"),
                        object.getString("vpnUserName"),
                        object.getString("vpnPassword")
                ));
                Log.v("Servers",object.getString("ovpnConfiguration"));
                if((i % 2 == 0)&&(i > 0)){
                    if (!Config.vip_subscription && !Config.all_subscription) {
                        servers.add(null);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        serverAdapter.setData(servers);
    }

    @Override
    public void onAttach(@NonNull Context ctx) {
        super.onAttach(ctx);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSelected(Server server) {
        if (getActivity() != null)
        {
            Intent mIntent = new Intent();
            mIntent.putExtra("server", server);
            getActivity().setResult(getActivity().RESULT_OK, mIntent);
            getActivity().finish();
        }
    }
}