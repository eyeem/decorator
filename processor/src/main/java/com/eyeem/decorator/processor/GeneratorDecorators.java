package com.eyeem.decorator.processor;

import com.eyeem.decorator.base_classes.AbstractDecorators;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

/**
 * Created by budius on 23.11.15.
 */
public class GeneratorDecorators implements Generator {

   private static final String ID = "Decorators";
   private final Log log;

   public GeneratorDecorators(Log log) {
      this.log = log;
   }

   @Override public void generate(ProcessingEnvironment processingEnv, DecoratorDef def) {

      // create class
      TypeSpec.Builder decoratorsClassBuilder = TypeSpec.classBuilder(def.getSimpleClassNameFor(ID))
         .superclass(ParameterizedTypeName.get(
            ClassName.get(AbstractDecorators.class),
            TypeName.get(def.generatingClass.getSuperclass())))
         .addModifiers(Modifier.PUBLIC);

      // create constructor
      MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
         .addModifiers(Modifier.PROTECTED)
         .addParameter(ParameterizedTypeName.get(
            ClassName.get(AbstractDecorators.Builder.class),
            TypeName.get(def.generatingClass.getSuperclass())), "builder")
         .addException(InstantiationException.class)
         .addException(IllegalAccessException.class)
         .addStatement("super(builder)");
      decoratorsClassBuilder.addMethod(constructor.build());

      // add methods
      addMethodsToClassBuilder(decoratorsClassBuilder, def.methods);


   }

   private static void addMethodsToClassBuilder(TypeSpec.Builder typeBuilder, List<DecoratorDef.MethodDef> methods) {
      for (DecoratorDef.MethodDef m : methods) {


      }
   }

   private static MethodSpec.Builder getBaseBuilder(DecoratorDef.MethodDef m) {
      // clone the method signature by "overriding it"
      MethodSpec.Builder methodSpecBuilder = GeneratorUtils.buildEmptyMethod(m)
         .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
      return methodSpecBuilder;
   }
}
