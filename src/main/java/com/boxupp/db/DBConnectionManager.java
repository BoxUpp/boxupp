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
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.boxupp.dao.ProviderDAOManager;
import com.boxupp.db.beans.DockerLinkBean;
import com.boxupp.db.beans.ForwardedPortsBean;
import com.boxupp.db.beans.GitRepoBean;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.MachineProjectMapping;
import com.boxupp.db.beans.ProjectBean;
import com.boxupp.db.beans.ProjectProviderMappingBean;
import com.boxupp.db.beans.ProviderBean;
import com.boxupp.db.beans.PuppetModuleBean;
import com.boxupp.db.beans.PuppetModuleMapping;
import com.boxupp.db.beans.ShellScriptBean;
import com.boxupp.db.beans.ShellScriptMapping;
import com.boxupp.db.beans.SyncFoldersBean;
import com.boxupp.db.beans.UserDetailBean;
import com.boxupp.db.beans.UserProjectMapping;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DBConnectionManager {
	private static Logger logger = LogManager.getLogger(DBConnectionManager.class.getName());
	
	private static DBConnectionManager dbManager;
	private static ConnectionSource connectionSource;
	
	public static DBConnectionManager getInstance(){
		if(dbManager == null){
			dbManager = new DBConnectionManager();
		}
		return dbManager;		
	}
	
	public DBConnectionManager(){
		initDBConnection();	
		initDB();		
	}
	
	private void initDBConnection(){
		try {
			connectionSource = new JdbcConnectionSource(DerbyConfig.JDBC_URL);
		} catch (SQLException e) {
			logger.fatal("Error fetching connection from " + DerbyConfig.JDBC_URL + " : "+e.getMessage());
		}
	}
	
	public ConnectionSource fetchDBConnection(){
		return connectionSource;
	}
	
	private boolean initDB(){
		return createDatabases();
	}
	
	private static boolean createDatabases(){
		
		//************* CREATE TABLES **************//

			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(ProviderBean.class);
			classList.add(UserDetailBean.class);
			classList.add(ProjectBean.class);
			classList.add(ProjectProviderMappingBean.class);
			classList.add(UserProjectMapping.class);
			classList.add(ShellScriptBean.class);
			classList.add(ShellScriptMapping.class);
			classList.add(PuppetModuleMapping.class);
			classList.add(PuppetModuleBean.class);
			classList.add(ForwardedPortsBean.class);
			classList.add(SyncFoldersBean.class);
			classList.add(DockerLinkBean.class);
			classList.add(MachineConfigurationBean.class);
			classList.add(MachineProjectMapping.class);
			classList.add(GitRepoBean.class);
				
			for(Class className : classList){
				createTableIfNotExists(className);
			}
			return true;

	}
	
	public static void createTableIfNotExists(Class className){
		
		try {
			TableUtils.createTable(connectionSource, className);
		} catch (SQLException e) {
				
		}

	}
		
	public boolean checkForProviderEntries(){
		try {
			List<ProviderBean> providerList = ProviderDAOManager.getInstance().providerDao.queryForAll();
			if(providerList.size() == 0){
				if(addProviderEntries()){
					logger.info("Provider entries created");
				}else{
					logger.error("Provider entries could not be created");
					System.exit(1);
				}
			}
		} catch (SQLException e) {
			logger.error("Error querying the provider database");
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean addProviderEntries(){
		ProviderBean provider1 = new ProviderBean();
		provider1.setDisabled(false);
		provider1.setName("VirtualBox");
		
		try {
			DAOProvider.getInstance().fetchProviderDao().create(provider1);
		} catch (SQLException e) {
			logger.error("Error committing provider1 data to database");
			return false;
		}
		
		ProviderBean provider2 = new ProviderBean();
		provider2.setDisabled(false);
		provider2.setName("Docker");
		try {
			DAOProvider.getInstance().fetchProviderDao().create(provider2);
		} catch (SQLException e) {
			logger.error("Error committing provider2 data to database");
			return false;
		}
		return true;
	}
	
	public boolean releaseConnection(ConnectionSource connection){
		try {
			connection.close();
		} catch (SQLException e) {
			logger.debug("Error releasing the connection");
			return false;
		}
		return true;
	}

	public static void main(String args[]) throws SQLException{
		ProviderBean provider1 = new ProviderBean();
		provider1.setDisabled(false);
		provider1.setName("VirtualBox");
		
		DAOProvider.getInstance().fetchProviderDao().create(provider1);
		
		ProviderBean provider2 = new ProviderBean();
		provider2.setDisabled(false);
		provider2.setName("Docker");
		DAOProvider.getInstance().fetchProviderDao().create(provider2);
	}
}
