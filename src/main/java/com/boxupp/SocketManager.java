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

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;



public class SocketManager {	
	/*private WebSocketHandler context;
	
	public SocketManager(WebSocketHandler context){
		this.context = context;
	}
	
	public ContextHandler initiate(){
		
		this.addWebSocket(VagrantConsole.class, "/appConsole/");
		this.addWebSocket(list,AppDataUpdater.class, "/appDataUpdater/");
	}*/
	
	/*public void addWebSocket(WebSocketHandler contextHandler,final Class<?> webSocket, String contextPath){
		contextHandler.addServlet(new ServletHolder(new WebSocketsServletFactory(webSocket)),contextPath);
		
	}*/
	
	public ContextHandler addWebSocket(final Class<?> webSocket, String contextPath){
		WebSocketHandler wsHandler = new WebSocketHandler(){
			@Override
            public void configure(WebSocketServletFactory webSocketServletFactory) {
                webSocketServletFactory.register(webSocket);
            }
		};
		
		ContextHandler wsContextHandler = new ContextHandler();
		wsContextHandler.setHandler(wsHandler);
		wsContextHandler.setContextPath(contextPath);
		return wsContextHandler;
	}
	
}
