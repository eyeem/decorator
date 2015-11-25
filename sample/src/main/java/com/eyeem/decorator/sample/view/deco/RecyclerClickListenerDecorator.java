package com.eyeem.decorator.sample.view.deco;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.eyeem.decorator.sample.view.Deco;
import com.eyeem.recyclerviewtools.OnItemClickListener;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 16.12.15.
 */
public class RecyclerClickListenerDecorator extends Deco implements OnItemClickListener {

   @Override public void onSetupRecycler(RecyclerView recyclerView, WrapAdapter wrapAdapter) {
      wrapAdapter.setOnItemClickListener(recyclerView, this);
   }

   @Override
   public void onItemClick(RecyclerView parent, View view, int position, long id, RecyclerView.ViewHolder viewHolder) {
      Toast.makeText(parent.getContext(), "Wow... " + position, Toast.LENGTH_SHORT).show();
   }
}
