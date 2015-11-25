package com.eyeem.decorator.doodling.example.generated;

import com.eyeem.decorator.base_classes.AbstractDecorator;

import java.util.List;

/**
 * Created by budius on 29.10.15.
 */
public abstract class Decorator extends AbstractDecorator<DecoratedMyClass> {

   public void method1() {/**/}

   public void method2(String val) {/**/}


   public boolean isTrue() {
      return false;
   }


   public interface ExtraCall {
      public void method3(String val1, int val2);
   }

   public interface InstigateCreateList {
      public List<String> createList(int val1, int val2);
   }
}
