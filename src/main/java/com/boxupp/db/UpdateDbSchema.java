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

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.boxupp.dao.AwsProjectDAOManager;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.ProviderBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;

public class UpdateDbSchema {
	private static Logger logger = LogManager.getLogger(UpdateDbSchema.class.getName());

	private static UpdateDbSchema updateDBSchemaObj = null;
	
	private UpdateDbSchema(){
	}
	
	public static UpdateDbSchema getInstance(){
		if(updateDBSchemaObj==null){
			updateDBSchemaObj=new UpdateDbSchema();
		}
		return updateDBSchemaObj;
	}
	
	public void checkRequiredColumnsExists(ConnectionSource connectionSource){
		try{
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			Connection connection = DriverManager.getConnection(DerbyConfig.JDBC_URL);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from MACHINECONFIGURATION");
			ResultSetMetaData rsMetaData = resultSet.getMetaData();
			
			Field[] beanClassFields = MachineConfigurationBean.class.getDeclaredFields();

			HashMap<String, Boolean> dbTableColumnsMap = new HashMap<String, Boolean>();
			for(int metaDataCounter = 1 ; metaDataCounter<=rsMetaData.getColumnCount();metaDataCounter++){
				dbTableColumnsMap.put(rsMetaData.getColumnName(metaDataCounter).toLowerCase(), true);
			}
			
			for(int fieldCounter=0;fieldCounter<beanClassFields.length;fieldCounter++){
				
				String fieldName = beanClassFields[fieldCounter].getName();
				if( beanClassFields[fieldCounter].isAnnotationPresent(DatabaseField.class)  
						&& !dbTableColumnsMap.containsKey(fieldName.toLowerCase())){
					if(beanClassFields[fieldCounter].getType().getSimpleName().equals("String"))
						statement.executeUpdate("ALTER TABLE MACHINECONFIGURATION  ADD COLUMN "+ fieldName +" VARCHAR(255) ");
					else if(beanClassFields[fieldCounter].getType().getSimpleName().equals("Integer"))
						statement.executeUpdate("ALTER TABLE MACHINECONFIGURATION  ADD COLUMN "+ fieldName +" INTEGER");
				}

			}

			resultSet.close();
			statement.close();
			connection.close();
			
			boolean flagToAddAwsProviderEntry = true;

			Dao<ProviderBean, Integer> dao = DaoManager.createDao(connectionSource, ProviderBean.class);
			List<ProviderBean> providers = dao.queryForAll();
			for(ProviderBean provider : providers){
				if(provider.getName().equals(AwsProjectDAOManager.PROVIDER_NAME))
					flagToAddAwsProviderEntry=false;
			}
			
			if(flagToAddAwsProviderEntry){
				ProviderBean newProvider = new ProviderBean();
				newProvider.setDisabled(false);
				newProvider.setName("AWS");
				dao.create(newProvider);
			}
			
		}
		catch(Exception exception){
			logger.info("SQL exception while updating machine config table"+exception.getMessage());
		}
	}

}
