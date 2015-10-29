package com.eyeem.decorator.doodling.example.impl;

import com.eyeem.decorator.doodling.example.generated.Decorator;

/**
 * Created by budius on 29.10.15.
 */
public class HelloDecorator extends Decorator implements Decorator.ExtraCall {

   @Override protected void initDecorator() {

   }

   @Override public void method1() {
      System.out.println("Hello method 1");
   }

   @Override public void method2(String val) {
      System.out.println("Hello method 2: " + val);
   }

   @Override public void method3(String val1, int val2) {
      System.out.println("Hello optional method 3: " + val1 + "; " + val2);
   }
}
