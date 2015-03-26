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

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.boxupp.db.DAOProvider;
import com.boxupp.db.beans.AwsProjectCredentialsBean;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.ProjectAwsCredentialsMapping;
import com.boxupp.db.beans.ProjectBean;
import com.boxupp.responseBeans.StatusBean;
import com.boxupp.utilities.Utilities;
import com.boxupp.vagrant.VagrantCommandExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

public class AwsProjectDAOManager {
	private static Logger logger = LogManager.getLogger(AwsProjectDAOManager.class.getName());

	private static AwsProjectDAOManager awsProjectDAOManager;
	private static Dao<AwsProjectCredentialsBean,Integer> awsCredentialsDao = null;
	Dao<ProjectAwsCredentialsMapping,Integer> projectAwsCredentialsMappingDao = null;
	
	public static final String PROVIDER_NAME = "AWS";


	public static AwsProjectDAOManager getInstance(){
		if(awsProjectDAOManager==null){
			awsProjectDAOManager=new AwsProjectDAOManager();
		}
		return awsProjectDAOManager;
	}

	private AwsProjectDAOManager(){
		awsCredentialsDao = DAOProvider.getInstance().fetchDao(AwsProjectCredentialsBean.class);
		projectAwsCredentialsMappingDao = DAOProvider.getInstance().fetchDao(ProjectAwsCredentialsMapping.class);
	}

	public StatusBean create(JsonNode newData,ProjectBean projectBean) {
		StatusBean statusBean = null;
		Gson projectData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		AwsProjectCredentialsBean awsProjectCredBean = new AwsProjectCredentialsBean();
		awsProjectCredBean = projectData.fromJson(newData.toString(), AwsProjectCredentialsBean.class);

		try{
			if(awsProjectCredBean.getPrivateKeyPath().contains("\\")){
				String newPrivateKeyPath = AwsProjectDAOManager.getInstance().resetPrivateKeyPath(awsProjectCredBean.getPrivateKeyPath());
				awsProjectCredBean.setPrivateKeyPath(newPrivateKeyPath);
			}
			int rowsUpdated = awsCredentialsDao.create(awsProjectCredBean);

			ProjectAwsCredentialsMapping projectAwsCredMapBean = new ProjectAwsCredentialsMapping(projectBean,awsProjectCredBean);
			rowsUpdated = projectAwsCredentialsMappingDao.create(projectAwsCredMapBean);
		}
		catch(SQLException exception){
			logger.info("Error while saving project credentials "+exception.getMessage());
		}
		return statusBean;
	}

	public <T> T read(String projectID) {
		AwsProjectCredentialsBean awsProjcetCredBean = retireveAwsProjectCredentials(projectID) ;
		return (T) awsProjcetCredBean;
	}


	public StatusBean setHostName(JsonNode newData){

		StatusBean statusBean = new StatusBean();
		Gson machineConfigData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		MachineConfigurationBean machineConfigBean = null;
		machineConfigBean = machineConfigData.fromJson(newData.toString(),MachineConfigurationBean.class);

		String path = Utilities.getInstance().fetchActiveProjectDirectory(Integer.parseInt(newData.get("userID").getTextValue()));
		path+=Utilities.getInstance().osProperties.getOSFileSeparator()+".vagrant"+Utilities.getInstance().osProperties.getOSFileSeparator()+"machines"+Utilities.getInstance().osProperties.getOSFileSeparator()+
				machineConfigBean.getVagrantID()+Utilities.getInstance().osProperties.getOSFileSeparator()+"aws"+
				Utilities.getInstance().osProperties.getOSFileSeparator()+"id";

		AwsProjectCredentialsBean awsProjectCredentialsBean = null;
		String instanceID = AwsProjectDAOManager.getInstance().getInstanceId(path);
		if(instanceID!=null){
			awsProjectCredentialsBean = AwsProjectDAOManager.getInstance().read(newData.get("projectID").asText());
			String privateHostName =AwsProjectDAOManager.getInstance().getPrivateHostName(instanceID, awsProjectCredentialsBean.getAwsAccessKeyId(), awsProjectCredentialsBean.getAwsSecretAccessKey(),machineConfigBean.getInstanceRegion()); ;

			machineConfigBean.setHostName(privateHostName);
			machineConfigBean.setConfigChangeFlag(0);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode json = objectMapper.valueToTree(machineConfigBean);

			MachineConfigDAOManager.getInstance().update(json);
			statusBean.setData(machineConfigBean);
			statusBean.setStatusCode(0);
			statusBean.setStatusMessage("Host Name Set");
		}
		else{
			statusBean.setData(machineConfigBean);
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Unable to set Host Name");
		}

		return statusBean;
	}

