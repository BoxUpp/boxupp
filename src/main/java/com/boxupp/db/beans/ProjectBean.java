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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "project")
public class ProjectBean  {
	
	public static final String ID_FIELD_NAME = "projectID";

	@DatabaseField(canBeNull = false, generatedId=true, useGetSet = true, columnName=ID_FIELD_NAME)
	private Integer projectID;
	
	@DatabaseField(useGetSet = true)
	private String name;
	
	@DatabaseField(useGetSet = true)
	private String owner;
	
	@DatabaseField(useGetSet = true, format="yyyy-MM-dd HH:mm:ss")
	private Date creationTime;
	
	@DatabaseField(useGetSet = true)
	private String description;
	
	@DatabaseField(useGetSet = true)
	private Integer providerType;
	
	@DatabaseField(useGetSet = true)
	private boolean isDisabled;

	
	public Integer getProjectID() {
		return projectID;
	}
	public void setProjectID(Integer projectID) {
		this.projectID = projectID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
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
	public Integer getProviderType() {
		return providerType;
	}
	public void setProviderType(Integer providerType) {
		this.providerType = providerType;
	}
	public boolean getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}
	
}
