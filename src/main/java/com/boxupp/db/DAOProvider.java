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
package com.boxupp.db;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.MachineProjectMapping;
import com.boxupp.db.beans.ProjectBean;
import com.boxupp.db.beans.ProviderBean;
import com.boxupp.db.beans.ShellScriptBean;
import com.boxupp.db.beans.ShellScriptMapping;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

public class DAOProvider {
	
	private static Logger logger = LogManager.getLogger(DAOProvider.class.getName());
	private static DAOProvider daoController;
	
	public static DAOProvider getInstance(){
		if(daoController == null){
			daoController = new DAOProvider();
		}
		return daoController;
	}
	
	public DAOProvider(){
		
	}
	public  <T> Dao<T, Integer> fetchDao(Class<T> T){
		Dao<T, Integer> projectDao = null;
		try{
			projectDao = DaoManager.createDao(DBConnectionManager.getInstance().fetchDBConnection(), T);
		}catch(SQLException e){
			logger.error("Error initializing DAO access object for"+T +" "+e.getMessage());
		}
		return projectDao;
	}
	
	public Dao<ProjectBean,Integer> fetchProjectDao(){
		Dao<ProjectBean, Integer> projectDao = null;
		try{
			projectDao = DaoManager.createDao(DBConnectionManager.getInstance().fetchDBConnection(), ProjectBean.class);
		}catch(SQLException e){
			logger.error("Error initializing DAO access object for ProjectBean.class : "+e.getMessage());
		}
		return projectDao;
	}
	
	public Dao<ProviderBean,Integer> fetchProviderDao(){
		Dao<ProviderBean, Integer> providerDao = null;
		try{
			
			providerDao = DaoManager.createDao(DBConnectionManager.getInstance().fetchDBConnection(), ProviderBean.class);
		}catch(SQLException e){
			logger.error("Error initializing DAO access object for ProviderBean.class : "+e.getMessage());
		}
		return providerDao;
	}
	public Dao<MachineConfigurationBean,Integer> fetchMachineConfigDao(){
		Dao<MachineConfigurationBean, Integer> machineConfigDao = null;
		try{
			machineConfigDao = DaoManager.createDao(DBConnectionManager.getInstance().fetchDBConnection(), MachineConfigurationBean.class);
		}catch(SQLException e){
			logger.error("Error initializing DAO access object for MachineConfigurationBean.class : "+e.getMessage());
		}
		return machineConfigDao;
	} 
	public Dao<MachineProjectMapping,Integer> fetchMachineMappingDao(){
		Dao<MachineProjectMapping, Integer> machineMappingDao = null;
		try{
			machineMappingDao = DaoManager.createDao(DBConnectionManager.getInstance().fetchDBConnection(), MachineProjectMapping.class);
		}catch(SQLException e){
			logger.error("Error initializing DAO access object for MachineMapping.class : "+e.getMessage());
		}
		return machineMappingDao;
	} 
	
	public Dao<ShellScriptBean,Integer> fetchShellScriptDao(){
		Dao<ShellScriptBean, Integer> shellScriptDao = null;
		try{
			
			shellScriptDao = DaoManager.createDao(DBConnectionManager.getInstance().fetchDBConnection(), ShellScriptBean.class);
		}catch(SQLException e){
			logger.error("Error initializing DAO access object for ShellScriptBean.class : "+e.getMessage());
		}
		return shellScriptDao;
	}
	public Dao<ShellScriptMapping,Integer> fetchShellScriptMappingDao(){
		Dao<ShellScriptMapping, Integer> shellScriptMappingDao = null;
		try{
			
			shellScriptMappingDao = DaoManager.createDao(DBConnectionManager.getInstance().fetchDBConnection(), ShellScriptMapping.class);
		}catch(SQLException e){
			logger.error("Error initializing DAO access object for ProviderBean.class : "+e.getMessage());
		}
		return shellScriptMappingDao;
	}
}
