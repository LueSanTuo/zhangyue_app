<%@page import="mybean.data.BookMark"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="myscript.connect.BookMarkCon"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%
    request.setCharacterEncoding("UTF-8");
    String mode = request.getParameter("mode");
    if (mode != null && mode.trim().equals("getBookMarks")) {
    	// 获取书签
    	// 获得用户账户
    	String account = request.getParameter("account");
    	if (account == null) {
    		out.print("");
    		return;
    	}
    	// 实例化连接器
    	BookMarkCon bmCon = new BookMarkCon();
    	// 获得连接
    	bmCon.getCon();
    	List<BookMark> bmList = bmCon.getBookMarksByAccount(account);
    	int size = bmList.size();
        if (size > 0) {
        	BookMark lastBook = bmList.get(size - 1);
    	    out.print("[");
    	    for(BookMark b : bmList) {
    	    	out.print(b.toString());
    	    	if (b == lastBook)
    				break;
    	    	out.print(",");
    	    }
    	    out.print("]");
        }
    	return;
    }
    // 处理书签
    String sizeStr = request.getParameter("size");
	if (sizeStr == null || sizeStr.equals("")) {
		out.print("");
		return;
	}
	String account = request.getParameter("account");
	List<BookMark> bmList = new ArrayList<>();
	int size = Integer.parseInt(sizeStr);
	
	for (int i = 0; i < size; i++) {
		String bookId = request.getParameter("bookId_" + i);
		String chapterId = request.getParameter("chapterId_" + i);
		String firstLine = request.getParameter("firstLine_" + i);
		String process = request.getParameter("process_" + i);
		String date = request.getParameter("date_" + i);
		BookMark bm = new BookMark(account, bookId, chapterId, firstLine, process, date);
		bmList.add(bm);
	}
	
	// 实例化连接器
	BookMarkCon bmCon = new BookMarkCon();
	// 获得连接
	bmCon.getCon();
	// 删除用户所有书签 并重新添加
	bmCon.delBookMark(account);
    // 添加书签
   	for (BookMark b : bmList) {
   		bmCon.getCon();
   		if (bmCon.addBookMark(b)) {
   			out.print("");
   			//System.out.println("Add " + b.toString());
   		}
   	}
    %>