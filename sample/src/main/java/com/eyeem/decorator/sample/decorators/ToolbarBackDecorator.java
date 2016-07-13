package com.eyeem.decorator.sample.decorators;

import com.eyeem.decorator.sample.Deco;

/**
 * Created by budius on 11.07.16.
 */
public class ToolbarBackDecorator extends Deco {
   @Override public void onUpClicked() {
      getDecorated().finish();
   }
}
