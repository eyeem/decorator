# Decorator

You had one job. __ONE JOB__. Fixing the broken dream of the single responsibility principle.

![](http://vignette2.wikia.nocookie.net/vampirediaries/images/a/ae/Sam-one-job.gif/revision/latest?cb=20150211170438)

The idea behind the decorator pattern is to decouple responsibilities from the parent class into reusable components. Individual decorators can then be attached to otherwise empty parent classes to do a single job. This allows a simple java class to have multiple and dynamic inheritance. In order to make this pattern scalable with minimal boilerplate, an easy to use  code-generation library was created to automate most of its creation.

## The Perils of Android UI Development

Regardless of usage of Activity, Fragment or some MVP framework, following steps are frequently involved in building an Android UI and they tend to be found inside one class:

- view inflation (e.g. RecyclerView)
- view configuration (e.g. instantiation of LayoutManager/Adapter)
- connecting adapter to data source
- loading cached data source on background thread
- callback registration/unregistration during lifecycle
- listening to user interaction events (tap, scroll) and acting accordingly

Additionally, there are other UI building parts coming from product/design decisions and these can vary dramatically and dynamically. As developers we address this either by defining some extra flags or by extending base classes to cover for new functionalities.

>_Example: An app can have a list of users for “friends” and another list of suggested users with an extra headers with social networks links for “find friends”; linearlayout manager for phones and Grid or Staggered layout manager for tablets; different data sources (API endpoints); `tap` will do action for logged user and send to login page for not logged user;_

Flags usually lead to a source code with huge amount of `if` cases with custom behaviors during several points of the life-cycle. The bigger the number of the flags the more permutations you get and the more messy your code gets.

Inheritance is a base concept of object oriented programming and a common practice of getting things done on Android. You pretty much override everything constantly (View, Application, Activity, Fragment etc.).  Contrary to flags approach, extending class can help you avoid multiple ‘if’ cases but can also turn very quickly into “inheritance hell”.

![screen shot 2016-03-29 at 17 43 46](https://cloud.githubusercontent.com/assets/121164/14114158/43cffbec-f5d6-11e5-8692-6fcd886d0a06.png)

## Base concepts

The idea behind decorators pattern is to decouple responsibilities/features from the base class and never inherit from the base class anymore. If you need to extend a base class you lose.

![screen shot 2016-03-29 at 18 05 25](https://cloud.githubusercontent.com/assets/121164/14114818/e2a95d88-f5d8-11e5-8963-4a8c00df33aa.png)

To make the Decorator pattern work, four classes components are build:
- __Decorator__ class with empty methods. These methods come from the base class. Decorator acts much like observer on steroids. This is the class you extend in order to add your features.
- __Decorators__ class with a list/map/array of Decorator that dispatches all the callbacks and optional callbacks to the Decorator list in a for-loop.
- __Decorated__ class that extends from the base class. It contains and initialises a Decorators object and dispatches its original callbacks to it.
- __Instigators__ class that is a special case of decorator. It produces some object e.g. adapter instance and it can’t be put together with another instigator of the same time. Example: The base class only requires one adapter and we can’t have two or more decorators providing different adapters as this would lead to ambiguity.

## Key Advantages

- Clear separation of responsibilities
- Dynamic and runtime permutations of features that are added to the base class.
- Future proof - if your base class is Fragment and you want to switch to a Presenter, you instantly see the scope of migration (all the decorators to be rewritten) and will come up with a migration plan faster.
- Development parallelization - because features are separated per class, it’s easy to add new ones without interfering much with other developers.

## Constraints

- decorator can only have default constructor
- decorator configuration params must be passed outside of decorator’s constructor
- decorators will only be operational when they are bound to an instance of the base class

## Example usage

As complex as it might sound, it’s actually pretty easy to build a very flexible structure with that approach. Let’s illustrate here a decorated activity.
First we create a template code for the annotation processor, this class can be ProGuard`ed later on, that’s only a blueprint and it’s not needed for the final .apk.

```java
@Decorate( // indicate the processor to do this whole class
   decorator = "Decorator", // optional rename the classes
   decoratored = "DecoratedActivity",
   decorators = "Decorators")
public class DecoratedActivityBlueprint extends AppCompatActivity {
   @Override protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }
   @Override protected void onStart() { super.onStart(); }
   @Override protected void onStop() { super.onStop(); }
   @Override protected void onDestroy() { super.onDestroy(); }
}
```

This will generate 3 classes, Decorator.java, DecoratedActivity.java (extends from AppCompatActivity) and Decorators.java. Then, we create our MainActivity from the generated DecoratedActivity class.

```java
public class MainActivity extends DecoratedActivity {

   @Override public void onCreate(Bundle savedInstanceState) {
      // bind the decorators, `Builder` class can be passed as serializable
      bind(getIntent().getSerializableExtra("decorators"));
      super.onCreate(savedInstanceState);
   }

   @Override public void onDestroy(){
       unbind(); // destroy instances of decorator and remove ref to this activity
   }

}
```

That’s the absolutely base and it’s not doing much at all. Let’s code for this example an UI with a RecyclerView showing data from some GET photos request and using a local data storage from [Potato Library](https://github.com/eyeem/potato).

First we’ll add a few extra callbacks to our Blueprint class.

```java
@Decorate( // indicate the processor to do this whole class
decorator = "Decorator", // optional rename the classes
decoratored = "DecoratedActivity",
decorators = "Decorators")
public class DecoratedActivityBlueprint extends AppCompatActivity {

   // base life-cycle
   @Override protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }
   @Override protected void onStart(){ super.onStart(); }
   @Override protected void onStop(){ super.onStop(); }
   @Override protected void onDestroy(){ super.onDestroy(); }

   // extras
   public int getLayoutId() { return 0; }
   public LayoutManager getLayoutManager() { return null; }
   public RecyclerView.Adapter getAdapter() { return null; }
   public PhotoStorage.List getData() { return null; }
   public void onRecyclerViewCreated(RecyclerView recyclerView) {}
}
```

after re-built we can start calling those methods on the DecoratedActivity class:

```java
public class MainActivity extends DecoratedActivity {

   @Override public void onCreate(Bundle savedInstanceState) {
      // bind the decorators, `Builder` class can be passed as serializable
      bind(getIntent().getSerializableExtra("decorators"));
      super.onCreate(savedInstanceState);
      setContentView(getLayoutId());
      RecyclerView rv = (RecyclerView) findViewById(R.id.recycler);
      LayoutManager lm = getLayoutManager();
      if (lm == null) {
         lm = new LinearLayoutManager(this);
      }
      rv.setLayoutManager(lm);
      rv.setAdapter(getAdapter());
      onRecyclerViewCreated(rv);
   }

   @Override public void onDestroy(){
      super.onDestroy();
      unbind(); // destroy instances of decorator and remove ref to this activity
   }

}
```

Now we should create a few classes (decorators) that can handle those callbacks:

- Instantiates a simple RecyclerView layout.

```java
// Maybe another class could instantiate a RecyclerView inside a CoordinatorLayout
public class SimpleRecyclerLayoutIdInstigator extends Decorator implements Decorator.InstigateGetLayoutId {

   @Override public void int getLayoutId();
      return R.layout.simple_recycler_view;
   }
}
```

- Instantiates a suitable adapter for the RecyclerView
```java
public class CardDetailsAdapterInstigator extends Decorator implements Decorator.InstigateGetAdapter {

