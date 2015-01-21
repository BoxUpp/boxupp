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

public class CommonProperties {
	private static CommonProperties commonProperties = null;
	private String dockerProvider = "Docker";
	private String puppetForgeDownloadAPIPath = "https://forgeapi.puppetlabs.com:443";
	
	public static CommonProperties getInstance(){
		if(commonProperties == null){
			commonProperties = new CommonProperties();
		}
		return commonProperties;
	}
	
	public String getPuppetForgeDownloadAPIPath() {
		return puppetForgeDownloadAPIPath;
	}

	public void setPuppetForgeDownloadAPIPath(String puppetForgeDownloadAPIPath) {
		this.puppetForgeDownloadAPIPath = puppetForgeDownloadAPIPath;
	}

	public String getDockerProvider() {
		return dockerProvider;
	}

	public void setDockerProvider(String dockerProvider) {
		this.dockerProvider = dockerProvider;
	}
	
	
}
