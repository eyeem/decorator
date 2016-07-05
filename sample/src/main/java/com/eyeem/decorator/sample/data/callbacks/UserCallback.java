package com.eyeem.decorator.sample.data.callbacks;

import android.util.Log;

import com.eyeem.decorator.sample.data.model.User;
import com.eyeem.decorator.sample.data.model.list.ListUser;
import com.eyeem.decorator.sample.util.PerformanceLog;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by budius on 16.06.16.
 */
public class UserCallback implements Callback<UserCallback.UserResponse> {

   @Override
   public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
      String id = call.request().url().encodedPath();
      PerformanceLog.stop(id);
      Log.d("Budius", response.message() + " for path " + id + ". Received " + response.body().users.items.size() + " items");

      PerformanceLog.start("response");
      Realm realm = Realm.getDefaultInstance();

      ListUser list = realm.where(ListUser.class).equalTo("id", id).findFirst();
      realm.beginTransaction();

      if (list == null) {
         list = realm.createObject(ListUser.class);
         list.id = id;
      } else if (response.body().users.offset == 0) {
         list.list.clear();
      }

      list.list.addAll(response.body().users.items);
      realm.commitTransaction();
      PerformanceLog.stop("response");
   }

   @Override public void onFailure(Call<UserResponse> call, Throwable t) {
      PerformanceLog.stop(call.request().url().encodedPath());
      Log.e("Budius", "Failed: " + call.request().url().toString(), t);
   }

   public static class UserResponse {

      public Users users;

      public static class Users {
         public int offset;
         public int limit;
         public int total;
         public String sort = "chronological";
         public List<User> items;
      }
   }
}
