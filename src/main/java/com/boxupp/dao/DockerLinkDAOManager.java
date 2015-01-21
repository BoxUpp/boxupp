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
import com.boxupp.db.beans.DockerLinkBean;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.responseBeans.StatusBean;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

public class DockerLinkDAOManager {
	private static Logger logger = LogManager.getLogger(DockerLinkDAOManager.class.getName());
	protected  static Dao<DockerLinkBean, Integer> dockerLinkDao = null;
	private static DockerLinkDAOManager dockerLinkDBManager = null;

	public static DockerLinkDAOManager getInstance(){
		if(dockerLinkDBManager == null){
			dockerLinkDBManager = new DockerLinkDAOManager();
		}
		return dockerLinkDBManager;
		
	}
	
	private  DockerLinkDAOManager() {
		dockerLinkDao = DAOProvider.getInstance().fetchDao(DockerLinkBean.class);
	}
	
	public StatusBean save(MachineConfigurationBean machineConfig, JsonNode dockerLinkMapping) {
		StatusBean statusBean = new StatusBean();
		//DockerLinkBean dockerLink = new DockerLinkBean();
		Gson dockerLink = new Gson();
		
		try {
			for(JsonNode mapping : dockerLinkMapping){
				DockerLinkBean dockerLinkBean = dockerLink.fromJson(mapping.toString(), DockerLinkBean.class);
				dockerLinkBean.setMachineConfig(machineConfig);
				dockerLinkDao.create(dockerLinkBean);
			}
		} catch (SQLException e) {
			logger.error("Error creating a new sync folder mapping : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error in creating sync folder mapping :"+ e.getMessage());

		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("sync folder mapping create successfully");
		return statusBean;
	}
	public StatusBean update(MachineConfigurationBean machineConfig, JsonNode dockerLinkMapping) {
		StatusBean statusBean = new StatusBean();
		Gson dockerLink = new Gson();
			try {
				for(JsonNode mapping : dockerLinkMapping){
					DockerLinkBean dockerLinkBean = dockerLink.fromJson(mapping.toString(), DockerLinkBean.class);
					dockerLinkBean.setMachineConfig(machineConfig);
					dockerLinkDao.update(dockerLinkBean);
				}
			} catch (SQLException e) {
				statusBean.setStatusCode(1);
				statusBean.setStatusMessage("Error in dockerLink mapping update : "+e.getMessage());
			}
		
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Docker link  update successfully");
		return statusBean;
	}
	
	public  StatusBean delete(MachineConfigurationBean machineConfig) {
		StatusBean statusBean = new StatusBean();
			try { 
				DeleteBuilder<DockerLinkBean, Integer> deleteBuilder = dockerLinkDao.deleteBuilder();
				deleteBuilder.where().eq(DockerLinkBean.MACHINE_ID_FIELD_NAME, machineConfig);
				deleteBuilder.delete();
			} catch (SQLException e) {
				statusBean.setStatusCode(1);
				statusBean.setStatusMessage("Error in deleting  dockerLink mapping  : "+e.getMessage());
			}
		
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage(" dockerLink mapping deleting successfully");
		return statusBean;
	}
}
