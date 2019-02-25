<%@page import="mybean.data.User"%>
<%@page import="myscript.connect.UserCon"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%
    // 注册
    String account = request.getParameter("account");
    String password = request.getParameter("password");
    if (account != null && password != null) {
    	UserCon uCon = new UserCon();
    	uCon.getCon();
    	if (uCon.addUser(new User(account.trim(), password.trim()))) {
    		// 如果注册成功
    		out.print("1");
    	} else {
    		// 注册失败
    		out.print("0");
    	}
    } else {
    	// 注册失败
		out.print("0");
    }
    %>