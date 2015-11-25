package com.eyeem.decorator.processor;

import com.eyeem.decorator.annotation.Decorate;

import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import static com.eyeem.decorator.processor.Utils.*;

/**
 * Created by budius on 26.11.15.
 */
public class Parser {

   private static final String SUPPORTED_ANNOTATION = "com.eyeem.decorator.annotation.Decorate";

   private final Log log;

   // here we have all the info the generators need
   private final HashMap<String, Data> results;

   // processing parameters
   private RoundEnvironment roundEnv;

   // current processing in a loop
   // to avoid having to be passing so many parameters in internal methods,
   // add here any temp values
   private Data current;

   //region boring region
   public Parser(Log log) {
      this.log = log;
      results = new HashMap<>();
   }

   public HashMap<String, Data> getResults() {
      return results;
   }
   //endregion

   //region main parsing loop
   public void parse(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

      if (annotations.size() == 0) {
         log.i("Request to parse with zero elements");
         return;
      }
      this.roundEnv = roundEnv;
      results.clear();
      for (TypeElement annotation : annotations) {
         if (SUPPORTED_ANNOTATION.equals(annotation.getQualifiedName().toString())) {
            parse(annotation);
         }
      }
      this.roundEnv = null;
      current = null;
   }


   private void parse(TypeElement annotation) {
      log.i("Parse start for annotation: " + annotation.getQualifiedName());

      // loop the list with all classes with `@Decorate`
      for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

         TypeElement typeElement = (TypeElement) element;
         String className = typeElement.getQualifiedName().toString();
         results.put(className, current = new Data());
         parseHeader(typeElement);

         // loop all the methods for this class
         for (ExecutableElement executableElement : ElementFilter.methodsIn(element.getEnclosedElements())) {
            log.i("Found method " + executableElement.getSimpleName());
            parseMethod(executableElement);
         }

         // loop all the interfaces for this class
         for (TypeElement interfaceElement : ElementFilter.typesIn(element.getEnclosedElements())) {
            if (interfaceElement.getKind() == ElementKind.INTERFACE) {
               log.i("Found interface " + interfaceElement.getSimpleName());
               parseInterface(interfaceElement);
            }
         }

      }

      log.i("Parse end for annotation: " + annotation.getQualifiedName());
   }
   //endregion

   //region parse individual parts
   private void parseHeader(TypeElement typeElement) {
      log.i("Parsing header " + typeElement.getSuperclass().toString());
      current.generatingClass = typeElement;
      current.generatingClassPackage = getPackage(typeElement);
      current.superClass = typeElement.getSuperclass();
      String superFullyQualifiedName = current.superClass.toString();
      current.superClassSimpleName = superFullyQualifiedName.substring(superFullyQualifiedName.lastIndexOf(".") + 1);
      String prefix = capitalize(selectNonEmpty(typeElement.getAnnotation(Decorate.class).value(), current.superClassSimpleName));
      current.decoratorName = capitalize(selectNonEmpty(typeElement.getAnnotation(Decorate.class).decorator(), prefix + "Decorator"));
      current.decoratorsName = capitalize(selectNonEmpty(typeElement.getAnnotation(Decorate.class).decorators(), prefix + "Decorators"));
      current.decoratedName = capitalize(selectNonEmpty(typeElement.getAnnotation(Decorate.class).decoratored(), prefix + "Decoratored"));
   }

   private void parseMethod(ExecutableElement method) {
      log.i("Parsing method " + method.getSimpleName());
      current.methods.add(new Data.MethodData(method));
   }

   private void parseInterface(TypeElement _interface) {
      log.i("Parsing interface " + _interface.getSimpleName());
      Data.InterfaceData i = new Data.InterfaceData(_interface);

      for (ExecutableElement executableElement : ElementFilter.methodsIn(_interface.getEnclosedElements())) {
         log.i("... adding method " + executableElement.getSimpleName());
         Data.MethodData methodData = new Data.MethodData(executableElement);
         methodData.belongsToExplicitInterface = true;
         i.methods.add(methodData);

         // if any method on this interface returns a value, this interface should be `instigated`
         if (!methodData.returnsVoid() && !methodData.returnsBoolean()) {
            i.isInstigate = true;
         }
      }
      current.interfaces.add(i);
   }
   //endregion
}
