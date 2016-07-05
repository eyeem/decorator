package com.eyeem.decorator.sample.decorators;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.DecoratedAct;
import com.eyeem.decorator.sample.KEY;
import com.eyeem.decorator.sample.R;

/**
 * Created by budius on 12.07.16.
 */
public class DebugMenu extends Deco implements Deco.MenuDecorator {

   @Override protected void onCreate(@Nullable Bundle savedInstanceState) {

      if (savedInstanceState == null) {
         String[] d = getDecoratorArray();
         Log.d("Budius", "======================================================");
         Log.d("Budius", String.format("Starting new activity with %s decorators:", d.length));
         for (int i = 0, size = d.length; i < size; i++) {
            Log.d("Budius", String.format("%s. %s", i, d[i]));
         }
         Log.d("Budius", "======================================================");
      }

   }

   private String[] getDecoratorArray() {
      return ((DecoratedAct.Builder) getDecorated().getIntent().getSerializableExtra(KEY.BUILDER)).getDecorators();
   }


   @Override public void inflateMenu(Toolbar toolbar) {
      toolbar.inflateMenu(R.menu.menu_debug);
   }

   @Override public boolean onMenuItemClick(MenuItem item) {
      if (item.getItemId() == R.id.menu_item_debug_log) {
         AlertDialog.Builder builder = new AlertDialog.Builder(getDecorated());
         builder.setTitle("Current decorators:")
               .setItems(getDecoratorArray(),
                     new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                        }
                     }).show();
         return true;
      } else {
         return false;
      }
   }
}
