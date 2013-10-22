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

<h2>Controller Based Exception Handling using @ExceptionHandler</h2>

You can add extra (<code>@ExceptionHandler</code>) methods to any controller to specifically handle exceptions
thrown by request handling (<code>@RequestMapping</code>) methods in the same controller.  Such methods can:

  1. Handle exceptions without the <code>@ResponseStatus</code> annotation (typically predefined exceptions
that you didn't write)
  2. Redirect the user to a customer error view
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

  // Total control - setup a model and return the view name yourself - may be easier than
  // subclassing ExceptionHandlerExceptionResolver.
  @ExceptionHandler(Exception.class)
  public ModelAndView handleError(HttpServletRequest req, Exception exception) {
    logger.error("Request: " + req.getRequestURI() + " raised " + exception);

    ModelAndView mav = new ModelAndView();
    mav.addObject("exception", exception);
    mav.addObject("url", req.getRequestURI());
    mav.setViewName("error");
    return mav;
  }
}
```

In any of these methods you might choose to do additional processing - the most common example is to log the
exception.

Handler methods have flexible signatures so you can pass in obvious servlet-related objects such
as HttpServletRequest, HttpServletResponse, HttpSession and/or Principle.  __Important Note:__ the
Model may __not__ be a parameter of any ExceptionHandler method.  Instead, setup a model inside the method
using a <code>ModelAndView</code> as shown by ```handleError()``` above.

<strong>Warning:</strong> Be careful when adding exceptions to the model.  Your users do not want to see
web-pages containing Java exception details and stack-traces. However, it can be useful to put exception
details in the page <em>source</em> as a comment, to assist your support people.  If using JSP, you could
do something like this to output the exception and the corresponding stack-trace (using a hidden
&lt;div/&gt; is another option).

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

The result looks like this: [INSERT IMAGE].

##Global Exception handling using @ControllerAdvice Classes

A controller advice allows you to use exactly the same exception handling techniques but apply them
across the whole application, not just to an individual controller.  You can think of them as an annotation
driven interceptor.

Any class annotated with <code>@ControllerAdvice</code> becomes a controller-advice and three types of method
are supported:

<ul>
<li>Exception handling methods annotated with <code>@ExceptionHandler</code></li>
<li>Model enhancement methods (for adding additional data to the model) annotated with
<code>@ModelAttribute</code></li>
<li>Binder initialization methods (used for configuring form-handling) annotated with
<code>@InitBinder</code></li>
</ul>

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
        mav.addObject("url", req.getRequestURI());
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
<code>@Cotnroller</code> instances are only one type of handler supported by Spring MVC.
For example: <code>HttpInvokerExporter</code> and the WebFlow Executor are also types of handler). 

Behind the scenes, MVC creates three such resolvers by default.  It is these resolvers that implement the
behaviours discussed above:

  * <code>ResponseStatusExceptionResolver</code> looks for uncaught exceptions
annotated by <code>@ResponseStatus</code> (as described in Section 1)
  * <code>DefaultHandlerExceptionResolver</code> converts standard Spring exceptions and converts them
to HTTP Status Codes (I have not mentioned this above as it is internal Spring MVC).
  * <code>ExceptionHandlerExceptionResolver</code> matches uncaught exceptions against for
suitable <code>@ExceptionHandler</code> methods on both the handler and on any controller-advices.

Notice that the method signature of ```resolveException``` does not include the ```Model```.  This is why
```@ExceptionHandler``` methods cannot be injected with the model.

You can, if you wish, implement your own <code>HandlerExceptionResolver</code> to setup your own custom
exception handling system.

###SimpleMappingExceptionResolver

Spring has long provided a simple but convenient implementation that you may well find being used in your appication
already - the <code>SimpleMappingExceptionResolver</code>.  It provides options to:

  * Map exception class names to view names - just specify the classname, no package needed.
  * Specify a default (fallback) error page for any exception not handled anywhere else
  * Log a message (this is not enabled by default).
  * Set the name of the <code>exception</code> attribute to add to the Model so it can be used inside a View
(such as a JSP). By default this attribute is named ```exception```.  Set to ```null``` to disable.

Here is a typical configuration using XML:

```xml
    <bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
        <property name="exceptionMappings">
            <map>
                <entry key="DataAccessException" value="databaseError"/>
                <entry key="InvalidCreditCardException" value="creditCardError"/>
            </map>
        </property>
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
    @Bean
    public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver r =
              new SimpleMappingExceptionResolver();

        Properties mappings = new Properties();
        mappings.setProperty("SimpleMappingExceptionResolver", "databaseError");
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
        
        // Make the URL available to the view - note ModelAndView uses addObject() but Model
        // uses addAttribute(). The work the same. 
        mav.addObject("url", request.getRequestURI());
        return mav;
    }
}
```

###Extending ExceptionHandlerExceptionResolver

It is also possible to extend <code>ExceptionHandlerExceptionResolver</code> and override its
<code>doResolveHandlerMethodException</code> method in the same way. It has almost the same signature
(it just takes the new <code>HandlerMethod</code> instead of a <code>Handler</code>).

To make sure it gets used, also set the inherited order property (for example in the constructor of
your new class) to a value less than <code>MAX_INT</code> so it runs _before_ the default
ExceptionHandlerExceptionResolver instance (it is easier to create your own handler instance than try to
modify/replace the one created by Spring.  See <code>ExampleExceptionHandlerExceptionResolver</code> in the
demo app for details.

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
    return new ErrorInfo(req.getRequestURI(), ex);
} 
```

