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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;

import com.boxupp.dao.PuppetModuleDAOManager;
import com.boxupp.db.beans.PuppetModuleBean;
import com.boxupp.db.beans.SearchModuleBean;
import com.boxupp.responseBeans.StatusBean;
import com.boxupp.utilities.PuppetUtilities;

@Path("/puppetModule/")
public class PuppetModule {
	@POST 
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/json")
	public StatusBean savePuppetModule(JsonNode newPuppetModuleData) {
		return PuppetModuleDAOManager.getInstance().create(newPuppetModuleData);
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PuppetModuleBean> getPuppetModules(@PathParam("id") String puppetModuleId) {
		return PuppetModuleDAOManager.getInstance().read(puppetModuleId);
	}
	
	@POST 
	@Path("/updatePuppetModule")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/json")
	public StatusBean updatePuppetModule(JsonNode updatedPuppetModuleData) {
		return PuppetModuleDAOManager.getInstance().update(updatedPuppetModuleData);
	}
	
	@DELETE 
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public StatusBean deletePuppetModule(@PathParam("id") String puppetModuleId) {
		return PuppetModuleDAOManager.getInstance().delete(puppetModuleId);
	}
	
	/*@GET
	@Path("/deletePuppetModule")
	@Produces(MediaType.APPLICATION_JSON)
	public StatusBean deletePuppetModule(@Context HttpServletRequest request) {
		String puppetModuleID = request.getParameter("moduleID");
		Integer userID = Integer.parseInt(request.getParameter("userID"));
		return PuppetModuleDAOManager.getInstance().delete(puppetModuleID);
	}*/
	
	/*@POST
	@Path("/linkModule")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/json")
	public StatusBean savePuppetMapping(JsonNode moduleMachineMapping){
		return PuppetModuleDAOManager.getInstance().linkModuleWithMachine(moduleMachineMapping);
	}
	
	@POST
	@Path("/deLinkModule")
	@Produces(MediaType.APPLICATION_JSON)
	public StatusBean deleteModuleMapping(JsonNode moduleMachineMapping) {
		return PuppetModuleDAOManager.getInstance().deLinkModuleWithMachine(moduleMachineMapping);
	}*/
	@POST
	@Path("/updateModuleMappings")
	@Produces(MediaType.APPLICATION_JSON)
	public StatusBean updateModuleMapping(JsonNode moduleMachineMapping) {
		return PuppetModuleDAOManager.getInstance().updateModuleMapping(moduleMachineMapping);
	}
	@GET
	@Path("/searchPuppetModule")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SearchModuleBean> searchPuppetModule(@Context HttpServletRequest request) {
		return PuppetUtilities.getInstance().searchModule(request);
	}

	@POST
	@Path("/downloadPuppetModule")              
	@Produces(MediaType.APPLICATION_JSON)
	public StatusBean downloadPuppetModule(JsonNode moduleData) {
		return PuppetUtilities.getInstance().downloadModule(moduleData);
		
	}


}
