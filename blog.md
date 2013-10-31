#Exception Handling in Spring MVC

Spring MVC provides several complimentary approaches to exception handling but, when teaching Spring MVC, 
I often find that my students are confused or not comfortable with them.

Today I'm going to show you the
various options available.  Our goal is to <i>not</i> handle exceptions explicitly in Controller methods
where possible.  They are a cross-cutting concern better handled separately in dedicated code.

There are three options: per exception, per controller or globally.

_A demonstration application that shows the points discussed here can be found at
<a href="https://github.com/paulc4/mvc-exceptions">https://github.com/paulc4/mvc-exceptions</a>.
See <a href="#sample-application-and-spring-boot">Sample Application and Spring Boot</a> below for details._

##Using HTTP Status Codes

Normally any unhandled exception thrown when processing a web-request causes the server to return an
HTTP 500 response.  However, any exception that you write yourself can be annotated with the
<code>@ResponseStatus</code> annotation (which supports all the HTTP status codes defined by the HTTP
specification).  When an _annotated_ exception is thrown from a controller method, and not handled elsewhere,
it will automatically cause the appropriate HTTP response to be returned with the specified status-code.

For example, here is an exception for a missing order.

```java
    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Order")  // 404
    public class OrderNotFoundException extends RuntimeException {
        // ...
    }
```

And here is a controller method using it:

```java
    @RequestMapping(value="/orders/{id}", method=GET)
    public String showOrder(@PathVariable("id") long id, Model model) {
        Order order = orderRepository.findOrderById(id);
        if (order == null) throw new OrderNotFoundException(id);
        model.addAttribute(order);
        return "orderDetail";
    }
```

A familiar HTTP 404 response will be returned if the URL handled by this method includes an unknown order id.

##Controller Based Exception Handling
###Using @ExceptionHandler

