package com.eyeem.decorator.base_classes;

/**
 * Created by budius on 29.10.15.
 */
public class AbstractDecorator<BASE> {

   protected BASE decorated;

   protected AbstractDecorators decorators;

   protected void bind() {/**/}

   protected void unbind() {/**/}

   protected BASE getDecorated() {
      return decorated;
   }
}
