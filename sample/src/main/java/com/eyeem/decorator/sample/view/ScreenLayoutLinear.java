package com.eyeem.decorator.sample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.eyeem.decorator.sample.mortar.DecoratorService;

import static com.eyeem.decorator.sample.mortar.DecoratorService.SERVICE_NAME;

/**
 * Created by budius on 08.12.15.
 */
public class ScreenLayoutLinear extends LinearLayout {

   private Presenter presenter;

   //region default constructors
   public ScreenLayoutLinear(Context context) {
      super(context);
      initDecorators(context);
      LayoutInflater.from(getContext()).inflate(this.presenter.getLayoutId(), this);
      presenter.onViewInflated(this);
   }

   public ScreenLayoutLinear(Context context, AttributeSet attrs) {
      super(context, attrs);
      initDecorators(context);
   }

   public ScreenLayoutLinear(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      initDecorators(context);
   }

   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
   public ScreenLayoutLinear(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      initDecorators(context);
   }

   @Override protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      presenter.onViewInflated(this); // XML layout only
   }

   private void initDecorators(Context context) {
      if (isInEditMode()) return;
      if (presenter != null) return;
      @SuppressWarnings({"ResourceType", "WrongConstant"})
      DecoratorService ds = (DecoratorService) context.getSystemService(SERVICE_NAME);
      presenter = new Presenter();
      presenter.bind(ds.getBuilder());
   }
   //endregion

   @Override protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      if (presenter != null) {
         presenter.takeView(this);
      }
   }

   @Override protected void onDetachedFromWindow() {
      if (presenter != null) {
         presenter.dropView(this);
      }
      super.onDetachedFromWindow();
   }
}
