package com.boxupp.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.boxupp.beans.LogBean;
import com.boxupp.utilities.VagrantUtilities;

@Path("/vagrantLog/")
public class VagrantLog {
	
	@GET
	@Path("/getVagrantLogFiles")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LogBean> getVagrantLog(@Context HttpServletRequest request) {
		return VagrantUtilities.getInstance().getVagrantLogs(request);
	}
	
	@GET
	@Path("/getVagrantLogFileContent")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVagrantLogFileContent(@Context HttpServletRequest request) {
		return VagrantUtilities.getInstance().getVagrantLogContent(request);
	}

}
