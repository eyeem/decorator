package com.eyeem.decorator.sample.view.deco;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eyeem.decorator.sample.R;
import com.eyeem.decorator.sample.view.Deco;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 15.12.15.
 */
public class RecyclerViewDecorator extends Deco implements
   Deco.InstigateGetLayoutId {

   @Override public int getLayoutId() {
      return R.layout.recycler;
   }

   @Override public void onViewInflated(View view) {
      RecyclerView rv = (RecyclerView) view.findViewById(R.id.recycler);
      RecyclerView.LayoutManager lm = getDecorators().getRecyclerViewLayoutManager();
      if (lm == null) {
         lm = new LinearLayoutManager(rv.getContext());
      }
      rv.setLayoutManager(lm);
      WrapAdapter wrapAdapter = new WrapAdapter(getDecorators().getRecyclerViewAdapter());
      rv.setAdapter(wrapAdapter);
      getDecorators().onSetupRecycler(rv, wrapAdapter);
   }
}
