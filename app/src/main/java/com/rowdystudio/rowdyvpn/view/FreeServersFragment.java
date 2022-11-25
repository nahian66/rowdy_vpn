package com.rowdystudio.rowdyvpn.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.adapter.FreeServerAdapter;
import com.rowdystudio.rowdyvpn.api.WebAPI;
import com.rowdystudio.rowdyvpn.model.Server;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class FreeServersFragment extends Fragment implements
        FreeServerAdapter.OnSelectListener {

    RecyclerView rcvServers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        loadServers();


        View view = inflater.inflate(R.layout.fragment_native_ad_recycler, container, false);
        rcvServers = view.findViewById(R.id.recyclerView);

        ProgressBar progressBar = view.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void loadServers()
    {
        ArrayList<Server> mPostItemList = new ArrayList<>();
        try
        {
            if (!TextUtils.isEmpty(WebAPI.FREE_SERVERS))
            {
                JSONArray jsonArray = new JSONArray(WebAPI.FREE_SERVERS);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    mPostItemList.add(new Server(object.getString("serverName"),
                            object.getString("flagURL"),
                            object.getString("ovpnConfiguration"),
                            object.getString("vpnUserName"),
                            object.getString("vpnPassword")
                    ));
                    Log.v("Servers", object.getString("ovpnConfiguration"));

                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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
    public void onSelected(Server server)
    {
        if (getActivity() != null)
        {
            Intent mIntent = new Intent();
            mIntent.putExtra("server", server);
            getActivity().setResult(getActivity().RESULT_OK, mIntent);
            getActivity().finish();
        }
    }
}