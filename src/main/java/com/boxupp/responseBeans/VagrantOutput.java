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
package com.boxupp.responseBeans;

public class VagrantOutput {
	
	private String output;
	private boolean dataEnd = false;
	private boolean vagrantFileExists = true;
	
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public boolean isDataEnd() {
		return dataEnd;
	}
	public void setDataEnd(boolean dataEnd) {
		this.dataEnd = dataEnd;
	}
	public boolean isVagrantFileExists() {
		return vagrantFileExists;
	}
	public void setVagrantFileExists(boolean vagrantFileExists) {
		this.vagrantFileExists = vagrantFileExists;
	}


}