	private String getPrivateHostName(String instanceID,String accessKeyId,String secretKey,String instanceRegion){
		String privateHostName = null;
		BasicAWSCredentials cred = new BasicAWSCredentials(accessKeyId,secretKey);
		AmazonEC2Client ec2Client = new AmazonEC2Client(cred);
		try{
			ec2Client.setRegion(Region.getRegion(Regions.fromName(instanceRegion)));
			ArrayList<String> instanceIds=new ArrayList<String>();
			instanceIds.add(instanceID);
			DescribeInstancesRequest req = new DescribeInstancesRequest();
			req.setInstanceIds(instanceIds);
			DescribeInstancesResult result =ec2Client.describeInstances(req);
			Instance instance = result.getReservations().get(0).getInstances().get(0);
			privateHostName=instance.getPrivateDnsName();
		}
		catch(AmazonServiceException amazonServiceException){
			logger.info("Error while fecthing instance info from aws "+amazonServiceException.getMessage());
		}
		catch(Exception exception){
			logger.info("Error while fecthing instance info from aws "+exception.getMessage());
		}
		return privateHostName;

	}
	private String getInstanceId(String path){
		File file =new File(path);
		String instanceID = null;
		try{
			Scanner fileReader = new Scanner(file);
			instanceID = fileReader.next();
			fileReader.close();
		}
		catch(FileNotFoundException exception){
			logger.info("Error  while reading instance id file "+exception.getMessage());	    	
		}
		return instanceID; 

	}
	public StatusBean authenticateAwsCredentials(JsonNode awsCredentials){
		StatusBean statusBean = new StatusBean();
		String keyPair = awsCredentials.get("awsKeyPair").asText();
		String privateKeyPath = awsCredentials.get("privateKeyPath").asText();
		BasicAWSCredentials cred = new BasicAWSCredentials(awsCredentials.get("awsAccessKeyId").asText(),awsCredentials.get("awsSecretAccessKey").asText());
		AmazonEC2Client ec2Client = new AmazonEC2Client(cred);
		try{
			statusBean = validateKeyPair(ec2Client, keyPair);
			if(statusBean.getStatusCode()==0){
				checkIfPrivateFileExists(privateKeyPath);
			}
		}
		catch(AmazonServiceException amazonServiceException){
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error while authenticating aws credentials");
			logger.info("invalid aws credentials "+amazonServiceException.getMessage());
		}
		catch(FileNotFoundException fileNotFoundException){
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Private Key file not found at entered path");
			logger.info("Private Key file not found at entered path "+fileNotFoundException.getMessage());
		}
		catch(Exception exception){
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error while authenticating aws credentials");
			logger.info("invalid aws credentials "+exception.getMessage());
		}
		return statusBean;
	}

	private void checkIfPrivateFileExists(String privatekeyPath)throws FileNotFoundException{
		File file = new File(privatekeyPath);
		Scanner fileReader = new Scanner(file);
		fileReader.close();
	}

	private StatusBean validateKeyPair(AmazonEC2Client ec2Client,String keyPair)throws AmazonServiceException{
		StatusBean statusBean = new StatusBean();
		List<com.amazonaws.services.ec2.model.Region> regions = ec2Client.describeRegions().getRegions();
		for(com.amazonaws.services.ec2.model.Region region:regions){
    		ec2Client.setRegion(com.amazonaws.regions.Region.getRegion(com.amazonaws.regions.Regions.fromName(region.getRegionName())));
    			List<KeyPairInfo> keyPairs= ec2Client.describeKeyPairs().getKeyPairs();
    			for(KeyPairInfo keyPairInfo:keyPairs){
    				if(keyPairInfo.getKeyName().equals(keyPair)){
    					statusBean.setStatusCode(0);
    					statusBean.setStatusMessage("Key Pair validated with region "+region.getRegionName());
    					return statusBean;
    				}
    			}
    		}
		statusBean.setStatusCode(1);
		statusBean.setStatusMessage("Key Pair not found Please enter a valid key pair");
		return statusBean;
	}

	private String resetPrivateKeyPath(String privateKeyPath){
		StringBuilder newKeyPath = new StringBuilder();
		for(int i=0;i<privateKeyPath.length();i++){
			char ch = privateKeyPath.charAt(i);
			if(ch=='\\'){
				newKeyPath.append('/');
			}
			else{
				newKeyPath.append(ch);
			}
		}
		return newKeyPath.toString();
	}


	public AwsProjectCredentialsBean retireveAwsProjectCredentials(String projectID){
		List<AwsProjectCredentialsBean> credentialsList = new ArrayList<AwsProjectCredentialsBean>();
		PreparedQuery<AwsProjectCredentialsBean> awsCredentialsQuery = null;
		try{
			awsCredentialsQuery=makeQueryForAwsProjectCredentials();
			ProjectBean projectBean = ProjectDAOManager.getInstance().projectDao.queryForId(Integer.parseInt(projectID));
			if(projectBean!=null){
				awsCredentialsQuery.setArgumentHolderValue(0, projectBean);
				credentialsList = awsCredentialsDao.query(awsCredentialsQuery);
			}
		}
		catch (NumberFormatException e) {
			logger.info("Error in retireveing aws credentials for project : "+e.getMessage());
		} catch (SQLException e) {
			logger.info("Error in retireveing aws credentials for project : "+e.getMessage());
		}
		return credentialsList.get(0);
	}
	
	private PreparedQuery<AwsProjectCredentialsBean> makeQueryForAwsProjectCredentials() throws SQLException{
		QueryBuilder<ProjectAwsCredentialsMapping, Integer> projectAwsMappingQb = projectAwsCredentialsMappingDao.queryBuilder();
		projectAwsMappingQb.selectColumns(ProjectAwsCredentialsMapping.CREDENTIALS_ID_FIELD_NAME);
		SelectArg userSelectArg = new SelectArg();
		projectAwsMappingQb.where().eq(ProjectAwsCredentialsMapping.PROJECT_ID_FIELD_NAME, userSelectArg);
		QueryBuilder<AwsProjectCredentialsBean, Integer>  awsCredQb = awsCredentialsDao.queryBuilder();
		awsCredQb.where().in(AwsProjectCredentialsBean.ID_FIELD_NAME, projectAwsMappingQb);
		return awsCredQb.prepare();
	}
	

}
