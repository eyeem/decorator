package com.eyeem.decorator.testmodule;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.AnimatorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;

import com.eyeem.decorator.annotation.Decorate;

/**
 * Created by budius on 8/26/16.
 * Not a real test in the sense of Unit or Espresso testing.
 * But in this module we can generate all weird edge cases of code-generation "manually".
 * The sample wouldn't necessarily use all edge cases such as generics or Annotations
 * With this we make sure that generated code is compiled and can inspect generated code for correctness.
 */
@Decorate(
      decorator = "TestDecorator",
      decorators = "TestDecorators",
      decoratored = "TestDecorated"
)
public class TestBlueprint extends Fragment {

   //region test method without parameters
   // test void method without parameters
   public void voidMethod_NoParameters() {

   }

   public int primitiveMethod_NoParameters() {
      return 10; // test default value
   }

   // test boolean loop
   public boolean booleanMethod_NoParameters() {
      return false;
   }

   public View objectMethod_NoParameters() {
      return new View(getActivity()); // test default value
   }
   //endregion

   //region test methods with parameters
   // test void method without parameters
   public void voidMethod_WithParameters(Context context) {

   }

   public int intMethod_WithParameters(Resources resources) {
      return 10; // test default value
   }

   // test boolean loop
   public boolean booleanMethod_WithParameters(View view) {
      return false;
   }

   public View objectMethod_WithParameters(LayoutInflater layoutInflater) {
      return new View(getActivity()); // test default value
   }
   //endregion

   //region test annotation
   // test overridden method, super call
   @Override public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Deprecated
   @Override public void onAttach(Activity activity) {
      super.onAttach(activity);
   }

   @StringRes
   public int nonVoidMethod_AnnotatedReturnVale() {
      return R.string.app_name;
   }

   public void method_AnnotatedParameters(
         @DrawableRes int drawableId,
         @NonNull View view,
         @Nullable Object someObject) {
   }
   //endregion

   //region test interfaces
   public interface Interface {
      void interfaceVoidMethod(@NonNull Context context);

      @LayoutRes int interfacePrimitiveMethod(Context context);

      @NonNull FragmentManager interfaceObjectMethod(@Nullable View view, @AnimatorRes int val);
   }
   //endregion

   //region test generics
   // TODO: issue #19
   //endregion

}
