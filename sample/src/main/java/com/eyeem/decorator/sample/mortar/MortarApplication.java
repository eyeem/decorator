package com.eyeem.decorator.sample.mortar;

import android.app.Application;

import mortar.MortarScope;

/**
 * Created by budius on 15.12.15.
 */
public class MortarApplication extends Application {

   public MortarScope rootScope;

   @Override public Object getSystemService(String name) {
      if (rootScope == null)
         rootScope = MortarScope.buildRootScope().build(this.getClass().getCanonicalName() + ".scope");
      return rootScope.hasService(name) ? rootScope.getService(name) : super.getSystemService(name);
   }
}
