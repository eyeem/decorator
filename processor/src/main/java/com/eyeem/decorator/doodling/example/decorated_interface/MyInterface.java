package com.eyeem.decorator.doodling.example.decorated_interface;

import com.eyeem.decorator.annotation.Decorate;

import java.util.List;

/**
 * Created by budius on 13.10.15.
 * <p/>
 * Example on how a final user would create a decorator based on an interface
 */
@Decorate
public interface MyInterface {

   public void method1();

   public void method2(String val);

   /**
    * The annotation processor must be smart enough to recognize a method have return value
    * and instigate it instead of decorate
    *
    * @param val1
    * @param val2
    * @return
    */
   public List<String> createList(int val1, int val2);

   public boolean isTrue();

   /**
    * sub interfaces should be copied to the generated class.
    * the interface methods should be decorated/instigated appropriately.
    */
   public interface ExtraCall {
      public void method3(String val1, int val2);
   }
}
