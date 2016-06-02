package com.eyeem.decorator.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.eyeem.decorator.sample.R;
import com.eyeem.recyclerviewtools.LoadMoreOnScrollListener;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 26.11.15.
 */
public class DecoratedActivitySample extends AppCompatActivityDecoratored implements LoadMoreOnScrollListener.Listener {

   public static Intent buildIntent(Context context) {
      Intent i = new Intent(context, DecoratedActivitySample.class);
      i.putExtra("builder", new DecoratedActivitySample.Builder()
            .addDecorator(AdapterInstigator.class)
            .addDecorator(HeaderType1Instigator.class)
            .addDecorator(ItemClickListener.class)
            .addDecorator(LayoutManagerInstigator.class));
      return i;
   }

   @Override protected void onCreate(Bundle savedInstanceState) {
      bind(getBuilder(getIntent().getSerializableExtra("builder")));
      super.onCreate(savedInstanceState);

      // build view
      int layoutId = getDecorators().getLayoutId();
      if (layoutId <= 0) {
         layoutId = R.layout.recycler;
      }
      setContentView(layoutId);
      getDecorators().onViewCreated(savedInstanceState);
      RecyclerView rv = (RecyclerView) findViewById(R.id.recycler);

      // build adapter
      RecyclerView.Adapter adapter = getDecorators().getAdapter();
      WrapAdapter wrapAdapter;
      if (adapter instanceof WrapAdapter) {
         wrapAdapter = (WrapAdapter) adapter;
      } else {
         wrapAdapter = new WrapAdapter(adapter);
      }

      // build layout manager
      RecyclerView.LayoutManager layoutManager = getLayoutManager();
      if (layoutManager instanceof GridLayoutManager) {
         ((GridLayoutManager) layoutManager).setSpanSizeLookup(wrapAdapter.createSpanSizeLookup(((GridLayoutManager) layoutManager).getSpanCount()));
      }

      // assemble layout manager and adapter
      rv.setLayoutManager(layoutManager);
      rv.setAdapter(wrapAdapter);

      // extras
      getDecorators().setupRecycler(rv, wrapAdapter);
      rv.addOnScrollListener(new LoadMoreOnScrollListener(this));
   }

   @Override protected void onDestroy() {
      getDecorators().onViewWillDestroy();
      super.onDestroy();
      unbind();
   }

   @Override public void onLoadMore(RecyclerView recyclerView) {
      getDecorators().onLoadMore(recyclerView);
   }
}
