package com.eyeem.decorator.sample.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eyeem.recyclerviewtools.OnItemClickListener;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 27.11.15.
 */
public class ItemClickListener2 extends AppCompatActivityDecorator implements
      AppCompatActivityDecorator.SetupRecyclerDecorator, OnItemClickListener {

   @Override public void setupRecycler(RecyclerView recyclerView, WrapAdapter wrapAdapter) {
      wrapAdapter.setOnItemClickListener(recyclerView, this);
   }

   @Override
   public void onItemClick(RecyclerView parent, View view, int position, long id, RecyclerView.ViewHolder viewHolder) {
      decorated.finish();
   }
}
