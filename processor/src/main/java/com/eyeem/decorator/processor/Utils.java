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

   public static TypeElement getEnclosingClass(Element type) {
      while (type.getKind() != ElementKind.CLASS) {
         type = type.getEnclosingElement();
      }
      return (TypeElement) type;
   }

   public static ExecutableElement getMethod(Element element) {
      return (ExecutableElement) element;
   }

   public static TypeElement getInterface(Element element) {
      return (TypeElement) element;
   }


   public static boolean isMethod(Element type) {
      return type.getKind() == ElementKind.METHOD;
   }

   public static boolean isClass(Element type) {
      return type.getKind() == ElementKind.CLASS;
   }

   public static boolean isInterface(Element type) {
      return type.getKind() == ElementKind.INTERFACE;
   }

   public static String capitalize(String name) {
      if (name != null && name.length() != 0) {
         char[] chars = name.toCharArray();
         chars[0] = Character.toUpperCase(chars[0]);
         return new String(chars);
      } else {
         return name;
      }
   }

}
