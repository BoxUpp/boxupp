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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import com.boxupp.db.DAOProvider;
import com.boxupp.db.beans.ProjectBean;
import com.boxupp.db.beans.UserDetailBean;
import com.boxupp.db.beans.UserProjectMapping;
import com.boxupp.mail.MailManager;
import com.boxupp.responseBeans.StatusBean;
import com.boxupp.responseBeans.UserAuthenticationResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;

public class UserDAOManager implements DAOImplInterface{

	private static Logger logger = LogManager.getLogger(UserDAOManager.class.getName());
	protected static Dao<UserDetailBean, Integer>userDetailDao = null;
	protected static Dao<UserProjectMapping, Integer> userProjectMappingDao = null;
	private static UserDAOManager userDBManager = null;
	
	public static UserDAOManager getInstance(){
		if(userDetailDao == null){
			userDBManager = new UserDAOManager();
		}
		return userDBManager;
		
	}
	
	private  UserDAOManager() {
		userDetailDao = (Dao<UserDetailBean, Integer>) DAOProvider.getInstance().fetchDao(UserDetailBean.class);
		userProjectMappingDao = (Dao<UserProjectMapping, Integer>) DAOProvider.getInstance().fetchDao(UserProjectMapping.class);
	}
	
	@Override
	public StatusBean create(JsonNode newData){
		
		UserAuthenticationResponse response = new UserAuthenticationResponse();
		UserDetailBean userDetailBean  = null;
		Gson userData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		userDetailBean = userData.fromJson(newData.toString(), UserDetailBean.class);
		userDetailBean.setIsDisabled(false);
		userDetailBean.setUserType(1); //admin
		
		try {
			userDetailDao.create(userDetailBean);
		} catch (SQLException e) {
			logger.error("Error registering new user : " + e.getMessage());
			response.setStatusCode(1);
			response.setStatusMessage("Error registering new user : " + e.getMessage());
		}
		response.setStatusCode(0);
		response.setStatusMessage("User created successfully");
		response.setUserID(userDetailBean.getUserID());
		MailManager mailManager = new MailManager();
		mailManager.sendRegistrationMail(newData.get("mailID").getTextValue(),
										 newData.get("firstName").getTextValue(),
										 newData.get("password").getTextValue());
		return response;
	}

	
	@Override
	public StatusBean update(JsonNode updatedData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatusBean delete(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public <T>T read(String userId) {
		UserDetailBean userDetail = null;
		try{
			userDetail = userDetailDao.queryForId(Integer.parseInt(userId));
		}catch(SQLException e){
			logger.error("Error querying the user from DB : " + e.getMessage());
		}
		return  (T)userDetail;
	}

	public <T> T populateMappingBean(T mappingBean, String userId) {

		UserProjectMapping userProjectMappingBean = null;
		try {
//			userProjectMappingBean = new UserProjectMapping(userDetailDao.queryForId(Integer.parseInt(ids[0])), (ProjectBean) mappingBean);
			userProjectMappingBean = new UserProjectMapping(userDetailDao.queryForId(Integer.parseInt(userId)), (ProjectBean) mappingBean);
			userProjectMappingDao.create(userProjectMappingBean);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		}
		return (T) userProjectMappingBean;
	}
	public StatusBean checkForExistingUser(String userID){
		StatusBean statusBean = new StatusBean();
		try {
			List<UserDetailBean> userDetail = userDetailDao.queryBuilder().where().eq("mailID", userID).query();
			if(!userDetail.isEmpty()){
				statusBean.setStatusCode(0);
			}else{
				statusBean.setStatusCode(1);
			}
		} catch (SQLException e) {
			statusBean.setStatusCode(2);
			statusBean.setStatusMessage("Error in fetching data from userDetail");
		}
		return statusBean ;
	} 
	
	public static void main(String[] args){
		UserDAOManager user = new UserDAOManager();
		StatusBean statusBean = user.checkForExistingUser("a");
		
	}
}
