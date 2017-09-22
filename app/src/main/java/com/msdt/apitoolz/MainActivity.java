package com.msdt.apitoolz;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.msdt.apitoolz.models.User;

public class MainActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, fbCallback);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_profile).setIcon(getIconicDrawable(CommunityMaterial.Icon.cmd_account_box_outline.toString(), R.color.colorPrimary, 18));
        navigationView.getMenu().findItem(R.id.nav_settings).setIcon(getIconicDrawable(CommunityMaterial.Icon.cmd_settings.toString(), R.color.colorPrimary, 18));
        navigationView.getMenu().findItem(R.id.nav_call_us).setIcon(getIconicDrawable(CommunityMaterial.Icon.cmd_phone.toString(), R.color.colorPrimary, 18));
        navigationView.getMenu().findItem(R.id.nav_help_us).setIcon(getIconicDrawable(CommunityMaterial.Icon.cmd_thumb_up_outline.toString(), R.color.colorPrimary, 18));
        navigationView.getMenu().findItem(R.id.nav_about_us).setIcon(getIconicDrawable(CommunityMaterial.Icon.cmd_information_outline.toString(), R.color.colorPrimary, 18));
        navigationView.getMenu().findItem(R.id.nav_share).setIcon(getIconicDrawable(CommunityMaterial.Icon.cmd_share_variant.toString(), R.color.colorPrimary, 18));
        navigationView.getMenu().findItem(R.id.nav_sign_in_out).setIcon(getIconicDrawable(CommunityMaterial.Icon.cmd_login.toString(), R.color.colorPrimary, 18));
        navigationView.setNavigationItemSelectedListener(this);

        TextView accountName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_name);
        TextView accountEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_email);

        // Check already login
        if (BaseApplication.isLogin()) {
            User user = (User) BaseApplication.getUser();
            accountName.setText(user.getFirstName() +" "+user.getLastName());
            accountEmail.setText(user.getEmail());
            navigationView.getMenu().findItem(R.id.nav_sign_in_out).setIcon(getIconicDrawable(CommunityMaterial.Icon.cmd_logout.toString(), R.color.colorPrimary, 18));
            navigationView.getMenu().findItem(R.id.nav_sign_in_out).setTitle(getResources().getString(R.string.logout));
        }
    }

    private FacebookCallback<Sharer.Result> fbCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

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
        getMenuInflater().inflate(R.menu.main, menu);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
        if (item.getItemId() == R.id.nav_settings) {
            //startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }
        if (item.getItemId() == R.id.nav_call_us) {
            String number = "tel: 09777335336";
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            startActivity(callIntent);
        }
        if (item.getItemId() == R.id.nav_help_us){
            //startActivity(new Intent(getApplicationContext(), HelpUsActivity.class));
        }
        if (item.getItemId() == R.id.nav_about_us){
            //startActivity(new Intent(getApplicationContext(), AboutusActivity.class));
        }
        if (item.getItemId() == R.id.nav_share){
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.msdt.newsbook"))
                        .build();
                shareDialog.show(linkContent);
            }

        }
        if(item.getItemId() == R.id.nav_sign_in_out){
            LoginManager.getInstance().logOut();
            AccountKit.logOut();
            BaseApplication.logout();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            closeAllActivities();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
