package com.eyeem.decorator.processor;

import com.eyeem.decorator.base_classes.AbstractDecorators;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.Serializable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.ElementFilter;

import static com.eyeem.decorator.processor.GeneratorUtils.buildEmptyConstructor;
import static com.eyeem.decorator.processor.GeneratorUtils.buildEmptyMethod;
import static com.eyeem.decorator.processor.GeneratorUtils.getCommaSeparatedParams;
import static com.eyeem.decorator.processor.GeneratorUtils.writeClass;

/**
 * Created by budius on 23.11.15.
 */
public class GeneratorDecorated implements Generator {

   private final Log log;
   private StringBuilder buffer = new StringBuilder();

   public GeneratorDecorated(Log log) {
      this.log = log;
   }

   /* package */
   static String getClassName(Data data) {
      return data.decoratedName;
   }

   @Override public void generate(ProcessingEnvironment processingEnv, Data def) {

      String className = getClassName(def);
      String decoratorsClassName = GeneratorDecorators.getClassName(def);

      // create class
      TypeSpec.Builder decoratedClassBuilder = TypeSpec.classBuilder(className)
         .superclass(TypeName.get(def.superClass))
         .addModifiers(Modifier.PUBLIC);

      // create constructors matching super
      for (ExecutableElement element : ElementFilter.constructorsIn(processingEnv.getTypeUtils().asElement(def.superClass).getEnclosedElements())) {
         decoratedClassBuilder.addMethod(buildEmptyConstructor(element).build());
      }

      // add decorators variable
      decoratedClassBuilder.addField(FieldSpec
         .builder(ClassName.get(
               def.getPackageName(),
               decoratorsClassName),
            "decorators",
            Modifier.PRIVATE).build());

      // add getDecorators()
      MethodSpec.Builder getDecorator = MethodSpec.methodBuilder("getDecorators")
         .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
         .returns(ClassName.get(def.getPackageName(), decoratorsClassName))
         .addStatement("return ($L)decorators", decoratorsClassName);
      decoratedClassBuilder.addMethod(getDecorator.build());

      // add bind
      decoratedClassBuilder.addMethod(
         MethodSpec.methodBuilder("bind")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addParameter(ClassName.get(def.getPackageName(), decoratorsClassName, "Builder"), "builder")
            .addCode(
               CodeBlock.builder()
                  .beginControlFlow("try")
                  .addStatement("decorators = ($L) builder.build()", decoratorsClassName)
                  .addStatement("decorators.bind(this)")
                  .nextControlFlow("catch(Exception e)")
                  .addStatement("e.printStackTrace()")
                  .endControlFlow()
                  .build()
            )
            .build());

      // add unbind
      decoratedClassBuilder.addMethod(
         MethodSpec.methodBuilder("unbind")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addStatement("decorators.unbind()").build());

      // add annotated methods
      for (Data.MethodData m : def.methods) {
         if (m.returnsVoid()) {
            decoratedClassBuilder.addMethod(getVoidMethod(m));
         } else {
            decoratedClassBuilder.addMethod(getNonVoidMethod(m));
         }
      }

      /*

      public static AbstractDecorators.Builder<AppCompatActivity, AbstractDecoratorAppCompatActivity> builder() {
               return new AbstractDecorators.Builder<>(Decorators$$AppCompatActivity.class);
      }

       */

      // add static builder()
      decoratedClassBuilder.addMethod(MethodSpec.methodBuilder("builder")
         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
         .returns(
            ParameterizedTypeName.get(
               ClassName.get(AbstractDecorators.Builder.class),
               TypeName.get(def.generatingClass.getSuperclass()),
               ClassName.get(def.getPackageName(), GeneratorDecorator.getClassName(def)))
         )
         .addStatement("return new AbstractDecorators.Builder<>($L.class)", decoratorsClassName)
         .build());

      decoratedClassBuilder.addMethod(MethodSpec.methodBuilder("getBuilder")
         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
         .addParameter(TypeName.get(Serializable.class), "serialized")
         .returns(
            ParameterizedTypeName.get(
               ClassName.get(AbstractDecorators.Builder.class),
               TypeName.get(def.generatingClass.getSuperclass()),
               ClassName.get(def.getPackageName(), GeneratorDecorator.getClassName(def))))
         .addStatement(
            "return (AbstractDecorators.Builder<$L, $L>) serialized",
            def.generatingClass.getSuperclass().toString(),
            GeneratorDecorator.getClassName(def))
         .build());

      // write it to disk
      writeClass(
         log,
         processingEnv,
         decoratedClassBuilder,
         def.getPackageName(),
         def.getFullyQualifiedClassNameFor(className)
      );
   }


   private MethodSpec getVoidMethod(Data.MethodData m) {
      return addOverrideIfNecessary(buildEmptyMethod(m), m)
         .addStatement("decorators.$L($L)",
            m._method.getSimpleName(),
            getCommaSeparatedParams(m, buffer)).build();
   }

   private MethodSpec getNonVoidMethod(Data.MethodData m) {
      return addOverrideIfNecessary(buildEmptyMethod(m), m)
         .addStatement("return decorators.$L($L)",
            m._method.getSimpleName(),
            getCommaSeparatedParams(m, buffer)).build();
   }

   private MethodSpec.Builder addOverrideIfNecessary(MethodSpec.Builder builder, Data.MethodData methodData) {
      for (AnnotationMirror a : methodData._method.getAnnotationMirrors()) {
         if ("@java.lang.Override".equals(a.toString())) {
            builder.addAnnotation(AnnotationSpec.builder(Override.class).build());
            if (methodData.returnsVoid()) {
               builder.addStatement("super.$L($L)", methodData._method.getSimpleName(), getCommaSeparatedParams(methodData, buffer));
            }
         }
      }
      return builder;
   }
}
