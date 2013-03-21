<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
	<title>311ngBlackboard:test:query</title>
	
	<script src="http://localhost:8080/311ngBlackboard/js/jquery-1.8.2.min.js"></script>
	<script src="http://localhost:8080/311ngBlackboard/js/json2.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#test1").click(function(){
				alert("About to fire select ?s ?p ?o {?s ?p ?o}");
				$.ajax({
					url : "http://localhost:8080/311ngBlackboard/rest/request/query",
					type : "post",
					data : {
						username : "david",
						password : "19890117",
						query : "select ?s ?p ?o {?s ?p ?o}"
						},
					success : function(msg) {
						$("#result").html(JSON.stringify(msg));
					},
					error : function(msg) {
						alert(msg);
					}
				});
			});
		});
	</script>
</head>

<body style="margin-top: 20px; margin-left: 20px">
	<p>
		<a href="../test/main">Back to Test Main</a>
	</p>
	
	<a id="test1" href="#">Test Query "select ?s ?p ?o {?s ?p ?o}"</a>
	
	<p id="result">
	</p>
</body>
</html>