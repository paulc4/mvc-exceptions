<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html SYSTEM "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring3-3.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:th="http://www.thymeleaf.org">
<head th:fragment="copy">
<title>Exception Blog Demo Code</title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta
	content="width=device-width, initial-scale=1.0, maximum-scale=1, minimum-scale=1, user-scalable=no"
	id="Viewport" name="viewport" />

<!-- Bootstrap -->
<link rel="stylesheet" media="screen"
	href="//netdna.bootstrapcdn.com/bootstrap/3.0.1/css/bootstrap.min.css" />

<!-- Optional theme -->
<link rel="stylesheet"
	href="//netdna.bootstrapcdn.com/bootstrap/3.0.1/css/bootstrap-theme.min.css" />

<link rel="stylesheet" href="/styles.css" />

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]
	      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
	      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
	    <![endif]-->
</head>

<body th:fragment="copy" class="container">
	<nav class="navbar navbar-default navbar-inverse" role="navigation">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#bs-example-navbar-collapse-1">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span>
				<!-- Logo -->
				<span class="icon-bar"></span>
				<!-- Home -->
				<span class="icon-bar"></span>
				<!-- This Blog -->
				<span class="icon-bar"></span>
				<!-- Other Blogs -->
			</button>
			<a alt="Spring IO" title="Spring IO" href="http://www.spring.io"><img
				height="50" src="spring-trans.png" /></a> &nbsp;&nbsp;&nbsp;
		</div>

		<!-- Collect the nav links, forms, and other content for toggling -->
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<c:url var="home" value="/"></c:url>
				<li class="active"><a href="${home}">Home</a></li>
				<li><a
					href="https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc">This
						Blog</a></li>
				<li><a href="http://spring.io/blog">Other Blogs</a></li>

			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="http://www.gopivotal.com"><img alt="Pivotal"
						title="Pivotal" height="25" src="pivotal-logo-600.png" /></a></li>

			</ul>
		</div>
		<!-- /.navbar-collapse -->
	</nav>

	<!-- 
   An error page with hidden stack-trace suitable for tech support.
 -->
	<h1>Example Support-Ready Error Page</h1>

	<!-- You could use ${requestScope['javax.servlet.forward.request_uri']} 
	     but it's a lot more verbose and doesn't give you the full page URL. -->
	<c:if test="${not empty url}">
		<p>
			<b>Page:</b> ${url}
		</p>
	</c:if>

	<c:if test="${not empty timestamp}">
		<p id='created'>
			<b>Occurred:</b> ${timestamp}
		</p>
	</c:if>

	<c:if test="${not empty status}">
		<p>
			<b>Response Status:</b> ${status}
			<c:if test="${error}">(${error})</c:if>
		</p>
	</c:if>

	<p>Application has encountered an error. Please contact support on
		...</p>

	<p>Support may ask you to right click to view page source.</p>

	<!--
    Failed URL: ${url}
    Exception:  ${exception.message}
        <c:forEach items="${exception.stackTrace}" var="ste">    ${ste} 
    </c:forEach>
    -->

	<footer th:fragment="copy">&copy; 2013 Pivotal Inc</footer>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script th:fragment="copy" src="https://code.jquery.com/jquery.js"></script>

	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script th:fragment="copy"
		src="//netdna.bootstrapcdn.com/bootstrap/3.0.1/js/bootstrap.min.js"></script>
</body>
</html>
