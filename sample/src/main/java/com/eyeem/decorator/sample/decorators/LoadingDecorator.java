package com.eyeem.decorator.sample.decorators;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.R;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 12.07.16.
 */
public class LoadingDecorator extends Deco {

   private WrapAdapter wrapAdapter;
   private View loading;

   @Override
   public void setupRecyclerView(RecyclerView recyclerView, WrapAdapter wrapAdapter, RecyclerView.Adapter adapter) {
      this.wrapAdapter = wrapAdapter;
      this.wrapAdapter.getWrapped().registerAdapterDataObserver(observer);
      loading = LayoutInflater.from(getDecorated()).inflate(R.layout.loading, recyclerView, false);
   }

   @Override protected void onStart() {
      checkStatus();
   }

   private void checkStatus() {
      wrapAdapter.setCustomView(wrapAdapter.getWrappedCount() == 0 ? loading : null);
   }

   @Override protected void onDestroy() {
      this.wrapAdapter.getWrapped().unregisterAdapterDataObserver(observer);
      this.wrapAdapter = null;
   }


   private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
      @Override public void onChanged() {
         checkStatus();
      }

      @Override public void onItemRangeInserted(int positionStart, int itemCount) {
         checkStatus();
      }

      @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
         checkStatus();
      }
   };
}
