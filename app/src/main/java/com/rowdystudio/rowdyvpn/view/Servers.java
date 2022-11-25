package com.rowdystudio.rowdyvpn.view;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.rowdystudio.rowdyvpn.R;
import com.rowdystudio.rowdyvpn.adapter.TabAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import java.util.Objects;

public class Servers extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);

        Toolbar toolbar = findViewById(R.id.toolbarold);
        toolbar.setTitle("Free Servers");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        /*
         *         Vip Server will be shown 27640849 in the "Vip Server" fragment...
         *         Free Server will be shown in the "Free Server" fragment...
         * */
        adapter.addFragment(new FreeServersFragmentAdMob(), "Free Servers");

        adapter.addFragment(new VipServersFragment(), "Premium Servers");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
                toolbar.setTitle(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
        toolbar.setNavigationOnClickListener(view -> Servers.super.onBackPressed());
    }
}
