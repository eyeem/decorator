package com.eyeem.decorator.doodling.example.generated;

import com.eyeem.decorator.pre_classes.AbstractDecorators;

import java.util.List;

/**
 * Created by budius on 29.10.15.
 */
public class Decorators extends AbstractDecorators<DecoratedMyClass> {

   protected Decorators(Builder<DecoratedMyClass> builder) throws InstantiationException, IllegalAccessException {
      super(builder);
   }

   //region method callbacks
   public void method1() {
      for (int i = 0; i < size; i++) {
         ((Decorator) decorators.get(i)).method1();
      }
   }

   public void method2(String val) {
      for (int i = 0; i < size; i++) {
         ((Decorator) decorators.get(i)).method2(val);
      }
   }

   public boolean isTrue() {
      for (int i = 0; i < size; i++) {
         if (((Decorator) decorators.get(i)).isTrue()) {
            return true;
         }
      }
      return false;
   }

   public void method3(String val1, int val2) {
      for (int i = 0; i < size; i++) {
         ((Decorator.ExtraCall) decorators.get(i)).method3(val1, val2);
      }
   }

   public List<String> createList(int val1, int val2) {
      Decorator.InstigateCreateList i = getInstigator(Decorator.InstigateCreateList.class);
      if (i != null) {
         return i.createList(val1, val2);
      } else {
         return null;
      }
   }
   //endregion

   //region setup
   private static final Class[] NON_COMPOSABLE = {
      Decorator.InstigateCreateList.class
   };

   public static Builder<DecoratedMyClass> newBuilder() {
      return new Builder<>(Decorators.class);
   }

   @Override protected Class[] getNonComposable() {
      return NON_COMPOSABLE;
   }
   //endregion
}
