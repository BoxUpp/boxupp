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
package com.boxupp.ws;

import java.io.IOException;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.boxupp.responseBeans.VagrantOutput;
import com.boxupp.responseBeans.VagrantStreamError;
import com.boxupp.responseBeans.VagrantStreamOutput;
import com.boxupp.utilities.Utilities;
import com.boxupp.vagrant.VagrantCommandProcessor;
import com.google.gson.Gson;

@WebSocket
public class VagrantConsole implements OutputConsole{
	
	private static Logger logger = LogManager.getLogger(VagrantConsole.class.getName());
	private RemoteEndpoint remote;
	private static Gson gson = new Gson();
	
	@OnWebSocketConnect
    public void onConnect(Session session) {
		logger.info(session.getRemote()+ " : connected");
		this.remote = session.getRemote();
    }

	@OnWebSocketMessage
    public void onMessage(Session session,String command) throws IOException, InterruptedException {
		String[] commands = command.split(":");
		Integer userID = Integer.parseInt(commands[1]);
		command = commands[0];
        VagrantCommandProcessor shellProcessor = new VagrantCommandProcessor();
        String location = Utilities.getInstance().fetchActiveProjectDirectory(userID);
        if(command.toLowerCase(Locale.ENGLISH).indexOf("vagrant")!= -1 ){
        	shellProcessor.executeVagrantFile(location,command,userID, this);
		}else{
			this.pushError("Not a valid Vagrant command");
			this.pushDataTermination();
		}
        
    }
	
	@OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        logger.info("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        logger.error("Error: " + t.getMessage());
    }

	@Override
	public void pushOutput(String data) {
		
		VagrantStreamOutput vagrantStreamOutput = new VagrantStreamOutput();
		vagrantStreamOutput.setOutput(data);
		commitOutput(vagrantStreamOutput);
	}

	@Override
	public void pushError(String data) {
		
		VagrantStreamError vagrantStreamError = new VagrantStreamError();
		vagrantStreamError.setOutput(data);
		commitOutput(vagrantStreamError);
	}

	@Override
	public void pushDataTermination() {
		
		VagrantStreamOutput vagrantStreamOutput = new VagrantStreamOutput();
		vagrantStreamOutput.setOutput("Execution Completed");
		vagrantStreamOutput.setDataEnd(true);
		commitOutput(vagrantStreamOutput);
		
		
	}
	
	public void commitOutput(VagrantOutput output){
		try{
			remote.sendString(gson.toJson(output));
		}
		catch(IOException e){
			logger.error("Error committing output to console : "+e.getMessage());
		}
	}

}
