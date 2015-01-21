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

import java.sql.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "puppetModule")
public class PuppetModuleBean {
	public static final String ID_FIELD_NAME = "puppetID";

	@DatabaseField(canBeNull = false, generatedId = true, useGetSet = true, columnName=ID_FIELD_NAME)
	private Integer puppetID;
	
	@DatabaseField(useGetSet = true)
	private String comman_Uri;
	
	@DatabaseField(useGetSet = true)
	private Integer downloads;
	
	@DatabaseField(useGetSet = true)
	private String created_at;
	
	@DatabaseField(useGetSet = true)
	private String updated_at;
	
	@DatabaseField(useGetSet = true)
	private Boolean supported;
	
	@DatabaseField(useGetSet = true)
	private String metaData_name;
	
	@DatabaseField(useGetSet = true)
	private String version;
	
	@DatabaseField(useGetSet = true, dataType=DataType.LONG_STRING)
	private String summary;
	
	@DatabaseField(useGetSet = true)
	private String license;
	
	@DatabaseField(useGetSet = true)
	private String author;
	
	@DatabaseField(useGetSet = true)
	private String tags;
	
	@DatabaseField(useGetSet = true)
	private String currentRelease_uri;
	
	@DatabaseField(useGetSet = true)
	private String currentRelease_version;
	
	@DatabaseField(useGetSet = true)
	private String file_uri;
	
	@DatabaseField(useGetSet = true)
	private Integer currentRelease_downloads;
	
	@DatabaseField(useGetSet = true)
	private Integer file_size;
	
	@DatabaseField(useGetSet = true)
	private String owner_uri;
	
	@DatabaseField(useGetSet = true)
	private String owner_username;
	
	@DatabaseField(useGetSet = true)
	private String gravatar_id;
	
	@DatabaseField(useGetSet = true)
	private String moduleName;
	
	@DatabaseField(useGetSet = true)
	private Boolean isDisabled;
	
	@DatabaseField(useGetSet = true)
	private Integer creatorUserID;
	
	@DatabaseField(useGetSet = true, format="yyyy-MM-dd HH:mm:ss")
	private Date creationTime;
	
	@DatabaseField(useGetSet = true, dataType =DataType.LONG_STRING)
	private String description;
	
	public Integer getPuppetID() {
		return puppetID;
	}

	public void setPuppetID(Integer puppetID) {
		this.puppetID = puppetID;
	}

	public String getComman_Uri() {
		return comman_Uri;
	}

	public void setComman_Uri(String comman_Uri) {
		this.comman_Uri = comman_Uri;
	}

	public Integer getDownloads() {
		return downloads;
	}

	public void setDownloads(Integer downloads) {
		this.downloads = downloads;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public Boolean getSupported() {
		return supported;
	}

	public void setSupported(Boolean supported) {
		this.supported = supported;
	}

	public String getMetaData_name() {
		return metaData_name;
	}

	public void setMetaData_name(String metaData_name) {
		this.metaData_name = metaData_name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getCurrentRelease_uri() {
		return currentRelease_uri;
	}

	public void setCurrentRelease_uri(String currentRelease_uri) {
		this.currentRelease_uri = currentRelease_uri;
	}

	public String getCurrentRelease_version() {
		return currentRelease_version;
	}

	public void setCurrentRelease_version(String currentRelease_version) {
		this.currentRelease_version = currentRelease_version;
	}

	public String getFile_uri() {
		return file_uri;
	}

	public void setFile_uri(String file_uri) {
		this.file_uri = file_uri;
	}

	public Integer getCurrentRelease_downloads() {
		return currentRelease_downloads;
	}

	public void setCurrentRelease_downloads(Integer currentRelease_downloads) {
		this.currentRelease_downloads = currentRelease_downloads;
	}

	public Integer getFile_size() {
		return file_size;
	}

	public void setFile_size(Integer file_size) {
		this.file_size = file_size;
	}

	public String getOwner_uri() {
		return owner_uri;
	}

	public void setOwner_uri(String owner_uri) {
		this.owner_uri = owner_uri;
	}

	public String getOwner_username() {
		return owner_username;
	}

	public void setOwner_username(String owner_username) {
		this.owner_username = owner_username;
	}

	public String getGravatar_id() {
		return gravatar_id;
	}

	public void setGravatar_id(String gravatar_id) {
		this.gravatar_id = gravatar_id;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Integer getCreatorUserID() {
		return creatorUserID;
	}

	public void setCreatorUserID(Integer creatorUserID) {
		this.creatorUserID = creatorUserID;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	
}