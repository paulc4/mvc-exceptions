mvc-exceptions
==============

WARNINGS:

  * This project is built using the latest M5 snapshot release of Spring Boot.
  * APIs may change and this project may not build.
  * Check http://spring.io/spring-boot for snapshot, milestone and other releases.
  * Update the pom.xml if necessary.

This application demos most of the points covered on my MVC Exceptions blog.

The files are:

  * <code>pom.xml</code>
     * Maven POM - notice how much shorter it is - Spring Boot does most of the work.  However heed the comments in the file.
  * <code>src/main/java/demo1/exceptions/CustomException.java</code>
  * <code>src/main/java/demo1/exceptions/DatabaseException.java</code>
  * <code>src/main/java/demo1/exceptions/InvalidCreditCardException.java</code>
  * <code>src/main/java/demo1/exceptions/OrderNotFoundException.java</code>
  * <code>src/main/java/demo1/exceptions/UnhandledException.java</code>
     * Custom exceptions - see blog for usage.
  * <code>src/main/java/demo1/main/LoggingUtilities.java</code>
     * Handy utility for setting logging levels - the code is put here to keep the classes that use it simpler.
  * <code>src/main/java/demo1/main/Main.java</code>
     * Main entyr point for the application.  Can run as a Java application (using an embedded Tomcat container) or as a WAR inside a container.  Sets a few inisialization properties.
  * <code>src/main/java/demo1/main/Profiles.java</code>
    * The Spring Bean profiles used in the application.
  * <code>src/main/java/demo1/web/BeanLogger.java</code>
     * Simple BeanPostProcessor to log all beana created.  Not required by the demo, but as Spring Boot is new, it allows all the beans created to be logged.
  * <code>src/main/java/demo1/web/ControllerWithoutExceptionHandlers.java</code>
     * A controller with all the same handlers as ExceptionHandlingController (so they all throw exceptions) but no handler methods. Only confugured if the "global" profile is enabled.
  * <code>src/main/java/demo1/web/ExampleExceptionHandlerExceptionResolver.java</code>
     * Unused in the demo (to keep it simple), but implments the example discussed in the blog.
  * <code>src/main/java/demo1/web/ExampleSimpleMappingExceptionResolver.java</code>
     * Unused in the demo (to keep it simple), but implments the example shown in the blog (called MySimpleMappingExceptionResolver in the blog article).
  * <code>src/main/java/demo1/web/ExceptionConfiguration.java</code>
     * Additional configuration to enable a SimpleMappingExceptionResolver. Only confugured if the "global" profile is enabled.
  * <code>src/main/java/demo1/web/ExceptionHandlingController.java</code>
     * A controller that raises exceptions and provided handlers to catch and process them.  Only defined if the "controller" profile is active.
  * <code>src/main/java/demo1/web/ExtraThymeleafConfiguration.java</code>
     * Some extra configuration to allow Thymeleaf and JSP to co-exist in the same application. Spring-Boot cannot do this out-of-the box because of the way ThymeleafViewResover works - see Javadoc for more.
  * <code>src/main/java/demo1/web/GlobalControllerExceptionHandler.java</code>
     * @ControllerAdvice class with all the same handlers as ExceptionConfiguration, but they would apply to all controllers. Only confugured if the "global" profile is enabled.
  * <code>src/main/java/org/springframework/dao/DataAccessException.java</code>
     * Example of a predefined annotation, copied from Spring.
  * <code>src/main/java/org/springframework/dao/DataIntegrityViolationException.java</code>
     * Example of a predefined annotation, copied from Spring.
  * <code>src/main/resources/templates/contacts.html</code>
  * <code>src/main/resources/templates/creditCardError.html</code>
  * <code>src/main/resources/templates/databaseError.html</code>
  * <code>src/main/resources/templates/error.html</code>
  * <code>src/main/resources/templates/exceptionPage.html</code>
  * <code>src/main/resources/templates/index.html</code>
     * All the views used, generated via Thymeleaf.
  * <code>src/main/webapp/WEB-INF/support.jsp</code>
     * A JSP error page with exception details, including a stack-trace hidden in a comment.  It can be revealed by viewing the PAge Source (for example at the request of Support).
