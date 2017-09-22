package com.msdt.apitoolz.clients;

import com.msdt.apitoolz.models.ErrorTrace;
import com.msdt.apitoolz.models.RequestToken;
import com.msdt.apitoolz.models.ResponseToken;
import com.msdt.apitoolz.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface INetworkEngine {
    @POST("/oauth/token")
    Call<ResponseToken> getAccessToken(@Body RequestToken token);

    @POST("/api/user")
    Call<String> register(@Body User user);

    @FormUrlEncoded
    @POST("/api/login")
    Call<User> authenticate(@Field("email") String email, @Field("password") String password);

    @GET("/api/user/{id}")
    Call<User> profile(@Path("id") Integer id);

    @GET("/api/user")
    Call<List<User>> checkUser(@Query("search") String search);

    @POST("/api/user/{id}")
    Call<User> editProfile(@Path("id") Integer id, @Body User user);

    @Multipart
    @POST("/api/avatar/{id}")
    Call<User> uploadAvatar(@Path("id") Integer id, @Part MultipartBody.Part image);

    @GET("/api/{model}/total")
    Call<Integer> getTotal(@Path("model") String model, @Query("search") String search);

    @POST("/api/androiderror")
    Call<ErrorTrace> postError(ErrorTrace trace);
}
