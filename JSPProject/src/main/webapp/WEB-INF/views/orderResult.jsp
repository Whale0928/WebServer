<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
 <!--  html 주석 (개발자 도구에 노출됨).-->
 
 <%-- jsp 주석 (개발자 도구에 노출되지 않는다.)
  
  	<%@ $> : 지시자-> 알려주거나 지시하는 속성을 기입.
  	charset=UTF-8			 : 현재 문서를 해석할 때 UTF-8 문자인코딩을 이용해서 해석
  	pageEncoding ="UTF-8" 	 : 현재 문서가 UTF-8 문자 인코딩 형식으로 작성되었음.
  
  	<%  %> : 스크립틀릿(Scriptlet) -> JSP에서 자바코드를 작성할 수 잇는 영역 
  	-> JSTL라이브러리를 이용해 태그 형식으로 변경.
  	
  	<%= %> : 표현식(Expression)	   -> 자바 코드의 값을 HTML 형식으로 표현(출력).
  	-> EL (Expression Language) 을 이용해 간단하게 값 출력 가능
 --%> 
 
  <% //자바 코드 작성 영역(scriptlet,스크립 틀릿.) 
  int result= (int)request.getAttribute("res"); 
  /* String pizza=request.getParameter("pizza"); */
  String size = request.getParameter("size");
  String amount = request.getParameter("amount");
  %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>주문 결과</title>
<style>
	#area{
		font-size: 18px;
		font-weight: bold;
	}
	 h1{color:red}
</style>
</head>
<body>

	<!-- webapp 폴더 내부(html,css,jsp)등은 서버를 끄지 않아도 수정 가능 -->
	
	<div id="area">
		피자 : <%=request.getParameter("pizza") %><br>
		
		사이즈 : 
		<%-- <%=	(request.getParameter("size").equals("L")?"Large":"Regular")%> SIZE<br> --%>
		
		<%	if(size.equals("R")){ %>
		Regular
		<%} else { %>
		Large
		<%} %>		
		<br>
		수량  : <%=amount %> 개<br>
	</div>
	<h1>계산결과 : <%= result %>원</h1>
				<!-- 자바코드 영역안에 '='은 출력하겟다라는 의미 -->
				
	<% for(int i=1; i<=6;i++){ %>
		<h<%=i%>><%=i %>번 테스트</h<%=i%>>
	<% } %>			
</body>
</html>