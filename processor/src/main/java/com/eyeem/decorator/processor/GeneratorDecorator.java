package com.eyeem.decorator.processor;

import com.eyeem.decorator.base_classes.AbstractDecorator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

import static com.eyeem.decorator.processor.GeneratorUtils.*;

/**
 * Created by budius on 23.11.15.
 */
public class GeneratorDecorator implements Generator {

   private static final List<Modifier> PUBLIC_ABSTRACT = Arrays.asList(Modifier.PUBLIC, Modifier.ABSTRACT);
   public static final String ID = "Decorator";
   private final Log log;

   public GeneratorDecorator(Log log) {
      this.log = log;
   }

   @Override public void generate(ProcessingEnvironment processingEnv, DecoratorDef def) {

      // create class
      TypeSpec.Builder decoratorClassBuilder = TypeSpec.classBuilder(def.getSimpleClassNameFor(ID))
         .superclass(ParameterizedTypeName.get(
            ClassName.get(AbstractDecorator.class),
            TypeName.get(def.generatingClass.getSuperclass())))
         .addModifiers(Modifier.PUBLIC);

      // add methods
      addMethodsToClassBuilder(decoratorClassBuilder, def.methods);

      // add interfaces
      for (DecoratorDef.InterfaceDef interfaceDef : def.interfaces) {

         // create interface
         TypeSpec.Builder interfaceBuilder =
            TypeSpec.interfaceBuilder(interfaceDef._interface.getSimpleName().toString())
               .addModifiers(Modifier.PUBLIC);

         // add methods
         addMethodsToInterfaceBuilder(interfaceBuilder, interfaceDef.methods);

         // add to class
         decoratorClassBuilder.addType(interfaceBuilder.build());
      }

      // write it to disk
      writeClass(
         log,
         processingEnv,
         decoratorClassBuilder,
         def.getPackageName(),
         def.getFullyQualifiedClassNameFor(ID)
      );
   }

   private static void addMethodsToClassBuilder(TypeSpec.Builder typeBuilder, List<DecoratorDef.MethodDef> methods) {

      for (DecoratorDef.MethodDef m : methods) {

         // if `void` just create empty method
         if (m.returnsVoid()) {
            typeBuilder.addMethod(buildEmptyMethod(m).build());
         }

         // if `boolean` return false
         else if (m.returnsBoolean()) {
            typeBuilder.addMethod(buildEmptyMethod(m).addStatement("return false").build());
         }

         // if `Object` or primitive create interface for it
         else {
            typeBuilder.addType(
               TypeSpec.interfaceBuilder(getInterfaceName(m))
                  .addModifiers(Modifier.PUBLIC)
                  .addMethod(buildEmptyMethod(m, PUBLIC_ABSTRACT).build())
                  .build());
         }
      }
   }

   private static void addMethodsToInterfaceBuilder(TypeSpec.Builder typeBuilder, List<DecoratorDef.MethodDef> methods) {
      for (DecoratorDef.MethodDef m : methods) {
         typeBuilder.addMethod(buildEmptyMethod(m, PUBLIC_ABSTRACT).build());
      }
   }
}

