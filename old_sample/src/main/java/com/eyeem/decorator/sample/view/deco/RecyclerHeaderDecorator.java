package com.eyeem.decorator.sample.view.deco;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.eyeem.decorator.sample.mortar.MortarApplication;
import com.eyeem.decorator.sample.view.Deco;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

import mortar.MortarScopeDevHelper;

/**
 * Created by budius on 16.12.15.
 */
public class RecyclerHeaderDecorator extends Deco implements View.OnClickListener {

   @Override public void onSetupRecycler(RecyclerView recyclerView, WrapAdapter wrapAdapter) {

      Button button = new Button(recyclerView.getContext());
      button.setOnClickListener(this);
      button.setText("Dump Mortar Hierarchy");

      int dp16 = (int) (recyclerView.getContext().getResources().getDisplayMetrics().density * 16);
      button.setMinHeight(dp16 * 4);
      button.setPadding(dp16, dp16, dp16, dp16);
      wrapAdapter.addHeader(button);
   }

   @Override public void onClick(View v) {
      String hierarchy = MortarScopeDevHelper.scopeHierarchyToString(
            ((MortarApplication) v.getContext().getApplicationContext()).rootScope);
      Log.d("Budius", hierarchy);
   }
}
