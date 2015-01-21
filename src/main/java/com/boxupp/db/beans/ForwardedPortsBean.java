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

@DatabaseTable(tableName = "forwarded_port")
public class ForwardedPortsBean {
	
	public static final String MACHINE_ID_FIELD_NAME = "machineID";

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = MACHINE_ID_FIELD_NAME)
	private MachineConfigurationBean machineConfig;
	
	@DatabaseField(useGetSet = true)
	private String hostPort;
	
	@DatabaseField(useGetSet = true)
	private String vmPort;
	
	public String getHostPort() {
		return hostPort;
	}
	public void setHostPort(String hostPort) {
		this.hostPort = hostPort;
	}
	public String getVmPort() {
		return vmPort;
	}
	public void setVmPort(String vmPort) {
		this.vmPort = vmPort;
	}
	public MachineConfigurationBean getMachineConfig() {
		return machineConfig;
	}
	public void setMachineConfig(MachineConfigurationBean machineConfig) {
		this.machineConfig = machineConfig;
	}
	
}
