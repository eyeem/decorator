package com.eyeem.decorator.base_classes;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by budius on 13.10.15.
 */
public abstract class AbstractDecorators<BASE, DECORATOR extends AbstractDecorator<BASE>> {

   private final HashMap<Class, DECORATOR> noComposeMap;
   private final Builder<BASE, DECORATOR> builder;
   protected final ArrayList<DECORATOR> decorators;
   protected final int size;

   public AbstractDecorators(Builder<BASE, DECORATOR> builder) throws InstantiationException, IllegalAccessException {

      this.builder = builder.copy();

      Class[] nonComposable = getNonComposable();
      noComposeMap = new HashMap<>(nonComposable.length);

      // builds the decorators
      size = builder.decorators.size();
      decorators = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
         Class<? extends DECORATOR> klass = builder.decorators.get(i);
         DECORATOR decorator = klass.newInstance();
         composableCheck(nonComposable, decorator);
         decorators.add(decorator);
      }
   }

   private void composableCheck(Class[] nonComposable, DECORATOR decorator) {
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

   public <I> I getFirstDecoratorOfType(Class clazz) {
      for (DECORATOR decorator : decorators) {
         if (clazz.isAssignableFrom(decorator.getClass()))
            return (I) decorator;
      }
      return null;
   }

   public void bind(BASE base) {
      for (int i = 0, size = decorators.size(); i < size; i++) {
         DECORATOR deco = decorators.get(i);
         deco.decorated = base;
         deco.decorators = this;
      }
      for (int i = 0, size = decorators.size(); i < size; i++) {
         DECORATOR deco = decorators.get(i);
         deco.bind();
      }
   }

   public void unbind() {
      for (int i = 0, size = decorators.size(); i < size; i++) {
         DECORATOR deco = decorators.get(i);
         deco.unbind();
      }
      for (int i = 0, size = decorators.size(); i < size; i++) {
         DECORATOR deco = decorators.get(i);
         deco.decorated = null;
         deco.decorators = null;
      }
   }

   public Builder<BASE, DECORATOR> buildUpon() {
      // TODO: move to generated Decorator
      return builder.copy();
   }

   protected <I> I getInstigator(Class klass) {
      return (I) noComposeMap.get(klass);
   }

   protected abstract Class[] getNonComposable();

   public static class Builder<BASE, DECORATOR extends AbstractDecorator<BASE>> implements Serializable {

      private final ArrayList<Class<? extends DECORATOR>> decorators = new ArrayList<>();
      public final Class<? extends AbstractDecorators<BASE, DECORATOR>> decoratorsClass;

      public Builder(Class<? extends AbstractDecorators<BASE, DECORATOR>> decoratorsClass) {
         this.decoratorsClass = decoratorsClass;
      }

      public Builder<BASE, DECORATOR> addDecorator(Class<? extends DECORATOR> klass) {
         decorators.add(klass);
         return this;
      }

      public Builder<BASE, DECORATOR> removeDecorator(Class<? extends DECORATOR> klass) {
         decorators.remove(klass);
         return this;
      }

      public boolean contains(Class klass) {
         return decorators.contains(klass);
      }

      @Deprecated
      public Builder<BASE, DECORATOR> copy() {
         // TODO: remove this method, only here to keep compatible with buildUpon();
         Builder<BASE, DECORATOR> copy = new Builder<>(decoratorsClass);
         copyTo(copy);
         return copy;
      }

      protected void copyTo(Builder copy) {
         for (int i = 0, size = decorators.size(); i < size; i++) {
            copy.decorators.add(decorators.get(i));
         }
      }

      public AbstractDecorators<BASE, DECORATOR> build() throws
         NoSuchMethodException,
         IllegalAccessException,
         InvocationTargetException,
         InstantiationException {
         return decoratorsClass.getDeclaredConstructor(Builder.class).newInstance(this);
      }
   }
}
