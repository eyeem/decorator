package com.eyeem.decorator.processor;

import com.eyeem.decorator.base_classes.AbstractDecorators;
import com.eyeem.decorator.exception.DecoratorNotFoundException;
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

import static com.eyeem.decorator.processor.GeneratorUtils.buildEmptyMethod;
import static com.eyeem.decorator.processor.GeneratorUtils.getCommaSeparatedParams;
import static com.eyeem.decorator.processor.GeneratorUtils.getInterfaceName;
import static com.eyeem.decorator.processor.GeneratorUtils.writeClass;

/**
 * Created by budius on 23.11.15.
 */
public class GeneratorDecorators implements Generator {

   private static final List<Modifier> PUBLIC_FINAL = Arrays.asList(Modifier.PUBLIC, Modifier.FINAL);
   private final Log log;

   private StringBuilder buffer = new StringBuilder();
   private StringBuilder nonComposableList = new StringBuilder();
   private String decoratorSimpleClassName;

   public GeneratorDecorators(Log log) {
      this.log = log;
   }

   /* package */
   static String getClassName(Data data) {
      return data.decoratorsName;
   }

   @Override public void generate(ProcessingEnvironment processingEnv, Data def) {

      nonComposableList.setLength(0);
      decoratorSimpleClassName = GeneratorDecorator.getClassName(def);

      // create class
      TypeSpec.Builder decoratorsClassBuilder = TypeSpec.classBuilder(getClassName(def))
            .superclass(ParameterizedTypeName.get(
                  ClassName.get(AbstractDecorators.class),
                  TypeName.get(def.generatingClass.getSuperclass()),
                  ClassName.get(def.getPackageName(), decoratorSimpleClassName),
                  ClassName.get(def.getPackageName(), GeneratorDecorated.getClassName(def), "Builder")))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

      // create constructor
      MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(
                  ClassName.get(def.getPackageName(), GeneratorDecorated.getClassName(def), "Builder"),
                  "builder")
            .addException(InstantiationException.class)
            .addException(IllegalAccessException.class)
            .addStatement("super(builder)");
      decoratorsClassBuilder.addMethod(constructor.build());

      // add methods from class
      for (Data.MethodData m : def.methods) {
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
      for (Data.InterfaceData interfaceData : def.interfaces) {
         if (interfaceData.isInstigate) {
            addToNonComposableList(decoratorSimpleClassName + "." +
                  interfaceData._interface.getSimpleName().toString());
         }
         for (Data.MethodData methodData : interfaceData.methods) {
            MethodSpec methodSpec = null;

            // add methods using `getInstigator`
            if (interfaceData.isInstigate) {
               methodSpec = getTypeMethod(methodData, interfaceData);
            }
            // add methods using a loop, and return void
            else if (methodData.returnsVoid()) {
               methodSpec = getVoidMethod(methodData, interfaceData);
            }
            // add methods using a loop, and return boolean
            else {
               methodSpec = getBooleanMethod(methodData, interfaceData);
            }
            decoratorsClassBuilder.addMethod(methodSpec);
         }
      }

      // add NON_COMPOSABLE
      decoratorsClassBuilder.addField(FieldSpec
            .builder(Class[].class, "NON_COMPOSABLE")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("{\n$L\n}", nonComposableList.toString())
            .build());

      decoratorsClassBuilder.addMethod(MethodSpec.methodBuilder("getNonComposable")
            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
            .addAnnotation(Override.class)
            .returns(TypeName.get(Class[].class))
            .addStatement("return NON_COMPOSABLE")
            .build());

      // write it to disk
      writeClass(
            log,
            processingEnv,
            decoratorsClassBuilder,
            def.getPackageName(),
            def.getFullyQualifiedClassNameFor(getClassName(def))
      );
   }

   private MethodSpec getVoidMethod(Data.MethodData m) {
      CodeBlock.Builder code = CodeBlock.builder()
            .beginControlFlow("for (int i = 0; i < size; i++)");
      code.addStatement(
            "decorators.get(i).$L($L)",
            m._method.getSimpleName(),
            getCommaSeparatedParams(m, buffer));
      code.endControlFlow();
      return buildEmptyMethod(m, PUBLIC_FINAL).addCode(code.build()).build();
   }

   private MethodSpec getVoidMethod(Data.MethodData m, Data.InterfaceData i) {
      buffer.setLength(0);
      buffer
            .append(decoratorSimpleClassName)
            .append(".")
            .append(i._interface.getSimpleName().toString());
      String interfaceName = buffer.toString();

      CodeBlock.Builder code = CodeBlock.builder()
            .beginControlFlow("for (int i = 0; i < size; i++)")
            .addStatement("$L deco = decorators.get(i)", decoratorSimpleClassName)
            .beginControlFlow("if (deco instanceof $L)", interfaceName)
            .addStatement(
                  "(($L) deco).$L($L)",
                  interfaceName,
                  m._method.getSimpleName(),
                  getCommaSeparatedParams(m, buffer))
            .endControlFlow()
            .endControlFlow();
      return buildEmptyMethod(m, PUBLIC_FINAL).addCode(code.build()).build();
   }


   private MethodSpec getBooleanMethod(Data.MethodData m) {
      CodeBlock.Builder code = CodeBlock.builder()
            .beginControlFlow("for (int i = 0; i < size; i++)")
            .beginControlFlow(
                  "if (decorators.get(i).$L($L))",
                  m._method.getSimpleName(),
                  getCommaSeparatedParams(m, buffer))
            .addStatement("return true")
            .endControlFlow()
            .endControlFlow()
            .addStatement("return false");
      return buildEmptyMethod(m, PUBLIC_FINAL).addCode(code.build()).build();
   }

   private MethodSpec getBooleanMethod(Data.MethodData m, Data.InterfaceData i) {
      buffer.setLength(0);
      buffer
            .append(decoratorSimpleClassName)
            .append(".")
            .append(i._interface.getSimpleName().toString());
      String interfaceName = buffer.toString();

      CodeBlock.Builder code = CodeBlock.builder()
            .beginControlFlow("for (int i = 0; i < size; i++)")
            .addStatement("$L deco = decorators.get(i)", decoratorSimpleClassName)
            .beginControlFlow("if (deco instanceof $L)", interfaceName)
            .beginControlFlow(
                  "if ((($L) deco).$L($L))",
                  interfaceName,
                  m._method.getSimpleName(),
                  getCommaSeparatedParams(m, buffer))
            .addStatement("return true")
            .endControlFlow()
            .endControlFlow()
            .endControlFlow()
            .addStatement("return false");
      return buildEmptyMethod(m, PUBLIC_FINAL).addCode(code.build()).build();
   }

   private MethodSpec getTypeMethod(Data.MethodData m) {

      buffer.setLength(0);
      buffer.append(decoratorSimpleClassName);
      buffer.append(".");
      buffer.append(getInterfaceName(m));

      String instigatorName = buffer.toString();
      addToNonComposableList(instigatorName); // add this interface to our non-composable list
      buffer.append(".class");
      String instigatorClass = buffer.toString();
      String returnType = m.returnsPrimitive() ? "0" : "null";

      CodeBlock.Builder code = CodeBlock.builder()
            .addStatement("$L deco = getInstigator($L)", instigatorName, instigatorClass)
            .beginControlFlow("if (deco != null)")
            .addStatement(
                  "return deco.$L($L)",
                  m._method.getSimpleName(),
                  getCommaSeparatedParams(m, buffer))
            .nextControlFlow("else");

      if (m.returnsPrimitive()) {
         code.addStatement("throw new $T()", TypeName.get(DecoratorNotFoundException.class));
      } else {
         code.addStatement("return $L", returnType);
      }
      code.endControlFlow();
      return buildEmptyMethod(m, PUBLIC_FINAL).addCode(code.build()).build();
   }

   private MethodSpec getTypeMethod(Data.MethodData m, Data.InterfaceData i) {

      buffer.setLength(0);
      buffer.append(decoratorSimpleClassName);
      buffer.append(".");
      buffer.append(i._interface.getSimpleName());
      String interfaceName = buffer.toString();
      buffer.append(".class");
      String interfaceClass = buffer.toString();

      CodeBlock.Builder code = CodeBlock.builder()
            .addStatement("$L deco = getInstigator($L)", interfaceName, interfaceClass)
            .beginControlFlow("if (deco != null)");

      // invoke method with no return type
      if (m.returnsVoid()) {
         code.addStatement("(($L) deco).$L($L)",
               interfaceName,
               m._method.getSimpleName(),
               getCommaSeparatedParams(m, buffer));
      }
      // invoke method with return type
      else {
         code.addStatement("return (($L) deco).$L($L)",
               interfaceName,
               m._method.getSimpleName(),
               getCommaSeparatedParams(m, buffer));
      }

      if (m.returnsVoid()) {
         code.endControlFlow();
      } else {
         String returnType = m.returnsBoolean() ? "false" : m.returnsPrimitive() ? "0" : "null";
         code.nextControlFlow("else");
         code.addStatement("return $L", returnType);
         code.endControlFlow();
      }

      return buildEmptyMethod(m, PUBLIC_FINAL).addCode(code.build()).build();
   }

   private void addToNonComposableList(String klazz) {
      if (nonComposableList.length() > 0) {
         nonComposableList.append(", \n");
      }
      nonComposableList.append(klazz);
      nonComposableList.append(".class");
   }
}
