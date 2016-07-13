package com.eyeem.decorator.sample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyeem.decorator.sample.R;
import com.eyeem.decorator.sample.data.model.Photo;
import com.eyeem.decorator.sample.util.PhotoUtils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;

/**
 * Created by budius on 07.07.16.
 */
public class PhotoAdapter extends RealmAdapter<Photo, PhotoAdapter.Holder> {

   private final LayoutInflater inflater;
   private int width = -1;
   private int avatarSize = -1;

   public PhotoAdapter(Context context, OrderedRealmCollection<Photo> data) {
      super(context, data);
      setHasStableIds(true);
      inflater = LayoutInflater.from(context);
   }

   @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new Holder(inflater.inflate(R.layout.item_photo, parent, false));
   }

   @Override public void onBindViewHolder(Holder holder, int position) {

      if (width <= 0) {
         width = context.getResources().getDisplayMetrics().widthPixels -
               holder.container.getPaddingLeft() -
               holder.container.getPaddingRight();
      }
      if (avatarSize <= 0) {
         avatarSize = holder.profile.getLayoutParams().width;
      }

      Photo p = getItem(position);
      int height = PhotoUtils.getHeight(p, width);
      holder.photo.getLayoutParams().height = height;
      Picasso.with(context)
            .load(PhotoUtils.getPhotoUrlbyWidth(p, width))
            .resize(width, height)
            .centerCrop()
            .placeholder(PhotoUtils.getPlaceholder(p))
            .into(holder.photo);

      Picasso.with(context)
            .load(PhotoUtils.getProfilePhotoUrl(p, avatarSize))
            .resize(avatarSize, avatarSize)
            .centerCrop()
            .placeholder(PhotoUtils.getPlaceholder(p))
            .into(holder.profile);

      holder.name.setText("@" + p.user.nickname);

   }

   @Override public long getItemId(int index) {
      return Math.abs(getItem(index).id.hashCode());
   }

   static class Holder extends RecyclerView.ViewHolder {

      @Bind(R.id.profile) CircleImageView profile;
      @Bind(R.id.name) TextView name;
      @Bind(R.id.photo) ImageView photo;
      @Bind(R.id.padding_container) View container;

      public Holder(View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
      }
   }
}
