package com.eyeem.decorator.processor;

import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import static com.eyeem.decorator.processor.Utils.*;

/**
 * Created by budius on 23.11.15.
 */
public class Parser {

   private final Log log;

   // here we have all the info the generators need
   private final HashMap<String, DecoratorDef> results;

   // processing parameters
   private Set<? extends TypeElement> annotations;
   private RoundEnvironment roundEnv;

   // current processing in a loop
   // to avoid having to be passing so many parameters in internal methods,
   // add here any temp values
   private DecoratorDef current;

   //region boring region
   public Parser(Log log) {
      this.log = log;
      results = new HashMap<>();
   }

   public HashMap<String, DecoratorDef> getResults() {
      return results;
   }
   //endregion

   //region main parsing loop
   public void parse(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      this.annotations = annotations;
      this.roundEnv = roundEnv;
      parse();
      this.annotations = null;
      this.roundEnv = null;
      current = null;
   }

   private void parse() {
      log.i("Parse start");

      // here we should have a list of size 1 with `com.eyeem.decorator.Decorate`
      for (TypeElement annotation : annotations) {
         log.i("Parse for annotation: " + annotation.getQualifiedName());

         // loop the list with all methods OR interfaces annotated with `@Decorate`
         for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

            TypeElement enclosingClass = getEnclosingClass(element);
            String className = enclosingClass.getQualifiedName().toString();

            current = results.get(className);
            if (current == null) {
               log.i("Creating decorator definition for " + className);
               results.put(className, current = new DecoratorDef());
               parseHeader(enclosingClass);
            }

            // parse annotated methods
            if (isMethod(element)) {
               parseMethod(getMethod(element));
            }

            // parse annotated interface
            else if (isInterface(element)) {
               parseInterface(getInterface(element));
            }
         }
      }
      log.i("Parse end");
   }
   //endregion

   //region parse individual parts
   private void parseHeader(TypeElement enclosingClass) {
      log.i("Parsing header " + enclosingClass.getSuperclass().toString());
      current.generatingClass = enclosingClass;
      current.generatingClassPackage = getPackage(enclosingClass);
      String superFullyQualifiedName = enclosingClass.getSuperclass().toString();
      current.superSimpleName = superFullyQualifiedName.substring(superFullyQualifiedName.lastIndexOf(".") + 1);
   }

   private void parseMethod(ExecutableElement method) {
      log.i("Parsing method " + method.getSimpleName());
      current.methods.add(new DecoratorDef.MethodDef(method));
   }

   private void parseInterface(TypeElement _interface) {
      log.i("Parsing interface " + _interface.getSimpleName());
      DecoratorDef.InterfaceDef i = new DecoratorDef.InterfaceDef(_interface);
      for (Element element : _interface.getEnclosedElements()) {
         if (isMethod(element)) {
            log.i("... adding method " + element.getSimpleName());
            DecoratorDef.MethodDef methodDef = new DecoratorDef.MethodDef(getMethod(element));
            methodDef.belongsToExplicitInterface = true;
            i.methods.add(methodDef);

            // if any method on this interface returns a value, this interface should be `instigated`
            if (!methodDef.returnsVoid() && !methodDef.returnsPrimitive()) {
               i.isInstigate = true;
            }
         }
      }
      current.interfaces.add(i);
   }
   //endregion
}
