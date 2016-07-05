package com.eyeem.decorator.sample.decorators;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.KEY;
import com.eyeem.decorator.sample.MainActivity;
import com.eyeem.decorator.sample.adapter.PhotoAdapter;
import com.eyeem.decorator.sample.data.api.EyeEm;
import com.eyeem.decorator.sample.data.model.Photo;
import com.eyeem.recyclerviewtools.OnItemClickListener;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 07.07.16.
 */
public class PhotoCardAdapterDecorator extends Deco implements Deco.InstigateGetAdapter, OnItemClickListener {

   @Override
   public void setupRecyclerView(RecyclerView recyclerView, WrapAdapter wrapAdapter, RecyclerView.Adapter adapter) {
      wrapAdapter.setOnItemClickListener(recyclerView, this);
   }

   @Override public RecyclerView.Adapter getAdapter() {
      return new PhotoAdapter(getDecorated(), getDecorators().getList());
   }

   @Override
   public void onItemClick(RecyclerView parent, View view, int position, long id, RecyclerView.ViewHolder viewHolder) {
      Photo p = ((PhotoAdapter) ((WrapAdapter) parent.getAdapter()).getWrapped()).getItem(position);
      Intent i = new Intent(getDecorated(), MainActivity.class);
      i.putExtra(KEY.ID, p.user.id);
      i.putExtra(KEY.URL, EyeEm.PHOTOS_FROM_USER(p.user.id));
      i.putExtra(KEY.BUILDER, getDecorators().buildUpon()
            .removeDecorator(PhotoCardAdapterDecorator.class)
            .removeDecorator(DataSizeTitleInstigator.class)
            .removeDecorator(HelloWorldTitleInstigator.class)
            .addDecorator(UserTitleInstigator.class)
            .addDecorator(StaggeredLayoutManagerInstigator.class)
            .addDecorator(PhotoImageAdapterDecorator.class)
      );

      getDecorated().startActivity(i);
   }
}
