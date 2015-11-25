package com.eyeem.decorator.processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import static com.eyeem.decorator.processor.Utils.getMethod;
import static com.eyeem.decorator.processor.Utils.getPackage;
// warning: [options] bootstrap class path not set in conjunction with -source 1.7
// import static com.eyeem.decorator.processor.Utils.getClass;

/**
 * Created by vishna on 24/07/15.
 */
public class old_Parser {

   HashMap<String, old_DecoratedClassDefinition> map = new HashMap<>();
   Log log;

   Set<? extends TypeElement> annotations;
   RoundEnvironment roundEnv;

   /* package */ old_Parser(Log log, Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      this.log = log;
      this.annotations = annotations;
      this.roundEnv = roundEnv;
      parse();
   }

   private void parse() {
      String fullName;
      old_DecoratedClassDefinition klazz;
      int numberOfMethods = 0;
      int numberOfClasses = 0;

      //region Identify all the annotated classes we should built for ============================
      for (TypeElement annotation : annotations) {

         log.i("Processing annotation: " + annotation.getQualifiedName());

         for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

            //log("element: " + element.getKind().toString() + "; " + element.getClass().getCanonicalName());

            ExecutableElement methodElement = getMethod(element);
            PackageElement packageElement = getPackage(element);
            TypeElement typeElement = Utils.getEnclosingClass(element);

            fullName = typeElement.getQualifiedName().toString();

            klazz = map.get(fullName);
            if (klazz == null) {
               klazz = new old_DecoratedClassDefinition();
               klazz.classElement = typeElement;
               klazz.packageElement = packageElement;
               map.put(fullName, klazz);
               numberOfClasses++;
               log.i("Adding class: " + fullName);
            }
            log.i("Adding method: " + methodElement.getSimpleName().toString());
            klazz.decoratedMethods.add(methodElement);
            numberOfMethods++;
         }
      }

      log.i("Found total of " + numberOfClasses + " classes, with total of " + numberOfMethods + " annotated methods");
   }

   public Collection<old_DecoratedClassDefinition> definitions() {
      return map.values();
   }
}
