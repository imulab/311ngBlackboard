<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
	<title>311ngBlackboard:test:unsubscribe</title>
	
	<script src="js/jquery-1.8.2.min.js"></script>
	<script src="js/json2.js"></script>
</head>

<body style="margin-top: 20px; margin-left: 20px">
	<p>
		<a href="../test/main">Back to Test Main</a>
	</p>
	
	<hr/>
	
	<form>
		<input id="instance" type="text" placeholder="instance to unsubscribe">
		<input id="class" type="text" placeholder="class to unsubscribe from">
	</form>
	
</body>
</html>