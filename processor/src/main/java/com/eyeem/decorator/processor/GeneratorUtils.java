package com.eyeem.decorator.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

/**
 * Created by budius on 23.11.15.
 */
public class GeneratorUtils {

   /**
    * Create empty builder for an empty method based of the given method definition.
    * Code copied from {@link MethodSpec#overriding(ExecutableElement)} and removed the annotation.
    *
    * @param methodDef
    * @return
    */
   public static MethodSpec.Builder buildEmptyMethod(DecoratorDef.MethodDef methodDef) {
      ExecutableElement method = methodDef._method;
      Set<Modifier> modifiers = method.getModifiers();
      if (modifiers.contains(Modifier.PRIVATE)
         || modifiers.contains(Modifier.FINAL)
         || modifiers.contains(Modifier.STATIC)) {
         throw new IllegalArgumentException("cannot override method with modifiers: " + modifiers);
      }

      String methodName = method.getSimpleName().toString();
      MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName);

      modifiers = new LinkedHashSet<>(modifiers);
      modifiers.remove(Modifier.ABSTRACT);
      methodBuilder.addModifiers(modifiers);

      for (TypeParameterElement typeParameterElement : method.getTypeParameters()) {
         TypeVariable var = (TypeVariable) typeParameterElement.asType();
         methodBuilder.addTypeVariable(TypeVariableName.get(var));
      }

      methodBuilder.returns(TypeName.get(method.getReturnType()));

      List<? extends VariableElement> parameters = method.getParameters();
      for (VariableElement parameter : parameters) {
         TypeName type = TypeName.get(parameter.asType());
         String name = parameter.getSimpleName().toString();
         Set<Modifier> parameterModifiers = parameter.getModifiers();
         ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(type, name)
            .addModifiers(parameterModifiers.toArray(new Modifier[parameterModifiers.size()]));
         for (AnnotationMirror mirror : parameter.getAnnotationMirrors()) {
            parameterBuilder.addAnnotation(AnnotationSpec.get(mirror));
         }
         methodBuilder.addParameter(parameterBuilder.build());
      }
      methodBuilder.varargs(method.isVarArgs());

      for (TypeMirror thrownType : method.getThrownTypes()) {
         methodBuilder.addException(TypeName.get(thrownType));
      }

      return methodBuilder;
   }

}
