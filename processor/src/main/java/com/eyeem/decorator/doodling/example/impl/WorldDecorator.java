package com.eyeem.decorator.doodling.example.impl;

import com.eyeem.decorator.doodling.example.generated.Decorator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by budius on 29.10.15.
 */
public class WorldDecorator extends Decorator implements Decorator.InstigateCreateList {

   @Override public void method1() {
      System.out.println("World method 1");
   }

   @Override public void method2(String val) {
      System.out.println("World method 2: " + val);
   }

   @Override public List<String> createList(int val1, int val2) {
      System.out.println("World createList: " + val1 + "; " + val2);
      return Arrays.asList("Hello", "World", "Decorator");
   }
}
