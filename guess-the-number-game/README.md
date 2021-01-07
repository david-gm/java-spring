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
      
## Use Java Annotation Configuration

- use an Annotation Configuration
- use the @Configuration annotation
- use the @ComponentScan annotation
- use the @Bean annotation
  
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
