package com.eyeem.decorator.sample.decorators;

import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.ImageView;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.R;
import com.squareup.picasso.Picasso;

/**
 * Created by budius on 07.07.16.
 */
public class MountainHeaderInstigator extends Deco implements Deco.HeaderInstigator {

   @Override public int getHeaderId() {
      return R.layout.header;
   }

   @Override public void onHeaderCreated(CollapsingToolbarLayout collapsingToolbarLayout) {
      ImageView backdrop = (ImageView) collapsingToolbarLayout.findViewById(R.id.header_backdrop);
      Picasso.with(getDecorated())
            .load(R.drawable.header)
            .fit()
            .centerCrop()
            .into(backdrop);
   }
}
