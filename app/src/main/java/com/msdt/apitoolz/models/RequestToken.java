package com.msdt.apitoolz.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SMK on 3/29/2017.
 */

public class RequestToken implements Serializable {
    @SerializedName("grant_type")
    @Expose
    private String grantType;
    @SerializedName("client_id")
    @Expose
    private Integer clientId;
    @SerializedName("client_secret")
    @Expose
    private String clientSecret;

    public RequestToken(String grantType, Integer clientId, String clientSecret) {
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getGrandType() {
        return grantType;
    }

    public void setGrandType(String grandType) {
        this.grantType = grandType;
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
}
