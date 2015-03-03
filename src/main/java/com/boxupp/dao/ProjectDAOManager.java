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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.boxupp.VagrantOutputStream;
import com.boxupp.db.DAOProvider;
import com.boxupp.db.DerbyConfig;
import com.boxupp.db.beans.AwsProjectCredentialsBean;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.ProjectAwsCredentialsMapping;
import com.boxupp.db.beans.ProjectBean;
import com.boxupp.db.beans.ProjectProviderMappingBean;
import com.boxupp.db.beans.PuppetModuleBean;
import com.boxupp.db.beans.PuppetModuleMapping;
import com.boxupp.db.beans.ShellScriptBean;
import com.boxupp.db.beans.ShellScriptMapping;
import com.boxupp.db.beans.UserProjectMapping;
import com.boxupp.responseBeans.StatusBean;
import com.boxupp.utilities.CommonProperties;
import com.boxupp.utilities.OSProperties;
import com.boxupp.utilities.PuppetUtilities;
import com.boxupp.utilities.Utilities;
import com.boxupp.vagrant.VagrantCommandProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

public class ProjectDAOManager implements DAOImplInterface {

	private static Logger logger = LogManager.getLogger(ProjectDAOManager.class
			.getName());
	public static Dao<ProjectBean, Integer> projectDao = null;
	protected static Dao<ProjectProviderMappingBean, Integer> projectProviderMappingDao = null;
	protected static Dao<UserProjectMapping, Integer> userProjectMappingDao = null;
	private static ProjectDAOManager projectDBManager;
	private static PreparedQuery<ProjectBean> userProjectsQuery = null;

	public static ProjectDAOManager getInstance() {
		if (projectDBManager == null) {
			projectDBManager = new ProjectDAOManager();
		}
		return projectDBManager;
	}

	private ProjectDAOManager() {
		projectDao = DAOProvider.getInstance().fetchDao(ProjectBean.class);
		projectProviderMappingDao = DAOProvider.getInstance().fetchDao(
				ProjectProviderMappingBean.class);
		userProjectMappingDao = DAOProvider.getInstance().fetchDao(
				UserProjectMapping.class);
		userProjectsQuery = makeProjectForUserQuery();
	}

