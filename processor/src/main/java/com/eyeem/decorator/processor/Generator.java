package com.eyeem.decorator.processor;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * Created by budius on 24.11.15.
 */
public interface Generator {
   public void generate(ProcessingEnvironment processingEnv, Data def);
}
