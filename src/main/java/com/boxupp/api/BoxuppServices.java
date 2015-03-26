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
package com.boxupp.api;


import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.boxupp.FileManager;
import com.boxupp.VagrantOutputStream;
import com.boxupp.dao.ProjectDAOManager;
import com.boxupp.responseBeans.BoxURLResponse;
import com.boxupp.responseBeans.StatusBean;
import com.boxupp.responseBeans.VagrantFile;
import com.boxupp.responseBeans.VagrantOutput;
import com.boxupp.responseBeans.VagrantStatus;
import com.boxupp.utilities.OSProperties;
import com.boxupp.utilities.Utilities;
import com.boxupp.vagrant.VagrantCommandProcessor;
import com.boxupp.ws.OutputConsole;

@Path("/")
public class BoxuppServices {
	
	private static Logger logger = LogManager.getLogger(BoxuppServices.class.getName());
	
	@GET
	@Path("/selectProject")
	@Produces(MediaType.APPLICATION_JSON)
	public StatusBean activateProject(@Context HttpServletRequest request){
		Integer projectID = Integer.parseInt(request.getParameter("projectID"));
		Integer userID = Integer.parseInt(request.getParameter("userID"));
		StatusBean statusBean = new StatusBean();
		try {
			statusBean.setData(ProjectDAOManager.getInstance().projectDao.queryForId(projectID));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Utilities.getInstance().changeActiveDirectory(userID,projectID);
		statusBean.setStatusCode(0);
		return statusBean;
	}
	
	@GET
	@Path("/getStream")
	@Produces(MediaType.APPLICATION_JSON)
	public VagrantOutput getStream() throws IOException{
		return VagrantOutputStream.pop();
	}
	
	@GET
	@Path("/getVagrantFile")
	@Produces(MediaType.APPLICATION_JSON)
	public VagrantFile getVagrantFile(@Context HttpServletRequest request) throws IOException{
		FileManager manager = new FileManager();
		String projectID = request.getParameter("projectID");
		String userID = request.getParameter("userID");
		return manager.fetchVagrantFileData(projectID, userID);
	}
	
	@GET
	@Path("/boxupp")
	public String runVagrantFile(@Context HttpServletRequest request) throws IOException, InterruptedException{
		Integer userID = Integer.parseInt(request.getParameter("userID"));
		String projectDir = Utilities.getInstance().fetchActiveProjectDirectory(userID);
		Utilities.getInstance().checkIfDirExists(new File(projectDir+OSProperties.getInstance().getOSFileSeparator()+OSProperties.getInstance().getLogDirName()));
		VagrantCommandProcessor shellProcessor = new VagrantCommandProcessor();
		String location = Utilities.getInstance().fetchActiveProjectDirectory(userID);
		String command = request.getParameter("command");
		if(command.toLowerCase(Locale.ENGLISH).indexOf("vagrant")!= -1 ){
			shellProcessor.executeVagrantFile(location,command,userID, new VagrantOutputStream());
		}else{
			OutputConsole console = new VagrantOutputStream();
			console.pushError("Not a valid Vagrant command");
			console.pushDataTermination();
		}
		return "";
	}

	/*@GET
	@Path("/projectConfig")
	@Produces(MediaType.APPLICATION_JSON)
	
	public ProjectConfig checkProjectConfig(@Context HttpServletRequest request,@Context HttpServletResponse response){
		ProjectConfig config = new ProjectConfig();
		String port = PropertyReader.getInstance().getProperty("port");
		if(port != null){
			config.setProjectPort(Integer.parseInt(port));
		}else{
			config.setProjectPort(8585);
		}
		return config;
	}*/

	@GET
	@Path("/checkVagrantStatus")
	@Produces(MediaType.APPLICATION_JSON)
	
	public VagrantStatus checkVagrantStatus(@Context HttpServletRequest request){
		Integer userID = Integer.parseInt(request.getParameter("userID"));
		VagrantCommandProcessor shellProcessor = new VagrantCommandProcessor();
		String location = Utilities.getInstance().fetchActiveProjectDirectory(userID);
		return shellProcessor.checkVagrantStatus(location);
	}
	
	@GET
	@Path("/checkMachineStatus")
	@Produces(MediaType.APPLICATION_JSON)
	
	public VagrantStatus checkMachineStatus(@Context HttpServletRequest request){
		Integer userID = Integer.parseInt(request.getParameter("userID"));
		String vagrantID = request.getParameter("vagrantID");
		VagrantCommandProcessor commandProcessor = new VagrantCommandProcessor();
		String location = Utilities.getInstance().fetchActiveProjectDirectory(userID);
		return commandProcessor.checkMachineStatus(location,vagrantID);
	}
	
	@GET
	@Path("/checkURL")
	@Produces(MediaType.APPLICATION_JSON)
	
	public BoxURLResponse checkDownloadURL(@Context HttpServletRequest request,@Context HttpServletResponse response) {
		
		BoxURLResponse urlResponse = null;
		String url = request.getParameter("boxURL");
		try{
			URL boxURL = new URL(url);
			HttpURLConnection boxURLConnection = (HttpURLConnection) boxURL.openConnection();
			urlResponse = new BoxURLResponse();
			urlResponse.setContentLength(boxURLConnection.getContentLength());
			urlResponse.setStatusCode(boxURLConnection.getResponseCode());
			return urlResponse;
		}
		catch(Exception e){
			urlResponse = new BoxURLResponse();
			urlResponse.setContentLength(0);
			urlResponse.setStatusCode(-1);
		}
		return urlResponse;
	}
	
}
