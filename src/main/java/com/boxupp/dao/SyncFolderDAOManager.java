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
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.SyncFoldersBean;
import com.boxupp.responseBeans.StatusBean;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

public class SyncFolderDAOManager {
	private static Logger logger = LogManager.getLogger(SyncFolderDAOManager.class.getName());
	protected static Dao<SyncFoldersBean, Integer> syncFolderDao = null;
	private static SyncFolderDAOManager syncFolderDBManager = null;

	public static SyncFolderDAOManager getInstance(){
		if(syncFolderDBManager == null){
			syncFolderDBManager = new SyncFolderDAOManager();
		}
		return syncFolderDBManager;
		
	}
	
	private  SyncFolderDAOManager() {
		syncFolderDao = DAOProvider.getInstance().fetchDao(SyncFoldersBean.class);
	}
	public StatusBean save(MachineConfigurationBean machineConfig, JsonNode syncFolderData) {
		StatusBean statusBean = new StatusBean();
		
		Gson syncFolder = new Gson();
		try {
			for(JsonNode mapping : syncFolderData){
				SyncFoldersBean syncFolderMapping = syncFolder.fromJson(mapping.toString(), SyncFoldersBean.class);
				syncFolderMapping.setMachineConfig(machineConfig);
				syncFolderDao.create(syncFolderMapping);
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


	public StatusBean update(MachineConfigurationBean machineConfig, JsonNode syncFolderData) {
		StatusBean statusBean = new StatusBean();
		Gson syncFolder = new Gson();
		try {
				for(JsonNode mapping : syncFolderData){
					SyncFoldersBean syncFolderMapping = syncFolder.fromJson(mapping.toString(), SyncFoldersBean.class);
					syncFolderMapping.setMachineConfig(machineConfig);
					syncFolderDao.update(syncFolderMapping);
				}
			
		} catch (SQLException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error in updating sync Folder  : "+e.getMessage());
		}
		
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Sync Folder upadte successfully");
		return statusBean;
	}
	public  StatusBean delete(MachineConfigurationBean machineConfig) {
		StatusBean statusBean = new StatusBean();
			try { 
				DeleteBuilder<SyncFoldersBean, Integer> deleteBuilder = syncFolderDao.deleteBuilder();
				deleteBuilder.where().eq(SyncFoldersBean.MACHINE_ID_FIELD_NAME, machineConfig);
				deleteBuilder.delete();
			} catch (SQLException e) {
				statusBean.setStatusCode(1);
				statusBean.setStatusMessage("Error in deleting  sync Folder  : "+e.getMessage());
			}
		
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage(" sync Folder  deleting successfully");
		return statusBean;
	}
}
