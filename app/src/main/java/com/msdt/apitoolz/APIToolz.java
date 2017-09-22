package com.msdt.apitoolz;

/**
 * Created by SMK on 3/29/2017.
 */

public class APIToolz {
    public static final String APIToolzToken = "APIToolzToken";
    public static final String TAG = "APIToolz";
    public static String URI = "";
    private static APIToolz ourInstance;
    private Integer clientId;
    private String clientSecret;
    private String hostAddress;
    private String storagePath;

    public static APIToolz getInstance() {
        if(ourInstance != null) {
            return ourInstance;
        } else {
            ourInstance = new APIToolz();
            return ourInstance;
        }
    }

    private APIToolz() {
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

}


