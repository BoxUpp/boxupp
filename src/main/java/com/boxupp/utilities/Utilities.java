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
package com.boxupp.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.boxupp.ConfigurationGenerator;
import com.boxupp.FileManager;
import com.boxupp.dao.AwsProjectDAOManager;
import com.boxupp.dao.MachineConfigDAOManager;
import com.boxupp.dao.ProjectDAOManager;
import com.boxupp.dao.PuppetModuleDAOManager;
import com.boxupp.dao.ShellScriptDAOManager;
import com.boxupp.db.DAOProvider;
import com.boxupp.db.beans.AwsProjectCredentialsBean;
import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.ProjectAwsCredentialsMapping;
import com.boxupp.db.beans.PuppetModuleBean;
import com.boxupp.db.beans.PuppetModuleMapping;
import com.boxupp.db.beans.ShellScriptBean;
import com.boxupp.db.beans.ShellScriptMapping;
import com.boxupp.db.beans.SyncFoldersBean;
import com.boxupp.responseBeans.StatusBean;
import com.boxupp.responseBeans.VagrantFileStatus;

public class Utilities { 

	private static Logger logger = LogManager.getLogger(Utilities.class.getName());
	private static Utilities utilities = null;
	private String activeProjectDirectory = "";
	public OSProperties osProperties = null;
	private static HashMap<Integer,Integer> activeMappings = null;

	public Utilities(){
		osProperties = OSProperties.getInstance();
		activeMappings = new HashMap<Integer,Integer>();
	}

