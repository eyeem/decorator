package com.eyeem.decorator.sample.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;


/**
 * Created by budius on 27.11.15.
 */
public class LayoutManagerInstigator extends AppCompatActivityDecorator implements
   AppCompatActivityDecorator.InstigateGetLayoutManager {

   @Override public RecyclerView.LayoutManager getLayoutManager() {
      return new GridLayoutManager(decorated, 3);
   }
}
