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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.boxupp.dao.ProviderDAOManager;
import com.boxupp.db.beans.ProviderBean;

@Path("/provider/")
public class Provider {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProviderBean> getAllProviders(){
		return ProviderDAOManager.getInstance().retireveProviders();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProviderBean getProviderData(@PathParam("id") String providerID){
		return ProviderDAOManager.getInstance().read(providerID);
	}
}
