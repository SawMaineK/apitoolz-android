package com.msdt.apitoolz;

import android.util.Log;

import com.msdt.apitoolz.clients.NetworkEngine;
import com.msdt.apitoolz.models.RequestToken;
import com.msdt.apitoolz.models.ResponseToken;
import com.msdt.apitoolz.utils.StorageDriver;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SMK on 3/29/2017.
 */

public class APIToolzTokenManager {

    private static APIToolzTokenManager ourInstance;
    private String accessToken;
    private TakenCallback tokenCallback;

    public static APIToolzTokenManager getInstance() {
        if(ourInstance != null) {
            return ourInstance;
        } else {
            ourInstance = new APIToolzTokenManager();
            return ourInstance;
        }
    }

    public String getAccessToken() {
        ResponseToken token = StorageDriver.getInstance().selectFrom(APIToolz.APIToolzToken);
        if(token != null) {
            accessToken = token.getAccessToken();
        } else {
            APIToolz toolz = APIToolz.getInstance();
            RequestToken requestToken = new RequestToken("client_credentials",toolz.getClientId(), toolz.getClientSecret());
            NetworkEngine.getInstance().getAccessToken(requestToken).enqueue(new Callback<ResponseToken>() {
                @Override
                public void onResponse(Call<ResponseToken> call, Response<ResponseToken> response) {

                    if(response.isSuccessful())
                    {
                        ResponseToken responseToken = response.body();
                        if(tokenCallback != null) {
                            tokenCallback.onSuccess(responseToken.getAccessToken());
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH-mm-ss");
                        String datetime = sdf.format(new Date());
                        responseToken.setCreatedAt(datetime);
                        StorageDriver.getInstance().saveTo(APIToolz.APIToolzToken, responseToken);
                    }

                }

                @Override
                public void onFailure(Call<ResponseToken> call, Throwable t) {
                    Log.e("Error","Retrofit Error "+t.getMessage());
                }
            });
        }
        return accessToken;
    }

    public void setCallbackListener(TakenCallback callback) {
        tokenCallback = callback;
        if(tokenCallback != null) {
            String accessToken = getAccessToken();
            if(accessToken != null)
                tokenCallback.onSuccess(accessToken);
            else
                getAccessToken();
        }
    }

    public interface TakenCallback {
        void onSuccess(String accessToken);
    }
}