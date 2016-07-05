package com.eyeem.decorator.sample.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eyeem.decorator.sample.data.model.Photo;
import com.eyeem.decorator.sample.util.PhotoUtils;
import com.squareup.picasso.Picasso;

import io.realm.OrderedRealmCollection;

/**
 * Created by budius on 06.07.16.
 */
public class PhotoGridAdapter extends RealmAdapter<Photo, PhotoGridAdapter.Holder> {

   private final int screenWidth;
   private int width;

   public PhotoGridAdapter(Context context, OrderedRealmCollection<Photo> data) {
      super(context, data);
      screenWidth = context.getResources().getDisplayMetrics().widthPixels;
      setHasStableIds(true);
   }

   @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
      super.onAttachedToRecyclerView(recyclerView);
      RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
      int count = 1;
      if (lm instanceof GridLayoutManager) {
         count = ((GridLayoutManager) lm).getSpanCount();
      } else if (lm instanceof StaggeredGridLayoutManager) {
         count = ((StaggeredGridLayoutManager) lm).getSpanCount();
      }
      width = screenWidth / count;
   }

   @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
      ImageView imageView = new ImageView(parent.getContext());
      imageView.setLayoutParams(
            new ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT));
      return new Holder(imageView);
   }

   @Override public void onBindViewHolder(Holder holder, int position) {
      Photo p = getItem(position);
      int height = PhotoUtils.getHeight(p, width);
      holder.itemView.getLayoutParams().width = width;
      holder.itemView.getLayoutParams().height = height;
      String url = PhotoUtils.getPhotoUrlbyWidth(p, width);
      Picasso.with(context)
            .load(url)
            .resize(width, height)
            .centerCrop()
            .placeholder(PhotoUtils.getPlaceholder(p))
            .into((ImageView) holder.itemView);
   }

   @Override public long getItemId(int index) {
      return Math.abs(getItem(index).id.hashCode());
   }

   static class Holder extends RecyclerView.ViewHolder {
      public Holder(View itemView) {
         super(itemView);
      }
   }
}
