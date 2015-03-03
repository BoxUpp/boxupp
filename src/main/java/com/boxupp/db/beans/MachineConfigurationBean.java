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
package com.boxupp.db.beans;

import java.sql.SQLException;
import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.boxupp.dao.MachineConfigDAOManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "machineConfiguration")
public class MachineConfigurationBean {
	/*public MachineConfigurationBean(ForwardedPortsBean portMappings, SyncFoldersBean syncFolders, DockerLinkBean dockerLinks){
		
	}*/
	
	public static final String ID_FIELD_NAME = "machineID";


	@DatabaseField(canBeNull = false, generatedId = true, useGetSet = true, columnName=ID_FIELD_NAME)
	private Integer machineID;

	@DatabaseField(useGetSet = true, canBeNull=false)
	private String vagrantID;

	@DatabaseField(useGetSet = true)
	private String hostName;

	@DatabaseField(useGetSet = true)
	private String boxType;

	@DatabaseField(useGetSet = true)
	private String boxUrl;

	@DatabaseField(useGetSet = true)
	private String networkIP;

	@DatabaseField(useGetSet=true)
	private String machineAmi;
	
	@DatabaseField(useGetSet=true)
	private String instanceType;
	
	@DatabaseField(useGetSet=true)
	private String instanceRegion;
	
	@DatabaseField(useGetSet=true)
	private String sshUserName;
	
	@DatabaseField(useGetSet=true)
	private String instanceCategory;

	
	@ForeignCollectionField(eager = true, maxEagerLevel =1)
	private ForeignCollection<ForwardedPortsBean> ormPortMappings;
	
	private ArrayList<ForwardedPortsBean> portMappings;
	
	@ForeignCollectionField(eager = true, maxEagerLevel =1)
	private ForeignCollection<SyncFoldersBean> ormSyncFolders;
	
	private ArrayList<SyncFoldersBean> syncFolders;
	
	@ForeignCollectionField(eager = true, maxEagerLevel =1)
	private ForeignCollection<DockerLinkBean> ormDockerLinks;

	private ArrayList<DockerLinkBean> dockerLinks;
	
	@DatabaseField(useGetSet = true)
	private String provisionerName;

	@DatabaseField(useGetSet = true)
	private String cpuExecCap;

	@DatabaseField(useGetSet = true)
	private String memory;

	@DatabaseField(useGetSet = true)
	private String bootTimeout;

	@DatabaseField(useGetSet = true)
	private boolean guiMode;

	@DatabaseField(useGetSet = true)
	private boolean puppetMasterStatus;

	@DatabaseField(useGetSet = true)
	private String providerType;

	@DatabaseField(useGetSet = true)
	private boolean shellMappedStatus;

	@DatabaseField(useGetSet = true)
	private boolean puppetMappedStatus;

	@DatabaseField(useGetSet = true)
	private boolean chefMappedStatus;

	@DatabaseField(useGetSet = true)
	private String dockerImage;

	@DatabaseField(useGetSet = true)
	private String username;

	@DatabaseField(useGetSet = true)
	private String password;
	
	@DatabaseField(useGetSet = true)
	private Boolean isDisabled;
	
	@DatabaseField(useGetSet = true, defaultValue="0")
	private Integer configChangeFlag;
	
	@DatabaseField(useGetSet = true, defaultValue="0")
	private Integer scriptChangeFlag;
	
	@DatabaseField(useGetSet = true, defaultValue="0")
	private Integer moduleChangeFlag;
	
	public void setMachineAmi(String machineAmi){
		this.machineAmi=machineAmi;
	}
	
	public String getMachineAmi(){
		return machineAmi;
	}

	public void setInstanceType(String instanceType){
		this.instanceType=instanceType;
	}
	
	public String getInstanceType(){
		return instanceType;
	}
	
	public void setInstanceRegion(String instanceRegion){
		this.instanceRegion=instanceRegion;
	}

	public String getInstanceRegion(){
		return instanceRegion;
	}
	
	public void setSshUserName(String sshUserName){
		this.sshUserName=sshUserName;
	}
	
	public String getSshUserName(){
		return sshUserName;
	}
	
	public void setInstanceCategory(String instanceCategory){
		this.instanceCategory=instanceCategory;
	}
	
	public String getInstanceCategory(){
		return instanceCategory;
	}

	public Integer getMachineID() {
		return machineID;
	}

	public void setMachineID(Integer machineID) {
		this.machineID = machineID;
	}

	public String getVagrantID() {
		return vagrantID;
	}

	public void setVagrantID(String vagrantID) {
		this.vagrantID = vagrantID;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getBoxType() {
		return boxType;
	}

	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}

	public String getBoxUrl() {
		return boxUrl;
	}

	public void setBoxUrl(String boxUrl) {
		this.boxUrl = boxUrl;
	}

	public String getNetworkIP() {
		return networkIP;
	}

	public void setNetworkIP(String networkIP) {
		this.networkIP = networkIP;
	}

	@JsonIgnore
	public ForeignCollection<ForwardedPortsBean> getOrmPortMappings() {
		return ormPortMappings;
	}

	/*public void setOrmPortMappings(ForeignCollection<ForwardedPortsBean> ormPortMappings) {
		this.ormPortMappings = ormPortMappings;
		this.portMappings = new ArrayList<ForwardedPortsBean>(ormPortMappings);
	}*/

	public void setPortMappings(ArrayList<ForwardedPortsBean> portMappings) {
		this.portMappings = portMappings;
		this.ormPortMappings.addAll(portMappings);
	}
	
