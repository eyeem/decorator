package com.eyeem.decorator.processor;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * Created by budius on 23.11.15.
 */
public class GeneratorDecorated implements Generator {

   private final Log log;

   public GeneratorDecorated(Log log) {
      this.log = log;
   }

   @Override public void generate(ProcessingEnvironment processingEnv, DecoratorDef def) {

   }
}
