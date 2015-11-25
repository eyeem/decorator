package com.eyeem.decorator.sample.view.deco;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.eyeem.decorator.base_classes.AbstractDecorators;
import com.eyeem.decorator.sample.ScreenPagerAdapter;
import com.eyeem.decorator.sample.mortar.DecoratorService;
import com.eyeem.decorator.sample.view.Deco;
import com.eyeem.decorator.sample.view.Presenter;
import com.eyeem.decorator.sample.view.ScreenLayoutFrame;

/**
 * Created by budius on 04.01.16.
 */
public class ViewPagerAdapterDecorator extends Deco implements
   Deco.InstigateGetViewPagerAdapter {

   @Override public PagerAdapter getViewPagerAdapter() {
      return new Adapter();
   }

   static class Holder extends ScreenPagerAdapter.ViewHolder {

      public Holder(View itemView) {
         super(itemView);
      }
   }

   static class Adapter extends ScreenPagerAdapter<Holder> {

      @Override
      public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
         return new Holder(new ScreenLayoutFrame(new DecoratorService.WrapContext(parent.getContext(), PAGES[viewType], "PAGER_SCOPE_" + Integer.toString(viewType))));
      }

      @Override public void onBindViewHolder(Holder holder, int position) {

      }

      @Override public void onUnbindViewHolder(Holder holder, int position) {

      }

      @Override public int getCount() {
         return PAGES.length;
      }

      @Override public int getItemViewType(int position) {
         return position; // unique
      }

      @Override public CharSequence getPageTitle(int position) {
         return "Tab " + position;
      }
   }

   // this could come from the intent
   public static final AbstractDecorators.Builder[] PAGES = {

      // PAGE 0
      Presenter.builder()
         .addDecorator(RecyclerViewDecorator.class)
         .addDecorator(RecyclerAdapterDecorator.class)
         .addDecorator(RecyclerClickListenerDecorator.class)
         .addDecorator(RecyclerHeaderDecorator.class),

      // PAGE 1
      Presenter.builder()
         .addDecorator(RecyclerViewDecorator.class)
         .addDecorator(RecyclerAdapterDecorator.class)
         .addDecorator(RecyclerClickListenerDecorator.class),

      // PAGE 2
      Presenter.builder()
         .addDecorator(RecyclerViewDecorator.class)
         .addDecorator(RecyclerAdapterDecorator.class)
         .addDecorator(RecyclerClickListenerDecorator.class),

      // PAGE 3
      Presenter.builder()
         .addDecorator(RecyclerViewDecorator.class)
         .addDecorator(RecyclerAdapterDecorator.class)
         .addDecorator(RecyclerClickListenerDecorator.class),
   };

}
