package com.eyeem.decorator.base_classes;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by budius on 13.10.15.
 */
public abstract class AbstractDecorators<
      BASE, // that is the class we're decorating. e.g. Activity, Fragment or Presenter
      DECORATOR extends AbstractDecorator<BASE>, // that is the decorator class
      BUILDER extends AbstractDecorators.Builder<BASE, DECORATOR, ? extends AbstractDecorators.Builder> // that is the generated class that extends builder
      > {

   private final HashMap<Class, DECORATOR> noComposeMap;
   private final BUILDER builder;
   protected final ArrayList<DECORATOR> decorators;
   protected final int size;

   public AbstractDecorators(BUILDER builder) throws InstantiationException, IllegalAccessException {

      this.builder = (BUILDER) builder.copy();

      Class[] nonComposable = getNonComposable();
      noComposeMap = new HashMap<>(nonComposable.length);

      // builds the decorators
      size = this.builder.decorators.size();
      decorators = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
         Class<? extends DECORATOR> klass = this.builder.decorators.get(i);
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

   public boolean hasDecorator(Class clazz) {
      return getFirstDecoratorOfType(clazz) != null;
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

   public BUILDER buildUpon() {
      return (BUILDER) builder.copy();
   }

   protected <I> I getInstigator(Class klass) {
      return (I) noComposeMap.get(klass);
   }

   protected abstract Class[] getNonComposable();

   public static class Builder<
         BASE,
         DECORATOR extends AbstractDecorator<BASE>,
         BUILDER extends Builder
         > implements Serializable {

      protected final ArrayList<Class<? extends DECORATOR>> decorators = new ArrayList<>();
      public final Class<? extends AbstractDecorators> decoratorsClass;

      /**
       * For debugging usages
       *
       * @return array of all the decorators currently in use
       */
      public String[] getDecorators() {
         String[] d = new String[decorators.size()];
         for (int i = 0, size = d.length; i < size; i++) {
            d[i] = decorators.get(i).getSimpleName();
         }
         return d;
      }

      public Builder(Class<? extends AbstractDecorators> decoratorsClass) {
         this.decoratorsClass = decoratorsClass;
      }

      public BUILDER addDecorator(Class<? extends DECORATOR> klass) {
         if (!decorators.contains(klass)) {
            decorators.add(klass);
         }
         return (BUILDER) this;
      }

      public BUILDER removeDecorator(Class<? extends DECORATOR> klass) {
         decorators.remove(klass);
         return (BUILDER) this;
      }

      public boolean hasDecorator(Class<? extends DECORATOR> klass) {
         return decorators.contains(klass);
      }

      public boolean contains(Class klass) {
         return decorators.contains(klass);
      }

      public BUILDER copy() {
         try {
            BUILDER b = ((Class<BUILDER>) getClass()).getConstructor().newInstance();
            for (int i = 0, size = decorators.size(); i < size; i++) {
               b.decorators.add(decorators.get(i));
            }
            return b;
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }
      }

      public AbstractDecorators build() throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
         return decoratorsClass.getDeclaredConstructor(getClass()).newInstance(this);
      }
   }
}
