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

   public static String capitalize(String name) {
      if (name != null && name.length() != 0) {
         char[] chars = name.toCharArray();
         chars[0] = Character.toUpperCase(chars[0]);
         return new String(chars);
      } else {
         return name;
      }
   }

   public static String selectNonEmpty(String s1, String s2) {
      if (s1 != null && s1.length() > 0) {
         return s1;
      } else {
         return s2;
      }
   }

}
