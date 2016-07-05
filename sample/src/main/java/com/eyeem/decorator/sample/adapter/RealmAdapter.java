package com.eyeem.decorator.sample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmModel;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by budius on 06.07.16.
 */
public abstract class RealmAdapter<T extends RealmModel, VH extends RecyclerView.ViewHolder> extends RealmRecyclerViewAdapter<T, VH> {

   public RealmAdapter(Context context, OrderedRealmCollection<T> data) {
      super(context, data, true);
   }
}