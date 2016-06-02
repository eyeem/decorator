package com.eyeem.decorator.sample.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eyeem.decorator.sample.R;
import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

/**
 * Created by budius on 27.11.15.
 */
public class HeaderType1Instigator extends AppCompatActivityDecorator implements
      AppCompatActivityDecorator.SetupRecyclerDecorator {

   View header;

   @Override public void setupRecycler(RecyclerView recyclerView, WrapAdapter wrapAdapter) {

      String title = decorated.getIntent().getStringExtra("title");
      if (title == null) {
         title = "Hello World";
      }

      header = LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.header1, null);
      ((TextView) header.findViewById(R.id.txt1)).setText(title);
      wrapAdapter.addHeader(header);
   }
}
