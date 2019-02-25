
<%@page import="java.util.List"%>
<%@page import="myutil.ReadFromFile"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%
    // 获取书籍的目录
    // 获得书籍的id
    String bookId = request.getParameter("bookId");
    List<String> content = ReadFromFile.getFirstLines(ReadFromFile.DEFAULT_BOOK_PATH + bookId + "/");
    if (content.size() <= 0)
    	out.println("");
    else {
    	for (String s : content) {
    		out.println(s + "\n");
    	}
    }
    %>