<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html SYSTEM "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring3-3.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:th="http://www.thymeleaf.org">
<head>
<title>Exception Blog Demo Code</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<!-- 
   An error page with hidden stack-trace suitable for tech support.
 -->
<body>
	<h1>Example Support Error Page</h1>
	<p>Application has encountered an error. Please contact support on
		...</p>

	<c:if test="${timestamp}">
		<p id='created'>
			<b>Occurred:</b> ${timestamp}
		</p>
	</c:if>

	<c:if test="${status}">
		<p>
			<b>Response Status:</b> ${status}
			<c:if test="${error}">(${error})</c:if>
		</p>
	</c:if>

    <p>Support may ask you to right click to view page source</p>
    
	<!--
    Failed URL: ${url}
    Exception:  ${exception.message}
        <c:forEach items="${exception.stackTrace}" var="ste">    ${ste} 
    </c:forEach>
    -->
</body>
</html>
