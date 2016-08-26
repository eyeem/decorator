package com.eyeem.decorator.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.sun.tools.javac.code.Type;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.JavaFileObject;

/**
 * Created by budius on 23.11.15.
 */
public class GeneratorUtils {

   public static void writeClass(
         Log log,
         ProcessingEnvironment processingEnv,
         TypeSpec.Builder typeBuilder,
         String packageName,
         String fullName) {

      TypeSpec typeSpec = typeBuilder.build();
      JavaFile javaFile = JavaFile
            .builder(packageName, typeSpec)
            .indent("   ")
            .build();

      Writer writer = null;

      try {
         JavaFileObject file = processingEnv.getFiler().createSourceFile(fullName);
         writer = file.openWriter();
         javaFile.writeTo(writer);
      } catch (IOException e) {
         log.e("Failed to write to " + fullName + ". " + e.getMessage());
      } finally {
         if (writer != null) {
            try {
               writer.close();
            } catch (Exception notCarin) {/**/}
         }
      }
   }

   public static MethodSpec.Builder buildEmptyConstructor(ExecutableElement method) {
      return buildEmptyMethod(method, null, true, false, false);
   }

   public static MethodSpec.Builder buildEmptyMethod(Data.MethodData methodData, Iterable<Modifier> modifiers, boolean withOverride) {
      return buildEmptyMethod(methodData._method, modifiers, false, false, withOverride);
   }

   public static MethodSpec.Builder buildEmptyMethodWithGenericCasting(Data.MethodData methodData, Iterable<Modifier> modifiers, boolean withOverride) {
      return buildEmptyMethod(methodData._method, modifiers, false, true, withOverride);
   }

   public static MethodSpec.Builder buildEmptyMethod(Data.MethodData methodData, boolean withOverride) {
      return buildEmptyMethod(methodData._method, null, false, false, withOverride);
   }

   /**
    * Most of this method is a copy from JavaPoet {@link MethodSpec#overriding(ExecutableElement)}.
    * I've just added a few parameters to control modifiers and use for constructors
    *
    * @param method
    * @param modifiers
    * @param isConstructor
    * @return
    */
   private static MethodSpec.Builder buildEmptyMethod(ExecutableElement method, Iterable<Modifier> modifiers, boolean isConstructor, boolean addGenericCasting, boolean withOverride) {

      if (modifiers == null) {
         modifiers = getModifiers(method);
      }

      String methodName = method.getSimpleName().toString();
      MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName);
      methodBuilder.addModifiers(modifiers);

      for (AnnotationMirror am : method.getAnnotationMirrors()) {
         boolean isOverride = am.toString().equals("@java.lang.Override");
         boolean isOverrideWithOverride = isOverride && withOverride;

         if (!isOverride ||
               isOverrideWithOverride) {
            methodBuilder.addAnnotation(AnnotationSpec.get(am));
         }

         if (isOverrideWithOverride &&
               Data.MethodData.returnsVoid(method.getReturnType())) {
            methodBuilder.addStatement("super.$L($L)", method.getSimpleName(), getCommaSeparatedParams(method, null));
         }
      }

      for (TypeParameterElement typeParameterElement : method.getTypeParameters()) {
         TypeVariable var = (TypeVariable) typeParameterElement.asType();
         methodBuilder.addTypeVariable(TypeVariableName.get(var));
      }

      if (addGenericCasting) {

         // TODO: loop up on `getTypeArguments` to find generic in any possible layer
         if (method.getReturnType() instanceof Type.ClassType) {
            for (Type type : ((Type.ClassType) method.getReturnType()).getTypeArguments()) {

               Type.TypeVar typeVar = null;
               while (typeVar == null) {
                  for (Type t : type.getTypeArguments()) {
                     if (t.getKind().equals(TypeKind.TYPEVAR)) {
                        typeVar = (Type.TypeVar) t;
                     }
                  }
               }

               if (typeVar != null) {
                  methodBuilder.addTypeVariable(TypeVariableName.get(typeVar));
               }
            }
         }
      }

      if (!isConstructor) {
         methodBuilder.returns(TypeName.get(method.getReturnType()));
      } else {
         // constructors always must call super
         methodBuilder.addStatement("super($L)", getCommaSeparatedParams(method, null));
      }

      List<? extends VariableElement> parameters = method.getParameters();
      for (VariableElement parameter : parameters) {
         TypeName type = TypeName.get(parameter.asType());
         String name = parameter.getSimpleName().toString();
         Set<Modifier> parameterModifiers = parameter.getModifiers();
         ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(type, name)
               .addModifiers(parameterModifiers.toArray(new Modifier[parameterModifiers.size()]));
         for (AnnotationMirror mirror : parameter.getAnnotationMirrors()) {
            parameterBuilder.addAnnotation(AnnotationSpec.get(mirror));
         }
         methodBuilder.addParameter(parameterBuilder.build());
      }
      methodBuilder.varargs(method.isVarArgs());

      for (TypeMirror thrownType : method.getThrownTypes()) {
         methodBuilder.addException(TypeName.get(thrownType));
      }

      return methodBuilder;
   }

   private static Iterable<Modifier> getModifiers(ExecutableElement executableElement) {
      Set<Modifier> modifiers = executableElement.getModifiers();
      if (modifiers.contains(Modifier.PRIVATE)
            || modifiers.contains(Modifier.FINAL)
            || modifiers.contains(Modifier.STATIC)) {
         throw new IllegalArgumentException("cannot override method with modifiers: " + modifiers);
      }
      modifiers = new LinkedHashSet<>(modifiers);
      modifiers.remove(Modifier.ABSTRACT);
      return modifiers;
   }

   public static String getInterfaceName(Data.MethodData m) {
      return "Instigate" + Utils.capitalize(m._method.getSimpleName().toString());
   }

   public static String getCommaSeparatedParams(Data.MethodData m, StringBuilder buffer) {
      return getCommaSeparatedParams(m._method, buffer);
   }

   public static String getCommaSeparatedParams(ExecutableElement method, StringBuilder buffer) {

      if (buffer == null) {
         buffer = new StringBuilder();
      } else {
         buffer.setLength(0);
      }

      // build a string with comma separated parameters for this method
      for (VariableElement variableElement : method.getParameters()) {
         if (buffer.length() > 0)
            buffer.append(", ");
         buffer.append(variableElement.getSimpleName());
      }
      return buffer.toString();
   }

}
