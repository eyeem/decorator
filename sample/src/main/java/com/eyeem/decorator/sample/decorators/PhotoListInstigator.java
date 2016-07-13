package com.eyeem.decorator.sample.decorators;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.KEY;
import com.eyeem.decorator.sample.data.Creator;
import com.eyeem.decorator.sample.data.model.list.ListPhoto;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by budius on 16.06.16.
 */
public class PhotoListInstigator extends Deco implements Deco.DataInstigator {

   private ListPhoto listPhoto;

   @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
      String id = getDecorated().getIntent().getStringExtra(KEY.URL);
      listPhoto = Creator.getListPhoto(id);
   }

   @Override protected void onDestroy() {
      listPhoto = null;
   }

   @Override public RealmList getList() {
      return listPhoto.list;
   }

   @Override public RealmObject getModel() {
      return listPhoto;
   }
}
