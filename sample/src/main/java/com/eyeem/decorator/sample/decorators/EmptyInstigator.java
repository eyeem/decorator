package com.eyeem.decorator.sample.decorators;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.DecoratedAct;
import com.eyeem.decorator.sample.KEY;
import com.eyeem.decorator.sample.MainActivity;
import com.eyeem.decorator.sample.adapter.EmptyAdapter;
import com.eyeem.decorator.sample.data.api.EyeEm;
import com.eyeem.recyclerviewtools.OnItemClickListener;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 11.07.16.
 */
public class EmptyInstigator extends Deco implements Deco.InstigateGetAdapter, OnItemClickListener {
   @Override public RecyclerView.Adapter getAdapter() {
      return new EmptyAdapter();
   }

   @Override
   public void setupRecyclerView(RecyclerView recyclerView, WrapAdapter wrapAdapter, RecyclerView.Adapter adapter) {
      wrapAdapter.setOnItemClickListener(recyclerView, this);
   }

   @Override
   public void onItemClick(RecyclerView parent, View view, int position, long id, RecyclerView.ViewHolder viewHolder) {
      DecoratedAct.Builder b = getDecorators().buildUpon();
      Intent i = new Intent(getDecorated(), MainActivity.class);
      if (position == 0) {
         b.removeDecorator(EmptyInstigator.class)
               .addDecorator(PhotoListInstigator.class)
               .addDecorator(PhotoRequestInstigator.class)
               .addDecorator(MenuAdapterOptionsInstigator.class)
               .addDecorator(DebugMenu.class)
               .addDecorator(ToolbarInstigator.class);
         i.putExtra(KEY.BUILDER, b);
         i.putExtra(KEY.URL, EyeEm.PHOTOS_POPULAR);
         getDecorated().startActivity(i);
      } else {
         b.removeDecorator(EmptyInstigator.class)
               .addDecorator(UserListInstigator.class)
               .addDecorator(UserRequestInstigator.class)
               .addDecorator(MenuAdapterOptionsInstigator.class)
               .addDecorator(DebugMenu.class)
               .addDecorator(ToolbarInstigator.class);
         i.putExtra(KEY.BUILDER, b);
         i.putExtra(KEY.URL, EyeEm.PHOTOS_POPULAR);
      }
   }
}
