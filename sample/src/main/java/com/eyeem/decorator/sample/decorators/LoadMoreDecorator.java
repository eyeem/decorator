package com.eyeem.decorator.sample.decorators;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.data.api.EyeEm;
import com.eyeem.decorator.sample.data.api.RequestObservableList;
import com.eyeem.recyclerviewtools.LoadMoreOnScrollListener;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 05.07.16.
 */
public class LoadMoreDecorator extends Deco implements LoadMoreOnScrollListener.Listener, RequestObservableList.Listener {

   private View loading;

   @Override
   public void setupRecyclerView(RecyclerView recyclerView, WrapAdapter wrapAdapter, RecyclerView.Adapter adapter) {
      loading = new ProgressBar(recyclerView.getContext());
      loading.setVisibility(View.GONE);
      FrameLayout frameLayout = new FrameLayout(recyclerView.getContext());
      frameLayout.addView(loading);
      wrapAdapter.addFooter(frameLayout);
      recyclerView.addOnScrollListener(new LoadMoreOnScrollListener(this));
   }

   @Override protected void onStart() {
      loading.setVisibility(EyeEm.requests().isExecuting(getDecorators().getRequestId()) ? View.VISIBLE : View.GONE);
   }

   @Override protected void onStop() {
      EyeEm.requests().removeListener(this);
   }

   @Override protected void onDestroy() {
      loading = null;
   }

   @Override public void onLoadMore(RecyclerView recyclerView) {
      EyeEm.requests().addListener(this, getDecorators().getRequestId());
      loading.setVisibility(View.VISIBLE);
      getDecorators().loadMore();
   }

   @Override public void onRequestFinished(boolean success, int offset) {
      if (offset > 0) {
         loading.setVisibility(View.GONE);
         EyeEm.requests().removeListener(LoadMoreDecorator.this);
      }
   }
}
