package com.example.marcin.smarthomeandroid.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.marcin.smarthomeandroid.data.MySharedPreferences;
import com.example.marcin.smarthomeandroid.data.Names;
import com.example.marcin.smarthomeandroid.R;
import com.example.marcin.smarthomeandroid.background.MyService;
import com.example.marcin.smarthomeandroid.ui.fragment.ControlFragment;
import com.example.marcin.smarthomeandroid.ui.fragment.MapsFragment;
import com.example.marcin.smarthomeandroid.ui.fragment.StateHistoryFragment;


public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ControlFragment.OnControlListner {
    ControlFragment controlFragment;
    MapsFragment mapsFragment;
    StateHistoryFragment stateHistoryFragment;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        controlFragment = new ControlFragment();
        mapsFragment = new MapsFragment();
        stateHistoryFragment = new StateHistoryFragment();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_content, stateHistoryFragment);
        fragmentTransaction.detach(stateHistoryFragment);
        fragmentTransaction.add(R.id.fragment_content, mapsFragment);
        fragmentTransaction.detach(mapsFragment);
        fragmentTransaction.add(R.id.fragment_content, controlFragment);
        fragmentTransaction.commit();
    }

    void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(stateHistoryFragment);
        fragmentTransaction.detach(controlFragment);
        fragmentTransaction.detach(mapsFragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.drawer_control:
                setFragment(controlFragment);
                break;
            case R.id.drawer_maps:
                setFragment(mapsFragment);
                break;
            case R.id.drawer_state_history:
                setFragment(stateHistoryFragment);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openGate(View view) {
        controlFragment.sendCommandViaSocket(Names.GATE_OPEN_COMMAND);
    }

    public void openWicket(View view) {
        controlFragment.sendCommandViaSocket(Names.WICKET_OPEN_COMMAND);
    }

    public void closeGate(View view) {
        controlFragment.sendCommandViaSocket(Names.GATE_CLOSE_COMMAND);
    }

    public void analyserClick(View view) {
        if (!isServiceRunning(MyService.class)) {
            startService(new Intent(getApplicationContext(), MyService.class));
            Snackbar.make(view, "Start analizy", Snackbar.LENGTH_SHORT).show();
            controlFragment.setButtonAnalyse(true);
            MySharedPreferences.setLocationAnalise(true, getApplicationContext());
        } else {
            stopService(new Intent(getApplicationContext(), MyService.class));
            Log.e("main", "koniec");
            Snackbar.make(view, "Koniec analizy", Snackbar.LENGTH_SHORT).show();
            controlFragment.setButtonAnalyse(true);
            MySharedPreferences.setLocationAnalise(false, getApplicationContext());
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;

        return false;
    }

    @Override
    public boolean isServiceRunning() {
        return isServiceRunning(MyService.class);
    }
}
