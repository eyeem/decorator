package com.eyeem.decorator.sample.data.api;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by budius on 05.07.16.
 */
public class RequestObservableList implements Interceptor {

   private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());
   private List<String> running = new ArrayList<>();
   private WeakHashMap<Listener, String> listeners = new WeakHashMap<>(3);
   private WeakHashMap<Listener, String> listenersToBeRemoved = new WeakHashMap<>(3);
   private boolean isDispatchingFinished = false;

   /* package */ RequestObservableList() {/**/}

   @Override public Response intercept(Interceptor.Chain chain) throws IOException {
      HttpUrl url = chain.request().url();
      String id = url.encodedPath();
      int offset = 0;
      boolean success = false;
      try {
         offset = Integer.parseInt(url.queryParameter("offset"));
      } catch (NumberFormatException e) {/**/}

      try {
         running.add(id);
         Response r = chain.proceed(chain.request());
         success = true;
         return r;
      } finally {
         running.remove(id);
         dispatchFinished(id, success, offset);
      }
   }

   private void dispatchFinished(String url, boolean success, int offset) {
      UI_HANDLER.post(new OnFinishDispatcher(url, success, offset));
   }

   public boolean isExecuting(String url) {
      return url != null && running.contains(url);
   }

   public void addListener(Listener l, String url) {
      if (!listeners.containsKey(l)) {
         listeners.put(l, url);
      }
   }

   public void removeListener(Listener l) {
      if (isDispatchingFinished) {
         // if called while looping `listeners` we get ConcurrentModificationException
         // so we postpone until right after finish looping
         listenersToBeRemoved.put(l, listeners.get(l));
      } else {
         listeners.remove(l);
      }
   }

   /**
    * Interface to receive request completed
    */
   public interface Listener {
      void onRequestFinished(boolean success, int offset);
   }

   /**
    * We use this class to dispatch the the `onRequestFinished` on the UI thread
    */
   private class OnFinishDispatcher implements Runnable {

      private final String id;
      private final boolean success;
      private final int offset;

      private OnFinishDispatcher(String id, boolean success, int offset) {
         this.id = id;
         this.success = success;
         this.offset = offset;
      }

      @Override public void run() {
         isDispatchingFinished = true;
         for (Map.Entry<Listener, String> e : listeners.entrySet()) {
            if (id.equals(e.getValue())) {
               e.getKey().onRequestFinished(success, offset);
            }
         }
         isDispatchingFinished = false;
         for (Listener l : listenersToBeRemoved.keySet()) {
            removeListener(l);
         }
      }
   }
}