# Spring Container

IoC Container:
- Inversion of Control: the control of objects or portions of a program is transferred to a container or framework. By contrast with traditional programming, in which our custom code makes calls to a library, IoC enables a framework to take control of the flow of a program and make calls to our custom code.
- Bean: a simple Java Object, that is instantiated, configured, assembled and managed as well as beeing responsible for their lifecicle by that IoC container
- see https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring

The advantages of this architecture are:
- decoupling the execution of a task from its implementation
- making it easier to switch between different implementations
- greater modularity of a program
- greater ease in testing a program by isolating a component or mocking its dependencies and allowing components to communicate through contracts

Inversion of Control can be achieved through various mechanisms such as: Strategy design pattern, Service Locator pattern, Factory pattern, and Dependency Injection (DI).

Example:

Traditional approach:
```java
public class Store {
    private Item item;
 
    public Store() {
        item = new ItemImpl1();    
    }
}
```

Dependency Injection example:

```java
public class Store {
    private Item item;
    public Store(Item item) {
        this.item = item;
    }
}
```

## Possible Dependency Incejtion methods in Spring:
- Constructor based DI
In a class (`GameImpl`), the `NumberGenerator` is a dependency:
```java
public GameImpl(NumberGenerator numberGenerator) {
    this.numberGenerator = numberGenerator;
}
```

The dependency (`NumberGenerator`) is declared in a XML file (a bean file) 
and referenced via the porperty `ref` in the tag`<constructor-arg ref />`:
```xml
<bean id="numberGenerator" class="academy.learnprogramming.NumberGeneratorImpl"/>
<bean id="game" class="academy.learnprogramming.GameImpl">
    <!-- ref references to the id of another bean -->
    <constructor-arg ref="numberGenerator"/>
</bean>
```

- Setter based DI
Create a setter of the dependency (remove constructor before):
```java
public void setNumberGenerator(NumberGenerator numberGenerator) {
    this.numberGenerator = numberGenerator;
}
```
In the XML file (bean file) the `property` tag is added. 
The property `name` within the the `property` tag has to match the instance variable in the corresponding class.
The property `ref` indicates the reference to the corresponding bean.
```xml
<bean id="numberGenerator" class="academy.learnprogramming.NumberGeneratorImpl"/>
<bean id="game" class="academy.learnprogramming.GameImpl">
        <property name="numberGenerator" ref="numberGenerator" />
    </bean>
```

You can mix constructor-based and setter-based DI.
- It is a good rule of thumb to use constructors for mandatory dependencies and setter methods for optional dependencies.
- The spring team generally advocates constructor injection, as it enables one to implement application components as immutable objects and to ensure that required dependecies are not null.
- Furthermore, constructor-injected components are always returned to the client (calling) code in a fully initialized state.
- A large number of constructor arguments is considered bad practice (3 args should be max)
- A large number of constructor arguments implies that the class likely has too many responsibilities --> refactor to better address proper seperation of concerns.
- Setter injection should primarily only be used for optional dependencies that can be assigned reasonable default values within the class.
  - Othersies, not-null checks must be performed everywhere the code uses that dependencies.
  - Benefit: make objects of that class amenable to reconfiguration or re-injection later.
  
 Circular dependencies:
 - If you predominatly use construcotr injection, it is possible to create an unresolveable circular dependency scenario.
 - For example: class A requires instance of class B (constructor injection) and class B requires instance of class A through constructor injection.
   - exception from Spring: BeanCurrenrlyInCreationException
 - Solution: use setter-based injection for one of the classes, or use setter-based injection only.
 
## Bean lifecycle callbacks
 
Sometimes there is the need to perform certain action upon initalization & destruction of your beans.
Two approaches:
- XML based configuration:
  - +: seperation of concerns: outside of Java classes (no recompilation)
  - +: XML helps to centralize configuration metadata
  - -: typos are difficult to debug
  - -: XML is not type safe
    
- Annotations:
  - +: shorter and more concise configurations vs XML
  - +: dependency wiring is closer to the source
  - +: ensures type safety, it can also help to document the class
  - -: Annotations reside in your source code
  - -: decentralized configuration of metadata
  - -: Annotations can clutter your POJO's (Plain Old Java Objects)
  - -: Change of annotations require recompilation
  - -: may be considered unintuitive due to ther brevity

