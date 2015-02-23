package com.boxupp.resources;

import java.security.NoSuchAlgorithmException;

import javax.servlet.*;
import javax.servlet.http.*;


public class SessionTracker extends HttpServlet {

	private static SessionTracker sessionTracker;

	public static SessionTracker getInstance(){
		if(sessionTracker == null){
			sessionTracker = new SessionTracker();
		}
		return sessionTracker;
	}

	public void createSession(HttpServletRequest request,String userID)
			throws ServletException, NoSuchAlgorithmException
	{
		HttpSession session = request.getSession(true);
		if (session.isNew()){
			session.setMaxInactiveInterval(3600);
		} 	
	}

	public void destroySession(HttpServletRequest request){
		HttpSession session = request.getSession();
		session.invalidate();
	}

	public Boolean isSessionActive(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session==null){
			return false;
		}
		else{
			return true;
		}
	}
}