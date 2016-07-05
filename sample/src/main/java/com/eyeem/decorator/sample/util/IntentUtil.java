package com.eyeem.decorator.sample.util;

import android.app.Activity;
import android.content.Intent;

import com.eyeem.decorator.sample.DecoratedAct;
import com.eyeem.decorator.sample.KEY;
import com.eyeem.decorator.sample.MainActivity;

/**
 * Created by budius on 11.07.16.
 */
public class IntentUtil {

   public static void startActivity(Activity activity, DecoratedAct.Builder b) {
      activity.startActivity(getIntent(activity, b));
   }

   public static Intent getIntent(Activity activity, DecoratedAct.Builder b) {
      Intent i = new Intent(activity, MainActivity.class);
      i.putExtra(KEY.URL, activity.getIntent().getStringExtra(KEY.URL));
      i.putExtra(KEY.BUILDER, b);
      return i;
   }
}
