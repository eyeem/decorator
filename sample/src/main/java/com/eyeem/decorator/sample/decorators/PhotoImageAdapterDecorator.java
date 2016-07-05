package com.eyeem.decorator.sample.decorators;

import android.support.v7.widget.RecyclerView;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.adapter.PhotoGridAdapter;

/**
 * Created by budius on 06.07.16.
 */
public class PhotoImageAdapterDecorator extends Deco implements Deco.InstigateGetAdapter {

   @Override public RecyclerView.Adapter getAdapter() {
      return new PhotoGridAdapter(getDecorated(), getDecorators().getList());
   }
}
