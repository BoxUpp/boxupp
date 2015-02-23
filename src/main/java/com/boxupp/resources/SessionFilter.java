package com.boxupp.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionFilter implements Filter  {

	private ArrayList<String> avoidSessionUrlList; 

	public void init(FilterConfig config) throws ServletException { 
		String urls = config.getInitParameter("avoid-urls");
		StringTokenizer token = new StringTokenizer(urls,",");
		avoidSessionUrlList = new ArrayList<String>();

		while (token.hasMoreTokens()) {
			avoidSessionUrlList.add(token.nextToken());
		}
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String curentUrl = request.getPathInfo();
		boolean isUrlAllowed = false;
		if(avoidSessionUrlList.contains(curentUrl)) {
			isUrlAllowed = true;
		}

		if (!isUrlAllowed) {
			HttpSession session = request.getSession(false);
			if (session == null) {
				response.sendError(401);
				return;
			}
		}

		chain.doFilter(req, res);        
	}

	public void destroy() {
	}

}
