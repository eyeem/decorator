package com.eyeem.decorator.sample.data.api;

import com.eyeem.decorator.sample.data.callbacks.PhotoCallback;
import com.eyeem.decorator.sample.data.callbacks.UserCallback;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by budius on 15.06.16.
 */
public interface EyeEmService {

   @GET Call<PhotoCallback.PhotoResponse> getPhotos(@Url String url, @Query("offset") int offset);

   @GET
   Call<PhotoCallback.PhotoResponse> getPhotos(@Url String url, @Query("offset") int offset, @QueryMap Map<String, String> options);

   @GET
   Call<UserCallback.UserResponse> getUsers(@Url String url, @Query("offset") int offset, @QueryMap Map<String, String> options);

}
