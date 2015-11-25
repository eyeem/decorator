package com.eyeem.decorator.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by budius on 23.11.15.
 */
public class DecoratorDef {

   //region header
   /**
    * Header:
    * - package name
    * - non-composable list
    * - static new Builder()
    * - override getNonComposable
    */
   /**
    * The class that was annotated and created this definition
    */
   TypeElement generatingClass;
   /**
    * The package in which the generating class (and our generated classes)
    * will be put into
    */
   PackageElement generatingClassPackage;

   /**
    * Simple name for the super class from `generatingClass`
    */
   public String superSimpleName;

   String getPackageName() {
      return generatingClassPackage.getQualifiedName().toString();
   }

   String getFullyQualifiedClassNameFor(String id) {
      return getPackageName() + "." + getSimpleClassNameFor(id);
   }

   String getSimpleClassNameFor(String id) {
      return superSimpleName + "$$" + id;
   }
   //endregion

   /**
    * Body:
    * - List of methods
    * - List of Interfaces
    */
   final ArrayList<MethodDef> methods = new ArrayList<>();

   final ArrayList<InterfaceDef> interfaces = new ArrayList<>();

   //region methods

   /**
    * Method
    */
   public static class MethodDef {
      final ExecutableElement _method;
      final TypeMirror returnType;

      /**
       * Flags that this method belongs to an interface explicitly declared by the user.
       * Auto-generated interfaces, such as the ones from methods with return values, will be `false`
       */
      boolean belongsToExplicitInterface = false;

      public MethodDef(ExecutableElement method) {
         _method = method;
         this.returnType = method.getReturnType();
      }

      public boolean returnsPrimitive() {
         return returnType.getKind().isPrimitive();
      }

      public boolean returnsVoid() {
         return (returnType instanceof NoType && returnType.getKind() == TypeKind.VOID);
      }

      public boolean returnsBoolean() {
         return returnsPrimitive() && returnType.getKind() == TypeKind.BOOLEAN;
      }
   }
   //endregion

   //region interfaces

   /**
    * Interface
    */
   public static class InterfaceDef {
      final TypeElement _interface;
      final ArrayList<MethodDef> methods = new ArrayList<>();
      boolean isInstigate = false;

      public InterfaceDef(TypeElement anInterface) {
         _interface = anInterface;
      }
   }

   public void log(Log log) {
      log.i("Originating class: " + generatingClass.getQualifiedName().toString());
      log.i(":Methods: " + methods.size());
      for (MethodDef m : methods) {
         logMethod(log, m, "::");
      }
      log.i(":Interfaces: " + interfaces.size());
      for (InterfaceDef i : interfaces) {
         log.i("::" + i._interface.getQualifiedName());
         for (MethodDef m : i.methods) {
            logMethod(log, m, ":::");
         }
      }
   }
   //endregion

   //region log
   private void logMethod(Log log, MethodDef m, String prefix) {

      // method modifiers
      StringBuilder sb = new StringBuilder();
      Set<Modifier> modifierSet = m._method.getModifiers();
      for (Modifier modifier : modifierSet) {
         if (sb.length() > 0) sb.append(" ");
         sb.append(modifier.name());
      }
      sb.append(" ");

      // return type
      sb.append(m.returnType.getKind().name());
      sb.append(" ");

      // method name
      sb.append(m._method.getSimpleName());

      // parameters
      int currentSize = sb.length();
      List<? extends VariableElement> params = m._method.getParameters();

      if (params.size() > 0) {
         sb.append("(");
         for (VariableElement variableElement : params) {
            if (sb.length() > currentSize + 1) sb.append(", ");

            // parameter modifiers
            modifierSet = variableElement.getModifiers();
            for (Modifier modifier : modifierSet) {
               sb.append(modifier.name());
               sb.append(" ");
            }

            // parameter type
            sb.append(variableElement.asType().toString());
            sb.append(" ");

            // parameter name
            sb.append(variableElement.getSimpleName());
         }
         sb.append(")");
      }
      log.i(prefix + sb.toString());
   }
   //endregion
}
