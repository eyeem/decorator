package com.eyeem.decorator.sample.decorators;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.R;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * Created by budius on 28.06.16.
 */
public class DataSizeTitleInstigator extends Deco implements RealmChangeListener<RealmModel> {

   private RealmObject realmObject;

   @Override protected void onStart() {
      setTitle();
      realmObject = getDecorators().getModel();
      realmObject.addChangeListener(this);
   }

   @Override protected void onStop() {
      realmObject.removeChangeListener(this);
      realmObject = null;
   }

   private void setTitle() {
      getDecorators().setTitle(
            getDecorated().getString(R.string.app_name) + " - " + getDecorators().getList().size());
   }

   @Override public void onChange(RealmModel element) {
      setTitle();
   }
}
