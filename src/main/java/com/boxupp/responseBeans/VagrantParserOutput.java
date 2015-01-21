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

public class VagrantParserOutput {
	
	private int statusCode = 1;
	private boolean validVagrantFile = false;
	private String parsedData;
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public boolean isValidVagrantFile() {
		return validVagrantFile;
	}
	public void setValidVagrantFile(boolean validVagrantFile) {
		this.validVagrantFile = validVagrantFile;
	}
	public String getParsedData() {
		return parsedData;
	}
	public void setParsedData(String parsedData) {
		this.parsedData = parsedData;
	}
	
	

}
