package com.eyeem.decorator.sample.blueprint;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.eyeem.decorator.annotation.Decorate;
import com.eyeem.decorator.sample.R;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by budius on 16.06.16.
 */
@Decorate(
      decorator = "Deco",
      decoratored = "DecoratedAct"
)
public class ActivityBlueprint extends AppCompatActivity {

   @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override protected void onStart() {
      super.onStart();
   }

   @Override protected void onStop() {
      super.onStop();
   }

   @Override protected void onDestroy() {
      super.onDestroy();
   }

   protected void onViewInflated() {
   }

   protected void onViewCreated() {
   }

   public void setTitle(String title) {

   }

   public interface MenuDecorator {
      void inflateMenu(Toolbar toolbar);

      boolean onMenuItemClick(MenuItem item);
   }

   public void onUpClicked() {

   }

   public int getLayoutId() {
      return R.layout.recycler_view;
   }

   public RecyclerView.LayoutManager getLayoutManager() {
      return new LinearLayoutManager(this);
   }

   public RecyclerView.Adapter getAdapter() {
      return null;
   }

   public void setupRecyclerView(RecyclerView recyclerView, WrapAdapter wrapAdapter, RecyclerView.Adapter adapter) {
   }

   public interface DataInstigator {

      RealmList getList();

      RealmObject getModel();

   }

   public interface RequestInstigator {

      String getRequestId();

      void reload();

      void loadMore();

   }

   public interface HeaderInstigator {
      int getHeaderId();

      void onHeaderCreated(CollapsingToolbarLayout collapsingToolbarLayout);
   }
}
