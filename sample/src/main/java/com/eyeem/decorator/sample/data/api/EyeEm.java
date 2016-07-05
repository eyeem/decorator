package com.eyeem.decorator.sample.data.api;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by budius on 15.06.16.
 */
public class EyeEm {

   private static final String VERSION = "/v2/";

   // photos
   public static final String PHOTOS_POPULAR = VERSION + "photos/popular";

   public static String PHOTOS_FROM_ALBUM(String id) {
      return String.format(VERSION + "albums/%s/photos", id);
   }

   public static String PHOTOS_FROM_USER(String id) {
      return String.format(VERSION + "users/%s/photos", id);
   }

   // users


   private static final EyeEmService service;
   private static final Executor callbackExecutor;
   private static final RequestObservableList requestsObserver;

   public static EyeEmService get() {
      return service;
   }

   public static RequestObservableList requests() {
      return requestsObserver;
   }

   static {

      requestsObserver = new RequestObservableList();

      OkHttpClient.Builder clientBuilder =
            new OkHttpClient
                  .Builder()
                  .addNetworkInterceptor(new EyeemHeaders())
                  .addInterceptor(requestsObserver);

      callbackExecutor = Executors.newSingleThreadExecutor();
      GsonBuilder gsonBuilder = new GsonBuilder();

      Retrofit retrofit = new Retrofit.Builder()
            .client(clientBuilder.build())
            .callbackExecutor(callbackExecutor)
            .baseUrl("https://api.eyeem.com/")
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .build();

      service = retrofit.create(EyeEmService.class);
   }


   private static class EyeemHeaders implements Interceptor {

      @Override public Response intercept(Chain chain) throws IOException {
         Request request = chain.request().newBuilder()
               .addHeader("X-Client-Id", "3DXa7ZXvMQDKd6iLZjfedN2cEMmnb0ev")
               .addHeader("X-Api-Version", "2.3.9")
               .addHeader("Accept-Language", "en")
               .build();
         return chain.proceed(request);
      }
   }

   /* Possible params
         ("detailed", "1")
         ("includeAlbums", "1")
         ("includeComments", "1")
         ("includeLikers", "1")
         ("includePeople", "1")
         ("includePhotos", "1")
         ("limit", "30")
         ("numComments", "3")
         ("numLikers", "2")
         ("numPeople", "10")
    */
}
