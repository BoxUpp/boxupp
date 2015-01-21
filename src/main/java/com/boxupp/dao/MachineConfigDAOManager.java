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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import com.boxupp.VagrantOutputStream;
import com.boxupp.db.DAOProvider;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.MachineProjectMapping;
import com.boxupp.db.beans.ProjectBean;
import com.boxupp.db.beans.UserProjectMapping;
import com.boxupp.responseBeans.StatusBean;
import com.boxupp.utilities.Utilities;
import com.boxupp.vagrant.VagrantCommandProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

public class MachineConfigDAOManager implements DAOImplInterface {
	private static Logger logger = LogManager.getLogger(MachineConfigDAOManager.class.getName());
	public static Dao<MachineConfigurationBean, Integer>machineConfigDao = null;
	protected static Dao<MachineProjectMapping, Integer> machineMappingDao = null;
	private static MachineConfigDAOManager machineConfigDBManager = null;
	private PreparedQuery<MachineConfigurationBean> queryForBoxesOfProject = null;
	
	public static MachineConfigDAOManager getInstance(){
		if(machineConfigDao == null){
			machineConfigDBManager = new MachineConfigDAOManager();
		}
		return machineConfigDBManager;
		
	}
	
	private  MachineConfigDAOManager() {
		machineConfigDao = (Dao<MachineConfigurationBean, Integer>) DAOProvider.getInstance().fetchDao(MachineConfigurationBean.class);
		machineMappingDao = (Dao<MachineProjectMapping, Integer>) DAOProvider.getInstance().fetchDao(MachineProjectMapping.class);
		
	}

