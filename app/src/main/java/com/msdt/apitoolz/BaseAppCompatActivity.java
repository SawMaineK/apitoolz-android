package com.msdt.apitoolz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikepenz.iconics.IconicsDrawable;
import com.msdt.apitoolz.models.Lang;
import com.msdt.apitoolz.models.Settings;
import com.msdt.apitoolz.models.Version;
import com.msdt.apitoolz.trace.ExceptionHandler;
import com.msdt.apitoolz.utils.StorageDriver;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by SMK on 9/21/2016.
 */
public class BaseAppCompatActivity extends AppCompatActivity {

    protected static final int NETWORK_SETTING = 1500;
    public static final String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION =  BaseApplication.PACKAGE_NAME+".FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";
    private BaseActivityReceiver baseActivityReceiver = new BaseActivityReceiver();
    public static final IntentFilter INTENT_FILTER = createIntentFilter();
    protected Lang language;

    private static IntentFilter createIntentFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);
        return filter;
    }

    protected void registerBaseActivityReceiver() {
        registerReceiver(baseActivityReceiver, INTENT_FILTER);
    }

    protected void unRegisterBaseActivityReceiver() {
        unregisterReceiver(baseActivityReceiver);
    }

    public class BaseActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION)){
                finish();
            }
        }
    }

    protected void closeAllActivities(){
        sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExceptionHandler.register(this);
        // Obtain the shared Tracker instance.
        //BaseApplication application = (BaseApplication) getApplication();
        //mTracker = application.getDefaultTracker();
        //mTracker.setScreenName("Screen~" + this.getClass().getSimpleName());
        //mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        // To close all activities
        registerBaseActivityReceiver();

        //To change language settings
        Locale locale = new Locale(getSettings().getLocale());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, null);
    }

    public Settings getSettings(){
        Settings settings = StorageDriver.getInstance().selectFrom(Constant.settings);
        if(settings != null){
            return settings;
        }
        Settings setting = new Settings();
        setting.save();
        return setting;
    }

    public boolean isNetworkAvailable(){
        boolean status = false;
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        status = true;
                    }
        }
        return status;
    }

    public boolean isUpdateAvailable(){
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            int currentVersion = pi.versionCode;
            Version toVersion = StorageDriver.getInstance().selectFrom(Constant.version);
            if(toVersion != null && currentVersion < toVersion.getId()){
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public IconicsDrawable getIconicDrawable(String icon, int color, int size){
        return new IconicsDrawable(this)
                .icon(icon)
                .color(getResources().getColor(color))
                .sizeDp(size);
    }

    public boolean isGooglePlayServicesAvailable() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(this);
        return googlePlayServicesAvailable == ConnectionResult.SUCCESS;
    }

    public void performLoading(boolean isShow){
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_loading);
        if(layout != null && isShow)
            layout.setVisibility(View.VISIBLE);
        else if(layout != null && !isShow)
            layout.setVisibility(View.GONE);
    }

    public String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public String getTodayDate(String format){
        DateFormat dateFormat = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public String getTomorrowDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        return dateFormat.format(cal.getTime());
    }

    @Override
    protected void onDestroy() {
        unRegisterBaseActivityReceiver();
        super.onDestroy();
    }
}
