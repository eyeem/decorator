package com.eyeem.decorator.sample.data.callbacks;

import android.util.Log;

import com.eyeem.decorator.sample.data.Creator;
import com.eyeem.decorator.sample.data.model.Photo;
import com.eyeem.decorator.sample.data.model.list.ListPhoto;
import com.eyeem.decorator.sample.util.PerformanceLog;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by budius on 15.06.16.
 */
public class PhotoCallback implements Callback<PhotoCallback.PhotoResponse> {

   @Override
   public void onResponse(Call<PhotoCallback.PhotoResponse> call, Response<PhotoCallback.PhotoResponse> response) {
      String id = call.request().url().encodedPath();
      PerformanceLog.stop(id);
      Log.d("Budius", String.format("%s for path %s. Received %s items",
            response.message(), id, response.body().photos.items.size()));

      PerformanceLog.start("Realm processing");

      ListPhoto list = Creator.getListPhoto(id);

      Realm realm = Realm.getDefaultInstance();
      realm.beginTransaction();

      if (response.body().photos.offset == 0) {
         list.list.clear();
         list.lastRefresh = System.currentTimeMillis();
      }

      list.list.addAll(response.body().photos.items);
      realm.commitTransaction();
      PerformanceLog.stop("Realm processing");
   }

   @Override public void onFailure(Call<PhotoCallback.PhotoResponse> call, Throwable t) {
      PerformanceLog.stop(call.request().url().encodedPath());
      Log.e("Budius", "Failed: " + call.request().url().toString(), t);
   }

   public static class PhotoResponse {

      public Photos photos;

      public static class Photos {
         public int offset;
         public int limit;
         public int total;
         public String sort = "chronological";
         public List<Photo> items;
      }
   }
}
