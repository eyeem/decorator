package com.eyeem.decorator.processor;

import com.eyeem.decorator.base_classes.AbstractDecorator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

import static com.eyeem.decorator.processor.GeneratorUtils.buildEmptyMethod;
import static com.eyeem.decorator.processor.GeneratorUtils.getInterfaceName;
import static com.eyeem.decorator.processor.GeneratorUtils.writeClass;

/**
 * Created by budius on 23.11.15.
 */
public class GeneratorDecorator implements Generator {

   private static final List<Modifier> PUBLIC_ABSTRACT = Arrays.asList(Modifier.PUBLIC, Modifier.ABSTRACT);
   private final Log log;

   public GeneratorDecorator(Log log) {
      this.log = log;
   }

   /* package */
   static String getClassName(Data data) {
      return data.decoratorName;
   }

   @Override public void generate(ProcessingEnvironment processingEnv, Data def) {

      // create class
      TypeSpec.Builder decoratorClassBuilder = TypeSpec.classBuilder(getClassName(def))
            .superclass(ParameterizedTypeName.get(
                  ClassName.get(AbstractDecorator.class),
                  TypeName.get(def.generatingClass.getSuperclass())))
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

      // add getDecorators()
      String decoratorsSimpleName = GeneratorDecorators.getClassName(def);
      MethodSpec.Builder getDecorator = MethodSpec.methodBuilder("getDecorators")
            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
            .returns(ClassName.get(def.getPackageName(), decoratorsSimpleName))
            .addStatement("return ($L)decorators", decoratorsSimpleName);
      decoratorClassBuilder.addMethod(getDecorator.build());

      // add methods
      addMethodsToClassBuilder(decoratorClassBuilder, def.methods);

      // add interfaces
      for (Data.InterfaceData interfaceData : def.interfaces) {

         // create interface
         TypeSpec.Builder interfaceBuilder =
               TypeSpec.interfaceBuilder(interfaceData._interface.getSimpleName().toString())
                     .addModifiers(Modifier.PUBLIC);

         for (TypeMirror typeMirror : interfaceData._interface.getInterfaces()) {
            interfaceBuilder.addSuperinterface(TypeName.get(typeMirror));
         }

         // add methods
         addMethodsToInterfaceBuilder(interfaceBuilder, interfaceData.methods);

         // add to class
         decoratorClassBuilder.addType(interfaceBuilder.build());
      }

      // write it to disk
      writeClass(
            log,
            processingEnv,
            decoratorClassBuilder,
            def.getPackageName(),
            def.getFullyQualifiedClassNameFor(getClassName(def))
      );
   }

   private static void addMethodsToClassBuilder(TypeSpec.Builder typeBuilder, List<Data.MethodData> methods) {

      for (Data.MethodData m : methods) {

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

   private static void addMethodsToInterfaceBuilder(TypeSpec.Builder typeBuilder, List<Data.MethodData> methods) {
      for (Data.MethodData m : methods) {
         typeBuilder.addMethod(buildEmptyMethod(m, PUBLIC_ABSTRACT).build());
      }
   }
}

