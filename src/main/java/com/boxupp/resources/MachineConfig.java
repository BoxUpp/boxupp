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

import com.boxupp.dao.MachineConfigDAOManager;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.responseBeans.StatusBean;
@Path("/machineConfig/")
public class MachineConfig {
	@POST 
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/json")
	public StatusBean saveMachineConfiguration(JsonNode mappings) {
		return MachineConfigDAOManager.getInstance().create(mappings);
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public MachineConfigurationBean getMachineConfig(@PathParam("id") String machineId) {
		return MachineConfigDAOManager.getInstance().read(machineId);
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/json")
	public StatusBean updateMachineConfiguration( JsonNode updatedmachineConfigData) {
		return MachineConfigDAOManager.getInstance().update(updatedmachineConfigData);
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public StatusBean deleteMachineConfiguration(@PathParam("id") String machineID) {
		return MachineConfigDAOManager.getInstance().delete(machineID);
	}
	
	@POST
	@Path("/stopMachine")
	@Produces(MediaType.APPLICATION_JSON)
	public StatusBean stopMachine(JsonNode machineData) {
		return MachineConfigDAOManager.getInstance().stop(machineData.get("machineID").toString());
	}
	
	@POST
	@Path("/reloadMachine")
	@Produces(MediaType.APPLICATION_JSON)
	public StatusBean reloadMachine(JsonNode machineData) {
		return MachineConfigDAOManager.getInstance().reload(machineData.get("machineID").toString());
	}
}
