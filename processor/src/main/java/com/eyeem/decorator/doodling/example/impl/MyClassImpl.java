package com.eyeem.decorator.doodling.example.impl;

import com.eyeem.decorator.doodling.example.generated.DecoratedMyClass;
import com.eyeem.decorator.doodling.example.generated.Decorators;

/**
 * Created by budius on 29.10.15.
 * Implementation of the class
 */
public class MyClassImpl extends DecoratedMyClass {

   public static Decorators.Builder getHelloWorldBuilder() {
      return Decorators.newBuilder()
         .addDecorator(HelloDecorator.class)
         .addDecorator(WorldDecorator.class);
   }

   public MyClassImpl() {
      onInitDecorator(getHelloWorldBuilder());
   }

   @Override protected void finalize() throws Throwable {
      onDestroyDecorator();
      super.finalize();
   }
}
