package com.eyeem.decorator.base_classes;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by budius on 13.10.15.
 */
public abstract class AbstractDecorators<T> {

   private final HashMap<Class, Object> noComposeMap;
   protected final ArrayList<AbstractDecorator<T>> decorators;
   protected final int size;

   protected AbstractDecorators(Builder<T> builder) throws InstantiationException, IllegalAccessException {

      Class[] nonComposable = getNonComposable();
      noComposeMap = new HashMap<>(nonComposable.length);

      // builds the decorators
      size = builder.decorators.size();
      decorators = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
         Class<? extends AbstractDecorator<T>> klass = builder.decorators.get(i);
         AbstractDecorator<T> t = klass.newInstance();
         composableCheck(nonComposable, noComposeMap, t);
         decorators.add(klass.newInstance());
      }
   }

   private static void composableCheck(Class[] nonComposable, HashMap<Class, Object> noComposeMap, Object decorator) {
      for (int i = 0, size = nonComposable.length; i < size; i++) {
         Class clazz = nonComposable[i];
         if (clazz.isAssignableFrom(decorator.getClass())) {
            // we've found a non composeable class, let's see if it was added ever before
            if (noComposeMap.get(clazz) != null) {
               throw new IllegalStateException("This type decorator was already added: " + clazz.getCanonicalName());
            } else {
               noComposeMap.put(clazz, decorator);
            }
         }
      }
   }

   public void initDecorator(T t) {
      for (int i = 0, size = decorators.size(); i < size; i++) {
         AbstractDecorator<T> d = decorators.get(i);
         d.decorated = t;
      }
      for (int i = 0, size = decorators.size(); i < size; i++) {
         AbstractDecorator<T> d = decorators.get(i);
         d.initDecorator();
      }
   }

   public void destroyDecorator() {
      for (int i = 0, size = decorators.size(); i < size; i++) {
         AbstractDecorator<T> d = decorators.get(i);
         d.destroyDecorator();
      }
      for (int i = 0, size = decorators.size(); i < size; i++) {
         AbstractDecorator<T> d = decorators.get(i);
         d.decorated = null;
      }
   }

   protected <I> I getInstigator(Class klass) {
      return (I) noComposeMap.get(klass);
   }

   protected abstract Class[] getNonComposable();

   public static class Builder<T> implements Serializable {

      private ArrayList<Class<? extends AbstractDecorator<T>>> decorators;
      private final Class<? extends AbstractDecorators<T>> decoratorsClass;

      public Builder(Class<? extends AbstractDecorators<T>> decoratorsClass) {
         this.decoratorsClass = decoratorsClass;
      }

      public Builder addDecorator(Class<? extends AbstractDecorator<T>> klass) {
         decorators.add(klass);
         return this;
      }

      public AbstractDecorators<T> build() throws
         NoSuchMethodException,
         IllegalAccessException,
         InvocationTargetException,
         InstantiationException {
         return decoratorsClass.getConstructor(Builder.class).newInstance(this);
      }
   }
}
