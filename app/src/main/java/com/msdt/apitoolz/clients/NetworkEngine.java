package com.msdt.apitoolz.clients;


import android.util.Log;

import com.msdt.apitoolz.APIToolz;
import com.msdt.apitoolz.APIToolzTokenManager;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkEngine {

	static INetworkEngine instance;

	public static INetworkEngine getInstance() {
		if (instance==null) {

			OkHttpClient client = new OkHttpClient.Builder()
					.addNetworkInterceptor(new LoggingInterceptor())
					.build();

			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl(APIToolz.getInstance().getHostAddress())
					.client(client)
					.addConverterFactory(GsonConverterFactory.create())
					.build();

			instance = retrofit.create(INetworkEngine.class);
		}
		return instance;
	}

	static class LoggingInterceptor implements Interceptor {
		@Override
		public Response intercept(final Chain chain) throws IOException {
			Request request = chain.request();
			Request[] mRequest = {null};
			String accessToken = APIToolzTokenManager.getInstance().getAccessToken();
			if(accessToken != null){
				mRequest[0] = request.newBuilder()
						.header("Authorization", accessToken)
						.header("Content-Encoding", "gzip, deflate, sdch")
						.header("Accept", "application/json")
						.method(request.method(), request.body())
						.build();
			}else{
                mRequest[0] = request.newBuilder()
                        .header("Content-Encoding", "gzip, deflate, sdch")
                        .header("Accept", "application/json")
                        .method(request.method(), request.body())
                        .build();
            }
            long t1 = System.nanoTime();
            Log.i("Retrofit", String.format("Sending request %s on %s%n%s",
                    mRequest[0].url(), chain.connection(), mRequest[0].headers()));

            Response response = chain.proceed(mRequest[0]);

            long t2 = System.nanoTime();
            Log.i("Retrofit", String.format("Received response for %s => %s in %.1fms %n%s",
                    response.code(), response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
		}

		private RequestBody gzip(final RequestBody body) {
			return new RequestBody() {
				@Override
				public MediaType contentType() {
					return body.contentType();
				}

				@Override
				public long contentLength() {
					return -1; // We don't know the compressed length in advance!
				}

				@Override
				public void writeTo(BufferedSink sink) throws IOException {
					BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
					body.writeTo(gzipSink);
					gzipSink.close();
				}
			};
		}
	}

}