Use the best of both worlds: `@Configuration` annotations. 

One way is to use XML and to specify `init` and `destroy` methods inside the XML. Ideally the method names are standardized (init, destroy, ...).
 In our example bean class `GameImpl` we have such a lifecycle method called `reset`. In order to call this method upon initalization of the bean, we define the `init-method`property in `beans.xml`:
```xml
<bean id="game" class="academy.learnprogramming.GameImpl" init-method="reset">
    <property name="numberGenerator" ref="numberGenerator" />
</bean>
```

Considered best practice: 
Another possible way to use lifecycle callbacks is to use annotations.
Therefore we need to define a new bean:
```xml
<bean class="org.springframework.context.annotation.CommonAnnotationBeanPostProcessor"/>
```
and add the dependency in the `pom.xml` & `core/pom.xml` files: `javax.annotation-api`.
then add the annotation `@PostConstruct` to the `reset()` method:
```java
class GameImpl {
  // == init ==
  @PostConstruct
  @Override
  public void reset() {
    smallest = 0;
    guess = 0;
    remainingGuesses = guessCount;
    biggest = numberGenerator.getMaxNumber();
    number = numberGenerator.next();
    log.debug("the number is {}", number);
  }
}
```
Other possible annotations:
- `@postConstruct`
- `@preDestroy`

## Autowiring Beans

- `@Autowired` annotation can be used with constructor and setter based DI
- here we injecting to instance fields
- best practice: constructor injection
  - in practice this means adding `@Autowired` to a constructor instead of the field
  - recommended by the Spring team: enables you to implement application components as immutable objects to ensure that required dependencies are not null
1. Change the `<beans>` tag:
  - add `xmlns:context="http://www.springframework.org/schema/context"`)
  - change the `xsi:schemaLocation` and add
    - `http://www.springframework.org/schema/context`
    - `http://www.springframework.org/schema/context/spring-context.xsd`
2. Add the `context` to beans.xml:
```xml
<context:annotation-config />
```
3. Remove the bean CommonAnnotationBeanPostProcessor.
4. Remove the property from the bean `game` as we now use autowiring.

Total changes:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config />

    <bean id="numberGenerator" class="academy.learnprogramming.NumberGeneratorImpl"/>

    <bean id="game" class="academy.learnprogramming.GameImpl">
    </bean>
