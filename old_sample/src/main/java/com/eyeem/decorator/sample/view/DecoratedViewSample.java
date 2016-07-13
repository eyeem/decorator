package com.eyeem.decorator.sample.view;

import android.os.Bundle;

import com.eyeem.decorator.sample.R;
import com.eyeem.decorator.sample.mortar.MortarActivity;

/**
 * Created by budius on 09.12.15.
 */
public class DecoratedViewSample extends MortarActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.screen_layout_view_pager);
   }
}
