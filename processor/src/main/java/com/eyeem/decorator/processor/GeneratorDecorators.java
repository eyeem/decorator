package com.eyeem.decorator.processor;

import com.eyeem.decorator.base_classes.AbstractDecorators;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import static com.eyeem.decorator.processor.GeneratorUtils.*;

/**
 * Created by budius on 23.11.15.
 */
public class GeneratorDecorators implements Generator {

   public static final String ID = "Decorators";
   private static final List<Modifier> PUBLIC_FINAL = Arrays.asList(Modifier.PUBLIC, Modifier.FINAL);
   private final Log log;

   private StringBuilder buffer = new StringBuilder();
   private StringBuilder nonComposableList = new StringBuilder();
   private ProcessingEnvironment processingEnv;
   private DecoratorDef def;

   public GeneratorDecorators(Log log) {
      this.log = log;
   }

   @Override public void generate(ProcessingEnvironment processingEnv, DecoratorDef def) {

      nonComposableList.setLength(0);
      this.processingEnv = processingEnv;
      this.def = def;

      // create class
      TypeSpec.Builder decoratorsClassBuilder = TypeSpec.classBuilder(def.getSimpleClassNameFor(ID))
         .superclass(ParameterizedTypeName.get(
            ClassName.get(AbstractDecorators.class),
            TypeName.get(def.generatingClass.getSuperclass()),
            ClassName.get(def.getPackageName(), def.getSimpleClassNameFor(GeneratorDecorator.ID))))
         .addModifiers(Modifier.PUBLIC);

      // create constructor
      MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
         .addModifiers(Modifier.PROTECTED)
         .addParameter(ParameterizedTypeName.get(
               ClassName.get(AbstractDecorators.Builder.class),
               TypeName.get(def.generatingClass.getSuperclass()),
               ClassName.get(def.getPackageName(), def.getSimpleClassNameFor(GeneratorDecorator.ID))),
            "builder")
         .addException(InstantiationException.class)
         .addException(IllegalAccessException.class)
         .addStatement("super(builder)");
      decoratorsClassBuilder.addMethod(constructor.build());

      // add methods from class
      List<DecoratorDef.MethodDef> methods = def.methods;
      for (DecoratorDef.MethodDef m : methods) {
         MethodSpec methodSpec;
         if (m.returnsVoid()) {
            methodSpec = getVoidMethod(m);
         } else if (m.returnsBoolean()) {
            methodSpec = getBooleanMethod(m);
         } else {
            methodSpec = getTypeMethod(m);
         }
         decoratorsClassBuilder.addMethod(methodSpec);
      }

      // add methods from interfaces
      // TODO:

      // add methods from AbstractDecorators (non-composable)
      FieldSpec NON_COMPOSABLE = FieldSpec
         .builder(Class[].class, "NON_COMPOSABLE")
         .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
         .initializer("")
         .build();


      // add static newBuilder()
      // TODO:

      /**/
      // write it to disk
      writeClass(
         log,
         processingEnv,
         decoratorsClassBuilder,
         def.getPackageName(),
         def.getFullyQualifiedClassNameFor(ID)
      );

      // zero out processing values
      this.processingEnv = null;
      this.def = null;
   }

   private MethodSpec getVoidMethod(DecoratorDef.MethodDef m) {
      CodeBlock code = CodeBlock.builder()
         .beginControlFlow("for (int i = 0; i < size; i++)")
         .addStatement(
            "decorators.get(i).$L($L)",
            m._method.getSimpleName(),
            getCommaSeparatedParams(m))
         .endControlFlow()
         .build();
      return buildEmptyMethod(m, PUBLIC_FINAL).addCode(code).build();
   }

   private MethodSpec getBooleanMethod(DecoratorDef.MethodDef m) {
      CodeBlock code = CodeBlock.builder()
         .beginControlFlow("for (int i = 0; i < size; i++)")
         .beginControlFlow(
            "if(decorators.get(i).$L($L))",
            m._method.getSimpleName(),
            getCommaSeparatedParams(m))
         .addStatement("return true")
         .endControlFlow()
         .endControlFlow()
         .addStatement("return false")
         .build();
      return buildEmptyMethod(m, PUBLIC_FINAL).addCode(code).build();
   }

   private MethodSpec getTypeMethod(DecoratorDef.MethodDef m) {

      buffer.setLength(0);
      buffer.append(def.getSimpleClassNameFor(GeneratorDecorator.ID));
      buffer.append(".");
      buffer.append(getInterfaceName(m));

      String var = buffer.toString();
      addToNonComposableList(var); // add this interface to our non-composable list
      buffer.append(".class");
      String klazz = buffer.toString();
      String returnType = m.returnsPrimitive() ? "0" : "null";

      CodeBlock code = CodeBlock.builder()
         .addStatement("$L i = getInstigator($L)", var, klazz)
         .beginControlFlow("if (i != null)")
         .addStatement(
            "return i.$L($L)",
            m._method.getSimpleName(),
            getCommaSeparatedParams(m))
         .nextControlFlow("else")
         .addStatement("return $L", returnType)
         .endControlFlow()
         .build();
      return buildEmptyMethod(m, PUBLIC_FINAL).addCode(code).build();
   }

   private String getCommaSeparatedParams(DecoratorDef.MethodDef m) {
      buffer.setLength(0);

      // build a string with comma separated parameters for this method
      for (VariableElement variableElement : m._method.getParameters()) {
         if (buffer.length() > 0)
            buffer.append(", ");
         buffer.append(variableElement.getSimpleName());
      }
      return buffer.toString();
   }

   private void addToNonComposableList(String klazz) {
      if (nonComposableList.length() > 0) {
         nonComposableList.append(", ");
      }
      nonComposableList.append(klazz);
      nonComposableList.append(".class");
   }
}
