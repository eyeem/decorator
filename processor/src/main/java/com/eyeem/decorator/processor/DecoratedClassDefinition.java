package com.eyeem.decorator.processor;

import java.util.ArrayList;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * Created by budius on 21.07.15.
 */
public class DecoratedClassDefinition {
    PackageElement packageElement;
    TypeElement classElement;
    ArrayList<ExecutableElement> decoratedMethods = new ArrayList<>();

    String getSourceFileName() {
        return classElement.getQualifiedName() + "$$Decorator";
    }

    String getClassName() {
        return classElement.getSimpleName() + "$$Decorator";
    }

    String getListSourceFileName() {
        return classElement.getQualifiedName() + "$$Decorators";
    }

    String getListClassName() {
        return classElement.getSimpleName() + "$$Decorators";
    }
}
