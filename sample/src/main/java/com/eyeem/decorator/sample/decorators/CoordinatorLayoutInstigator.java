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
      Deco.HeaderInstigator h = getDecorators().getFirstDecoratorOfType(Deco.HeaderInstigator.class);
      if (h != null) {
         LayoutInflater inflater = LayoutInflater.from(getDecorated());
         inflater.inflate(h.getHeaderId(), collapsing, true);
         h.onHeaderCreated(collapsing);
      }
   }

   @Override public void setTitle(String title) {
      collapsing.setTitle(title);
   }
}
