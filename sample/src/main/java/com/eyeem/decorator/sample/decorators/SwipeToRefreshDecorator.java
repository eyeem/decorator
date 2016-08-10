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
   private String requestId;

   @Override protected void onViewCreated() {
      requestId = getDecorators().getRequestId();
      refreshLayout = (SwipeRefreshLayout) getDecorated().findViewById(R.id.refresh);
      refreshLayout.setEnabled(true);
   }

   @Override protected void onStart() {
      if (EyeEm.requests().isExecuting(requestId)) {
         EyeEm.requests().addListener(this, requestId);
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
      EyeEm.requests().addListener(this, requestId);
      getDecorators().reload();
   }

   @Override public void onRequestFinished(boolean success, int offset) {
      if (offset == 0) {
         EyeEm.requests().removeListener(this);
         refreshLayout.setRefreshing(false);
      }
   }
}