	@Override
	public StatusBean create(JsonNode newData) {
		ProjectBean projectBean = new ProjectBean();
		Gson projectData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		projectBean = projectData.fromJson(newData.toString(), ProjectBean.class);

		StatusBean statusBean = new StatusBean();
		
		try {

			int rowsUpdated = projectDao.create(projectBean);

			// if(rowsUpdated == 1)
			// Utilities.getInstance().changeActiveDirectory(projectBean.getProjectId());
			UserDAOManager.getInstance().populateMappingBean(projectBean, newData.get("owner").getValueAsText());
			statusBean.setStatusCode(0);
			statusBean.setData(projectBean);
			Utilities.getInstance().initializeDirectory(projectBean.getProjectID());
			String scriptDir  = Utilities.getInstance().constructProjectDirectory(projectBean.getProjectID())+OSProperties.getInstance().getOSFileSeparator()+OSProperties.getInstance().getScriptsDirName()+OSProperties.getInstance().getOSFileSeparator();
			Utilities.getInstance().checkIfDirExists(new File (scriptDir));
			File puppetScriptFile = new File(getClass().getResource("/puppet.sh").toURI());
			Utilities.getInstance().copyFile(puppetScriptFile, new File(scriptDir+"puppet.sh"));
			
			String nodeFileLoc = Utilities.getInstance().constructProjectDirectory(projectBean.getProjectID())+OSProperties.getInstance().getOSFileSeparator()+OSProperties.getInstance().getManifestsDirName()+OSProperties.getInstance().getOSFileSeparator()+"site.pp";
			boolean nodeFile =	new File(nodeFileLoc).createNewFile();
			
			Integer providerID = ProviderDAOManager.getInstance().providerDao.queryForId(projectBean.getProviderType()).getProviderID();
			ProjectProviderMappingBean projectProvider = new ProjectProviderMappingBean(projectBean.getProjectID(), providerID);
			projectProviderMappingDao.create(projectProvider);
			String providerName = ProviderDAOManager.getInstance().providerDao.queryForId(projectBean.getProviderType()).getName();
			if(providerName.equalsIgnoreCase(CommonProperties.getInstance().getDockerProvider())){
				Utilities.getInstance().initializeDockerVagrantFile(projectBean.getProjectID());
			}
			else if(providerName.equalsIgnoreCase("AWS")){
				AwsProjectDAOManager.getInstance().create(newData,projectBean);
			}
		} catch (SQLException e) {
			logger.error("Error creating a new project : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error creating project : "+ e.getMessage());
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return statusBean;
	}


	@Override
	public StatusBean update(JsonNode updateddata) {
		ProjectBean projectBean = null;
		Gson projectData = new GsonBuilder().setDateFormat(
				"yyyy'-'MM'-'dd HH':'mm':'ss").create();
		projectBean = projectData.fromJson(updateddata.toString(),ProjectBean.class);
		StatusBean statusBean = new StatusBean();
		try {
			projectDao.update(projectBean);
		} catch (SQLException e) {
			logger.error("Error updating a  project : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error updating a project : "+ e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Project updated successfully");
		return statusBean;
	}

	@Override
	public <T> T read(String projectID) {

		ProjectBean project = null;
		try {
			project = projectDao.queryForId(Integer.parseInt(projectID));
		} catch (SQLException e) {
			logger.error("Error querying the project from DB : "+ e.getMessage());
		}
		return (T) project;
	}

	@Override
	public StatusBean delete(String projectID) {
		StatusBean statusBean = new StatusBean();
		try {
			projectDao.updateBuilder().updateColumnValue("isDisabled", true).where().idEq(Integer.parseInt(projectID));
			List<MachineConfigurationBean> machineConfigList = MachineConfigDAOManager.getInstance().retireveBoxesForProject(projectID);
			
			for(MachineConfigurationBean machineConfig : machineConfigList){
				MachineConfigDAOManager.getInstance().delete(machineConfig.getMachineID().toString());
			}
			
		} catch (SQLException e) {
			logger.error("Error deleting a project : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error deleting  a project : "+ e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Project deleted successfully");
		return statusBean;
	}
	
	public StatusBean delete(JsonNode projectData) {
		StatusBean statusBean = new StatusBean();
		Integer userID = Integer.parseInt(projectData.get("userID").getTextValue());
		Integer projectID = Integer.parseInt(projectData.get("projectID").getTextValue());
		String location = Utilities.getInstance().fetchActiveProjectDirectory(userID);
		
		try {
			ProjectBean project = projectDao.queryForId(projectID);
			project.setIsDisabled(true);
			projectDao.update(project);
			List<MachineConfigurationBean> machineConfigList = MachineConfigDAOManager.getInstance().retireveBoxesForProject(projectID.toString());
			
			for(MachineConfigurationBean machineConfig : machineConfigList){
				MachineConfigDAOManager.getInstance().delete(machineConfig.getMachineID().toString());
				String vagrantCommand = "vagrant destroy "+machineConfig.getVagrantID();
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
			}
			
		} catch (SQLException e) {
			logger.error("Error deleting a project : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error deleting  a project : "+ e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Project deleted successfully");
		return statusBean;
	}

	public <E> List<E> retireveProjectsForUser(String UserID) {
		List<ProjectBean> projectList = new ArrayList<ProjectBean>();
		try {

			List<UserProjectMapping> users = userProjectMappingDao.queryForAll();
			userProjectsQuery.setArgumentHolderValue(0, UserDAOManager.getInstance().userDetailDao.queryForId(Integer
					.parseInt(UserID)));
			projectList = projectDao.query(userProjectsQuery);

		} catch (NumberFormatException e) {
			logger.error("Error parsing user ID : " + e.getMessage());
		} catch (SQLException e) {
			logger.error("Error fetching projects for user " + UserID + " : "
					+ e.getMessage());
		}
		return (List<E>) projectList;
	}

	private PreparedQuery<ProjectBean> makeProjectForUserQuery() {

		QueryBuilder<UserProjectMapping, Integer> userPostQb = userProjectMappingDao
				.queryBuilder();

		userPostQb.selectColumns(UserProjectMapping.PROJECT_ID_FIELD_NAME);
		SelectArg userSelectArg = new SelectArg();
		QueryBuilder<ProjectBean, Integer> projectQb = null;
		try {
			userPostQb.where().eq(UserProjectMapping.USER_ID_FIELD_NAME,
					userSelectArg);
			projectQb = projectDao.queryBuilder();
			projectQb.where().eq("isDisabled", false).and().in(ProjectBean.ID_FIELD_NAME, userPostQb);
			return projectQb.prepare();
		} catch (SQLException e) {
			logger.error("Error creating prepared query for fetching user mapped projects : "
					+ e.getMessage());
		}
		return null;
	}

	public String getProviderForProject(String projectID) {
		String provider = null;
		try {
			Integer providerId = projectProviderMappingDao.queryForEq("projectID", Integer.parseInt(projectID))
					.get(0).getProviderID();
			provider = ProviderDAOManager.getInstance().providerDao.queryForId(providerId).getName();
		} catch (NumberFormatException e) {
			logger.error("Error in finding provider for project :"+ e.getMessage());
		} catch (SQLException e) {
			logger.error("Error in sql to finding provider for project :"+ e.getMessage());
		}
		return provider;
	}

	public <E> List<E> retireveAllModules() {
		List<PuppetModuleBean> puppetModuleList = new ArrayList<PuppetModuleBean>();
		try {
			puppetModuleList = PuppetModuleDAOManager.getInstance().puppetModuleDao.queryForEq("isDisabled", false);
		} catch (NumberFormatException e) {
			logger.error("Error in retireveing module :" + e.getMessage());
		} catch (SQLException e) {
			logger.error("Error in retireveing module :" + e.getMessage());
		}
		return (List<E>) puppetModuleList;
	}

	public <E> List<E> retireveScriptsMapping(String projectID) {
		List<ShellScriptMapping> scriptMappingList = new ArrayList<ShellScriptMapping>();
		try {
			ProjectBean project = projectDao.queryForId(Integer.parseInt(projectID));
			List<ShellScriptBean> shellScripts = ShellScriptDAOManager.getInstance().shellScriptDao.queryForEq("isDisabled", false);
			List<MachineConfigurationBean> machineConfigs = MachineConfigDAOManager.getInstance().machineConfigDao.queryForEq("isDisabled", false);
			if(!(shellScripts.isEmpty())){
				scriptMappingList = ShellScriptDAOManager.getInstance().shellScriptMappingDao.queryBuilder().where().in(ShellScriptMapping.SCRIPT_ID_FIELD_NAME, shellScripts).and().in(ShellScriptMapping.MACHINE_ID_FIELD_NAME, machineConfigs).and().eq(ShellScriptMapping.PROJECT_ID_FIELD_NAME, project).query();
			}
			//scriptMappingList = ShellScriptDAOManager.getInstance().shellScriptMappingDao.queryForAll();
		} catch (SQLException e) {
			logger.error("Error in retireving scripts mapping: "+ e.getMessage());
		}
		return (List<E>) scriptMappingList;
	}
	
	public <E> List<E> retireveModulesMapping(String projectID) {
		List<PuppetModuleMapping> moduleMappingList = new ArrayList<PuppetModuleMapping>();
		try {
			ProjectBean project = projectDao.queryForId(Integer.parseInt(projectID));
			List<PuppetModuleBean> puppetModules = PuppetModuleDAOManager.getInstance().puppetModuleDao.queryForEq("isDisabled", false);
			List<MachineConfigurationBean> machineConfigs = MachineConfigDAOManager.getInstance().machineConfigDao.queryForEq("isDisabled", false);
			if(!(puppetModules.isEmpty())){
				moduleMappingList = PuppetModuleDAOManager.getInstance().puppetModuleMappingDao.queryBuilder().where().in(PuppetModuleMapping.MODULE_ID_FIELD_NAME, puppetModules).and().in(PuppetModuleMapping.MACHINE_ID_FIELD_NAME, machineConfigs).and().eq(ShellScriptMapping.PROJECT_ID_FIELD_NAME, project).query();
			}
		} catch (SQLException e) {
			logger.error("Error in retireving module mapping: "	+ e.getMessage());
		}
		return (List<E>) moduleMappingList;
	}
	
}
