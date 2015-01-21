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
import com.boxupp.db.beans.ShellScriptBean;
import com.boxupp.db.beans.ShellScriptMapping;
import com.boxupp.db.beans.UserProjectMapping;
import com.boxupp.responseBeans.StatusBean;
import com.boxupp.utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

public class ShellScriptDAOManager implements DAOImplInterface {
	private static Logger logger = LogManager.getLogger(ShellScriptDAOManager.class.getName());
	protected static Dao<ShellScriptBean, Integer> shellScriptDao = null;
	public static Dao<ShellScriptMapping, Integer> shellScriptMappingDao = null;
	private static ShellScriptDAOManager shellScriptDAOManager = null;
	private PreparedQuery<ShellScriptBean> queryForScriptsOfProject = null;
	private PreparedQuery<ShellScriptBean> queryForScriptsOfbox = null;
	
	public static ShellScriptDAOManager getInstance(){
		if(shellScriptDAOManager == null){
			shellScriptDAOManager = new ShellScriptDAOManager();
		}
		return shellScriptDAOManager;
		
	}
	
	private ShellScriptDAOManager(){
		shellScriptDao = (Dao<ShellScriptBean, Integer>) DAOProvider.getInstance().fetchDao(ShellScriptBean.class);
		shellScriptMappingDao = (Dao<ShellScriptMapping, Integer>) DAOProvider.getInstance().fetchDao(ShellScriptMapping.class);
		
	}
	
