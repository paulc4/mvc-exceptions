# Practical Experience Building a Web Application using Spring Boot

###Requirement

  1. Build a simple web-application to demonstrate my MVC Exceptions blog
      1. Present a web-page with 8 or 9 links, each showing an exception scenario
      1. Use a single controller to respond to each link. Each method deliberately thows an exception
      1. Implement various exception handling techniques (not important here - see blog for details)
      1. Implement the main page and the error pages using a suitable view technology
  1. Use <a href="http://www.thymeleaf.org">Thymeleaf</a> to generate all views
  1. Build application using <a href="http://spring.io/spring-boot">Spring Boot</a>
 
Simple enough? And mostly it was.  If I had to do it again it wouldn't take that long.  But there were a
few things that tripped me up on the way, mostly because I had never used Thymeleaf or Spring Boot before.
I hope this blog can save you that time.

###Thymeleaf

A view technology alternative to JSP.  All pages are well-formed HTML with markup using a Thymeleaf namespace.

For example, here is the interesting part of a page to display a table of contact information.  The controller
method will have added a collection of Contact objects to the model as the "contacts" attribute:

```html
	<table th:if="${not #lists.isEmpty(contacts)}">
		<tr>
			<th>First Name &nbsp;</th>
			<th>Surname</th>
		</tr>
		<tr th:each="contact : ${contacts}">
			<td th:text="${contact.firstName}">Sonya</td>
			<td th:text="${contact.lastName}">Lee</td>
			<td th:text="${contact.phone}">123 456 7890</td>
			...
		</tr>
	</table>
```

Spring Boot will automatically setup Thymeleaf as a Sping MVC view-technology provided it recognises it on the
classpath.  So the Thymeleaf jars must be dependencies to your project.

From my Maven pom.xml:

```xml
	<dependency>
		<groupId>org.thymeleaf</groupId>
		<artifactId>thymeleaf-spring3</artifactId>
	</dependency>
```

###Spring Boot

An easy way to setup and configure a Spring application using built-in defaults.  It detects certain
key classes on the classpath and generates the typical configuration of beans that most users would use.
It can setup:

1. Persistence using JPA and auto-generated repositories (see <a href="http://spring.io/spring-data-jpa">Spring Data JPA</a>).
1. A RESTful CRUD interface to the data exposed by the repositories (see <a href="http://spring.io/springrest">Spring REST</a>).
1. Spring MVC with default handler mappers, adapters, view resolvers ...
1. An embedded Tomcat or other container for running your Spring MVC application

Essentailly you just add Controllers (to hande URLs), Services (for the business logic) and your domain-model
Entities.

It seemed the ideal choice for an application with a single Controller (just like the getting started
example - <a href="http://spring.io/spring-boot/#quick-start">Spring Boot Getting Started</a>).




