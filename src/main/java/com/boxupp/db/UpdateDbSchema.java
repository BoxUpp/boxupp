 package com.boxupp.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.ProviderBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
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
			Field[] fields = MachineConfigurationBean.class.getDeclaredFields();

			if(rsMetaData.getColumnCount()!=fields.length-7){
				for(int fieldCounter = rsMetaData.getColumnCount()+1;fieldCounter<=fields.length-7;fieldCounter++){
					if(fields[fieldCounter].getType().getSimpleName().equals("String"))
						statement.executeUpdate("ALTER TABLE MACHINECONFIGURATION  ADD COLUMN "+ fields[fieldCounter].getName() +" VARCHAR(255) ");
					else if(fields[fieldCounter].getType().getSimpleName().equals("Integer"))
						statement.executeUpdate("ALTER TABLE MACHINECONFIGURATION  ADD COLUMN "+ fields[fieldCounter].getName() +" INTEGER");
				}
				resultSet.close();
				statement.close();
				connection.close();
				
				ProviderBean newProvider = new ProviderBean();
				newProvider.setDisabled(false);
				newProvider.setName("AWS");
				Dao<ProviderBean, Integer> dao = DaoManager.createDao(connectionSource, ProviderBean.class);
				dao.create(newProvider);
			}
		}
		catch(Exception exception){
			logger.info("SQL exception while updating machine config table"+exception.getMessage());
		}
	}
	
}
