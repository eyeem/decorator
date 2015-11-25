package com.eyeem.decorator.sample.view.blueprint;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eyeem.decorator.annotation.Decorate;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

import mortar.ViewPresenter;

/**
 * Created by budius on 15.12.15.
 */
@Decorate(  // any class name can be specified if parameters are passed
   decorator = "Deco", // abstract class with empty methods
   decorators = "Decorators", // group of decorator, dispatch the callbacks in loop
   decoratored = "Presenter" // original class, contains decorators and dispatches callbacks to it
)
public class PresenterBlueprint extends ViewPresenter<View> {

   @Override protected void onLoad(Bundle savedInstanceState) {
      super.onLoad(savedInstanceState);
   }

   @Override protected void onSave(Bundle outState) {
      super.onSave(outState);
   }

   public int getLayoutId() {
      return 0;
   }

   public void onViewInflated(View view) {

   }

   public void onSetupRecycler(RecyclerView recyclerView, WrapAdapter wrapAdapter) {

   }

   public RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
      return null;
   }

   public RecyclerView.Adapter getRecyclerViewAdapter() {
      return null;
   }

   public PagerAdapter getViewPagerAdapter() {
      return null;
   }

}
