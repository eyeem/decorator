package com.eyeem.decorator.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * Created by vishna on 24/07/15.
 */
public class Utils {
   public static PackageElement getPackage(Element type) {
      while (type.getKind() != ElementKind.PACKAGE) {
         type = type.getEnclosingElement();
      }
      return (PackageElement) type;
   }

   public static TypeElement getClass(Element type) {
      while (type.getKind() != ElementKind.CLASS) {
         type = type.getEnclosingElement();
      }
      return (TypeElement) type;
   }

   public static ExecutableElement getMethod(Element type) {
      return (ExecutableElement) type;
   }
}
