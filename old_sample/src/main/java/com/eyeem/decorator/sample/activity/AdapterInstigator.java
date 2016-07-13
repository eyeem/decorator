package com.eyeem.decorator.sample.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by budius on 27.11.15.
 */
public class AdapterInstigator extends AppCompatActivityDecorator implements
      AppCompatActivityDecorator.InstigateGetAdapter {

   A a;

   @Override public RecyclerView.Adapter getAdapter() {
      return a = new A();
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

   public static class A extends RecyclerView.Adapter<H> {

      @Override public H onCreateViewHolder(ViewGroup parent, int viewType) {
         return new H(new TextView(parent.getContext()));
      }

      @Override public void onBindViewHolder(H holder, int position) {
         holder.t.setText("Position " + position);
      }

      @Override public int getItemCount() {
         return 150;
      }
   }


}
