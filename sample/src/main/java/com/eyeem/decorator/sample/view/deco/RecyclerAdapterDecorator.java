package com.eyeem.decorator.sample.view.deco;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyeem.decorator.sample.view.Deco;

/**
 * Created by budius on 04.01.16.
 */
public class RecyclerAdapterDecorator extends Deco implements
      Deco.InstigateGetRecyclerViewAdapter {

   @Override public RecyclerView.Adapter getRecyclerViewAdapter() {
      return new A();
   }

   static class H extends RecyclerView.ViewHolder {

      TextView t;

      public H(View itemView) {
         super(itemView);
         t = (TextView) itemView;
         int p = (int) (t.getContext().getResources().getDisplayMetrics().density * 16);
         t.setPadding(p, p, p, p);
      }
   }

   static class A extends RecyclerView.Adapter<H> {

      private final String value = "Position";

      @Override public H onCreateViewHolder(ViewGroup parent, int viewType) {
         return new H(new TextView(parent.getContext()));
      }

      @Override public void onBindViewHolder(H holder, int position) {
         holder.t.setText(value + " " + position);
      }

      @Override public int getItemCount() {
         return 150;
      }
   }
}