   @Override public void RecyclerView.Adapter getAdapter(){
      PhotoStorage.List dataset = getDecorators.getData();
      return new Adapter(dataset);
   }
}
```

- Provides a List from the Storage
```java
public class PhotoStorageListInstigator extends Decorator implements
   Decorator.InstigateGetData {

   PhotoStorage.List data;
   @Override public void PhotoStorage.List getData() {
      if(data == null) {
         // use data from the Intent to generate list name for the requested data.
         // For example:
         // if there's a USER_ID in the extras it could be "user_photos_" + id;
         String listName = getDecorated.getIntent.getExtras()... 
         data = PhotoStorage.get().obtainList(listName);
      }
      return data;
   }
}
```

- Provides network requests to fill the data (imaginary pseudo "NetworkRequest" object)
```java
public class NetworkRequestInstigator extends Decorator {

   RecyclerView recyclerView;
   NetworkRequest request;
   @Override public void onRecyclerViewCreated(RecyclerView recyclerView) {
      this.recyclerView = recyclerView;
      request = new NetworkRequest( …
         // use getDecorated().getIntent().getExtras(). to configure request
      request.setDataSet(getDecorators.getData()); // data from other decorator
      recyclerView.addOnScrollListener(loadMoreListener);
   }

   @Override public void onStart() {
      request.requestFront();
   }

   @Override public void onDestroy(){
      recyclerView.removeOnScrollListener(loadMoreListener);
      recyclerView = null;
   }

   private final OnScrollListener loadMoreListener = new OnScrollListener(){
      @Override public void onScrolled(RecyclerView rv, int dx, int dy) {
         … check for scroll to the end
         if(needsLoadMore) {
            request.loadMore();
         }       
      }
   }
}
```

Now that we broke down each of the UI building blocks in individual classes is easy to compose Activity behaviors on the fly. For example:

```java
Intent i = new Intent(context, DecoratedActivity.class);
// add necessary metadata
i.putExtra("userId", 12345);
// add decorators
i.putExtra("decorators", new Builder()
   .addDecorator(SimpleRecyclerLayoutIdInstigator.class)
   .addDecorator(CardDetailsAdapterInstigator.class)
   .addDecorator(PhotoStorageListInstigator.class)
   .addDecorator(NetworkRequestInstigator.class)
);

startActivity(i);
```

## Build integration

Gradle:

```gradle
buildscript {
  repositories {
    maven {
      url 'https://oss.sonatype.org/content/repositories/snapshots/'
    }
  }

  dependencies {
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
  }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    compile 'com.eyeem.decorator:annotation:0.0.1-SNAPSHOT'
    apt 'com.eyeem.decorator:processor:0.0.1-SNAPSHOT'
}
```

## License

    Copyright 2016 EyeEm Mobile GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.