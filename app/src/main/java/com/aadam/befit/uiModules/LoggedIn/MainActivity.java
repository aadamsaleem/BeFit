package com.aadam.befit.uiModules.LoggedIn;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aadam.befit.Constants;
import com.aadam.befit.R;
import com.aadam.befit.adapter.CustomRecordListAdapter;
import com.aadam.befit.database.RecordsDatabaseAdapter;
import com.aadam.befit.models.Record;
import com.aadam.befit.models.User;
import com.aadam.befit.uiModules.LoggedOut.DetailActivity;
import com.aadam.befit.uiModules.LoggedOut.LoginActivity;
import com.aadam.befit.services.ReminderService;
import com.aadam.befit.util.PrefUtils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Properties
    private CustomRecordListAdapter adapter;
    private User user;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FrameLayout fragmentsFrame;
    private ListView listView;
    private TextView stepsTextView, distanceTextView;
    private RecordsDatabaseAdapter recordsDatabaseAdapter;

    //region Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = PrefUtils.getCurrentUser(MainActivity.this);

        recordsDatabaseAdapter = new RecordsDatabaseAdapter(this);
        recordsDatabaseAdapter = recordsDatabaseAdapter.open();

        setupViews();

        startAlarm();
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
    public void onDestroy() {
        super.onDestroy();

        recordsDatabaseAdapter.close();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            closeFragments();

        } else if (id == R.id.nav_session) {
            addSessionFragment();

        } else if (id == R.id.nav_leaderboard) {
            addLeaderboardFragment();

        } else if (id == R.id.nav_preferences) {
            openPreferences();

        } else if (id == R.id.nav_logout) {
            logout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion

    //region Private Methods
    private void setupViews() {

        getIDs();

        setSupportActionBar(toolbar);

        //setup action bar and navigation drawer
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUser = (TextView) headerView.findViewById(R.id.textVew_userName);
        TextView navEmail = (TextView) headerView.findViewById(R.id.textView_email);
        navUser.setText(user.getName());
        navEmail.setText(user.getEmail());

        prepareListView();

    }

    private void getIDs() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fragmentsFrame = (FrameLayout) findViewById(R.id.fragmentsFrame);
        listView = (ListView) findViewById(R.id.list);
        stepsTextView = (TextView) findViewById(R.id.textView_totalSteps);
        distanceTextView = (TextView) findViewById(R.id.textView_totalDistance);
    }

    //populate the list view with user sessions
    private void prepareListView() {
        int totalSteps = 0;
        float totalDistance = 0.0f;
        ArrayList<Record> dataModels = new ArrayList<>();

        dataModels.addAll(recordsDatabaseAdapter.getUserRecords(user.getId()));

        for (Record rec : dataModels) {
            Date date = rec.getSessionDate();
            Date today = new Date();
            if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate()) {
                totalSteps += Integer.parseInt(rec.getSessionSteps());
                totalDistance += Float.parseFloat(rec.getSessionDistance());
            }
        }

        stepsTextView.setText("Steps: " + totalSteps);
        distanceTextView.setText("Distance: " + totalDistance + " miles");

        adapter = new CustomRecordListAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);
    }

    private void logout() {
        PrefUtils.clearCurrentUser(MainActivity.this);
        logoutTwitter();
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void logoutTwitter() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            clearCookies(getApplicationContext());
            Twitter.getSessionManager().clearActiveSession();
            Twitter.logOut();
        }
    }

    private void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    private void openPreferences() {
        Intent detailInt = new Intent(this, DetailActivity.class);
        detailInt.putExtra(Constants.KEY_EMAIL, user.getEmail());
        detailInt.putExtra(Constants.KEY_USERID, user.getId());
        startActivity(detailInt);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    private void addSessionFragment() {
        fragmentsFrame.setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SessionFragment sessionFragment = SessionFragment.newInstance();
        fragmentTransaction.add(R.id.fragmentsFrame, sessionFragment);
        fragmentTransaction.commit();
    }

    private void addLeaderboardFragment() {
        fragmentsFrame.setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LeaderboardFragment leaderboardFragment = LeaderboardFragment.newInstance();
        fragmentTransaction.add(R.id.fragmentsFrame, leaderboardFragment);
        fragmentTransaction.commit();
    }

    private void closeFragments() {
        fragmentsFrame.setVisibility(View.GONE);
        prepareListView();
    }

    //start alarm service for hourly reminders
    private void startAlarm() {
        Calendar calendar = Calendar.getInstance();
        Intent intent1 = new Intent(this, ReminderService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 60 * 60 * 1000, AlarmManager.INTERVAL_HOUR, pendingIntent);
    }
    //endregion
}
