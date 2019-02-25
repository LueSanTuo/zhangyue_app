<%@page import="mybean.data.User"%>
<%@page import="myscript.connect.UserCon"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    <%
    // 登录
    String account = request.getParameter("account");
    String password = request.getParameter("password");
    if (account != null && password != null) {
    	UserCon uCon = new UserCon();
    	uCon.getCon();
    	User user = uCon.getUserByAccount(account);
    	if (user != null) {
    		// 密码校验
    		if (password.trim().equals(user.getPassword())) {
    			// 如果登录成功
    			// 传回用户的信息
    			out.print("[");
    			out.print(user.toString());
    			out.print("]");
    		}
    	} else {
    		// 登录失败
    		out.print("");
    	}
    } else {
    	// 登录失败
		out.print("");
    }
    %>