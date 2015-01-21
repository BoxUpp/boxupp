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

public class OSProperties {
	
	private static OSProperties osProperties = null;
	private String OSFileSeparator = "";
	private String OSName = "";
	private String OSLineSeparator = "";
	private String UserHomeDirectory = "";
	private String vagrantFileName = "Vagrantfile";
	private String scriptsDirName = "scripts";
	private String primaryFolderName="Boxupp";
	private String serializedFileName = "serData";
	private String moduleDirName = "modules";
	private String manifestsDirName = "manifests";
	private String nodeFileName = "site.pp";
	private String dockerVagrantFileDir = "docker";
	private String databaseFolderName = "db";
	private String logDirName =  "logs";
			
	private OSProperties(){
			OSFileSeparator = System.getProperty("file.separator");
			OSName = System.getProperty("os.name").toLowerCase();
			OSLineSeparator = System.getProperty("line.separator");
			UserHomeDirectory = System.getProperty("user.home");
	}
	
	public static OSProperties getInstance(){
		if(osProperties == null){
			osProperties = new OSProperties();
		}
		return osProperties;
	}

	public String getOSFileSeparator() {
		return OSFileSeparator;
	}

	public String getOSName() {
		return OSName;
	}

	public String getOSLineSeparator() {
		return OSLineSeparator;
	}

	public String getUserHomeDirectory() {
		return UserHomeDirectory;
	}

	public String getVagrantFileName() {
		return vagrantFileName;
	}

	public void setVagrantFileName(String vagrantFileName) {
		this.vagrantFileName = vagrantFileName;
	}

	public String getScriptsDirName() {
		return scriptsDirName;
	}

	public void setScriptsDirName(String shellDirName) {
		this.scriptsDirName = shellDirName;
	}

	public String getPrimaryFolderName() {
		return primaryFolderName;
	}

	public void setPrimaryFolderName(String primaryFolderName) {
		this.primaryFolderName = primaryFolderName;
	}

	public String getSerializedFileName() {
		return serializedFileName;
	}

	public void setSerializedFileName(String serializedFileName) {
		this.serializedFileName = serializedFileName;
	}

	public String getModuleDirName() {
		return moduleDirName;
	}

	public void setModuleDirName(String moduleDirName) {
		this.moduleDirName = moduleDirName;
	}

	public String getManifestsDirName() {
		return manifestsDirName;
	}

	public void setManifestsDirName(String manifestsDirName) {
		this.manifestsDirName = manifestsDirName;
	}
	public String getNodeFileName() {
		return nodeFileName;
	}

	public void setNodeFileName(String nodeFileName) {
		this.nodeFileName = nodeFileName;
	}

	public String getDockerVagrantFileDir() {
		return dockerVagrantFileDir;
	}

	public void setDockerVagrantFileDir(String dockerVagrantFileDir) {
		this.dockerVagrantFileDir = dockerVagrantFileDir;
	}

	public String getDatabaseFolderName() {
		return databaseFolderName;
	}

	public void setDatabaseFolderName(String databaseFolderName) {
		this.databaseFolderName = databaseFolderName;
	}

	public String getLogDirName() {
		return logDirName;
	}

	public void setLogDirName(String logDirName) {
		this.logDirName = logDirName;
	}
	
}
