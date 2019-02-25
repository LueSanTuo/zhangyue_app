<%@page import="mybean.data.Book"%>
<%@page import="java.util.List"%>
<%@page import="myscript.connect.BookCon"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%
    // 获取满足条件的书们
    BookCon bCon = new BookCon();
    bCon.getCon();
    List<Book> bList = bCon.getBooks();
    int size = bList.size();
    if (size > 0) {
	    Book lastBook = bList.get(size - 1);
	    out.print("[");
	    for(Book b : bList) {
	    	out.print(b.toString());
	    	if (b == lastBook)
				break;
	    	out.print(",");
	    }
	    out.print("]");
    }
    %>