You can add extra (<code>@ExceptionHandler</code>) methods to any controller to specifically handle exceptions
thrown by request handling (<code>@RequestMapping</code>) methods in the same controller.  Such methods can:

  1. Handle exceptions without the <code>@ResponseStatus</code> annotation (typically predefined exceptions
that you didn't write)
  2. Redirect the user to a dedicated error view
  3. Build a totally custom error response

The following controller demonstrates these three options:

```java
@Controller
public class ExceptionHandlingController {

  // @RequestHandler methods
  ...
  
  // Exception handling methods
  
  // Convert a predefined exception to an HTTP Status code
  @ResponseStatus(value=HttpStatus.CONFLICT, reason="Data integrity violation")  // 409
  @ExceptionHandler(DataIntegrityViolationException.class)
  public void conflict() {
    // Nothing to do
  }
  
  // Specify the name of a specific view that will be used to display the error:
  @ExceptionHandler({SQLException.class,DataAccessException.class})
  public String databaseError() {
    // Nothing to do.  Returns the logical view name of an error page, passed to
    // the view-resolver(s) in usual way.
    // Note that the exception is _not_ available to this view (it is not added to
    // the model) but see "Extending ExceptionHandlerExceptionResolver" below.
    return "databaseError";
  }

  // Total control - setup a model and return the view name yourself. Or consider
  // subclassing ExceptionHandlerExceptionResolver (see below).
  @ExceptionHandler(Exception.class)
  public ModelAndView handleError(HttpServletRequest req, Exception exception) {
    logger.error("Request: " + req.getRequestURL() + " raised " + exception);

    ModelAndView mav = new ModelAndView();
    mav.addObject("exception", exception);
    mav.addObject("url", req.getRequestURL());
    mav.setViewName("error");
    return mav;
  }
}
```

In any of these methods you might choose to do additional processing - the most common example is to log the
exception.

Handler methods have flexible signatures so you can pass in obvious servlet-related objects such
as `HttpServletRequest`, `HttpServletResponse`, `HttpSession` and/or `Principle`.  __Important Note:__ the
`Model` may __not__ be a parameter of any `@ExceptionHandler` method.  Instead, setup a model inside the method
using a `ModelAndView` as shown by `handleError()` above.

###Exceptions and Views

Be careful when adding exceptions to the model.  Your users do not want to see
web-pages containing Java exception details and stack-traces. However, it can be useful to put exception
details in the page <em>source</em> as a comment, to assist your support people.  If using JSP, you could
do something like this to output the exception and the corresponding stack-trace (using a hidden
`<div>` is another option).

```html
    <h1>Error Page</h1>
    <p>Application has encountered an error. Please contact support on ...</p>
    
    <!--
    Failed URL: ${url}
    Exception:  ${exception.message}
        <c:forEach items="${exception.stackTrace}" var="ste">    ${ste} 
    </c:forEach>
    -->
```

The result looks like this (see also
<a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/webapp/WEB-INF/support.jsp">support.jsp</a>
in the demo application):

![Example of an error page with a hidden exception for support](http://assets.spring.io/wp/wp-content/uploads/2013/10/support-page-example.png "Error Page with Hidden Exception")

##Global Exception Handling
###Using @ControllerAdvice Classes

A controller advice allows you to use exactly the same exception handling techniques but apply them
across the whole application, not just to an individual controller.  You can think of them as an annotation
driven interceptor.

Any class annotated with <code>@ControllerAdvice</code> becomes a controller-advice and three types of method
are supported:

  * Exception handling methods annotated with <code>@ExceptionHandler</code>.
  * Model enhancement methods (for adding additional data to the model) annotated with
<code>@ModelAttribute</code>.  Note that these attributes are _not_ available to the exception handling views.
  * Binder initialization methods (used for configuring form-handling) annotated with
<code>@InitBinder</code>.

We are only going to look at exception handling - see the online manual for more on
<code>@ControllerAdvice</code> methods.

Any of the exception handlers you saw above can be defined on a controller-advice class - but now they
apply to exceptions thrown from <em>any</em> controller.  Here is a simple example:

```java
@ControllerAdvice
class GlobalControllerExceptionHandler {
    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleConflict() {
        // Nothing to do
    }
}
```

If you want to have a default handler for <em>any</em> exception, there is a slight wrinkle.  You need to ensure
annotated exceptions are handled by the framework.  The code looks like this:

```java
@ControllerAdvice
class GlobalDefaultExceptionHandler {
    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;

        // Otherwise setup and send the user to a default error-view.
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName(DEFAULT_ERROR_VIEW);
        return mav;
    }
}
```

##Going Deeper

###HandlerExceptionResolver

Any Spring bean declared in the <code>DispatcherServlet</code>'s application context that implements
<code>HandlerExceptionResolver</code> will be used to intercept and process any exception raised
in the MVC system and not handled by a Controller.  The interface looks like this:

```java
public interface HandlerExceptionResolver {
    ModelAndView resolveException(HttpServletRequest request, 
            HttpServletResponse response, Object handler, Exception ex);
}
```

The <code>handler</code> refers to the controller that generated the exception (remember that
<code>@Controller</code> instances are only one type of handler supported by Spring MVC.
For example: <code>HttpInvokerExporter</code> and the WebFlow Executor are also types of handler). 

Behind the scenes, MVC creates three such resolvers by default.  It is these resolvers that implement the
behaviours discussed above:

  * <code>ExceptionHandlerExceptionResolver</code> matches uncaught exceptions against for
suitable <code>@ExceptionHandler</code> methods on both the handler (controller) and on any controller-advices.
  * <code>ResponseStatusExceptionResolver</code> looks for uncaught exceptions
annotated by <code>@ResponseStatus</code> (as described in Section 1)
  * <code>DefaultHandlerExceptionResolver</code> converts standard Spring exceptions and converts them
to HTTP Status Codes (I have not mentioned this above as it is internal to Spring MVC).

These are chained and processed in the order listed (internally Spring creates a dedicated bean - the
<tt>HandlerExceptionResolverComposite</tt> to do this).

Notice that the method signature of ```resolveException``` does not include the ```Model```.  This is why
```@ExceptionHandler``` methods cannot be injected with the model.

You can, if you wish, implement your own <code>HandlerExceptionResolver</code> to setup your own custom
exception handling system. Handlers typically implement Spring's `Ordered` interface so you can define the
order that the handlers run in.

###SimpleMappingExceptionResolver

Spring has long provided a simple but convenient implementation of <code>HandlerExceptionResolver</code>
that you may well find being used in your appication already - the <code>SimpleMappingExceptionResolver</code>.
It provides options to:

  * Map exception class names to view names - just specify the classname, no package needed.
  * Specify a default (fallback) error page for any exception not handled anywhere else
  * Log a message (this is not enabled by default).
  * Set the name of the <code>exception</code> attribute to add to the Model so it can be used inside a View
(such as a JSP). By default this attribute is named ```exception```.  Set to ```null``` to disable.  Remember
that views returned from `@ExceptionHandler` methods _do not_ have access to the exception but views
defined to <code>SimpleMappingExceptionResolver</code> _do_.

Here is a typical configuration using XML:

```xml
    <bean id="simpleMappingExceptionResolver"
          class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
        <property name="exceptionMappings">
            <map>
                <entry key="DatabaseException" value="databaseError"/>
                <entry key="InvalidCreditCardException" value="creditCardError"/>
            </map>
        </property>
        <!-- See note below on how this interacts with Spring Boot -->
        <property name="defaultErrorView" value="error"/>
        <property name="exceptionAttribute" value="ex"/>
        
        <!-- Name of logger to use to log exceptions. Unset by default, so logging disabled -->
        <property name="warnLogCategory" value="example.MvcLogger"/>
    </bean>
```

Or using Java Configuration:

```java
@Configuration
@EnableWebMvc   // Optionally setup Spring MVC defaults if you aren't doing so elsewhere
public class MvcConfiguration extends WebMvcConfigurerAdapter {
    @Bean(name="simpleMappingExceptionResolver")
    public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver r =
              new SimpleMappingExceptionResolver();

        Properties mappings = new Properties();
        mappings.setProperty("DatabaseException", "databaseError");
        mappings.setProperty("InvalidCreditCardException", "creditCardError");

        r.setExceptionMappings(mappings);  // None by default
        r.setDefaultErrorView("error");    // No default
        r.setExceptionAttribute("ex");     // Default is "exception"
        r.setWarnLogCategory("example.MvcLogger");     // No default
        return r;
    }
    ...
}
```

The _defaultErrorView_ property is especially useful as it ensures any uncaught exception generates
a suitable application defined error page. (The default for most application servers is to display a Java
stack-trace - something your users should _never_ see).

###Extending SimpleMappingExceptionResolver

It is quite common to extend <code>SimpleMappingExceptionResolver</code> for several reasons:

  * Use the constructor to set properties directly - for example to enable exception logging and set the
logger to use
  * Override the default log message by overriding <code>buildLogMessage</code>. The default implementation
always returns this fixed text:<ul style="margin-left: 2em"><i>Handler execution resulted in exception</i></ul>
  * To make additional information available to the error view by overriding <code>doResolveException</code>

For example:

```java
public class MyMappingExceptionResolver extends SimpleMappingExceptionResolver {
    public MyMappingExceptionResolver() {
        // Enable logging by providing the name of the logger to use
        setWarnLogCategory(MyMappingExceptionResolver.class.getName());
    }

    @Override
    public String buildLogMessage(Exception e, HttpServletRequest req) {
        return "MVC exception: " + e.getLocalizedMessage();
    }
    
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception exception) {
        // Call super method to get the ModelAndView
        ModelAndView mav = super.doResolveException(request, response, handler, exception);
        
        // Make the full URL available to the view - note ModelAndView uses addObject()
        // but Model uses addAttribute(). They work the same. 
        mav.addObject("url", request.getRequestURL());
        return mav;
    }
}
```

This code is in the demo application as
<a href="https://github.com/paulc4/mvc-exceptions/blob/master/src/main/java/demo1/web/ExampleSimpleMappingExceptionResolver.java">ExampleSimpleMappingExceptionResolver</a>

###Extending ExceptionHandlerExceptionResolver

It is also possible to extend <code>ExceptionHandlerExceptionResolver</code> and override its
<code>doResolveHandlerMethodException</code> method in the same way. It has almost the same signature
(it just takes the new <code>HandlerMethod</code> instead of a <code>Handler</code>).

To make sure it gets used, also set the inherited order property (for example in the constructor of
your new class) to a value less than <code>MAX_INT</code> so it runs _before_ the default
ExceptionHandlerExceptionResolver instance (it is easier to create your own handler instance than try to
modify/replace the one created by Spring).  See
<a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/java/demo1/web/ExampleExceptionHandlerExceptionResolver.java">ExampleExceptionHandlerExceptionResolver</a>
in the demo app for more.

###Errors and REST

RESTful GET requests may also generate exceptions and we have already seen how we can return standard HTTP
Error response codes.  However, what if you want to return information about the error?  This is very easy to do.
Firstly define an error class:

```java
public class ErrorInfo {
    public final String url;
    public final String ex;

    public ErrorInfo(String url, Exception ex) {
        this.url = url;
        this.ex = ex.getLocalizedMessage();
    }
}
```

Now we can return an instance from a handler as the ```@ResponseBody``` like this:

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(MyBadDataException.class)
@ResponseBody ErrorInfo handleBadRequest(HttpServletRequest req, Exception ex) {
    return new ErrorInfo(req.getRequestURL(), ex);
} 
```

##What to Use When?

As usual, Spring likes to offer you choice, so what should you do?  Here are some rules of thumb.
However if you have a preference for XML configuration or Annotations, that's fine too.

<ul>
  <li>For exceptions you write, consider adding <code>@ResponseStatus</code> to them.
  <li>For all other exceptions implement an <code>@ExceptionHandler</code> method on a
      <code>@ControllerAdvice</code> class or use an instance of <code>SimpleMappingExceptionResolver</code>.
      You may well have <code>SimpleMappingExceptionResolver</code> configured for your application already,
      in which case it may be easier to add new exceptions to it than implement a <code>@ControllerAdvice</code>.
  <li>For Controller specific exception handling add <code>@ExceptionHandler</code> methods to your controller.
  <li><b>Warning:</b> Be careful mixing too many of these options in the same application.
   If the same exception can be
   handed in more than one way, you may not get the behavior you wanted. <code>@ExceptionHandler</code>
   methods on the Controller
   are always selected before those on any <code>@ControllerAdvice</code> instance.  It is <i>undefined</i>
   what order controller-advices are processed.
</ul>

##Sample Application and Spring Boot

A demonstration application can be found at <a href="http://github.com/paulc4/mvc-exceptions">github</a>.
It uses Spring Boot and Thymeleaf to build a simple web application.  Some explanation is needed ...

###About the Demo
The demo runs in one of two modes: _controller_ or _global_.  This is set via a boolean property in class
<a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/java/demo1/main/Main.java">Main</a>
which in turn enables a corresponding Spring Bean profile.

  * When <tt>Main.global</tt> is set to <tt>false</tt>, _controller_ mode is enabled. A
    <a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/java/demo1/web/ExceptionHandlingController.java">ExceptionHandlingController</a></tt>
    is created which handles all requests and also any exceptions generated.
  * When <tt>Main.global</tt> is set to <tt>true</tt>, _global_ mode is enabled. A
    <a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/java/demo1/web/ControllerWithoutExceptionHandlers.java">ControllerWithoutExceptionHandlers</a>
    is created to just handle requests.  Exceptions are handled globally by an instance of 
    <a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/java/demo1/web/GlobalControllerExceptionHandler.java">GlobalControllerExceptionHandler</a>
    which is a controller-advice.

In addition, a <code>SimpleMappingExceptionResolver</code> may optionally be defined.  Class `Main` has a
second property called `smerConfig` which can take one of three enumerated values:

  * `NONE`: No resolver defined.  Unhandled exceptions processed by the container.  Since we are using Tomcat
the familiar Tomcat error page with a full Java exception stack-trace is produced.
  * `XML`: A `SimpleMappingExceptionResolver` is configured using XML - see
<a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/resources/mvc-configuration.xml">mvc-configuration.xml</a>.
A Spring bean profile `xml-config` makes this happen.  If you prefer XML, this is the way to go.
  * `JAVA`: Also sets up a `SimpleMappingExceptionResolver` by enabling a different Spring profile, `java-config`,
using Java configuration. See
<a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/java/demo1/web/ExceptionConfiguration.java">ExceptionConfiguration</a>

A description of the most important files in the application is in the project's
<a href="http://github.com/paulc4/mvc-exceptions/blob/master/README.md">README.md</a>.

The one and only web-page is
<a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/resources/templates/index.html">index.html</a>
which:

 * Contains several links, all of which deliberately raise exceptions.
 * At the bottom of the page are links to Spring Boot endpoints for those interested in Spring Boot.
 
Thanks to Spring Boot, you can run this demo as a Java application or as a WAR in your favourite container.
Your choice. 

###Warning

  * This project is built using the latest (0.5.0 at time of writing) snapshot release of Spring Boot.
  * APIs may have since changed and this project may not build.
  * Check <a href="http://spring.io/spring-boot">http://spring.io/spring-boot</a> for snapshot, milestone
    and other releases.
  * Update the `pom.xml` and/or Java code if necessary.

###Spring Boot and Error Handling
<a href="http://spring.io/spring-boot">Spring Boot</a> allows a Spring project to be setup with
minimal configuration. Spring Boot creates sensible defaults automatically when it detects
certain key classes and packages on the classpath.  For example if it sees that you are using a Servlet
environment, it sets up Spring MVC with the most commonly used view-resolvers, hander mappings and so forth.
If it sees JSP and/or Thymeleaf, it sets up these view-layers.

Spring MVC offers no default (fall-back) error page out-of-the-box.  The most common way to set a default error
page has always been the <code>SimpleMappingExceptionResolver</code> (since Spring V1 in fact). However
Spring Boot also provides for a fallback error-handling page.

At start-up, Spring Boot tries to find a mapping for `/error`. (in the demo application this corresponds
to the `error` view which maps in turn to the `error.html` Thymeleaf template). If no
`/error` mapping can be found Spring Boot defines a fall-back error page - the so-called "Whitelabel Error Page"
(a minimal page with just the HTTP status information and any error details, such as the message from an uncaught
exception).  If you rename the `error.html` template to, say, `error2.html` then restart, you will see it being
used.

By defining a Java configuration `@Bean`method called `defaultErrorView()` you can
return your own error `View` instance. (see `ErrorMvcAutoConfiguration` for more information).

What if you are already using <code>SimpleMappingExceptionResolver</code> to setup a default
error view?  Simple, make sure the <code>defaultErrorView</code> maps to _/error_
to match the default path expected by Spring Boot.  In the demo application this corresponds to
`http://localhost/error` (when running as a Java application) or `http://localhost/mvc-exceptions/error` 
(when running as a WAR).  Make sure you are using Spring Boot
version <code>0.5.0.BUILD-SNAPSHOT</code> or later.  This does _not_ work with milestone
<code>0.5.0.M5</code> or earlier).  You can disable Spring boot's error page by setting the property
`error.whitelabel.enabled` to `false`.  There are examples of setting Spring Boot properties in the constructor of
<a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/java/demo1/main/Main.java">Main</a>

Note that in the demo the defaultErrorView property of the <code>SimpleMappingExceptionResolver</code> is
deliberately set to `defaultErrorPage` so you can see when the handler is generating the error page and when
Spring Boot is responsible. Normally both would be set to `error`.

Also in the demo application I show how to create a support-ready error page with a stack-trace hidden in the
HTML source (as a comment).  Turns out you cannot currently do this with Thymeleaf (next release they say)
so I have used JSP instead for just that page.  There is some additional configuration in the demo code to
allow JSP and Thymeleaf to work side by side (Spring Boot cannot set this up automatically - it needs application
specific information.  See Javadoc in
<a href="http://github.com/paulc4/mvc-exceptions/blob/master/src/main/java/demo1/web/ExtraThymeleafConfigurer.java">ExtraThymeleafConfigurer</a>
for details).



