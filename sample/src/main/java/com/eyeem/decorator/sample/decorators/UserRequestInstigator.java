package com.eyeem.decorator.sample.decorators;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.eyeem.decorator.sample.Deco;
import com.eyeem.decorator.sample.KEY;
import com.eyeem.decorator.sample.data.api.EyeEm;
import com.eyeem.decorator.sample.data.callbacks.UserCallback;
import com.eyeem.decorator.sample.util.PerformanceLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by budius on 11.07.16.
 */
public class UserRequestInstigator extends Deco implements Deco.RequestInstigator {

   private String url;
   private static final Map<String, String> QUERY;

   static {
      QUERY = new HashMap<>();
      QUERY.put("detailed", "1");
   }

   @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
      if (savedInstanceState == null) {
         url = getDecorated().getIntent().getStringExtra(KEY.URL);
         PerformanceLog.start(url);
         EyeEm.get().getUsers(url, 0, QUERY).enqueue(new UserCallback());
      }
   }

   @Override public String getRequestId() {
      return url;
   }

   @Override public void reload() {
      if (EyeEm.requests().isExecuting(url)) return;
      PerformanceLog.start(url);
      EyeEm.get().getUsers(url, 0, QUERY).enqueue(new UserCallback());
   }

   @Override public void loadMore() {
      if (EyeEm.requests().isExecuting(url)) return;
      PerformanceLog.start(url);
      List l = getDecorators().getList();
      EyeEm.get().getUsers(url, l == null ? 0 : l.size(), QUERY).enqueue(new UserCallback());
   }
}
