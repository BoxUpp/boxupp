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

public class ModuleCurrentReleaseBean {

	private String uri;
	private String version;
	private String file_uri;
	private Integer file_size;
	private ModuleMetaDataBean metadata;
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public ModuleMetaDataBean getMetadata() {
		return metadata;
	}
	public void setMetadata(ModuleMetaDataBean metadata) {
		this.metadata = metadata;
	}
	public String getFile_uri() {
		return file_uri;
	}
	public void setFile_uri(String file_uri) {
		this.file_uri = file_uri;
	}
	public Integer getFile_size() {
		return file_size;
	}
	public void setFile_size(Integer file_size) {
		this.file_size = file_size;
	}
}
