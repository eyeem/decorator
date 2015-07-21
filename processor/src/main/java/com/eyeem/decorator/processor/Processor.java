package com.eyeem.decorator.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by budius on 21.07.15.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.eyeem.decorator.annotation.Decorate")
public class Processor extends AbstractProcessor {

    private HashMap<String, ArrayList<String>> map = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement annotation : annotations) {

            // returns the annotation
            log("Annotation @" + annotation.getQualifiedName() + " ________________________");

            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                log("element: " + element.getSimpleName());

                int iteration = 0;
                Element enclosing = element.getEnclosingElement();
                while (enclosing != null) {
                    log("enclosing " + iteration + ": " + enclosing.getSimpleName() + "; kind: " + enclosing.getKind().toString());
                    enclosing = enclosing.getEnclosingElement();
                    iteration++;
                    if (iteration > 20) throw new RuntimeException("WTF DUDE ????");
                }
            }

        }

        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("Test")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.eyeem.decorator.processor", helloWorld)
                .build();


        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile("com.eyeem.decorator.processor.Test");
            Writer writer = file.openWriter();
            javaFile.writeTo(writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //throw new RuntimeException("IS IT ALIVE ????????????");
        return true;
    }


    private void log(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }

    private void err(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }

}
