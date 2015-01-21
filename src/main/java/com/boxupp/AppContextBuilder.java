/*******************************************************************************
 *  Copyright 2014 Paxcel Technologies
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *******************************************************************************/

package com.boxupp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class AppContextBuilder {
	private static Logger logger = LogManager.getLogger(AppContextBuilder.class.getName());
	private WebAppContext webAppContext;
	private ResourceHandler staticResourceContext;
	
	public WebAppContext getWebAppHandler(){
		
		webAppContext = new WebAppContext();
		webAppContext.setResourceBase(".");
		webAppContext.setContextPath("/boxupp/*");
		webAppContext.setDescriptor("/web.xml");
		logger.debug("webApp contextPath Initialized");
		
		return webAppContext;
	}
	
	public ResourceHandler getStaticResourceHandler(){
		
		staticResourceContext = new ResourceHandler();
		staticResourceContext.setDirectoriesListed(true);
		staticResourceContext.setWelcomeFiles(new String[]{ "index.html" });
		staticResourceContext.setResourceBase("./page");
		logger.debug("static contextPath Initialized");
		
		return staticResourceContext;
		
	}
	
	public ServletContextHandler getWebSocketHandler(){
		
		ServletContextHandler webSocketContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		return webSocketContext;
	}
}

















