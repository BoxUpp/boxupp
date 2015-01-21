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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "sync_folder")
public class SyncFoldersBean {
	public static final String MACHINE_ID_FIELD_NAME = "machineID";
	
	@DatabaseField(canBeNull = false, generatedId= true, useGetSet = true)
	private Integer syncFolderID;
	
	@DatabaseField(foreign = true,foreignAutoRefresh=true, maxForeignAutoRefreshLevel=1, columnName = MACHINE_ID_FIELD_NAME)
	MachineConfigurationBean machineConfig;
	
	@DatabaseField(useGetSet = true)
	private String hostFolder;
	
	@DatabaseField(useGetSet = true)
	private String vmFolder;
	
	public Integer getSyncFolderID() {
		return syncFolderID;
	}
	public void setSyncFolderID(Integer syncFolderID) {
		this.syncFolderID = syncFolderID;
	}
	public String getHostFolder() {
		return hostFolder;
	}
	public void setHostFolder(String hostFolder) {
		this.hostFolder = hostFolder;
	}
	public String getVmFolder() {
		return vmFolder;
	}
	public void setVmFolder(String vmFolder) {
		this.vmFolder = vmFolder;
	}
	public MachineConfigurationBean getMachineConfig() {
		return machineConfig;
	}
	public void setMachineConfig(MachineConfigurationBean machineConfig) {
		this.machineConfig = machineConfig;
	}
	
}
