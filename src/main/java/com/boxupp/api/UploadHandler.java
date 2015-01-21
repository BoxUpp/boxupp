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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/uploadHandler")
public class UploadHandler {
	
	private static Logger logger = LogManager.getLogger(UploadHandler.class.getName());
	private static String selectedModule = "";
	private static boolean isModuleDefined;
	
	@POST
	@Path("/destination")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void setDestinationFolder(@Context HttpServletRequest request, @Context HttpServletResponse response){
		String destinationFolder = request.getParameter("loc");
		if(destinationFolder != null){
			UploadHandler.selectedModule = destinationFolder;
			UploadHandler.isModuleDefined = true;
		}else{
			UploadHandler.isModuleDefined = false;
		}
	}
	
}
