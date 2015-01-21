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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import com.boxupp.db.DAOProvider;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.ProjectBean;
import com.boxupp.db.beans.PuppetModuleBean;
import com.boxupp.db.beans.PuppetModuleMapping;
import com.boxupp.db.beans.SearchModuleBean;
import com.boxupp.responseBeans.StatusBean;
import com.boxupp.utilities.PuppetUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

public class PuppetModuleDAOManager implements DAOImplInterface {
	private static Logger logger = LogManager
			.getLogger(PuppetModuleDAOManager.class.getName());

	protected static Dao<PuppetModuleBean, Integer> puppetModuleDao = null;
	public static Dao<PuppetModuleMapping, Integer> puppetModuleMappingDao = null;
	private static PuppetModuleDAOManager puppetModuleDBManager;
	private PreparedQuery<PuppetModuleBean> queryForModulesOfProject = null;

	public static PuppetModuleDAOManager getInstance() {
		if (puppetModuleDBManager == null) {
			puppetModuleDBManager = new PuppetModuleDAOManager();
		}
		return puppetModuleDBManager;
	}

	private PuppetModuleDAOManager() {
		puppetModuleDao = DAOProvider.getInstance().fetchDao(PuppetModuleBean.class);
		puppetModuleMappingDao = DAOProvider.getInstance().fetchDao(PuppetModuleMapping.class);
	}

