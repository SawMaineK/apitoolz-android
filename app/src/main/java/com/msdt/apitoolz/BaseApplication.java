package com.msdt.apitoolz;

import android.app.Application;

import com.msdt.apitoolz.utils.StorageDriver;

/**
 * Created by SMK on 9/24/2016.
 */
public class BaseApplication extends Application {

    public static String PACKAGE_NAME;

    @Override
    public void onCreate() {
        super.onCreate();

        PACKAGE_NAME = getApplicationContext().getPackageName();

        APIToolz apiToolz = APIToolz.getInstance();
        apiToolz.setClientId(3);
        apiToolz.setClientSecret("wKKJzMZ6rRCJjJREWeqpB4yeQn1bKr5meD4PlhKq");
        apiToolz.setHostAddress("https://demo.apitoolz.com");
        apiToolz.setStoragePath(getFilesDir().getAbsolutePath());
        APIToolzTokenManager.getInstance().getAccessToken();
    }

    public static boolean isLogin(){
        Object user = StorageDriver.getInstance().selectFrom("loginUser");
        if(user != null){
            return true;
        }
        return false;
    }

    public static void login(Object user){
        StorageDriver.getInstance().saveTo("loginUser", user);
    }

    public static void updateUser(Object user){
        StorageDriver.getInstance().saveTo("loginUser", user);
    }

    public static Object getUser(){
        if(isLogin()){
            return StorageDriver.getInstance().selectFrom("loginUser");
        }
        return null;
    }

    public static void logout(){
        if(isLogin()){
            StorageDriver.getInstance().destroy("loginUser");
        }
    }
}