	@Override
	public StatusBean create(JsonNode newData) {
		ShellScriptBean shellScriptBean  = null;
		Gson shellScriptData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		shellScriptBean = shellScriptData.fromJson(newData.toString(), ShellScriptBean.class);
		Integer userID = Integer.parseInt(newData.get("userID").getTextValue());
		StatusBean statusBean = new StatusBean();
		try {
			Utilities.getInstance().writeScriptToDisk(shellScriptBean, userID);
			shellScriptDao.create(shellScriptBean);
			ProjectBean project = ProjectDAOManager.getInstance().projectDao.queryForId(Integer.parseInt(newData.get("ProjectID").getTextValue()));
			ShellScriptMapping shellscriptMapping = new ShellScriptMapping(null, shellScriptBean, project);
			shellScriptMappingDao.create(shellscriptMapping);
		} catch (SQLException e) {
			logger.error("Error saving a new shell script : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error saving a new shell script : "+e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Shell script saved successfully");
		statusBean.setData(shellScriptBean);
		return statusBean;
	}

	@Override
	public StatusBean update(JsonNode updatedData){
		ShellScriptBean shellScriptBean  = null;
		Gson shellScriptData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		shellScriptBean = shellScriptData.fromJson(updatedData.toString(), ShellScriptBean.class);
		StatusBean statusBean = new StatusBean();
		Integer userID = Integer.parseInt(updatedData.get("userID").getTextValue());
		try {
			Utilities.getInstance().updateScriptData(shellScriptBean, userID);
			shellScriptDao.update(shellScriptBean);
		} catch (SQLException e) {
			logger.error("Error updating a shell script : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error updating a shell script : "+e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Shell script updated successfully");
		statusBean.setData(shellScriptBean);
		return statusBean;
	}

	
	@Override
	public StatusBean delete(String shellScriptID) {
		StatusBean statusBean = new StatusBean();
		try {
			ShellScriptBean scriptBean = shellScriptDao.queryForId(Integer.parseInt(shellScriptID));
			scriptBean.setIsDisabled(true);
			shellScriptDao.update(scriptBean);
			ProjectBean project = shellScriptMappingDao.queryBuilder().where().eq(ShellScriptMapping.MACHINE_ID_FIELD_NAME, shellScriptDao.queryForId(Integer.parseInt(shellScriptID))).queryForFirst().getProject();
			Integer userID = UserDAOManager.getInstance().userProjectMappingDao.queryBuilder().where().eq(UserProjectMapping.PROJECT_ID_FIELD_NAME, project).queryForFirst().getUser().getUserID();
			/*List<ShellScriptMapping> shellscriptMappping = shellScriptMappingDao.queryForEq(ShellScriptMapping.SCRIPT_ID_FIELD_NAME, Integer.parseInt(shellScriptID));
				for(ShellScriptMapping shellScript : shellscriptMappping){
					shellScriptMappingDao.delete(shellScript);
			}*/
			Utilities.getInstance().deleteScriptfileOnDisk(scriptBean.getScriptName(), userID);
		} catch (SQLException e) {
			logger.error("Error updating a shell script : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error deleting  shell script : "+e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Shell script deleted successfully");
		return statusBean;
	}
	@Override
	public <T> T read(String scriptID) {
		ShellScriptBean shellScript = null;
		try{
			shellScript = shellScriptDao.queryForId(Integer.parseInt(scriptID));
		}catch(SQLException e){
			logger.error("Error querying the script from DB : " + e.getMessage());
		}
		return  (T)shellScript;
		
	}

	
	public <E> List<E> retireveScriptsForProject(String projectID){
		List<ShellScriptBean> shellScriptList = new ArrayList<ShellScriptBean>();
		try {
		if (queryForScriptsOfProject == null) {
			queryForScriptsOfProject = makeQueryForScriptsOfProject();
		}
		
		queryForScriptsOfProject.setArgumentHolderValue(0, ProjectDAOManager.getInstance().projectDao.queryForId(Integer.parseInt(projectID)));
		shellScriptList = shellScriptDao.query(queryForScriptsOfProject);
		} catch (NumberFormatException e) {
			logger.error("Error in retireving scripts for project: "+e.getMessage());
		} catch (SQLException e) {
			logger.error("Error in retireving scripts for project: "+e.getMessage());
		}
		return (List<E>)shellScriptList;
	}

		private PreparedQuery<ShellScriptBean> makeQueryForScriptsOfProject() throws SQLException {
		
		QueryBuilder<ShellScriptMapping, Integer> scriptProjectQb = shellScriptMappingDao.queryBuilder();
		// just select the post-id field
		scriptProjectQb.selectColumns(ShellScriptMapping.SCRIPT_ID_FIELD_NAME);
		SelectArg projectSelectArg = new SelectArg();
		// you could also just pass in user1 here
		scriptProjectQb.where().eq(ShellScriptMapping.PROJECT_ID_FIELD_NAME, projectSelectArg);
		// build our outer query for Post objects
		QueryBuilder<ShellScriptBean, Integer> shellScriptQb = shellScriptDao.queryBuilder();
		// where the id matches in the post-id from the inner query
		shellScriptQb.where().eq("isDisabled", false).and().in(ShellScriptBean.ID_FIELD_NAME, scriptProjectQb);
		return shellScriptQb.prepare();
	}

	public StatusBean updateScriptMapping(JsonNode  shellScriptMapping) {
		StatusBean statusBean =   new StatusBean();
		Integer projectID = Integer.parseInt(shellScriptMapping.get("projectID").getTextValue());
		ProjectBean project = null;
		try {
			 project = ProjectDAOManager.projectDao.queryForId(projectID);
			
			 List<ShellScriptMapping> mapping = shellScriptMappingDao.queryForEq(ShellScriptMapping.PROJECT_ID_FIELD_NAME, project);
			 for(ShellScriptMapping scriptMapping : mapping){
				scriptMapping.setMachineConfig(null);

				 shellScriptMappingDao.update(scriptMapping);
			 }
			 JsonNode scriptMappings = shellScriptMapping.get("scriptMappings");
			   Iterator<Map.Entry<String,JsonNode>> fieldsIterator = scriptMappings.getFields();
		       while (fieldsIterator.hasNext()) {
		    	   Map.Entry<String,JsonNode> field = fieldsIterator.next();
		    	   String machineID = field.getKey().toString();
		    	   MachineConfigurationBean machineConfig = MachineConfigDAOManager.machineConfigDao.queryForId(Integer.parseInt(machineID));
		    	   Iterator<JsonNode> scriptValues = field.getValue().iterator();
		    	   while(scriptValues.hasNext()){
		    		   String scriptMappingValue = scriptValues.next().toString();
		    		   ShellScriptBean script = shellScriptDao.queryForId(Integer.parseInt(scriptMappingValue));
		    		   ShellScriptMapping scriptMapping = new ShellScriptMapping(machineConfig, script, project);
		    			   shellScriptMappingDao.create(scriptMapping);
		    	   }
		         
		         }
		} catch (SQLException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error in ScriptMapping :"+e.getMessage());
			
		}
		statusBean.setStatusCode(0);
		statusBean.setStatusMessage("Machine MApping with  Shell script saved successfully");
		
		return statusBean;
		
	}


}




