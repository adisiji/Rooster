package com.blikoon.rooster;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blikoon.rooster.fragments.ContactListFragment;
import com.blikoon.rooster.fragments.EditServerFragment;
import com.blikoon.rooster.fragments.TextFilterFragment;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import static com.blikoon.rooster.utils.AllUtil.browseUrl;
import static com.blikoon.rooster.utils.AllUtil.mailTo;
import static com.blikoon.rooster.utils.AllUtil.phoneCall;
import static com.blikoon.rooster.utils.ColorUtil.getNavIconColorState;
import static com.blikoon.rooster.utils.ColorUtil.getNavTextColorState;

/**
 * Created by neobyte on 10/24/2016.
 */

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView = null;
    private NavigationMenuView navigationMenuView = null;
    private DrawerLayout drawer = null;
    private View headerView;
    private boolean isDoubleBackToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
//        navigationView.getHeaderView(0).getLayoutParams().height = (int) (getWindowSize(this).y * 0.25);
        navigationView.setNavigationItemSelectedListener(this);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }
        navigationView.setItemTextColor(getNavTextColorState());
        navigationView.setItemIconTintList(getNavIconColorState());
        headerView = navigationView.getHeaderView(0);
        RelativeLayout navHeaderImgContainer = (RelativeLayout) headerView.findViewById(R.id.navHeaderImgContainer);
        RelativeLayout navActionPhone = (RelativeLayout) headerView.findViewById(R.id.navActionPhone);
        RelativeLayout navActionMail = (RelativeLayout) headerView.findViewById(R.id.navActionMail);
        RelativeLayout navActionWeb = (RelativeLayout) headerView.findViewById(R.id.navActionWeb);
        navHeaderImgContainer.setOnClickListener(actionListener);
        navActionPhone.setOnClickListener(actionListener);
        navActionMail.setOnClickListener(actionListener);
        navActionWeb.setOnClickListener(actionListener);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (navigationView.getMenu().findItem(itemId).isChecked()) {
            return true;
        }

        if (itemId == R.id.nav_home) {
            showHomeScreen();
        } else if (itemId == R.id.nav_server) {
            showServerScreen();
        } else if (itemId == R.id.nav_text) {
            showTextScreen();
        } else if (itemId == R.id.nav_logout) {
            LogOutScreen();
        }

        drawer.closeDrawer(GravityCompat.START);
        setTitle(navigationView.getMenu().findItem(itemId).getTitle());
        return true;
    }

    private void showHomeScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ContactListFragment.newInstance(" "))
                .commit();
    }

    private void showServerScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, EditServerFragment.newInstance(" "))
                .commit();
    }

    private void showTextScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, TextFilterFragment.newInstance(" "))
                .commit();
    }

    private void LogOutScreen() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RoosterConnectionService.class.getName().equals(service.service.getClassName())) {
                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                nMgr.cancel(0);
                stopService(new Intent(this,RoosterConnectionService.class));
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isDoubleBackToExit) {
                super.onBackPressed();
                finish();
            }
            if (!isDoubleBackToExit) {
                Toast.makeText(this, getString(R.string.re_tap_text), Toast.LENGTH_SHORT).show();
            }
            this.isDoubleBackToExit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isDoubleBackToExit = false;
                }
            }, 2000); //delay 2 detik
        }
    }

    View.OnClickListener actionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navHeaderImgContainer:
                    if (!navigationView.getMenu().findItem(R.id.nav_home).isChecked()) {
                        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
                        navigationView.setCheckedItem(R.id.nav_home);
                    }
                    break;
                case R.id.navActionPhone:
                    phoneCall(HomeActivity.this, getString(R.string.dev_phone));
                    break;
                case R.id.navActionMail:
                    mailTo(HomeActivity.this, getString(R.string.dev_mail));
                    break;
                case R.id.navActionWeb:
                    browseUrl(HomeActivity.this, getString(R.string.dev_web_page));
                    break;
            }

            drawer.closeDrawer(GravityCompat.START);
        }
    };

}
