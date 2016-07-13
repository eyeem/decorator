package com.eyeem.decorator.sample.decorators;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 16.06.16.
 */
public class GridInstigator extends Deco implements Deco.InstigateGetLayoutManager {

   private GridLayoutManager gridLayoutManager;

   @Override public RecyclerView.LayoutManager getLayoutManager() {
      return gridLayoutManager = new GridLayoutManager(getDecorated(), 3);
   }

   @Override
   public void setupRecyclerView(RecyclerView recyclerView, WrapAdapter wrapAdapter, RecyclerView.Adapter adapter) {
      gridLayoutManager.setSpanSizeLookup(wrapAdapter.createSpanSizeLookup(3));
   }
}
