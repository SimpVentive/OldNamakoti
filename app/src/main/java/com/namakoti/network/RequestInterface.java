package com.namakoti.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface RequestInterface {
//    @GET("files/Node-Android-Chat.zip")
//    @Streaming
//    Call<ResponseBody> downloadFile();
    @GET("{endpoint}")
    Call<ResponseBody> downloadFile(@Path("endpoint") String endpoint);
}
