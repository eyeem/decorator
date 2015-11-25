package com.eyeem.decorator.processor;

import com.eyeem.decorator.base_classes.AbstractDecorator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;

/**
 * Created by budius on 23.11.15.
 */
public class GeneratorDecorator implements Generator {

   private static final String ID = "Decorator";
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
      TypeSpec decoratorClass = decoratorClassBuilder.build();

      String packageName = def.generatingClassPackage.getQualifiedName().toString();
      String fullName = def.getFullyQualifiedClassNameFor(ID);
      JavaFile javaFile = JavaFile.builder(
         packageName,
         decoratorClass).build();
      JavaFileObject file = null;
      try {
         file = processingEnv.getFiler().createSourceFile(fullName);
         Writer writer = file.openWriter();
         javaFile.writeTo(writer);
         writer.close();
      } catch (IOException e) {
         log.e("Failed to write to " + fullName + ". " + e.getMessage());
      }
   }

   private static void addMethodsToClassBuilder(TypeSpec.Builder typeBuilder, List<DecoratorDef.MethodDef> methods) {

      for (DecoratorDef.MethodDef m : methods) {
         // clone the method signature by "overriding it"
         MethodSpec.Builder methodSpecBuilder = GeneratorUtils.buildEmptyMethod(m);

         // if `void` just create empty method
         if (m.returnsVoid()) {
            typeBuilder.addMethod(methodSpecBuilder.build());
         }

         // if `boolean` return false
         else if (m.returnsBoolean()) {
            typeBuilder.addMethod(methodSpecBuilder.addStatement("return false").build());
         }

         // if `Object` or primitive create interface for it
         else {
            methodSpecBuilder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
            typeBuilder.addType(
               TypeSpec.interfaceBuilder("Instigate" + Utils.capitalize(m._method.getSimpleName().toString()))
                  .addModifiers(Modifier.PUBLIC)
                  .addMethod(methodSpecBuilder.build())
                  .build());
         }
      }
   }

   private static void addMethodsToInterfaceBuilder(TypeSpec.Builder typeBuilder, List<DecoratorDef.MethodDef> methods) {
      for (DecoratorDef.MethodDef m : methods) {
         MethodSpec.Builder methodSpecBuilder = GeneratorUtils.buildEmptyMethod(m);
         methodSpecBuilder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);
         typeBuilder.addMethod(methodSpecBuilder.build());
      }
   }
}

