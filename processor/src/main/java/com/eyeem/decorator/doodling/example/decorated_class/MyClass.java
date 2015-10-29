package com.eyeem.decorator.doodling.example.decorated_class;

import com.eyeem.decorator.annotation.Decorate;
import com.eyeem.decorator.doodling.example.decorated_interface.MyInterface;

import java.util.HashMap;
import java.util.List;

/**
 * Created by budius on 13.10.15.
 * <p/>
 * Example on how a final user would create a decorator based on an class.
 * <p/>
 * Is it possible from code generation to delete a class?
 * For example, after we generated DecoratedMyClass.java we can delete MyClass.class
 */
public class MyClass implements MyInterface /* re-using the interface, generated should be the same */ {

   @Decorate
   @Override public void method1() {/**/}

   @Decorate
   @Override public void method2(String val) {/**/}

   /**
    * The annotation processor must be smart enough to recognize a method have return value
    * and instigate it instead of decorate.
    * <p/>
    * Instigate are automatically placed in a separate interface.
    *
    * @param val1
    * @param val2
    * @return
    */
   @Decorate
   @Override public List<String> createList(int val1, int val2) {
      return null;
   }

   /**
    * Special case. Methods that return boolean can do a loop.
    *
    * @return
    */
   @Decorate
   @Override public boolean isTrue() {
      return false;
   }

   /**
    * sub interfaces should be copied to the generated class.
    * the interface methods should be decorated/instigated appropriately.
    */
   @Decorate
   public interface ExtraCall {
      public void method3(String val1, int val2);
   }
}
