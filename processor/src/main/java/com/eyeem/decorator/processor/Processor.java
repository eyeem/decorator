package com.eyeem.decorator.processor;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by budius on 21.07.15.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.eyeem.decorator.annotation.Decorate")
public class Processor extends AbstractProcessor {

   private HashMap<String, DecoratedClassDefinition> map = new HashMap<>();

   @Override
   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

      String fullName;
      String methodName;
      DecoratedClassDefinition klazz;
      int numberOfMethods = 0;
      int numberOfClasses = 0;

      //region Identify all the annotated classes we should built for ============================
      for (TypeElement annotation : annotations) {

         log("Processing annotation: " + annotation.getQualifiedName());

         for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

            //log("element: " + element.getKind().toString() + "; " + element.getClass().getCanonicalName());

            ExecutableElement methodElement = getMethod(element);
            PackageElement packageElement = getPackage(element);
            TypeElement typeElement = getClass(element);

            fullName = typeElement.getQualifiedName().toString();

            klazz = map.get(fullName);
            if (klazz == null) {
               klazz = new DecoratedClassDefinition();
               klazz.classElement = typeElement;
               klazz.packageElement = packageElement;
               map.put(fullName, klazz);
               numberOfClasses++;
               log("Adding class: " + fullName);
            }
            log("Adding method: " + methodElement.getSimpleName().toString());
            klazz.decoratedMethods.add(methodElement);
            numberOfMethods++;
         }
      }

      log("Found total of " + numberOfClasses + " classes, with total of " + numberOfMethods + " annotated methods");
      //endregion


      //region Generate the classes from the annotated map
      // TODO: classes generation
      for (DecoratedClassDefinition d : map.values()) {
         log("Generating code for " + d.classElement.getQualifiedName());
      }
      //endregion

      MethodSpec main = MethodSpec.methodBuilder("main")
         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
         .returns(void.class)
         .addParameter(String[].class, "args")
         .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
         .build();

      String classToDecorate = "DecoratedFragment"; // FIXME hardcoded
      String packageName = "com.eyeem.decorator.sample"; // FIXME hardcoded

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

      return true;
   }

   void writeJavaFile(JavaFile javaFile, String packageName, String classToDecorate, String component) {
      try {
         JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + classToDecorate + "$$" + component);
         Writer writer = file.openWriter();
         javaFile.writeTo(writer);
         writer.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private void log(String message) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
   }

   private void err(String message) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
   }

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
