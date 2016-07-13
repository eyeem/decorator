package com.eyeem.decorator.sample;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by budius on 28.06.16.
 */
public class App extends Application {
   @Override public void onCreate() {
      super.onCreate();
      RealmConfiguration realmConfig = new RealmConfiguration
            .Builder(this)
            .deleteRealmIfMigrationNeeded()
            .build();
      Realm.setDefaultConfiguration(realmConfig);
   }
}