##What to Use When?

As usual, Spring likes to offer you choice, so what should you do?  Here are some rules of thumb.
However if you have a preference for XML configuration or Annotations, that's fine too.

<ul>
  <li>For exceptions you write, add <code>@ResponseStatus</code> to them.
  <li>For all other exceptions implement an <code>@ExceptionHandler</code> on a
      <code>@ControllerAdvice</code> class or use an instance of <code>SimpleMappingExceptionResolver</code>.
      You may well have <code>SimpleMappingExceptionResolver</code> configured for your application already,
      in which case it may be easier to add new exceptions to it than implement a <code>@ControllerAdvice</code>.
  <li>For Controller specific exception handling add <code>@ExceptionHandler</code> on your controller.
  <li>Be careful mixing too many of these options in the same application.  If the same exception can be
   handed in more than one way, you may not get the behavior you wanted. <code>@ExceptionHandler</code>
   methods on the Controller
   are always selected before those on any <code>@ControllerAdvice</code> instance.  It is <i>undefined</i>
   what order controller-advices are processed.
</ul>

###Sample Application and Spring Boot

A demonstration application can be found at <a href="http://github.com/paulc4/mvc-exceptions">github</a>.
It uses Spring Boot and Thymeleaf to build a simple web application.  Some explanation is needed ...

<a href="htp://spring.io/spring-boot">Spring Boot</a> allows a Spring project to be setup with
minimal configuration. Spring Boot sets up the most commonly used defaults automatically when it detects
certain key classes and packages on the claspath.  For example if it sees that you are using a Servlet
environment, it sets up Spring MVC with the most commonly used view-resolvers, hander mappings and so forth.
If it sees JSP and/or Thymeleaf, it sets up these view-layers.

Spring MVC offers no default (fall-back) error page out-of-the-box.  The most common way to set a default error
page has always been the <code>SimpleMappingExceptionResolver</code> (since Spring V1 in fact). However
Spring Boot does provide a default error-handling page - the so-called "Whitelabel Error Page".  A minimal
page with just the HTTP status information and any error details (such as the message from an uncaught
exception).  By defining a <code>@Bean</code> method called <code>defaultErrorView()</code> you can
return your own error <code>View</code> instance.

What if you are already using <code>SimpleMappingExceptionResolver</code> to do this?  Simple, define
a <code>defaultErrorView()</code> method that returns <code>null</code>.  This stops Spring Boot setting
up a default error page, so the system falls back onto <code>SimpleMappingExceptionResolver</code> in the
usual way.

In the demo application I show how to create a support-ready error page with a stack-trace hidden in the
HTML source (as a comment).  Turns out you cannot currently do this with Thymeleaf (next release they tell me)
so I have used JSP instead for just that page.  There is some additional configuration in the demo code to
allow JSP and Thymeleaf to work side by side (Spring Boot cannot set this up automatically - it needs application
specific information.  See Javadoc in <code>ExtraThymeleadConfiguration</code> for details).

