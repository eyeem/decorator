package com.eyeem.decorator.sample.decorators;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.DecoratedAct;
import com.eyeem.decorator.sample.util.IntentUtil;
import com.eyeem.recyclerviewtools.OnItemClickListener;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 12.07.16.
 */
public class SwitchToCardOnClick extends Deco implements OnItemClickListener {

   @Override
   public void setupRecyclerView(RecyclerView recyclerView, WrapAdapter wrapAdapter, RecyclerView.Adapter adapter) {
      wrapAdapter.setOnItemClickListener(recyclerView, this);
   }

   @Override
   public void onItemClick(RecyclerView parent, View view, int position, long id, RecyclerView.ViewHolder viewHolder) {
      DecoratedAct.Builder b = getDecorators().buildUpon();

      boolean isPhoto = b.contains(PhotoListInstigator.class);

      IntentUtil.startActivity(getDecorated(), b
            .removeDecorator(this.getClass())
            .removeDecorator(StaggeredLayoutManagerInstigator.class)
            .removeDecorator(GridInstigator.class)
            .removeDecorator(PhotoImageAdapterDecorator.class)
            .addDecorator(isPhoto ? PhotoCardAdapterDecorator.class : null)
      );
   }
}