	@Override
	public StatusBean create(JsonNode newData) {
		MachineConfigurationBean machineConfigBean  = null;
		Gson machineConfigData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		JsonNode syncFolderMappings = newData.get("syncFolders");
		JsonNode portForwardingMappings = newData.get("portMappings");
		JsonNode dockerLinkContainerMappings = newData.get("dockerLinks");
		machineConfigBean = machineConfigData.fromJson(newData.toString(),MachineConfigurationBean.class);
		StatusBean statusBean = new StatusBean();
		try {
			machineConfigDao.create(machineConfigBean);
			if(portForwardingMappings!= null && portForwardingMappings.size()>0)PortMappingDAOManager.getInstance().save(machineConfigBean, portForwardingMappings);
			if(syncFolderMappings!=null && syncFolderMappings.size()>0)SyncFolderDAOManager.getInstance().save(machineConfigBean, syncFolderMappings);
			if(dockerLinkContainerMappings!=null && dockerLinkContainerMappings.size()>0){
				DockerLinkDAOManager.getInstance().save(machineConfigBean, dockerLinkContainerMappings);
			}
			ProjectBean project = ProjectDAOManager.getInstance().projectDao.queryForId(Integer.parseInt(newData.get("projectID").getTextValue()));
			MachineProjectMapping machineProjectMApping = new MachineProjectMapping(project, machineConfigBean);
			machineMappingDao.create(machineProjectMApping);
			machineConfigDao.refresh(machineConfigBean);
			statusBean.setData(machineConfigBean);
		} catch (SQLException e) {
			logger.error("Error creating a new project : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error saving machine configuration : "+e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Machine Congifuration saved successfully");
		return statusBean;
	}

	@Override
	public StatusBean update(JsonNode updatedData){
		MachineConfigurationBean machineConfigBean  = null;
		Gson machineData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		JsonNode syncFolderMappings = updatedData.get("syncFolders");
		JsonNode portForwardingMappings = updatedData.get("portMappings");
		JsonNode dockerLinkContainerMappings = updatedData.get("dockerLinks");
		machineConfigBean = machineData.fromJson(updatedData.toString(),MachineConfigurationBean.class);
		StatusBean statusBean = new StatusBean();
		try {
			machineConfigDao.update(machineConfigBean);
			if(portForwardingMappings!= null && portForwardingMappings.size()>0)PortMappingDAOManager.getInstance().delete(machineConfigBean);
			if(syncFolderMappings!=null && syncFolderMappings.size()>0)SyncFolderDAOManager.getInstance().delete(machineConfigBean);
			if(dockerLinkContainerMappings!=null && dockerLinkContainerMappings.size()>0){
				DockerLinkDAOManager.getInstance().delete(machineConfigBean);
			}
			machineConfigDao.refresh(machineConfigBean);
			if(portForwardingMappings!= null && portForwardingMappings.size()>0)PortMappingDAOManager.getInstance().save(machineConfigBean, portForwardingMappings);
			if(syncFolderMappings!=null && syncFolderMappings.size()>0)SyncFolderDAOManager.getInstance().save(machineConfigBean, syncFolderMappings);
			if(dockerLinkContainerMappings!=null && dockerLinkContainerMappings.size()>0){
				DockerLinkDAOManager.getInstance().save(machineConfigBean, dockerLinkContainerMappings);
			}
			machineConfigDao.refresh(machineConfigBean);
		} catch (SQLException e) {
			logger.error("Error creating a new project : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error updating machine configuration : "+e.getMessage());
		}
		statusBean.setData(machineConfigBean);
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Machine Congifuration updated successfully");
		return statusBean;
	}

	@Override
	public StatusBean delete(String machineId) {
		Integer machineID = Integer.parseInt(machineId);
		StatusBean statusBean = new StatusBean();
		try {
			ProjectBean project = machineMappingDao.queryBuilder().where().eq(MachineProjectMapping.MACHINE_ID_FIELD_NAME, machineConfigDao.queryForId(machineID)).queryForFirst().getProject();
			List<UserProjectMapping> userProjectMapping = UserDAOManager.getInstance().userProjectMappingDao.queryForAll();
			Integer userID = UserDAOManager.getInstance().userProjectMappingDao.queryBuilder().where().eq(UserProjectMapping.PROJECT_ID_FIELD_NAME, project).queryForFirst().getUser().getUserID();
			String location = Utilities.getInstance().fetchActiveProjectDirectory(userID);
			MachineConfigurationBean machineConfig = machineConfigDao.queryForId(machineID);
			machineConfig.setIsDisabled(true);
			machineConfigDao.update(machineConfig);
			String vagrantCommand = "vagrant destroy "+machineConfig.getVagrantID()+" -f";
			VagrantCommandProcessor shellProcessor = new VagrantCommandProcessor();
			try {
				shellProcessor.executeVagrantFile(location,vagrantCommand, userID, new VagrantOutputStream());
			} catch (IOException e) {
				statusBean.setStatusCode(1);
				statusBean.setStatusMessage("error in destroting box"+e.getMessage());
			} catch (InterruptedException e) {
				statusBean.setStatusCode(1);
				statusBean.setStatusMessage("error in destroting box"+e.getMessage());
			}
		} catch (SQLException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error deleting machine configuration : "+e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Machine Congifuration delete successfully");
		return statusBean;
	}
	@Override
	public <T>T read(String machineID) {
		
		MachineConfigurationBean machineConfig = null;
		try{
			machineConfig = machineConfigDao.queryForId(Integer.parseInt(machineID));
		}catch(SQLException e){
			logger.error("Error querying the machineconfig from DB : " + e.getMessage());
		}
		return (T) machineConfig;
	}
	
	public <E> List<E> retireveBoxesForProject(String projectID) {
		List<MachineConfigurationBean> machineList = new ArrayList<MachineConfigurationBean>();
		try {
			if (queryForBoxesOfProject == null) {
				queryForBoxesOfProject =  makeQueryForBoxesOfProject();
			}
			ProjectBean projectBean = ProjectDAOManager.getInstance().projectDao.queryForId(Integer.parseInt(projectID));
			if(projectBean != null){
				queryForBoxesOfProject.setArgumentHolderValue(0, projectBean );
				machineList = machineConfigDao.query(queryForBoxesOfProject);
			}
		} catch (NumberFormatException e) {
			logger.error("Error in retireveing boxes for project : "+e.getMessage());
		} catch (SQLException e) {
			logger.error("Error in retireveing boxes for project : "+e.getMessage());
		}
		if(machineList == null)return null;
		return (List<E>)machineList;
	}
	
	private PreparedQuery<MachineConfigurationBean> makeQueryForBoxesOfProject() throws SQLException {
		
		QueryBuilder<MachineProjectMapping, Integer> machineProjectQb = machineMappingDao.queryBuilder();
		machineProjectQb.selectColumns(MachineProjectMapping.MACHINE_ID_FIELD_NAME);
		SelectArg userSelectArg = new SelectArg();
		machineProjectQb.where().eq(MachineProjectMapping.PROJECT_ID_FIELD_NAME, userSelectArg);
		QueryBuilder<MachineConfigurationBean, Integer> machineConfigQb = machineConfigDao.queryBuilder();
		machineConfigQb.where().eq("isDisabled", false).and().in(MachineConfigurationBean.ID_FIELD_NAME, machineProjectQb);
		return machineConfigQb.prepare();
		
	}

	public StatusBean stop(String machineID) {

		
		StatusBean statusBean = new StatusBean();
		ProjectBean project;
		try {
			project = machineMappingDao.queryBuilder().where().eq(MachineProjectMapping.MACHINE_ID_FIELD_NAME, machineConfigDao.queryForId(Integer.parseInt(machineID))).queryForFirst().getProject();
			Integer userID = UserDAOManager.getInstance().userProjectMappingDao.queryBuilder().where().eq(UserProjectMapping.PROJECT_ID_FIELD_NAME, project).queryForFirst().getUser().getUserID();
			String location = Utilities.getInstance().fetchActiveProjectDirectory(userID);
			MachineConfigurationBean machineConfig = machineConfigDao.queryForId(Integer.parseInt(machineID));
			String vagrantCommand = "vagrant halt "+machineConfig.getVagrantID();
			VagrantCommandProcessor shellProcessor = new VagrantCommandProcessor();
			shellProcessor.executeVagrantFile(location,vagrantCommand, userID, new VagrantOutputStream());
		} catch (NumberFormatException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("error in stoping box"+e.getMessage());
		} catch (SQLException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("error in stoping box"+e.getMessage());
		} catch (IOException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("error in stoping box"+e.getMessage());
		} catch (InterruptedException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("error in stoping box"+e.getMessage());
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Machine stop successfully");
		return statusBean;
	}
	
	public StatusBean reload(String machineID) {

		
		StatusBean statusBean = new StatusBean();
		ProjectBean project;
		try {
			project = machineMappingDao.queryBuilder().where().eq(MachineProjectMapping.MACHINE_ID_FIELD_NAME, machineConfigDao.queryForId(Integer.parseInt(machineID))).queryForFirst().getProject();
			Integer userID = UserDAOManager.getInstance().userProjectMappingDao.queryBuilder().where().eq(UserProjectMapping.PROJECT_ID_FIELD_NAME, project).queryForFirst().getUser().getUserID();
			String location = Utilities.getInstance().fetchActiveProjectDirectory(userID);
			MachineConfigurationBean machineConfig = machineConfigDao.queryForId(Integer.parseInt(machineID));
			String vagrantCommand = "vagrant reload "+machineConfig.getVagrantID();
			VagrantCommandProcessor shellProcessor = new VagrantCommandProcessor();
			shellProcessor.executeVagrantFile(location,vagrantCommand, userID, new VagrantOutputStream());
		} catch (NumberFormatException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("error in stoping box"+e.getMessage());
		} catch (SQLException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("error in stoping box"+e.getMessage());
		} catch (IOException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("error in stoping box"+e.getMessage());
		} catch (InterruptedException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("error in stoping box"+e.getMessage());
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Machine stop successfully");
		return statusBean;
	}
}
