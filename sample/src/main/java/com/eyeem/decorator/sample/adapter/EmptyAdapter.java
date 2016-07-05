package com.eyeem.decorator.sample.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyeem.decorator.sample.R;

/**
 * Created by budius on 06.07.16.
 */
public class EmptyAdapter extends RecyclerView.Adapter<EmptyAdapter.Holder> {

   @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selection, parent, false));
   }

   @Override public void onBindViewHolder(Holder holder, int position) {
      if (position == 0) {
         ((TextView) holder.itemView).setText("Photos");
      } else {
         ((TextView) holder.itemView).setText("Users");
      }
   }

   @Override public int getItemCount() {
      return 2;
   }

   static class Holder extends RecyclerView.ViewHolder {
      public Holder(View itemView) {
         super(itemView);
      }
   }

}
