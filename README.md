# mvc-exceptions

## Project Overview

* Last updated January 2021
* This project is built using release 2.4.2 of Spring Boot, Java 8 and Spring 5.3.3 - see pom.xml.
* Web pages use Thymeleaf 3.0.12 and Bootstrap 4.5.3.
* The POM builds a JAR file, not a WAR, so you must run it as a Java application.  Use `mvn exec:java` or `mvn spring-boot:run` to run it, then goto ```http://localhost:8080```.
* If you wish to build a WAR, see `pom-war.xml`

This application demos most of the points covered on my MVC Exceptions blog:

* https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc.

## Release History

* November 2013: V1
* October 2014: V2
* April 2018: V2.0.1
* January 2021: V2.1.0

## Application Overview

The most significant files are:

### Demo 1

Controller with @ExceptionHandler methods

* `src/main/java/demo1/web/ExceptionHandlingController.java`
  * A controller that raises exceptions and provides its own handlers to catch and process them.

### Demo 2

Controller relying on a ControllerAdvice to handle its exceptions.

* `src/main/java/demo2/web/ControllerWithoutExceptionHandlers.java`
  * A controller with all the same handlers as `ExceptionHandlingController` (so they all throw exceptions) but no handler methods.

* `src/main/java/demo2/web/GlobalControllerExceptionHandler.java`
  * `@ControllerAdvice` class with all the same handlers as `ExceptionHandlingController`, but they would apply to all controllers.

### Demo 3 and 4

Exception handling using a `SimpleMappingExceptionResolver`.  When running in demo mode (profile is set to `demo-config`, which is setup by default), it defines a `SimpleMappingExceptionResolver` subclass that can be enabled (Demo 3) or disabled (Demo 4) to show the difference.

* `src/main/java/demo3/config/DemoExceptionConfiguration.java`
  * Java configuration to setup the beans for this demo.
* `src/main/java/demo3/web/ExceptionThrowingController.java`
  * Controller used by the demo.
* `src/main/java/demo3/web/SwitchableSimpleMappingExceptionResolver.java`
  * The resolver subclass described above.
* `src/main/java/demo3/web/ExceptionThrowingController.java`
  * Controller that provides `/simpleMappingExceptionResolver/on` and
    `/simpleMappingExceptionResolver/off` for switching the resolver on/off.

### Demo 5

* `ReturnOrRedirectController`
  * Controller highlighting how Spring Boot implements its error-page mechanism.

### Exceptions

* `src/main/java/demo/exceptions/CustomException.java`
* `src/main/java/demo/exceptions/DatabaseException.java`
* `src/main/java/demo/exceptions/InvalidCreditCardException.java`
* `src/main/java/demo/exceptions/OrderNotFoundException.java`
* `src/main/java/demo/exceptions/UnhandledException.java`
  * Custom exceptions - see blog for usage.
* `src/main/java/org/springframework/dao/DataAccessException.java`
  * Example of a predefined annotation, copied from Spring.
* `src/main/java/org/springframework/dao/DataIntegrityViolationException.java`
  * Example of a predefined annotation, copied from Spring.
  
### Application Setup

The Demo configuration profile is useful for this demo application, but not typical.  So two other profiles are provided to configure a `SimpleMappingExceptionResolver` in a more typical way using either Java configuration or an XML bean file.

* `src/main/java/demo/main/Main.java`
  * Main entry point for the application.  Can run as a Java application (using an embedded Tomcat container) or as a WAR inside a container.  Sets a few initialization properties and Spring Bean profile to use. Available profiles are `demo-config` (default), `java-config`, `xml-config`.
* `src/main/java/demo/main/Profiles.java`
  * The Spring Bean profiles used in the application.
* `src/main/java/demo/config/ExceptionConfiguration.java`
  * Java configuration class to setup a `SimpleMappingExceptionResolver`. Only used if the `java-config` profile is active.
* `src/main/resources/mvc-configuration.xml`
  * XML alternative to `ExceptionConfiguration`. Also sets up a `SimpleMappingExceptionResolver`. Only used if the `xml-config` profile is active.
* `src/main/java/demo/config/ResponseDataControllerAdvice`
  * Controller advice that puts useful data into the model for every request.

### Utility Classes

* `src/main/java/demo/utils/BeanLogger.java`
  * Simple BeanPostProcessor to log all beans created.  Not required by the demo, but as Spring Boot does so much, it allows all the beans created to be easily logged. And a lot less output than enabling `debug=true`.

### Templates

All the views used, generated via Thymeleaf.

* Error views
  * `src/main/resources/templates/creditCardError.html`
  * `src/main/resources/templates/databaseError.html`
  * `src/main/resources/templates/databaseException.html`
  * `src/main/resources/templates/error.html`
  * `src/main/resources/templates/exceptionPage.html`
  * `src/main/resources/templates/support.html`

* Web Pages
  * `src/main/resources/templates/index.html` - Home page
  * `src/main/resources/templates/local.html` - Demo 1
  * `src/main/resources/templates/global.html` - Demo 2
  * `src/main/resources/templates/unannotated.html` - Demo 3
  * `src/main/resources/templates/nohandler.html` - Demo 4
  * `src/main/resources/templates/demo5.html` - Demo 5

### Build

* `pom.xml`
  * Maven POM - notice how short it is - Spring Boot does most of the work.  However heed the comments in the file.
  * Build in the usual way: `mvn package` to create an executable JAR with embedded Tomcat.
  * You can also run the demo using `java -jar target/mvc-exceptions-2.0.1-RELEASE.jar`

* `pom-war.xml` - If you prefer to build a traditional WAR file instead of an executable JAR.
  * Build using `mvn -f pom-war.xml package`

### Examples

Not used by the application, but provided as sample code.

* `src/main/java/demo/example/ExampleExceptionHandlerExceptionResolver.java`
  * Unused in the demo (to keep it simple), but implements the example discussed in the blog.
* `src/main/java/demo/example/ExampleSimpleMappingExceptionResolver.java`
  * Unused in the demo (to keep it simple), but implements the example shown in the blog (called `MySimpleMappingExceptionResolver` in the blog article).


