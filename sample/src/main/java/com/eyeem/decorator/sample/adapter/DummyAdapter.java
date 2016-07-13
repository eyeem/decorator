package com.eyeem.decorator.sample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;

/**
 * Created by budius on 16.06.16.
 */
public class DummyAdapter extends RealmAdapter {

   public DummyAdapter(Context context, OrderedRealmCollection data) {
      super(context, data);
   }

   @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new Holder(new TextView(parent.getContext()));
   }

   @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      ((TextView) holder.itemView).setText(getItem(position).toString());
   }

   public static class Holder extends RecyclerView.ViewHolder {

      public Holder(View itemView) {
         super(itemView);
      }
   }
}
