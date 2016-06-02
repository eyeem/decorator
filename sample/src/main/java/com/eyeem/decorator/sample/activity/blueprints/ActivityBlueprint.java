package com.eyeem.decorator.sample.activity.blueprints;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.eyeem.decorator.annotation.Decorate;
import com.eyeem.decorator.sample.R;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

import java.util.Random;

/**
 * Created by budius on 26.11.15.
 */
@Decorate // this will generated with standard naming. Classname+Deco_: AppCompatActivityDecora...
public class ActivityBlueprint extends AppCompatActivity {

   //region activity callbacks
   @Override protected void onStart() {
      super.onStart();
   }

   @Override protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
   }

   @Override protected void onStop() {
      super.onStop();
   }
   //endregion


   public void onViewCreated(Bundle savedInstanceState) {

   }

   public void onViewWillDestroy() {

   }

   public boolean onBackTapped() {
      return false;
   }

   public interface SetupRecyclerDecorator {
      public void setupRecycler(RecyclerView recyclerView, WrapAdapter wrapAdapter);
   }

   public RecyclerView.Adapter getAdapter() {
      return null;
   }

   public RecyclerView.LayoutManager getLayoutManager() {
      // that is general a stupid thing to wring,
      // but it's a good test to see if our generator is properly handling silly cases
      if (new Random().nextBoolean()) {
         return new GridLayoutManager(this, new java.util.Random().nextInt(3) + 2);
      } else {
         return new LinearLayoutManager(this);
      }
   }

   public interface LoadMore {
      public void onLoadMore(RecyclerView recyclerView);
   }

   public int getLayoutId() {
      return R.layout.screen_layout;
   }

   public interface MenuDecorator {
      public void onCreateOptionsMenu(Toolbar toolbar);

      public boolean onOptionsItemSelected(MenuItem item);
   }


   public interface ViewPagerAdapter {
      public PagerAdapter getPagerAdapter(FragmentManager fm);
   }

   public interface TitleDecorator {
      public void onNewTitle(CharSequence title);
   }

   public interface HeaderInstigator {
      @LayoutRes public int getHeaderViewLayoutId();

      public void onHeaderCreated(View root, View appBarLayout);
   }

   // tests the generation of interface that extends other
   public interface InterfaceExtendOtherTest extends
         View.OnClickListener,
         View.OnCreateContextMenuListener,
         View.OnDragListener {
      public void onMultiClick(View v, int clickCount);
   }

}
