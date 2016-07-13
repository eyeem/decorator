package com.eyeem.decorator.sample.decorators;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.R;

/**
 * Created by budius on 28.06.16.
 */
public class ToolbarInstigator extends Deco implements Toolbar.OnMenuItemClickListener, View.OnClickListener {

   private Toolbar toolbar;

   @Override protected void onViewInflated() {
      ViewStub stub = (ViewStub) getDecorated().findViewById(R.id.stub_toolbar);
      if (stub != null) {
         toolbar = (Toolbar) stub.inflate().findViewById(R.id.toolbar);
      }
   }

   @Override protected void onViewCreated() {

      if (toolbar == null) {
         // toolbar might have been inflated from the stub,
         // or might come from other sources (e.g. coordinator layout header)
         // so case it was not stub-inflated, we try to find it here on the whole view hierarchy
         toolbar = (Toolbar) getDecorated().findViewById(R.id.toolbar);
      }

      if (toolbar != null) {
         getDecorators().inflateMenu(toolbar);
         toolbar.setOnMenuItemClickListener(this);
         toolbar.setNavigationOnClickListener(this);
      }
   }

   @Override public void setTitle(String title) {
      if (toolbar != null) {
         toolbar.setTitle(title);
      }
   }

   @Override public boolean onMenuItemClick(MenuItem item) {
      return getDecorators().onMenuItemClick(item);
   }

   @Override public void onClick(View v) {
      getDecorators().onUpClicked();
   }
}
