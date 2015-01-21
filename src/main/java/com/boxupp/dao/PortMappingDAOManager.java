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
package com.boxupp.dao;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import com.boxupp.db.DAOProvider;
import com.boxupp.db.beans.ForwardedPortsBean;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.responseBeans.StatusBean;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

public class PortMappingDAOManager {
	private static Logger logger = LogManager.getLogger(PortMappingDAOManager.class.getName());
	
	protected static Dao<ForwardedPortsBean, Integer> forwardedPortDao = null;
	
	private static PortMappingDAOManager portMappingDBManager = null;

	public static PortMappingDAOManager getInstance(){
		if(portMappingDBManager == null){
			portMappingDBManager = new PortMappingDAOManager();
		}
		return portMappingDBManager;
		
	}
	
	private  PortMappingDAOManager() {
		forwardedPortDao =  DAOProvider.getInstance().fetchDao(ForwardedPortsBean.class);
		
	}
	public StatusBean save(MachineConfigurationBean machineConfig, JsonNode portForwardData){
		StatusBean statusBean = new StatusBean();
		
		Gson portForwarded = new Gson();
		try{
			for(JsonNode mapping : portForwardData){
				ForwardedPortsBean forwardedPort = portForwarded.fromJson(mapping.toString(), ForwardedPortsBean.class);
				forwardedPort.setMachineConfig(machineConfig);
				forwardedPortDao.create(forwardedPort);
			}
		}
		catch (SQLException e) {
			logger.error("Error creating a new forwarded port mapping : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error in creating forwarded port :"+ e.getMessage());
		}
	
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Forwarded port create successfully");
		return statusBean;
		
	}
	public  StatusBean update(MachineConfigurationBean machineConfig, JsonNode portForwardData) {
		StatusBean statusBean = new StatusBean();
		Gson portForwarded = new Gson();
			try {
				for(JsonNode mapping : portForwardData){
					ForwardedPortsBean forwardedPort = portForwarded.fromJson(mapping.toString(), ForwardedPortsBean.class);
					forwardedPort.setMachineConfig(machineConfig);
					forwardedPortDao.update(forwardedPort);
				}
			} catch (SQLException e) {
				statusBean.setStatusCode(1);
				statusBean.setStatusMessage("Error in updating forwarded Port  : "+e.getMessage());
			}
		
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("forwarded port  update successfully");
		return statusBean;
	}
	public  StatusBean delete(MachineConfigurationBean machineConfig) {
		StatusBean statusBean = new StatusBean();
			try {
				DeleteBuilder<ForwardedPortsBean, Integer> deleteBuilder = forwardedPortDao.deleteBuilder();
				deleteBuilder.where().eq(ForwardedPortsBean.MACHINE_ID_FIELD_NAME, machineConfig);
				deleteBuilder.delete();
			} catch (SQLException e) {
				statusBean.setStatusCode(1);
				statusBean.setStatusMessage("Error in deleting forwarded Port  : "+e.getMessage());
			}
		
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("forwarded port  deleting successfully");
		return statusBean;
	}
	
}