	@Override
	public StatusBean create(JsonNode newData) {
		PuppetModuleBean puppetModuleBean = null;
		Gson searchModuleData = new GsonBuilder().setDateFormat(
				"yyyy'-'MM'-'dd HH':'mm':'ss").create();
		SearchModuleBean searchModuleBean = searchModuleData.fromJson(
				newData.toString(), SearchModuleBean.class);
		Gson puppetModuleData = new GsonBuilder().setDateFormat(
				"yyyy'-'MM'-'dd HH':'mm':'ss").create();
		puppetModuleBean = puppetModuleData.fromJson(newData.toString(),
				PuppetModuleBean.class);
		puppetModuleBean.setModuleName(searchModuleBean.getName());
		puppetModuleBean.setComman_Uri(searchModuleBean.getUri());
		puppetModuleBean.setDownloads(searchModuleBean.getDownloads());
		puppetModuleBean.setCreated_at(searchModuleBean.getCreated_at());
		puppetModuleBean.setUpdated_at(searchModuleBean.getUpdated_at());
		puppetModuleBean.setSupported(searchModuleBean.getSupported());
		puppetModuleBean.setCurrentRelease_uri(searchModuleBean
				.getCurrent_release().getUri());
		puppetModuleBean.setCurrentRelease_version(searchModuleBean
				.getCurrent_release().getVersion());
		puppetModuleBean.setFile_uri(searchModuleBean.getCurrent_release()
				.getFile_uri());
		puppetModuleBean.setFile_size(searchModuleBean.getCurrent_release()
				.getFile_size());
		puppetModuleBean.setOwner_uri(searchModuleBean.getOwner().getUri());
		puppetModuleBean.setOwner_username(searchModuleBean.getOwner()
				.getUsername());
		puppetModuleBean.setGravatar_id(searchModuleBean.getOwner()
				.getGravatar_id());
		puppetModuleBean.setMetaData_name(searchModuleBean.getCurrent_release()
				.getMetadata().getName());
		puppetModuleBean.setVersion(searchModuleBean.getCurrent_release()
				.getMetadata().getVersion());
		puppetModuleBean.setSummary(searchModuleBean.getCurrent_release()
				.getMetadata().getSummary());
		puppetModuleBean.setLicense(searchModuleBean.getCurrent_release()
				.getMetadata().getLicense());
		puppetModuleBean.setAuthor(searchModuleBean.getCurrent_release()
				.getMetadata().getAuthor());
		puppetModuleBean.setTags(searchModuleBean.getCurrent_release().getMetadata()
						.getTags() != null ? searchModuleBean
						.getCurrent_release().getMetadata().getTags()
						.toString() : null);
		puppetModuleBean.setDescription(searchModuleBean.getCurrent_release()
				.getMetadata().getDescription());
		StatusBean statusBean = new StatusBean();
		try {
			puppetModuleDao.create(puppetModuleBean);
		} catch (SQLException e) {
			logger.error("Error saving a new puppet module : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error saving a new puppet module : "+ e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setData(puppetModuleBean);
		statusBean.setStatusMessage("Puppet module saved successfully");
		return statusBean;
	}

	/*
	 * @Override public <E> List<E> readAllDB(){ List<PuppetModuleBean>
	 * puppetModulesList = null; try{ puppetModulesList =
	 * puppetModuleDao.queryForAll(); }catch(SQLException e){
	 * logger.error("Error querying the projects list from DB : " +
	 * e.getMessage()); } return (List<E>) puppetModulesList;
	 * 
	 * 
	 * }
	 */
	@Override
	public StatusBean update(JsonNode updatedData) {
		PuppetModuleBean puppetModuleBean = null;
		Gson puppetModuleData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		puppetModuleBean = puppetModuleData.fromJson(updatedData.toString(), PuppetModuleBean.class);
		StatusBean statusBean = new StatusBean();
		try {
			puppetModuleDao.update(puppetModuleBean);
		} catch (SQLException e) {
			logger.error("Error updating a puppet module : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error updating a puppet module : "
					+ e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setData(puppetModuleBean);
		statusBean.setStatusMessage("Puppet module updated successfully");
		return statusBean;
	}

	@Override
	public StatusBean delete(String puppetModuleID) {
		StatusBean statusBean = new StatusBean();
		try {
			
			PuppetModuleBean puppetModule = puppetModuleDao.queryForId(Integer.parseInt(puppetModuleID));
			puppetModule.setIsDisabled(true);
			puppetModuleDao.update(puppetModule);
			PuppetUtilities.getInstance().deletePuppetModule(puppetModule.getModuleName());
		} catch (SQLException e) {
			logger.error("Error deleting a puppet Module : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error deleting  puppet Module : "
					+ e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Puppet module deleted successfully");
		return statusBean;
	}

	@Override
	public <T> T read(String puppetModuleID) {
		PuppetModuleBean puppetModule = null;
		try {
			puppetModule = puppetModuleDao.queryForId(Integer
					.parseInt(puppetModuleID));
		} catch (SQLException e) {
			logger.error("Error querying the module from DB : "
					+ e.getMessage());
		}

		return  (T) puppetModule;
	}
	
	public <E>List <E> retireveModulesForProject(String projectID) {
		  List<PuppetModuleBean> puppetModuleList = new
		  ArrayList<PuppetModuleBean>(); try { if (queryForModulesOfProject == null) {
			  queryForModulesOfProject = makeQueryForModulesOfProject();
		  }
		  
		  queryForModulesOfProject.setArgumentHolderValue(0, ProjectDAOManager
		  .getInstance().projectDao.queryForId(Integer.parseInt(projectID)));
		  puppetModuleList = puppetModuleDao.query(queryForModulesOfProject);
		  } catch (NumberFormatException e) {
		  logger.error("Error in retireveing module :"+e.getMessage());
		  } catch(SQLException e) {
		  logger.error("Error in retireveing module :"+e.getMessage());
		  }
	
		  return(List<E>) puppetModuleList; }
	 
	
	
	  private PreparedQuery<PuppetModuleBean> makeQueryForModulesOfProject() throws SQLException {
	  
		  QueryBuilder<PuppetModuleMapping, Integer> moduleProjectQb =
		  puppetModuleMappingDao.queryBuilder();
		  moduleProjectQb.selectColumns(PuppetModuleMapping.MODULE_ID_FIELD_NAME);
		  SelectArg projectSelectArg = new SelectArg(); moduleProjectQb.where().eq(
		  PuppetModuleMapping.PROJECT_ID_FIELD_NAME, projectSelectArg);
		  QueryBuilder<PuppetModuleBean, Integer> moduleQb =
		  puppetModuleDao.queryBuilder();
		  moduleQb.where().eq("isDisabled", false).and().in(PuppetModuleBean.ID_FIELD_NAME,moduleProjectQb);
		  return moduleQb.prepare();
	  }

	
	public StatusBean updateModuleMapping(JsonNode moduleMachineMapping) {

		StatusBean statusBean = new StatusBean();
		Integer projectID = Integer.parseInt(moduleMachineMapping.get("projectID").getTextValue());
		ProjectBean project = null;
		try {
			project = ProjectDAOManager.projectDao.queryForId(projectID);
			DeleteBuilder<PuppetModuleMapping, Integer> deleteBuilder = puppetModuleMappingDao
					.deleteBuilder();
			deleteBuilder.where().eq(PuppetModuleMapping.PROJECT_ID_FIELD_NAME,project);
			deleteBuilder.delete();
			JsonNode scriptMappings = moduleMachineMapping.get("moduleMappings");
			Iterator<Map.Entry<String, JsonNode>> fieldsIterator = scriptMappings.getFields();
			while (fieldsIterator.hasNext()) {
				Map.Entry<String, JsonNode> field = fieldsIterator.next();
				String machineID = field.getKey().toString();
				MachineConfigurationBean machineConfig = MachineConfigDAOManager.machineConfigDao
						.queryForId(Integer.parseInt(machineID));
				Iterator<JsonNode> moduleValues = field.getValue().iterator();
				while (moduleValues.hasNext()) {
					String moduleMappingValue = moduleValues.next().toString();
					PuppetModuleBean module = puppetModuleDao.queryForId(Integer.parseInt(moduleMappingValue));
					PuppetModuleMapping moduleMapping = new PuppetModuleMapping(machineConfig, module, project);
					puppetModuleMappingDao.create(moduleMapping);
				}
			}
			List<PuppetModuleMapping> puppetModuleMappingList = ProjectDAOManager.getInstance().retireveModulesMapping(projectID.toString());
			PuppetUtilities.getInstance().refreshNodeTemplate(puppetModuleMappingList, projectID.toString());
		} catch (NumberFormatException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error saving machine Mapping with module : "+ e.getMessage());
			e.printStackTrace();

		} catch (SQLException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error in moduleMapping :"
					+ e.getMessage());
		}
		statusBean.setStatusCode(0);
		statusBean
				.setStatusMessage("Machine Mapping with  module saved successfully");
		return statusBean;
	}

}
