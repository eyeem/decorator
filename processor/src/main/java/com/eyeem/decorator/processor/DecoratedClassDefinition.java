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
    TypeElement classElement; // class type
    ArrayList<ExecutableElement> decoratedMethods = new ArrayList<>();
}
