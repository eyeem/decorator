package com.eyeem.decorator.sample;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.eyeem.decorator.sample.adapter.DummyAdapter;
import com.eyeem.decorator.sample.decorators.EmptyInstigator;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

public class MainActivity extends DecoratedAct {

   @Override
   protected void onCreate(Bundle savedInstanceState) {

      if (getIntent() == null ||
            getIntent().getExtras() == null ||
            !getIntent().getExtras().containsKey(KEY.BUILDER)) {
         getIntent().putExtra(KEY.BUILDER, new Builder().addDecorator(EmptyInstigator.class));
      }

      bind(getBuilder(getIntent().getSerializableExtra(KEY.BUILDER)));
      super.onCreate(savedInstanceState);
      setContentView(getLayoutId());
      onViewInflated();

      RecyclerView rv = (RecyclerView) findViewById(R.id.recycler);
      rv.setLayoutManager(getLayoutManager());

      RecyclerView.Adapter adapter = getAdapter();
      if (adapter == null) {
         adapter = new DummyAdapter(this, getDecorators().getList());
      }

      WrapAdapter wrapAdapter;
      if (adapter instanceof WrapAdapter) {
         wrapAdapter = (WrapAdapter) adapter;
         adapter = wrapAdapter.getWrapped();
      } else {
         wrapAdapter = new WrapAdapter(adapter);
      }
      rv.setAdapter(wrapAdapter);
      setupRecyclerView(rv, wrapAdapter, adapter);
      onViewCreated();
   }

   @Override protected void onDestroy() {
      super.onDestroy();
      unbind();
   }
}