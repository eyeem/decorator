package com.eyeem.decorator.processor;

import java.util.ArrayList;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * Created by budius on 21.07.15.
 */
public class DecoratedClassDefinition {
    PackageElement packageElement;
    TypeElement typeElement; // class type
    ArrayList<String> decoratedMethods = new ArrayList<>();
}
