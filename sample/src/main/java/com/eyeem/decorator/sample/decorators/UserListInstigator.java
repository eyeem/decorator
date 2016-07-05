package com.eyeem.decorator.sample.decorators;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.KEY;
import com.eyeem.decorator.sample.data.Creator;
import com.eyeem.decorator.sample.data.model.list.ListUser;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by budius on 16.06.16.
 */
public class UserListInstigator extends Deco implements Deco.DataInstigator {

   private ListUser listUser;

   @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
      String id = getDecorated().getIntent().getStringExtra(KEY.URL);
      listUser = Creator.getListUser(id);
   }

   @Override protected void onDestroy() {
      listUser = null;
   }

   @Override public RealmList getList() {
      return listUser.list;
   }

   @Override public RealmObject getModel() {
      return listUser;
   }
}
