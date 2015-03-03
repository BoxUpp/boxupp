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
package com.boxupp.resources;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.boxupp.dao.AwsProjectDAOManager;
import com.boxupp.dao.MachineConfigDAOManager;
import com.boxupp.dao.ProjectDAOManager;
import com.boxupp.dao.ShellScriptDAOManager;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.ProjectBean;
import com.boxupp.db.beans.PuppetModuleBean;
import com.boxupp.db.beans.PuppetModuleMapping;
import com.boxupp.db.beans.ShellScriptBean;
import com.boxupp.db.beans.ShellScriptMapping;
import com.boxupp.responseBeans.StatusBean;
import com.boxupp.responseBeans.VagrantFileStatus;
import com.boxupp.utilities.Utilities;

@Path("/project/")
public class Project {
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProjectBean retrieveProject(@PathParam("id") String projectId){
		return (ProjectBean) ProjectDAOManager.getInstance().read(projectId);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/json")
	public StatusBean createNewProject(JsonNode newProjectData){
		return ProjectDAOManager.getInstance().create(newProjectData);
	}
	
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public StatusBean deleteProject(@PathParam("id") String projectID){
		return ProjectDAOManager.getInstance().delete(projectID);
	}
	
	@GET
	@Path("/getScripts/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ShellScriptBean> getAllShellScriptsList(@PathParam("id") String projectID){
		return ShellScriptDAOManager.getInstance().retireveScriptsForProject(projectID);
	}
	@GET
	@Path("/getBoxes/{id}")
	@JsonIgnore
	@Produces(MediaType.APPLICATION_JSON)
	public List<MachineConfigurationBean> getAllMachinConfigsList(@PathParam("id") String projectID){
		return  MachineConfigDAOManager.getInstance().retireveBoxesForProject(projectID);
	}

	@GET
	@Path("/getAllModules/")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PuppetModuleBean> getAllPuppetModulesList(){
		return  ProjectDAOManager.getInstance().retireveAllModules();
	}
	
	@GET
	@Path("/getModuleMapping/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PuppetModuleMapping> getAllModuleMapping(@PathParam("id") String projectID) {
		return ProjectDAOManager.getInstance().retireveModulesMapping(projectID);
	}
	
	@GET
	@Path("/getScriptMappping/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ShellScriptMapping> getAllScriptMapping(@PathParam("id") String projectID) {
		return ProjectDAOManager.getInstance().retireveScriptsMapping(projectID);
	}
	
	@POST
	@Path("/createVagrantFile")
	@Consumes(MediaType.APPLICATION_JSON)
	public VagrantFileStatus saveAsFile(JsonNode VagrantFileData) throws IOException
	{
		String projectID = VagrantFileData.get("projectID").getTextValue();
		String userID = VagrantFileData.get("userID").getTextValue();
		return Utilities.getInstance().saveVagrantFile(projectID, userID);
	}
		
	@POST
	@Path("/authenticateCred")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/json")
	public StatusBean authenticateAwsCredentials(JsonNode awsCredentials){
		return AwsProjectDAOManager.getInstance().authenticateAwsCredentials(awsCredentials);
	}

	
	
}
