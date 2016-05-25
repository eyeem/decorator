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
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
   private ProcessingEnvironment processingEnv;

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
      this.processingEnv = processingEnv;

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

      // add static class Builder

      decoratedClassBuilder.addType(
         TypeSpec.classBuilder("Builder")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .superclass(ParameterizedTypeName.get(
               ClassName.get(AbstractDecorators.Builder.class),
               TypeName.get(def.generatingClass.getSuperclass()),
               ClassName.get(def.getPackageName(), GeneratorDecorator.getClassName(def))))

            // add constructor
            .addMethod(MethodSpec.constructorBuilder()
               .addModifiers(Modifier.PUBLIC)
               .addStatement("super($L.class)", decoratorsClassName).build())

            // add copy method
            .addMethod(MethodSpec.methodBuilder("copy")
               .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
               .returns(ClassName.get(def.getPackageName(), className, "Builder"))
               .addStatement("Builder copy =  new Builder()")
               .addStatement("copyTo(copy)")
               .addStatement("return copy")
               .build())

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

      this.processingEnv = null;
   }


   private MethodSpec getVoidMethod(Data.MethodData m) {
      return addOverrideIfNecessary(buildEmptyMethod(m), m)
         .addStatement("decorators.$L($L)",
            m._method.getSimpleName(),
            getCommaSeparatedParams(m, buffer)).build();
   }

   private MethodSpec getNonVoidMethod(Data.MethodData m) {
      if (m.returnsPrimitive()) {
         return addOverrideIfNecessary(buildEmptyMethod(m), m)
            .addStatement("return decorators.$L($L)",
               m._method.getSimpleName(),
               getCommaSeparatedParams(m, buffer)).build();
      } else {
         CodeBlock.Builder code = CodeBlock.builder()
            .addStatement("$T obj = decorators.$L($L)",
               TypeName.get(m._method.getReturnType()),
               m._method.getSimpleName(),
               getCommaSeparatedParams(m, buffer))
            .beginControlFlow("if (obj != null)")
            .addStatement("return obj")
            .nextControlFlow("else")
            .add("//region user inputed code\n")
            .add(MethodBodyReader.getMethodBody(processingEnv, m))
            .add("//endregion\n")
            .endControlFlow();
         return addOverrideIfNecessary(buildEmptyMethod(m), m)
            .addCode(code.build())
            .build();
      }
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

   private static class MethodBodyReader extends TreePathScanner<Object, Trees> {

      private String body;

      static String getMethodBody(ProcessingEnvironment processingEnv, Data.MethodData methodData) {
         MethodBodyReader codeScanner = new MethodBodyReader();
         Trees trees = Trees.instance(processingEnv);

         TreePath tp = trees.getPath(methodData._method);
         codeScanner.scan(tp, trees);

         String val = codeScanner.body.substring(1, codeScanner.body.length() - 2).trim();
         while (val.contains("  ")) {
            val = val.replace("  ", " ");
         }
         val = val + "\n";

         // now we'll replace all the classes with fully qualified class names,
         // that's because JavaPoet doesn't allow to directly import classes

         // get all the imports on the file
         HashMap<String, String> imports = ImportReader.getImports(processingEnv, methodData);

         // loop the imports
         for (Map.Entry<String, String> item : imports.entrySet()) {

            // get class name and it's fully qualified class name
            String className = item.getValue();
            String fullyQualifiedClassName = item.getKey();

            // check the index of where a classname is written
            // if the index is higher than zero, than it found something
            int index = val.indexOf(className, 0);
            while (index >= 0) {

               // if the char before this is a dot,
               // then the code already got a fully qualified name, and we skip this
               if (index == 0 || val.charAt(index - 1) != '.') {

                  // rebuild the string adding the fully qualified name before the class name
                  val = val.substring(0, index) + fullyQualifiedClassName.replace(className, "") + val.substring(index);
               }

               // find the next
               index = val.indexOf(className, index + 1);
            }
         }
         return val;
      }

      @Override public Object visitMethod(MethodTree node, Trees trees) {
         body = node.getBody().toString();
         return super.visitMethod(node, trees);
      }
   }

   private static class ImportReader extends TreePathScanner<Object, Trees> {

      HashMap<String, String> imports = new HashMap<>();

      static HashMap<String, String> getImports(ProcessingEnvironment processingEnv, Data.MethodData methodData) {

         Trees trees = Trees.instance(processingEnv);

         TreePath tp = trees.getPath(methodData._method);
         TreePath lastTp = tp;

         while (tp != null) {
            lastTp = tp;
            tp = tp.getParentPath();
         }

         ImportReader ir = new ImportReader();
         ir.scan(lastTp, trees);
         return ir.imports;
      }

      @Override public Object visitImport(ImportTree node, Trees trees) {

         String fullyQualifiedClassName = node.toString().replace("import ", "").replace(";\n", "").trim();
         String[] classNameParts = fullyQualifiedClassName.split("\\."); // fucking regex
         String className = classNameParts[classNameParts.length - 1];

         imports.put(fullyQualifiedClassName, className);
         return super.visitImport(node, trees);
      }

   }
}
