<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="mybean.data.BookMark"%>
<%@page import="myscript.connect.ReadRecordCon"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    <%
    request.setCharacterEncoding("UTF-8");
    String mode = request.getParameter("mode");
    if (mode != null && mode.equals("getReadRecord")) {
    	// 获取阅读记录
    	// 获得用户账户
    	String account = request.getParameter("account");
    	if (account == null) {
    		out.print("");
    		return;
    	}
    	// 实例化连接器
    	ReadRecordCon bmCon = new ReadRecordCon();
    	
    	// 获得连接
    	bmCon.getCon();
    	List<BookMark> bmList = bmCon.getReadRecordsByAccount(account);
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
		String process = request.getParameter("process_" + i);
		BookMark bm = new BookMark(account, bookId, chapterId, "", process, "");
		bmList.add(bm);
	}
	
	// 实例化连接器
	ReadRecordCon bmCon = new ReadRecordCon();
	// 获得连接
	bmCon.getCon();
	// 删除
	bmCon.delReadRecord(account);
   	// 添加书签
   	for (BookMark b : bmList) {
   		bmCon.getCon();
   		if (bmCon.addReadRecord(b)) {
   			out.print("");
   			//System.out.println("Add " + b.toString());
   		}
   	}
    %>