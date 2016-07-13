package com.eyeem.decorator.sample.decorators;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.eyeem.decorator.sample.Deco;

/**
 * Created by budius on 11.07.16.
 */
public class StaggeredLayoutManagerInstigator extends Deco implements Deco.InstigateGetLayoutManager {
   @Override public RecyclerView.LayoutManager getLayoutManager() {
      return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
   }
}
