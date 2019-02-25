<%@page import="myutil.ReadFromFile"%>
<%@page import="java.util.List"%>
<%@page import="myscript.connect.*"%>
<%@page import="mybean.data.*" %>
<%@page import="java.util.Iterator"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

	<% 
	// 书籍的id
	String bookId = request.getParameter("bookId");
	// 章节目录
	// 章节的id
	String chapterId = request.getParameter("chapterId"); //"chapter3";
	System.out.println(chapterId);
    // 读取书籍文件夹里的内容
	String content = ReadFromFile.readFileByLines(ReadFromFile.DEFAULT_BOOK_PATH + bookId + "/chapter" + chapterId + ".txt");
	out.print(content);
	%>