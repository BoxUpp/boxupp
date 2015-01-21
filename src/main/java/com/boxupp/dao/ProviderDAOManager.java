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
import com.boxupp.db.beans.ProviderBean;
import com.boxupp.responseBeans.StatusBean;
import com.j256.ormlite.dao.Dao;

public class ProviderDAOManager implements DAOImplInterface {
	private static Logger logger = LogManager.getLogger(ProjectDAOManager.class.getName());

	public static Dao<ProviderBean, Integer> providerDao = null;
	private static ProviderDAOManager providerDAOManager;
	
	public static ProviderDAOManager getInstance(){
		if(providerDAOManager == null){
			providerDAOManager = new ProviderDAOManager();
		}
		return providerDAOManager;
	}
	
	private ProviderDAOManager() {
		providerDao = DAOProvider.getInstance().fetchDao(ProviderBean.class);
	}
	@Override
	public StatusBean create(JsonNode newData) {
		return null;
	}

	
	public <E> List<E> retireveProviders() {
		List<ProviderBean> providerList = null;
		try{
			providerList = providerDao.queryForAll();
		}catch(SQLException e){
			logger.error("Error querying the provider list from DB : " + e.getMessage());
		}
		return (List<E>) providerList;
	}
	
	@Override
	public <T>T read(String providerID) {
		ProviderBean providerBean= null;
		try{
			providerBean = providerDao.queryForId(Integer.parseInt(providerID));
		}catch(SQLException e){
			logger.error("Error querying the providers list from DB : " + e.getMessage());
		}
		return (T) providerBean;
	}

	@Override
	public StatusBean update(JsonNode updatedData) {
		return null;
	}

	@Override
	public StatusBean delete(String id) {
		return null;
	}
	
}
