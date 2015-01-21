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
import com.boxupp.db.beans.GitRepoBean;
import com.boxupp.db.beans.GitRepoMapping;
import com.boxupp.responseBeans.StatusBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;

public class GitRepoDAOManager implements DAOImplInterface {
	private static Logger logger = LogManager.getLogger(GitRepoDAOManager.class.getName());
	protected static Dao<GitRepoBean, Integer> gitRepoDao = null;
	protected static Dao<GitRepoMapping, Integer> gitRepoMappingDao = null;
	private static GitRepoDAOManager gitRepoManager = null;
	
	public static GitRepoDAOManager getInstance(){
		if(gitRepoManager == null){
			gitRepoManager = new GitRepoDAOManager();
		}
		return gitRepoManager;
		
	}
	
	private  GitRepoDAOManager() {
		gitRepoDao = (Dao<GitRepoBean, Integer>) DAOProvider.getInstance().fetchDao(GitRepoBean.class);
		gitRepoMappingDao = (Dao<GitRepoMapping, Integer>) DAOProvider.getInstance().fetchDao(GitRepoMapping.class);
		
	}

	@Override
	public StatusBean create(JsonNode newData) {
		GitRepoBean gitRepoBean  = null;
		Gson gitRepoData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		gitRepoBean = gitRepoData.fromJson(newData.toString(), GitRepoBean.class);
		StatusBean statusBean = new StatusBean();
		try {
			gitRepoDao.create(gitRepoBean);
		} catch (SQLException e) {
			logger.error("Error saving a git repo  : " + e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error saving a git repo : "+e.getMessage());
			e.printStackTrace();
		}
		statusBean.setStatusCode(0);
		statusBean.setData(gitRepoBean);
		statusBean.setStatusMessage("git repo saved successfully");
		return statusBean;
	}

	

	@Override
	public StatusBean update(JsonNode updatedData) {

		GitRepoBean gitRepoBean  = null;
		Gson gitRepoData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		gitRepoBean = gitRepoData.fromJson(updatedData.toString(), GitRepoBean.class);
		StatusBean statusBean = new StatusBean();
		
		try {
			gitRepoDao.update(gitRepoBean);
			statusBean.setStatusCode(0);
		} catch (SQLException e) {
			statusBean.setStatusMessage("Error updating  git repo : "+e.getMessage());
			statusBean.setStatusCode(1);
		}
		statusBean.setStatusMessage("git repo updated successfully");
		return statusBean;
	}

	@Override
	public StatusBean delete(String gitRepoID) {
		StatusBean statusBean = new StatusBean();
		try {
			gitRepoDao.deleteById(Integer.parseInt(gitRepoID));
			statusBean.setStatusCode(0);
			statusBean.setStatusMessage("Git Repo data deleted successfully");
		} catch (NumberFormatException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error in deleting git repo data : "+e.getMessage());
		} catch (SQLException e) {
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error in deleting git repo data : "+e.getMessage());
		}
		return statusBean;
	}
	

	@Override
	public<T>T read(String gitRepoID) {
		GitRepoBean gitRepo = null;
		try{
			gitRepo = gitRepoDao.queryForId(Integer.parseInt(gitRepoID));
		}catch(SQLException e){
			logger.error("Error querying the git repo from DB : " + e.getMessage());
		}
		return (T) gitRepo;
	}
	
	
}
