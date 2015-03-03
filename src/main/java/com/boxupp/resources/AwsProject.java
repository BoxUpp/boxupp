package com.boxupp.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;

import com.boxupp.responseBeans.StatusBean;

@Path("/awsProject/")
public class AwsProject {

	@POST
	@Path("/authenticateCred")
	@Consumes(MediaType.APPLICATION_JSON)
	public StatusBean authenticateAwsCredentials(JsonNode data){
		System.out.println("api Hit "+data);
		return new StatusBean();
	}
}
