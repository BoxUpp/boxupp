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
package com.boxupp.vagrant;

import com.boxupp.responseBeans.VagrantStatus;

public class VagrantCommandParser {
	
	public VagrantStatus parseVagrantStatusCMD(StringBuffer cmdOutput){
		VagrantStatus statusBean = new VagrantStatus();
		String output = cmdOutput.toString();
		String vagrantFileNotPresent = "vagrant init";
		String vagrantEnvUninitialized = "not created";
		String vagrantEnvRunning = "running";
		String vagrantEnvOff = "poweroff";
		/* Case for 'vagrant init' will probably not occur ever */
		if(output.indexOf(vagrantFileNotPresent) != -1){
			statusBean.setStatusCode(3);
			statusBean.setVagrantStatus("Vagrant file not present");
		} 
		else if(output.indexOf(vagrantEnvUninitialized) != -1){
			statusBean.setStatusCode(3);
			statusBean.setVagrantStatus("Vagrant file present but uninitialized");
		}
		else if(output.indexOf(vagrantEnvRunning) != -1){
			statusBean.setStatusCode(1);
			statusBean.setVagrantStatus("Vagrant environment running");
		}
		else if(output.indexOf(vagrantEnvOff) != -1){
			statusBean.setStatusCode(0);
			statusBean.setVagrantStatus("Vagrant environment not running");
		}
		return statusBean;
	}
}
