package com.eyeem.decorator.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by budius on 21.07.15.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.eyeem.decorator.annotation.Decorate")
public class Processor extends AbstractProcessor {

    private HashMap<String, DecoratedClassDefinition> map = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        String fullName;
        String methodName;
        DecoratedClassDefinition klazz;
        int numberOfMethods = 0;
        int numberOfClasses = 0;

        //region Identify all the annotated classes we should built for ============================
        for (TypeElement annotation : annotations) {

            log("Processing annotation: " + annotation.getQualifiedName());

            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

                //log("element: " + element.getKind().toString() + "; " + element.getClass().getCanonicalName());

                ExecutableElement methodElement = getMethod(element);
                PackageElement packageElement = getPackage(element);
                TypeElement typeElement = getClass(element);

                fullName = typeElement.getQualifiedName().toString();

                klazz = map.get(fullName);
                if (klazz == null) {
                    klazz = new DecoratedClassDefinition();
                    klazz.classElement = typeElement;
                    klazz.packageElement = packageElement;
                    map.put(fullName, klazz);
                    numberOfClasses++;
                    log("Adding class: " + fullName);
                }
                log("Adding method: " + methodElement.getSimpleName().toString());
                klazz.decoratedMethods.add(methodElement);
                numberOfMethods++;
            }
        }

        log("Found total of " + numberOfClasses + " classes, with total of " + numberOfMethods + " annotated methods");
        //endregion


        //region Generate the classes from the annotated map
        // TODO: classes generation
        for (DecoratedClassDefinition d : map.values()) {
            log("Generating code for " + d.classElement.getQualifiedName());
        }
        //endregion

        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("DecoratedFragment$$Decorators")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.eyeem.decorator.sample", helloWorld)
                .build();

        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile("com.eyeem.decorator.sample.DecoratedFragment$$Decorators");
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

    public static PackageElement getPackage(Element type) {
        while (type.getKind() != ElementKind.PACKAGE) {
            type = type.getEnclosingElement();
        }
        return (PackageElement) type;
    }

    public static TypeElement getClass(Element type) {
        while (type.getKind() != ElementKind.CLASS) {
            type = type.getEnclosingElement();
        }
        return (TypeElement) type;
    }

    public static ExecutableElement getMethod(Element type) {
        return (ExecutableElement) type;
    }


}
