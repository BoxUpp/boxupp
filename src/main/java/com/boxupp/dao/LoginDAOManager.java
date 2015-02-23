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

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import com.boxupp.db.DAOProvider;
import com.boxupp.db.beans.UserCredentials;
import com.boxupp.db.beans.UserDetailBean;
import com.boxupp.db.beans.UserProjectMapping;
import com.boxupp.resources.SessionTracker;
import com.boxupp.responseBeans.UserAuthenticationResponse;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

public class LoginDAOManager {
	
	
	private static Logger logger = LogManager.getLogger(LoginDAOManager.class.getName());
	protected static Dao<UserDetailBean, Integer> userDetailDao = null;
	protected static Dao<UserProjectMapping, Integer> userProjectMappingDao = null;
	private static LoginDAOManager loginDBManager;
	
	public static LoginDAOManager getInstance(){
		if(loginDBManager == null){
			loginDBManager = new LoginDAOManager();
		}
		return loginDBManager;
	}
	
	private LoginDAOManager() {
		userDetailDao = DAOProvider.getInstance().fetchDao(UserDetailBean.class);
	}
	
	public UserAuthenticationResponse loginAuthorization(JsonNode loginCredentials,HttpServletRequest request) {
		UserAuthenticationResponse authResponse = new UserAuthenticationResponse();
		UserCredentials loginBean = null;
		Gson loginParam = new Gson();
		loginBean = loginParam.fromJson(loginCredentials.toString(),UserCredentials.class);
		try{
			List<UserDetailBean> userData = userDetailDao.queryBuilder().where().eq("mailID", loginBean.getLoginID()).and().eq("password", loginBean.getPassword()).query();
			if(userData.size() == 1){
				authResponse.setStatusCode(0);
				authResponse.setUserID(userData.get(0).getUserID());
				authResponse.setStatusMessage("User authenticated successfully");
				// create session 
				try {
					SessionTracker.getInstance().createSession(request,(String)loginBean.getLoginID());
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (ServletException e) {
					e.printStackTrace();
				}
			}else{
				authResponse.setStatusCode(1);
				authResponse.setStatusMessage("User could not be authenticated");
			}
		}
		catch(SQLException e){
			logger.error("Error authorizing user : "+e.getMessage());
			e.printStackTrace();
			authResponse.setStatusCode(1);
			authResponse.setStatusMessage("Error authorizing user credentials");
		}
		return authResponse;
	}
	
	
}
