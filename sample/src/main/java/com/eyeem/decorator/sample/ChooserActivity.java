package com.eyeem.decorator.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.eyeem.decorator.sample.data.api.EyeEm;
import com.eyeem.decorator.sample.decorators.CoordinatorLayoutInstigator;
import com.eyeem.decorator.sample.decorators.DataSizeTitleInstigator;
import com.eyeem.decorator.sample.decorators.GridInstigator;
import com.eyeem.decorator.sample.decorators.HelloWorldTitleInstigator;
import com.eyeem.decorator.sample.decorators.LoadMoreDecorator;
import com.eyeem.decorator.sample.decorators.MountainHeaderInstigator;
import com.eyeem.decorator.sample.decorators.PhotoCardAdapterDecorator;
import com.eyeem.decorator.sample.decorators.PhotoImageAdapterDecorator;
import com.eyeem.decorator.sample.decorators.PhotoListInstigator;
import com.eyeem.decorator.sample.decorators.PhotoRequestInstigator;
import com.eyeem.decorator.sample.decorators.StaggeredLayoutManagerInstigator;
import com.eyeem.decorator.sample.decorators.SwipeToRefreshDecorator;
import com.eyeem.decorator.sample.decorators.ToolbarInstigator;
import com.eyeem.decorator.sample.decorators.UserListInstigator;
import com.eyeem.decorator.sample.decorators.UserRequestInstigator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by budius on 28.06.16.
 * </p>
 * The idea here is that it's so easy to add functionality via decoration,
 * that the best way to show it off is with a bunch of toggles to show all possible combinations.
 * This is a very visual way of showing off the flexibility of decorators, but very boring to code.
 * </p>
 * So this ChooserActivity is here as a PreferenceActivity,
 * but that  is a bad piece of code and I would never code it like this in a real app.
 * </p>
 * Instead a real app would just build the options during click for that one specific action.
 */
public class ChooserActivity extends PreferenceActivity {

   SharedPreferences prefs;
   private int step = 1;
   @Bind(R.id.go) Button button;

   @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_chooser);
      ButterKnife.bind(this);

      prefs = PreferenceManager.getDefaultSharedPreferences(this);
      step = getIntent().getIntExtra("step", 1);

      // just started, base options
      if (step == 1) {
         addPreferencesFromResource(R.xml.options);
         button.setText("Next");
      }

      // if photo type
      else if (prefs.getString("data_type", "Photo").equals("Photo")) {
         addPreferencesFromResource(R.xml.options_photo);
      }

      // else, it's user type
      else {

      }
   }

   @OnClick(R.id.go) public void go(View view) {

      if (step == 1) {
         Intent i = new Intent(this, ChooserActivity.class);
         i.putExtra("step", 2);
         startActivity(i);
         return;
      }

      Intent i = new Intent(this, MainActivity.class);
      DecoratedAct.Builder b = new DecoratedAct.Builder();
      String temp;

      // toolbar
      if (prefs.getBoolean("toolbar_enabled", false)) {
         b.addDecorator(ToolbarInstigator.class);
         temp = prefs.getString("toolbar_title", "Hello World");
         switch (temp) {
            case "Item Count":
               b.addDecorator(DataSizeTitleInstigator.class);
               break;
            case "Hello World":
            default:
               b.addDecorator(HelloWorldTitleInstigator.class);
               break;
         }
      }

      // Layout
      if (prefs.getBoolean("coordinator_layout", false)) {
         b.addDecorator(CoordinatorLayoutInstigator.class);
         if (prefs.getBoolean("coordinator_header", false)) {
            b.addDecorator(MountainHeaderInstigator.class);
         }
      }

      // Data and Recycler
      temp = prefs.getString("data_type", "Photo");
      switch (temp) {
         case "User":
            i.putExtra(KEY.URL, addUserOptions(b));
            break;
         case "Photo":
         default:
            i.putExtra(KEY.URL, addPhotoOptions(b));
            break;
      }

      temp = prefs.getString("recyclerview_layout", "List");
      switch (temp) {
         case "Grid":
            b.addDecorator(GridInstigator.class);
            break;
         case "Staggered":
            b.addDecorator(StaggeredLayoutManagerInstigator.class);
            break;
         case "Linear":
         default:
            //  no need to add, Blueprint default is LinearLayoutManager
            break;
      }

      // others
      if (prefs.getBoolean("load_more", false)) {
         b.addDecorator(LoadMoreDecorator.class);
      }

      if (prefs.getBoolean("swipe_to_refresh", false)) {
         b.addDecorator(SwipeToRefreshDecorator.class);
      }

      i.putExtra(KEY.BUILDER, b);
      startActivity(i);
      finish();
   }

   private String addPhotoOptions(DecoratedAct.Builder b) {
      b.addDecorator(PhotoListInstigator.class).addDecorator(PhotoRequestInstigator.class);
      String temp;

      temp = prefs.getString("photo_adapter", "Dummy");
      switch (temp) {
         case "Grid":
            b.addDecorator(PhotoImageAdapterDecorator.class);
            break;
         case "Card":
            b.addDecorator(PhotoCardAdapterDecorator.class);
            break;
         default:
         case "Dummy":
            // dummy is default
            break;
      }

      temp = prefs.getString("photo_endpoints", "Popular");
      switch (temp) {
         case "From User":
            return EyeEm.PHOTOS_FROM_USER("18856434");
         case "From Album":
            return EyeEm.PHOTOS_FROM_ALBUM("72762");
         case "Popular":
         default:
            return EyeEm.PHOTOS_POPULAR;
      }
   }

   private String addUserOptions(DecoratedAct.Builder b) {
      b.addDecorator(UserListInstigator.class).addDecorator(UserRequestInstigator.class);
      return null;
   }
}
