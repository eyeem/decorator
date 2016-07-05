package com.eyeem.decorator.sample.view.deco;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.eyeem.decorator.sample.R;
import com.eyeem.decorator.sample.view.Deco;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by budius on 04.01.16.
 */
public class ViewPagerDecorator extends Deco {

   @Bind(R.id.tab) TabLayout tab;
   @Bind(R.id.pager) ViewPager pager;

   @Override public void onViewInflated(View view) {
      ButterKnife.bind(this, view);
      pager.setAdapter(getDecorators().getViewPagerAdapter());
      tab.setupWithViewPager(pager);
      ButterKnife.unbind(this);
   }

   @Override protected void onLoad(Bundle savedInstanceState) {

   }

   @Override protected void onSave(Bundle outState) {

   }
}
