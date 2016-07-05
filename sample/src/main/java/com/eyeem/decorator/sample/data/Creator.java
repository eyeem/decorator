package com.eyeem.decorator.sample.data;

import com.eyeem.decorator.sample.data.model.list.ListPhoto;
import com.eyeem.decorator.sample.data.model.list.ListUser;

import io.realm.Realm;

/**
 * Created by budius on 13.07.16.
 * Make object creation synchronized to avoid multi-thread trying to create objects
 */
public class Creator {

   public static synchronized ListPhoto getListPhoto(String id) {
      Realm realm = Realm.getDefaultInstance();
      ListPhoto list = realm.where(ListPhoto.class).equalTo("id", id).findFirst();
      if (list == null) {
         list = new ListPhoto();
         list.id = id;
         realm.beginTransaction();
         list = realm.copyToRealmOrUpdate(list);
         realm.commitTransaction();
      }
      return list;
   }

   public static synchronized ListUser getListUser(String id) {
      Realm realm = Realm.getDefaultInstance();
      ListUser list = realm.where(ListUser.class).equalTo("id", id).findFirst();
      if (list == null) {
         list = new ListUser();
         list.id = id;
         realm.beginTransaction();
         list = realm.copyToRealmOrUpdate(list);
         realm.commitTransaction();
      }
      return list;
   }
}
