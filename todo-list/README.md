# Spring MVC

Based on the MVC design pattern:

- Model (M): models are responsible for the managing the application's data, business logic and business rules
- View (V): is an output representation of information, for example displaying information or reports to the user either as web form or as charts
- Controller (C): is responsible for invoking Models to perform business logic and then updating the view based on the Models output.

With Spring MVC we can use different view technologies to render web pages.
For example Groovy Markup, Freemarker and Thymeleaf. Spring MVC also integrates with other web frameworks.

Servlets: are JAVA-classes, who's instances inside a web-server handle client requests and answers them

## Maven WAR and Maven Cargo

- WAR: web application archive
  - contain the resources for developing web applications
- Maven WAR plugin: is responsible for collectiong all artifact dependencies, classes and resources of the web
  application and packaging them into a web application archive
  
- add war packaging to the pom-xml file:
  - create a new directory `webapp in src/main`: `webapp` and therein a new directory `WEB-INF`
  - `WEB-INF`: special directory that contains all things related to the application that are not in the root of the applications
    --> files cannot be served directly to the client by the container
    but the contents of `WEB-INF` are visible to the servlet code
    this folder will contain *.jsp files
  - add `src/main/webapp/index.html` with basic HTML hello world content to test the war plugin

```xml
<project>
    <packaging>war</packaging>
</project>
```

- add the maven-war plugin to `pom.xml`:
```xml
  <plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-war-plugin</artifactId>
  <version>3.2.0</version>
  </plugin>
```
