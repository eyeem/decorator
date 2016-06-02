package com.eyeem.decorator.sample.mortar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

/**
 * Created by budius on 15.12.15.
 */
public class MortarActivity extends AppCompatActivity {
   private MortarScope activityScope;

   public static final String SCOPE_NAME = "ACTIVITY_SCOPE";

   @Override public Object getSystemService(@NonNull String name) {
      activityScope = MortarScope.findChild(getApplicationContext(), SCOPE_NAME);

      if (activityScope == null) {
         activityScope = MortarScope.buildChild(getApplicationContext())
               .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
               .withService(DecoratorService.SERVICE_NAME, new DecoratorService(this))
               .build(SCOPE_NAME);
      }
      return activityScope.hasService(name) ? activityScope.getService(name)
            : super.getSystemService(name);
   }

   @Override protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);
   }

   @Override protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(outState);
   }

   @Override protected void onDestroy() {
      if (isFinishing() && activityScope != null) {
         activityScope.destroy();
      }
      super.onDestroy();
   }
}
