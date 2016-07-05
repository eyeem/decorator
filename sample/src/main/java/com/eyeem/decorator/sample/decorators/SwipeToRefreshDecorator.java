package com.eyeem.decorator.sample.decorators;

import android.support.v4.widget.SwipeRefreshLayout;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.R;
import com.eyeem.decorator.sample.data.api.EyeEm;
import com.eyeem.decorator.sample.data.api.RequestObservableList;

/**
 * Created by budius on 28.06.16.
 */
public class SwipeToRefreshDecorator extends Deco implements SwipeRefreshLayout.OnRefreshListener, RequestObservableList.Listener {

   private SwipeRefreshLayout refreshLayout;

   @Override protected void onViewCreated() {
      refreshLayout = (SwipeRefreshLayout) getDecorated().findViewById(R.id.refresh);
      refreshLayout.setEnabled(true);
   }

   @Override protected void onStart() {
      Deco.RequestInstigator d = getDecorators().getFirstDecoratorOfType(Deco.RequestInstigator.class);
      if (EyeEm.requests().isExecuting(d.getRequestId())) {
         EyeEm.requests().addListener(this, d.getRequestId());
         refreshLayout.setRefreshing(true);
      } else {
         refreshLayout.setRefreshing(false);
      }
      refreshLayout.setOnRefreshListener(this);
   }

   @Override protected void onStop() {
      refreshLayout.setOnRefreshListener(null);
      EyeEm.requests().removeListener(this);
   }

   @Override protected void onDestroy() {
      refreshLayout = null;
   }

   @Override public void onRefresh() {
      Deco.RequestInstigator d = getDecorators().getFirstDecoratorOfType(Deco.RequestInstigator.class);
      EyeEm.requests().addListener(this, d.getRequestId());
      d.reload();
   }

   @Override public void onRequestFinished(boolean success, int offset) {
      if (offset == 0) {
         EyeEm.requests().removeListener(this);
         refreshLayout.setRefreshing(false);
      }
   }
}
