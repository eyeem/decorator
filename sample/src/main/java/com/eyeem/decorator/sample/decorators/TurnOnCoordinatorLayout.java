package com.eyeem.decorator.sample.decorators;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.R;
import com.eyeem.decorator.sample.util.IntentUtil;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 12.07.16.
 */
public class TurnOnCoordinatorLayout extends Deco implements View.OnClickListener {

   private TextView v;

   @Override
   public void setupRecyclerView(RecyclerView recyclerView, WrapAdapter wrapAdapter, RecyclerView.Adapter adapter) {
      v = (TextView) LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.item_selection, recyclerView, false);
      v.setText("Click to enable CoordinatorLayout\n(with header)");
      v.setOnClickListener(this);
      wrapAdapter.addHeader(v);
   }

   @Override protected void onDestroy() {
      v.setOnClickListener(null);
      v = null;
   }

   @Override public void onClick(View v) {
      IntentUtil.startActivity(getDecorated(), getDecorators().buildUpon()
            .removeDecorator(TurnOnCoordinatorLayout.class)
            .addDecorator(MountainHeaderInstigator.class)
            .addDecorator(CoordinatorLayoutInstigator.class)
      );
   }
}