	public static Utilities getInstance(){
		if(utilities == null){
			try{
				utilities = new Utilities();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return utilities;
	}

	public boolean createRequiredFoldersIfNotExists(){
		String boxuppDirPath = osProperties.getUserHomeDirectory() + 
				osProperties.getOSFileSeparator() + "Boxupp";
		String moduleDir = PuppetUtilities.getInstance().constructModuleDirectory();
		
		checkIfDirExists(new File(moduleDir));
		
		File boxuppDir = new File(boxuppDirPath);
		if(!boxuppDir.exists()){
			boxuppDir.mkdirs();
		}
		return true;
	}
	public void initializeDirectory(Integer projectID){

		String userHomeDir = osProperties.getUserHomeDirectory() + 
				osProperties.getOSFileSeparator() + "Boxupp" + 
				osProperties.getOSFileSeparator() + projectID;
		File projectDir = new File(userHomeDir);
		if(projectDir.exists()){
			logger.debug("Project Directory found at : "+userHomeDir);
		}else{
			logger.debug("Project Directory does not exist");
			projectDir.mkdir();
			logger.debug("Project Directory initialized at : " + userHomeDir);
		}
		String manifestDir = userHomeDir+osProperties.getOSFileSeparator()+osProperties.getManifestsDirName();
		checkIfDirExists(new File(manifestDir));
		
		//		activeProjectDirectory = userHomeDir;
	}
	public void initializeDockerVagrantFile(Integer projectID){
		String projectDir = constructProjectDirectory(projectID);
		try {
			checkIfDirExists(new File(projectDir+osProperties.getOSFileSeparator()+osProperties.getDockerVagrantFileDir()));
			InputStream resourceUrl = getClass().getResourceAsStream("/Vagrantfile");
			BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl));
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(projectDir+osProperties.getOSFileSeparator()+
					osProperties.getDockerVagrantFileDir()+
					osProperties.getOSFileSeparator()+
					osProperties.getVagrantFileName())));
			String data = "";
			while((data = reader.readLine())!= null){
				writer.write(data);
				writer.newLine();
			}
			reader.close();
			writer.close();
		} catch (IOException e) {
			logger.error("Error in coping static vagrant file for docker "+e.getMessage());
		} 

	}

	public void commitSyncFoldersToDisk(List<MachineConfigurationBean> machineConfigs, Integer userID){

		for(MachineConfigurationBean machineConfig : machineConfigs){
			ArrayList<SyncFoldersBean> syncFolders = null;
			try {
				syncFolders = machineConfig.getSyncFolders();
			} catch (SQLException e) {
				logger.error("Error getting syncFolders : "+e.getMessage());
			}
			for(SyncFoldersBean syncFolder : syncFolders){
				createFolderOnDisk(syncFolder.getHostFolder(),userID);
			}
		}
	}
	public void createFolderOnDisk(String folderName, Integer userID){
		String folderLocation = fetchActiveProjectDirectory(userID) + osProperties.getOSFileSeparator() + folderName; 
		File directory = new File(folderLocation);
		if(!directory.exists()){
			directory.mkdir();
		}
	}

	public void writeScriptToDisk(ShellScriptBean scriptBean, Integer userID){
		String scriptsDir = fetchActiveProjectDirectory(userID)+osProperties.getOSFileSeparator()
				+osProperties.getScriptsDirName()+osProperties.getOSFileSeparator();
		checkIfDirExists(new File(scriptsDir));
		try{
			BufferedWriter scriptWriter = new BufferedWriter(new FileWriter(new File(scriptsDir + scriptBean.getScriptName())));
			scriptWriter.write(scriptBean.getScriptContent());
			scriptWriter.close();
		}
		catch(IOException e){
			logger.error("Error writing script : "+scriptBean.getScriptName() + " : "+e.getMessage());
		}
	}    
	public void updateScriptData(ShellScriptBean scriptBean, Integer userID){
		String scriptsDir = fetchActiveProjectDirectory(userID) + osProperties.getOSFileSeparator()
				+osProperties.getScriptsDirName()+osProperties.getOSFileSeparator();
		checkIfDirExists(new File(scriptsDir));
		try{
			deleteFile(new File(scriptsDir + scriptBean.getScriptName()));
			BufferedWriter scriptWriter = new BufferedWriter(new FileWriter(new File(scriptsDir + scriptBean.getScriptName())));
			scriptWriter.write(scriptBean.getScriptContent());
			scriptWriter.close();
		}
		catch(IOException e){
			logger.error("Error updating script : "+scriptBean.getScriptName() + " : "+e.getMessage());
		}
	}

	public String fetchActiveProjectDirectory(Integer userID){
		/*if(activeProjectDirectory.isEmpty()){
			initializeDirectory();
		}
		return activeProjectDirectory;*/
		return constructProjectDirectory(activeMappings.get(userID));
	}

	public String constructProjectDirectory(Integer projectID){
		return  osProperties.getUserHomeDirectory() + 
				osProperties.getOSFileSeparator() +
				"Boxupp" + osProperties.getOSFileSeparator() + projectID;
	}

	public void changeActiveDirectory(Integer userID, Integer projectID){
		activeMappings.put(userID, projectID);
		/*OSProperties osProperties = OSProperties.getInstance();
		try {
			String projectName = DAOProvider.getInstance().fetchProjectDao().queryForId(projectID).getName();
			activeProjectDirectory = osProperties.getUserHomeDirectory() + 
									 osProperties.getOSFileSeparator() +
									osProperties.getPrimaryFolderName() +
									osProperties.getOSFileSeparator() + projectName;
		} catch (SQLException e) {
			logger.error("Error setting the active project directory : "+e.getMessage());
			return false;
		}
		return true;*/
	}

	public void checkIfDirExists(File dirLocation){
		if(!dirLocation.isDirectory()){
			dirLocation.mkdirs();
		}
	}

	public void deleteScriptfileOnDisk(String fileName, Integer userID){
		String scriptFilepath = fetchActiveProjectDirectory(userID) + osProperties.getOSFileSeparator()
				+osProperties.getScriptsDirName()+osProperties.getOSFileSeparator()+fileName;
		File file  = new File(scriptFilepath);
		deleteFile(file);

	}

	public void deleteProjectFile(Integer projectID){
		String projectDir = constructProjectDirectory(projectID);
		File file  = new File(projectDir);
		deleteFile(file);
	}


	public void deleteFile(File file){

		if(file.isDirectory()){

			//directory is empty, then delete it
			if(file.list().length==0){

				file.delete();

			}else{

				//list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					//construct the file structure
					File fileDelete = new File(file, temp);

					//recursive delete
					deleteFile(fileDelete);
				}

				//check the directory again, if empty then delete it
				if(file.list().length==0){
					file.delete();
				}
			}

		}else{
			//if file, then delete it
			file.delete();
		}

	}

	public VagrantFileStatus saveVagrantFile(String projectID, String userID){
		/*String projectID = vargantFileData.get("projectID").getTextValue();
		String userID = vargantFileData.get("userID").getTextValue();*/
		String provider  = ProjectDAOManager.getInstance().getProviderForProject(projectID);
		VagrantFileStatus fileStatus = new VagrantFileStatus();
		AwsProjectCredentialsBean awsProjcetCredBean =  null;
		if(provider.equals("AWS")){
			awsProjcetCredBean = AwsProjectDAOManager.getInstance().read(projectID);
		}

		List<MachineConfigurationBean>  machineConfigList = MachineConfigDAOManager.getInstance().retireveBoxesForProject(projectID);
		List<PuppetModuleBean>  puppetModuleList = PuppetModuleDAOManager.getInstance().retireveModulesForProject(projectID);
		List<ShellScriptBean> shellScriptList = ShellScriptDAOManager.getInstance().retireveScriptsForProject(projectID);
		List<ShellScriptMapping> shellScriptMappingList = ProjectDAOManager.getInstance().retireveScriptsMapping(projectID);
		List<PuppetModuleMapping> puppetModuleMappingList = ProjectDAOManager.getInstance().retireveModulesMapping(projectID);
		Utilities.getInstance().commitSyncFoldersToDisk(machineConfigList, Integer.parseInt(userID));
		boolean configFileData = ConfigurationGenerator.generateConfig(machineConfigList, puppetModuleList,  shellScriptList, shellScriptMappingList, puppetModuleMappingList, provider, projectID,awsProjcetCredBean);
		if(configFileData){
			logger.info("Started saving vagrant file");
			FileManager fileManager = new FileManager();
			String renderedTemplate = ConfigurationGenerator.getVelocityFinalTemplate();
			fileStatus = fileManager.writeFileToDisk(renderedTemplate, Integer.parseInt(userID));
			logger.info("Vagrant file save completed");
		}
		else{
			logger.info("Failed to save vagrant file !!");
		}
		//persistData(mappings);
		return fileStatus;
	}
	public  StatusBean copyFile(File source, File dest) throws IOException {
		StatusBean stBean = new StatusBean();
		InputStream inStream = null;
		OutputStream outStream = null;
	 
	    	try{
	  
	    	    inStream = new FileInputStream(source);
	    	    outStream = new FileOutputStream(dest);
	 
	    	    byte[] buffer = new byte[1024];
	 
	    	    int length;
	    	    //copy the file content in bytes 
	    	    while ((length = inStream.read(buffer)) > 0){
	 	    	    	outStream.write(buffer, 0, length);
	 
	    	    }
	 
	    	    inStream.close();
	    	    outStream.close();
	    	    stBean.setStatusCode(0);
	    	    stBean.setStatusMessage("File is copied successful!");
	 
	    	}catch(IOException e){
	    		logger.error("Error in copy file : "+e.getMessage());
	    	}
	    return stBean;
	}
}
