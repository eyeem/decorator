package com.eyeem.decorator.sample.decorators;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.KEY;
import com.eyeem.decorator.sample.data.model.User;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by budius on 12.07.16.
 */
public class UserTitleInstigator extends Deco implements RealmChangeListener<RealmResults<User>> {

   private String id;
   private Realm realm;
   private RealmResults<User> userResults;

   @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
      id = getDecorated().getIntent().getStringExtra(KEY.ID);
      if (id == null) return;

      realm = Realm.getDefaultInstance();
   }

   @Override protected void onDestroy() {
      realm = null;
   }

   @Override protected void onStart() {
      if (id == null) return;
      userResults = realm.where(User.class).equalTo("id", id).findAllAsync();
      onChange(userResults);
      userResults.addChangeListener(this);
   }

   @Override protected void onStop() {
      if (id == null) return;
      userResults.removeChangeListener(this);
      userResults = null;
   }

   @Override public void onChange(RealmResults<User> element) {
      if (element.isValid() && element.isLoaded() && element.size() > 0) {
         getDecorators().setTitle(element.get(0).fullname);
      }
   }
}
