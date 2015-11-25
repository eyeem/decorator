package com.eyeem.decorator.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyeem.decorator.sample.activity.DecoratedActivitySample;
import com.eyeem.decorator.sample.mortar.DecoratorService;
import com.eyeem.decorator.sample.view.Presenter;
import com.eyeem.decorator.sample.view.DecoratedViewSample;
import com.eyeem.decorator.sample.view.deco.ViewPagerAdapterDecorator;
import com.eyeem.decorator.sample.view.deco.ViewPagerDecorator;
import com.eyeem.recyclerviewtools.OnItemClickListener;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by budius on 21.07.15.
 */
public class MainActivity extends AppCompatActivity implements OnItemClickListener {

   public static final List<String> OPTIONS = Arrays.asList(
      "Decorated Activity",
      "Decorated View"
   );

   private RecyclerView recyclerView;
   private WrapAdapter wrapAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      recyclerView = new RecyclerView(this);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.setAdapter(wrapAdapter = new WrapAdapter(new Adapter()));
      wrapAdapter.setOnItemClickListener(recyclerView, this);
      setContentView(recyclerView);
   }

   @Override
   public void onItemClick(RecyclerView parent, View view, int position, long id, RecyclerView.ViewHolder viewHolder) {
      switch (position) {
         case 0:
            startActivity(DecoratedActivitySample.buildIntent(this));
            break;
         case 1:
            Intent i = new Intent(this, DecoratedViewSample.class);

            // direct recycler view
/*            DecoratorService.addBuilder(i, DecoratedPresenter.builder()
               .addDecorator(RecyclerViewDecorator.class)
               .addDecorator(RecyclerAdapterDecorator.class)
               .addDecorator(RecyclerClickListenerDecorator.class)
               .addDecorator(RecyclerHeaderDecorator.class));*/

            // view pager with recycler views
            DecoratorService.addBuilder(i, Presenter.builder()
               .addDecorator(ViewPagerDecorator.class)
               .addDecorator(ViewPagerAdapterDecorator.class));
            startActivity(i);
            break;
      }
   }

   public static class Holder extends RecyclerView.ViewHolder {

      public Holder(View itemView) {
         super(itemView);
         ((TextView) itemView).setTextSize(18);
         int pad = (int) (16 * itemView.getResources().getDisplayMetrics().density);
         itemView.setPadding(pad, pad, pad, pad);
      }
   }

   public static class Adapter extends RecyclerView.Adapter<Holder> {

      @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
         return new Holder(new TextView(parent.getContext()));
      }

      @Override public void onBindViewHolder(Holder holder, int position) {
         ((TextView) holder.itemView).setText(OPTIONS.get(position));
      }

      @Override public int getItemCount() {
         return OPTIONS.size();
      }
   }
}