	public ArrayList<ForwardedPortsBean> getPortMappings() throws SQLException {
		if(this.portMappings !=null){return this.portMappings;}
		else{
			ArrayList<ForwardedPortsBean> newList = new ArrayList<ForwardedPortsBean>();
			CloseableIterator<ForwardedPortsBean> iterator = this.ormPortMappings.closeableIterator();
			while(iterator.hasNext()){
				ForwardedPortsBean forwardedPortBean = iterator.next();
				forwardedPortBean.setMachineConfig(null);
				newList.add(forwardedPortBean);
			}
			iterator.close();
			return newList;
		}
	}

	@JsonIgnore
	public ForeignCollection<SyncFoldersBean> getOrmSyncFolders() {
		return ormSyncFolders;
	}

	/*public void setOrmSyncFolders(ForeignCollection<SyncFoldersBean> ormSyncFolders) {
		this.ormSyncFolders = ormSyncFolders;
		this.syncFolders = new ArrayList<SyncFoldersBean>(ormSyncFolders);
	}*/

	public ArrayList<SyncFoldersBean> getSyncFolders() throws SQLException {
		if(this.syncFolders !=null){return this.syncFolders;}
		else{
			ArrayList<SyncFoldersBean> newList = new ArrayList<SyncFoldersBean>();
			CloseableIterator<SyncFoldersBean> iterator = this.ormSyncFolders.closeableIterator();
			while(iterator.hasNext()){
				SyncFoldersBean syncFolderBean = iterator.next();
				syncFolderBean.setMachineConfig(null);
				syncFolderBean.setSyncFolderID(null);
				newList.add(syncFolderBean);
			}
			iterator.close();
			return newList;
		}
	}

	public void setSyncFolders(ArrayList<SyncFoldersBean> syncFolders) throws SQLException {
		this.syncFolders = syncFolders;
		MachineConfigDAOManager.getInstance().machineConfigDao.assignEmptyForeignCollection(this, "ormSyncFolders");
		this.ormSyncFolders.addAll(syncFolders);
		this.ormSyncFolders.updateAll();
	}
	
	@JsonIgnore
	public ForeignCollection<DockerLinkBean> getOrmDockerLinks() {
		return ormDockerLinks;
	}

	/*public void setOrmDockerLinks(ForeignCollection<DockerLinkBean> ormDockerLinks) {
		this.ormDockerLinks = ormDockerLinks;
		this.dockerLinks = new ArrayList<DockerLinkBean>(ormDockerLinks);
	}*/

	public ArrayList<DockerLinkBean> getDockerLinks() throws SQLException {
		if(this.dockerLinks !=null){return this.dockerLinks;}
		else{
			ArrayList<DockerLinkBean> newList = new ArrayList<DockerLinkBean>();
			CloseableIterator<DockerLinkBean> iterator = this.ormDockerLinks.closeableIterator();
			while(iterator.hasNext()){
				DockerLinkBean dockerLinkBean = iterator.next();
				dockerLinkBean.setMachineConfig(null);
				newList.add(dockerLinkBean);
			}
			iterator.close();
			return newList;
		}
	}

	public void setDockerLinks(ArrayList<DockerLinkBean> dockerLinks) {
		this.dockerLinks = dockerLinks;
		this.ormDockerLinks.addAll(dockerLinks);
	}

	public String getProvisionerName() {
		return provisionerName;
	}

	public void setProvisionerName(String provisionerName) {
		this.provisionerName = provisionerName;
	}

	public String getCpuExecCap() {
		return cpuExecCap;
	}

	public void setCpuExecCap(String cpuExecCap) {
		this.cpuExecCap = cpuExecCap;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getBootTimeout() {
		return bootTimeout;
	}

	public void setBootTimeout(String bootTimeout) {
		this.bootTimeout = bootTimeout;
	}

	public boolean getGuiMode() {
		return guiMode;
	}

	public void setGuiMode(boolean guiMode) {
		this.guiMode = guiMode;
	}
	
	
	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public boolean getPuppetMasterStatus() {
		return puppetMasterStatus;
	}

	public void setPuppetMasterStatus(boolean puppetMasterStatus) {
		this.puppetMasterStatus = puppetMasterStatus;
	}

	public boolean getShellMappedStatus() {
		return shellMappedStatus;
	}

	public void setShellMappedStatus(boolean shellMappedStatus) {
		this.shellMappedStatus = shellMappedStatus;
	}

	public boolean getPuppetMappedStatus() {
		return puppetMappedStatus;
	}

	public void setPuppetMappedStatus(boolean puppetMappedStatus) {
		this.puppetMappedStatus = puppetMappedStatus;
	}

	public boolean getChefMappedStatus() {
		return chefMappedStatus;
	}

	public void setChefMappedStatus(boolean chefMappedStatus) {
		this.chefMappedStatus = chefMappedStatus;
	}

	public String getDockerImage() {
		return dockerImage;
	}

	public void setDockerImage(String dockerImage) {
		this.dockerImage = dockerImage;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Integer getConfigChangeFlag() {
		return configChangeFlag;
	}

	public void setConfigChangeFlag(Integer configChangeFlag) {
		this.configChangeFlag = configChangeFlag;
	}

	public Integer getScriptChangeFlag() {
		return scriptChangeFlag;
	}

	public void setScriptChangeFlag(Integer scriptChangeFlag) {
		this.scriptChangeFlag = scriptChangeFlag;
	}

	public Integer getModuleChangeFlag() {
		return moduleChangeFlag;
	}

	public void setModuleChangeFlag(Integer moduleChangeFlag) {
		this.moduleChangeFlag = moduleChangeFlag;
	}

}