package com.eyeem.decorator.sample.decorators;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.R;
import com.eyeem.decorator.sample.util.IntentUtil;

/**
 * Created by budius on 11.07.16.
 */
public class MenuAdapterOptionsInstigator extends Deco implements Deco.MenuDecorator {

   @Override public void inflateMenu(Toolbar toolbar) {
      toolbar.inflateMenu(R.menu.adapter_options);
   }

   @Override protected void onStart() {
      getDecorators().setTitle("Select Adapter ==>>");
   }

   @Override public boolean onMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.adapter_option_grid:
            IntentUtil.startActivity(getDecorated(),
                  getDecorators().buildUpon()
                        .removeDecorator(MenuAdapterOptionsInstigator.class)
                        .addDecorator(HelloWorldTitleInstigator.class)
                        .addDecorator(GridInstigator.class)
                        .addDecorator(TurnOnSwipeToRefresh.class)
                        .addDecorator(TurnOnLoadMore.class)
                        .addDecorator(SwitchToCardOnClick.class)
                        .addDecorator(LoadingDecorator.class)
                        .addDecorator(PhotoImageAdapterDecorator.class)
            );
            return true;
         case R.id.adapter_option_card:
            IntentUtil.startActivity(getDecorated(),
                  getDecorators().buildUpon()
                        .removeDecorator(MenuAdapterOptionsInstigator.class)
                        .addDecorator(HelloWorldTitleInstigator.class)
                        .addDecorator(PhotoCardAdapterDecorator.class)
            );
            return true;
         case R.id.adapter_option_staggered:
            IntentUtil.startActivity(getDecorated(),
                  getDecorators().buildUpon()
                        .removeDecorator(MenuAdapterOptionsInstigator.class)
                        .addDecorator(HelloWorldTitleInstigator.class)
                        .addDecorator(LoadingDecorator.class)
                        .addDecorator(TurnOnSwipeToRefresh.class)
                        .addDecorator(TurnOnLoadMore.class)
                        .addDecorator(StaggeredLayoutManagerInstigator.class)
                        .addDecorator(SwitchToCardOnClick.class)
                        .addDecorator(PhotoImageAdapterDecorator.class)
            );
            return true;
      }
      return false;
   }
}
