package com.eyeem.decorator.sample.mortar;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.eyeem.decorator.base_classes.AbstractDecorators;
import com.eyeem.decorator.sample.view.Deco;

import java.lang.ref.WeakReference;

import mortar.MortarScope;
import mortar.ViewPresenter;

/**
 * Created by budius on 04.01.16.
 */
public class DecoratorService {

   public static final String SERVICE_NAME = DecoratorService.class.getCanonicalName();

   private static final String KEY = SERVICE_NAME + ".key";

   public static void addBuilder(Intent intent, AbstractDecorators.Builder<ViewPresenter<View>, Deco> builder) {
      intent.putExtra(KEY, builder);
   }

   private AbstractDecorators.Builder<ViewPresenter<View>, Deco> originalBuilder;
   private WeakReference<Activity> weakActivity;

   public DecoratorService(Activity activity) {
      weakActivity = new WeakReference<>(activity);
   }

   DecoratorService(AbstractDecorators.Builder<ViewPresenter<View>, Deco> builder) {
      originalBuilder = builder;
   }

   private boolean lazyInitBuilder() {
      if (originalBuilder != null) return true;
      Activity a = weakActivity.get();
      if (a == null) return false;
      Intent i = a.getIntent();
      if (i != null && i.hasExtra(KEY)) {
         originalBuilder = (AbstractDecorators.Builder<ViewPresenter<View>, Deco>)
            i.getSerializableExtra(KEY);
         return originalBuilder != null;
      }
      return false;
   }

   public AbstractDecorators.Builder<ViewPresenter<View>, Deco> getBuilder() {
      if (lazyInitBuilder()) return originalBuilder.copy();
      else return null;
   }

   public static class WrapContext extends ContextWrapper {

      private final String scopeName;
      private MortarScope decoratorScope;
      private final AbstractDecorators.Builder<ViewPresenter<View>, Deco> builder;

      public WrapContext(Context base, AbstractDecorators.Builder<ViewPresenter<View>, Deco> builder, String scopeName) {
         super(base);
         this.scopeName = scopeName;
         this.builder = builder;
      }

      @Override public Object getSystemService(@NonNull String name) {
         decoratorScope = MortarScope.findChild(getBaseContext(), scopeName);

         if (decoratorScope == null) {
            decoratorScope = MortarScope.buildChild(getBaseContext())
               .withService(DecoratorService.SERVICE_NAME, new DecoratorService(builder))
               .build(scopeName);
         }
         return decoratorScope.hasService(name) ? decoratorScope.getService(name)
            : super.getSystemService(name);
      }
   }

}
