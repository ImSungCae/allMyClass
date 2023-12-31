<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<%
	request.setCharacterEncoding("utf-8");
%>
<head>
<meta charset="UTF-8">
<title>글보기</title>
<style>
#tr_btn_modify {
	display: none;
}
</style>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">
                  function backToList(obj) {
                     obj.action = "${contextPath}/board/listArticles.do";
                     obj.submit();
                  }

                  function fn_enable(obj) {
                     document.getElementById("i_title").disabled = false;
                     document.getElementById("i_content").disabled = false;
                     document.getElementById("tr_btn_modify").style.display = "block";
                     document.getElementById("tr_btn").style.display = "none";
                     document.getElementById("i_imageFileName").disabled = false;
                  }

                  function fn_modify_article(obj) {
                     obj.action = "${contextPath}/board/modArticle.do";
                     obj.submit();
                  }

                  function fn_remove_article(url, articleNO) {
                	 // 자바스크립트를 이용해 동적으로 <form>태그를 생성
                     var form = document.createElement("form");
                     form.setAttribute("method", "post");
                     form.setAttribute("action", url);
                     console.log(form);
                     
                     /* 자바스크립트를 이용해 동적으로 <input> 태그를 생성한 후 name과
                     	value를 articleNO와 컨트롤러로 글 번호를 설정합니다. */
                     var articleNOInput = document.createElement("input");
                     articleNOInput.setAttribute("type", "hidden");
                     articleNOInput.setAttribute("name", "articleNO");
                     articleNOInput.setAttribute("value", articleNO);
	//				동적으로 생성된 <input> 태그를 동적으로 생성한 <form>태그에 append 합니다
                     form.appendChild(articleNOInput);
	//				<form> 태그를 <body>태그에 추가(append)한 후 서버에 요청합니다.
                     document.body.appendChild(form);
                     form.submit();

                  }
                  function readURL(input) {
                     if (input.files && input.files[0]) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                           $('#preview').attr('src', e.target.result);
                        }
                        reader.readAsDataURL(input.files[0]);
                     }
                  }  
                  function fn_reply_form(obj){
                	  obj.action = "${contextPath}/board/replyForm.do?parentNO=${article.articleNO}";
                      obj.submit();
             	 }
               </script>
</head>

<body>
	<form name="frmArticle" method="post" action="${contextPath}"
		enctype="multipart/form-data">
		<table border="0" align="center">
			<tr>
				<td width="150" align="center" bgcolor="#FF9933">글번호</td>
				<td><input type="text" value="${article.articleNO }" disabled />
					<input type="hidden" name="articleNO" value="${article.articleNO}" />
				</td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#FF9933">작성자 아이디</td>
				<td><input type=text value="${article.id }" name="writer"
					disabled /></td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#FF9933">제목</td>
				<td><input type=text value="${article.title }" name="title"
					id="i_title" disabled /></td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#FF9933">내용</td>
				<td><textarea rows="20" cols="60" name="content" id="i_content"
						disabled>${article.content
                           }</textarea></td>
			</tr>
				<tr>
					<td width="150" align="center" bgcolor="#FF9933" rowspan="2">
						이미지</td>
					<td><input type="hidden" name="originalFileName"
						value="${article.imageFileName }" />
			<c:if
				test="${not empty article.imageFileName && article.imageFileName!='null' }">
						 
						<img src="${contextPath}/download.do?articleNO=${article.articleNO}&imageFileName=${article.imageFileName}"
						id="preview" />
			</c:if>
			<c:if test="${empty article.imageFileName && article.imageFileName!='null' }">
				<img src="" id="preview">
			</c:if>
					<br>
					</td>
				</tr>
				<tr>
					<td><input type="file" name="imageFileName "
						id="i_imageFileName" disabled onchange="readURL(this);"/></td>
				</tr>
			<tr>
				<td width=20% align=center bgcolor=#FF9933>등록일자</td>
				<td><input type=text
					value="<fmt:formatDate value='${article.writeDate}' />" disabled />
				</td>
			</tr>
			<tr id="tr_btn_modify">
				<td colspan="2" align="center">
					<input type=button value="수정반영하기" onClick="fn_modify_article(frmArticle)"> 
					<input type=button value="취소" onClick="backToList(frmArticle)">
				</td>
			</tr>

			<tr id="tr_btn">
				<td colspan=2 align=center>
					<input type=button value="수정하기" onClick="fn_enable(this.form)"> 
					<input type=button value="삭제하기" onClick="fn_remove_article('${contextPath}/board/removeArticle.do', ${article.articleNO})">
					<input type=button value="리스트로 돌아가기" onClick="backToList(this.form)"> 
					<input type=button value="답글쓰기" onClick="fn_reply_form(this.form)">
				</td>
			</tr>
		</table>
	</form>
</body>

</html>