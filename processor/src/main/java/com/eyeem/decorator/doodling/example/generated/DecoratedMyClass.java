package com.eyeem.decorator.doodling.example.generated;

import com.eyeem.decorator.doodling.example.decorated_interface.MyInterface;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by budius on 29.10.15.
 * <p/>
 * `implements MyInterface` copied from `MyClass`,
 * if `MyClass` was `extends Something`, here it would be `extends Something` too.
 */
public class DecoratedMyClass implements MyInterface {

   protected Decorators decorators;

   protected final void onInitDecorator(Decorators.Builder builder) {
      try {
         decorators = (Decorators) builder.build();
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (InstantiationException e) {
         e.printStackTrace();
      }
      decorators.initDecorator(this);
   }

   protected final void onDestroyDecorator() {
      decorators.destroyDecorator();
   }

   @Override public void method1() {
      decorators.method1();
   }

   @Override public void method2(String val) {
      decorators.method2(val);


      // all the `ExtraCalls` interface are ready to be called directly on the Decorators
      decorators.method3("Hello World", 2);
   }

   @Override public List<String> createList(int val1, int val2) {
      return decorators.createList(val1, val2);
   }

   @Override public boolean isTrue() {
      return decorators.isTrue();
   }
}
