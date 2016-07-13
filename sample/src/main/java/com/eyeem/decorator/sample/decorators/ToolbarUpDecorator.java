package com.eyeem.decorator.sample.decorators;

import android.content.Intent;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.MainActivity;

/**
 * Created by budius on 11.07.16.
 */
public class ToolbarUpDecorator extends Deco {
   @Override public void onUpClicked() {
      Intent i = new Intent(getDecorated(), MainActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      getDecorated().startActivity(i);
   }
}
