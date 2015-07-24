package com.eyeem.decorator.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;

/**
 * Created by vishna on 24/07/15.
 */
public class Generator {

   ProcessingEnvironment processingEnv;
   DecoratedClassDefinition definition;

   /* package */ Generator(ProcessingEnvironment processingEnv, DecoratedClassDefinition definition) {
      this.processingEnv = processingEnv;
      this.definition = definition;
   }

   /* package */ void generate() {
      MethodSpec main = MethodSpec.methodBuilder("main")
         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
         .returns(void.class)
         .addParameter(String[].class, "args")
         .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
         .build();

      String classToDecorate = definition.getSimpleClassName(); // FIXME hardcoded
      String packageName = definition.getPackageName(); // FIXME hardcoded

      // 1. generate Decorator
      TypeSpec decoratorSpec = TypeSpec.classBuilder(classToDecorate + "$$Decorator")
         .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
         .addMethod(main)
         .build();

      // TODO figure out shared methods and instigation methods
      // TODO generate instigation classes
      // TODO basic utility methods:
      //   - Decorator.self()

      JavaFile decoratorJavaFile = JavaFile.builder(packageName, decoratorSpec)
         .build();

      writeJavaFile(decoratorJavaFile, packageName, classToDecorate, "Decorator");

      // 2. generate Decorators (a container for $$Decorator instances)
      TypeName decoratorsSuperclass = ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(packageName, classToDecorate + "$$Decorator"));

      // TODO basic utility methods:
      //   - getFirstDecoratorOfType(Class clazz)
      //   - hasType(Class clazz)
      // .etc
      // TODO override appropriate methods from decorated methods and loop through decorators there

      TypeSpec decoratorsSpec = TypeSpec.classBuilder(classToDecorate + "$$Decorators")
         .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
         .superclass(decoratorsSuperclass)
         .addMethod(main)
         .build();

      JavaFile decoratorsJavaFile = JavaFile.builder(packageName, decoratorsSpec)
         .build();

      writeJavaFile(decoratorsJavaFile, packageName, classToDecorate, "Decorators");

      // TODO 3. generate "DecoratedFragment$$Decorated"
      //   - needs decorators field (so that we can bind/unbind) with the instance
      //   - all decorated methods must get overridden and execute appropriate .decorator methods
      //   - BindInterface so that we can provide arguments for the @Bind interface.
      // FIXME we should override all default constructors, otherwise compilation will fail

      TypeSpec decoratedSpec = TypeSpec.classBuilder(classToDecorate + "$$Decorated")
         .addModifiers(Modifier.PUBLIC)
         .superclass(ClassName.get(packageName, classToDecorate))
         .addMethod(main)
         .build();

      JavaFile decoratedJavaFile = JavaFile.builder(packageName, decoratedSpec)
         .build();

      writeJavaFile(decoratedJavaFile, packageName, classToDecorate, "Decorated");
   }

   private void writeJavaFile(JavaFile javaFile, String packageName, String classToDecorate, String component) {
      try {
         JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + classToDecorate + "$$" + component);
         Writer writer = file.openWriter();
         javaFile.writeTo(writer);
         writer.close();
      } catch (IOException e) {
         // FIXME we try to write twice to the same file, not very smart
         e.printStackTrace();
      }
   }
}
