package com.eyeem.decorator.sample.decorators;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by budius on 07.07.16.
 */
public class CoordinatorLayoutInstigator extends Deco implements Deco.InstigateGetLayoutId {

   @Bind(R.id.collapsing) CollapsingToolbarLayout collapsing;
   @Bind(R.id.appbar) AppBarLayout appbar;
   @Bind(R.id.coordinator) CoordinatorLayout coordinator;

   @Override public int getLayoutId() {
      return R.layout.coordinator_layout;
   }

   @Override protected void onViewInflated() {
      ButterKnife.bind(this, getDecorated());
      int headerId = getDecorators().getHeaderId();
      if (headerId > 0) {
         LayoutInflater inflater = LayoutInflater.from(getDecorated());
         inflater.inflate(headerId, collapsing, true);
         getDecorators().onHeaderCreated(collapsing);
      }
   }

   @Override public void setTitle(String title) {
      collapsing.setTitle(title);
   }
}
