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
package com.boxupp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import com.boxupp.responseBeans.StatusBean;
import com.boxupp.responseBeans.VagrantFile;
import com.boxupp.responseBeans.VagrantFileStatus;
import com.boxupp.utilities.OSProperties;
import com.boxupp.utilities.PuppetUtilities;
import com.boxupp.utilities.Utilities;


/*
 * For producing a Vagrant Syntactic file 
 * */
public class FileManager {
	private static Logger logger = LogManager.getLogger(FileManager.class.getName());
	
	public VagrantFileStatus writeFileToDisk(String data, Integer userID){
		
		String projectDir = Utilities.getInstance().fetchActiveProjectDirectory(userID);
		String fileOutputDir = projectDir + OSProperties.getInstance().getOSFileSeparator() + 
								OSProperties.getInstance().getVagrantFileName();
		File boxuppDir = new File(projectDir);
		if(!boxuppDir.exists()){
			Utilities.getInstance().checkIfDirExists(new File(projectDir));
		}
		
		VagrantFileStatus vagrantFileStatus = new VagrantFileStatus();
		
		File file = new File(fileOutputDir);
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();
			vagrantFileStatus.setFileCreated(true);
			vagrantFileStatus.setFileCreationPath(fileOutputDir);
		} catch (IOException e) {
			logger.error("Error writing to the Vagrant File : "+e.getMessage());
			e.printStackTrace();
			file.delete();
			vagrantFileStatus.setFileCreated(false);
		}
		
		return vagrantFileStatus;
	}
	
	public StatusBean writeNodeFileToDisk(String data, String projectID ){
		StatusBean statusBean = new StatusBean();
		String fileOutputPath = Utilities.getInstance().constructProjectDirectory(Integer.parseInt(projectID))+
				OSProperties.getInstance().getOSFileSeparator()+OSProperties.getInstance().getManifestsDirName()+OSProperties.getInstance().getOSFileSeparator()+"site.pp";
		VagrantFileStatus vagrantFileStatus = new VagrantFileStatus();
		
		File file = new File(fileOutputPath);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();
			statusBean.setStatusCode(0);
			statusBean.setStatusMessage("Nodes.pp File Saved Successfully");
		} catch (IOException e) {
			logger.error("Error writing to the Nodes.pp File : "+e.getMessage());
			file.delete();
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error in Saving nodes.pp file :"+e.getMessage());
		}
		return statusBean;
	}
	
	public VagrantFile fetchVagrantFileData(String projectID, String userID){
		/*Integer userID = Integer.parseInt(projectData.get("userID").getTextValue());*/
		String projectDir = Utilities.getInstance().fetchActiveProjectDirectory(Integer.parseInt(userID));
		String fileLocation = projectDir + OSProperties.getInstance().getOSFileSeparator() + 
								OSProperties.getInstance().getVagrantFileName();
		File vagrantFile = new File(fileLocation);
		StringBuffer fileData = new StringBuffer();
		String lineData;
		VagrantFile fileResponse = new VagrantFile();
		try{
			if(vagrantFile.exists()){
				fileResponse.setFileExists(true);
				BufferedReader bufferedReader = new BufferedReader(new FileReader(vagrantFile));
				while((lineData = bufferedReader.readLine()) != null){
					fileData.append(lineData + OSProperties.getInstance().getOSLineSeparator());
				}
				bufferedReader.close();
				fileResponse.setFileContent(fileData.toString());
				fileResponse.setStatusCode(0);
			}else{
				fileResponse.setStatusCode(0);
				Utilities.getInstance().saveVagrantFile(projectID, userID);
			}
			return fileResponse;
		}catch(Exception e){
			fileResponse.setStatusCode(1);
			return fileResponse;
		}
	}
}
