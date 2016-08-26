package com.eyeem.decorator.processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Created by budius on 21.07.15.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.eyeem.decorator.annotation.Decorate")
public class Processor extends AbstractProcessor implements Log {

   private boolean logEnabled = true;

   private static final boolean LOG_PARSER = false;
   private static final boolean LOG_PARSER_RESULT = false;
   private static final boolean LOG_GENERATION = false;

   @Override
   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {


      if (annotations.size() == 0) {
         return false;
      }
      
      i("@Decorate starting for " + annotations.size() + " element");

      long initialTime = System.currentTimeMillis();
      HashMap<String, Data> results;

      logEnabled = LOG_PARSER;

      // parse the annotated classes
      Parser parser = new Parser(this);
      parser.parse(annotations, roundEnv);

      // collect results
      results = parser.getResults();

      // log parsing results (if wanted)
      logEnabled = LOG_PARSER_RESULT;
      for (Map.Entry<String, Data> entry : results.entrySet()) {
         entry.getValue().log(this);
      }

      // getter generators
      List<Generator> generators = Arrays.asList(
            new GeneratorDecorator(this),
            new GeneratorDecorated(this),
            new GeneratorDecorators(this)
      );

      // loop through generators
      logEnabled = LOG_GENERATION;
      for (Map.Entry<String, Data> entry : results.entrySet()) {
         Data def = entry.getValue();
         for (Generator generator : generators) {
            generator.generate(processingEnv, def);
         }
      }

      long elapsedTime = System.currentTimeMillis() - initialTime;

      logEnabled = true;
      i("@Decorate processed in " + elapsedTime + "ms");

      return true;
   }

   @Override public void i(String message) {
      if (logEnabled)
         processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
   }

   @Override public void e(String message) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
   }

}