</beans>
```

5. In `GameImpl.java` the setter for NumberGenerator is not needed anymore - can be deleted
6. Add `@Autowired` to the field `numberGenerator`

## Beans as Components

- how to auto scan a package for components
- how to use the `@Component` annotation
- Spring provides several "stereotype" annotations:
    - @Component
    - @Service
    - @Controller
- stereotype means: something conforming to a fixed or general pattern
- Stereotype annotations are markers for any class that fulfills a role within an application
    - no need to use Spring XML based configuration for these components
- @Component is generic stereotype for any Spring-managed component.
- @Repository, @Service, @Controller are specializations of @Component for specific use cases

To use this, we need to enable the components scan in `bean.xml`:
- delete the tag `<context:annotation-config />`
- add the tag `<context:component-scan base-package="academy.learnprogramming" />`
- since the component scan is enabled now, we can remove any DI beans of `beans.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="academy.learnprogramming" />
</beans>
```

- add a `@Component` to the classes `NumberGeneratorImpl` and `GameImpl`
- we do not add this annotation to the interface classes, since it would introduce a dependency on string
- execution of the code leads to exception, since the bean with the name "numberGenerator" is not found.
  Spring creates beans based on the class name, so it created a bean named "numberGeneratorImpl", 
  but in the main method, we ask for a bean called "numberGenerator".
- we can define a name for a bean by using `@Component("numberGenerator)`.
- it is better to get the bean by type
    - this does not work, if multiple implementation of an interface are defined. 
      In the case of multiple implementations, we need to use qualifiers to specificy the correct implementation.
      
## Use Java Annotation Configuration (@Configuration)

- use an Annotation Configuration
- use the @Configuration annotation
- use the @ComponentScan annotation
- use the @Bean annotation

The `@Configuration` annotation is on a class-level and tells Spring that this class contains one or more @Bean methods
and may be processed by the Spring container to generate bean definitions.
  
We need to create a class within our project that represents a configuration for our Spring container.
- delete `beans.xml`
- create a new class `AppConfig`
```java
@Configuration
@ComponentScan(basePackages = "academy.learnprogramming")
public class AppConfig {
}
```
- delete the old XML Config Application Context in `Main.java` and replace it with:
```java
ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```

- oftentimes these config classes hold bean methods: methods that instantiate classes
- remove the @Component in `NumberGeneratorImpl` and `GameImpl`
- add bean methods to `AppConfig`:
```java
@Configuration
@ComponentScan(basePackages = "academy.learnprogramming") // searches for @Component annotations
public class AppConfig {

    // == bean methods ==
    @Bean
    public NumberGenerator numberGenerator() {
        return new NumberGeneratorImpl();
    }

    @Bean
    public Game game() {
        return new GameImpl();
    }
}
```
- name of the bean is the name of the bean method
- bean methods are useful, where we need additional configuration for our bean

## Application Event

- The `ApplicationContext` interface has a parent interface `ApplicationEventPublisher` that encapsulates 
  event publication functionalities (this means it is availabel also in `Application Context`).
  
- Event handling in the `ApplicationContext` is provided through the `ApplicationEvent` class and `ApplicationListener` interface.
- If a bean that omplements the `ApplicationListener` interface is deployed into the context,
  every time an `ApplicationEvent` gets published to the `ApplicationContext`, 
  that bean is notified.
  
- --> Standard **Observer-Design pattern**

One method is override the approriate method if the interface:
```java
@Component
public class ConsoleNumberGuess implements ApplicationListener<ContextRefreshedEvent> {

    // == constants ==
    private static final Logger log = LoggerFactory.getLogger(ConsoleNumberGuess.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // Context RefreshedEvent is published when the ApplicationContext is initialized or refreshed
        // --> all beans are loaded, singletons are pre-instantiated, ...

        log.info("Container ready to use.");

    }
}
```

Another method is to use annotations:
```java
@Component
public class ConsoleNumberGuess {

    // == constants ==
    private static final Logger log = LoggerFactory.getLogger(ConsoleNumberGuess.class);

    @EventListener
    // Note: the parameter contextRefreshedEvent defines the event
    public void onStart(ContextRefreshedEvent contextRefreshedEvent) {
        // Context RefreshedEvent is published when the ApplicationContext is initialized or refreshed
        // --> all beans are loaded, singletons are pre-instantiated, ...
      

        log.info("onStart(): Container ready to use.");
    }
}
```

Alternatively add an event type to the annotation instead of an unused parameter in the example above (`contextRefreshedEvent`):
```java
@Component
public class ConsoleNumberGuess {

    // == constants ==
    private static final Logger log = LoggerFactory.getLogger(ConsoleNumberGuess.class);

    @EventListener(ContextRefreshedEvent.class)
    public void onStart() {
        // Context RefreshedEvent is published when the ApplicationContext is initialized or refreshed
        // --> all beans are loaded, singletons are pre-instantiated, ...

        log.info("onStart(): Container ready to use.");
    }
}
```

## Qualifiers

- A problem of automatic autowiring occurs, if the method that creates a bean is renamed, but the field that requires the bean, is not renamed:
  - You will get a runtime exception, as the bean cannot be assigned to autoired field
  - Solution: Qualifiers
- When there is a need for fine-tuning annotation-based autowiring we can use qualifiers
- A qualifier is an annotation that you apply to a bean
- The `@Primary` annotation is an effective way to use autowiring by type with several
  instances when one primary candidate can be determined
  
- When more control over the selection process is required, the `@Qualifier` annotation can be used
- You can associate qualifier values with specific arguments, narrowing the set of type matches so that
  a specific bean is chosen for each argument
  
- You can create your own custon qualifier annotations. Simply define an annotation and provide the 
  `@Qualifier` annotation within your definition.
  
Import a `@Configuration` class to another `@Configuration` class:
```java
@Configuration
public class GameConfig {
}

@Configuration
@Import(GameConfig.class)
@ComponentScan(basePackages = "academy.learnprogramming") // searches for @Component annotations
public class AppConfig {
}
```

Fine tuning annotation based autowiring:
- Create a custom qualifier:
  - @interface creates a new annotation type
  - @Target: determines that our annotation can be added to fields, parameters of methods
  - @Retention: Indicates how long annotations with the annotated type are to be retained. If no Retention annotation is present on an annotation type declaration, the retention policy defaults to RetentionPolicy.CLASS
    - three retetnion policies:
      - SOURCE – where the annotations are removed by the compiler
      - CLASS – Annotations are present in the bytecode, but are not present at runtime and hence not available when trying to reflectively determine if a class has the annotation.
      - RUNTIME – Annotations are retained in the byte code and is available at runtime and hence can be reflectively found on a class.
  - @Qualifier: used annotate other custom annotations that can in turn be used as qualifiers
```java
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface MaxNumber {
}

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface GuessCount {
}
```

Then add the new qualifiers to the bean methods in `GameConfig.java`:

```java
@Configuration
public class GameConfig {

    // == fields ==
    private int maxNumber = 25;
    private int guessCount = 8;

    // == bean methods ==
    @Bean
    @MaxNumber
    public int maxNumber() {
        return maxNumber;
    }

    @Bean
    @GuessCount
    public int guessCount() {
        return guessCount;
    }
}
```

And add the new qualifier to the fields, that are autowired:
```java
class GameImpl {
  @Autowired
  @GuessCount
  private int guessCount;
}

class NumberGeneratorImpl {
  @Autowired
  @MaxNumber
  private int maxNumber;
}
```

> Now we can use any method name in the bean methods, as well as any field name.

## Autowire from a properties file

- How to use `@Value` annotation
- How to use `@PropertySource` annotation
- How to inject values from a properties file

The idea is to remove the hard coded fields in GameConfig (`maxNumber` and `guessCount`) and load the from a porperties file.

- Create a `game.properties` file in the resource folder of the core module: `resources/config/game.properties`
```properties
# game properties
game.maxNumber = 100
game.guessCount = 10
````
- Load the properties file in the `GameConfig` class:
```java
@Configuration
@PropertySource("classpath:config/game.properties")
public class GameConfig {
}
```
- assign the properties to the fields by using the `@Value` annotation
- the number after the `:` is default value, if no matching property is found
```java
@Configuration
@PropertySource("classpath:config/game.properties")
public class GameConfig {

    // == fields ==
    @Value("${game.maxNumber:20}")
    private int maxNumber;
    
    @Value("${game.guessCount:5}")
    private int guessCount;
}
```

## Use @Component instead of bean methods
- instead of creating bean methods in `AppConfig` we can use the `@Component` annotation on the `NumberGeneraorImpl` class instead:
  - The IoC Spring container will find the corresponding class based on the type and the `@Component` annotation
```java
@Component
public class NumberGeneratorImpl implements NumberGenerator {
}
```
- The bean method is only used when we need some configuration or initialization of our bean done
  (i.e Factory Methods).

## Constructor Injection

- considered best practice
  - instead of using the `@Autowire` annotation on fields, we use `@Autowired` to the constructor
  - it is common practice to make the instance variables, that we set through the constructor `final`
  - note that custom qualifiers are used for `maxNumber` and `minNumber`
  
- remove the `@Autowired` from the fields
- add a constructor with the fields that need to be injected and add `@Autowired` to the constructor
- the fields injected can now be final.
- > **NOTE** `@Autowired` annotation on a constructor is no longer necessary if the target bean defines only one constructor to begin with.
  > However, if several constructors are available and there is no primary/default constructor, 
  > at least one of the constructors must be annotated with @Autowired in order to instruct the container which one 
  > to use.

```java
public class NumberGeneratorImpl implements NumberGenerator {
    public NumberGeneratorImpl(@MaxNumber int maxNumber, @MinNumber int minNumber) {
      this.maxNumber = maxNumber;
      this.minNumber = minNumber;
    }
  }
```

