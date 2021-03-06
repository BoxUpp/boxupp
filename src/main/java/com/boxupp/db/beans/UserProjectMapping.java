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

@DatabaseTable(tableName= "UserProjectMapping")
public class UserProjectMapping {
	public UserProjectMapping() {
		
	}

	public final static String USER_ID_FIELD_NAME = "userID";
	public final static String PROJECT_ID_FIELD_NAME = "projectID";

	@DatabaseField(canBeNull = false, generatedId = true, useGetSet = true)
	private Integer ID;

	@DatabaseField(foreign = true, columnName = USER_ID_FIELD_NAME, foreignAutoRefresh=true, foreignAutoCreate=true)
	UserDetailBean user;

	@DatabaseField(foreign = true, columnName = PROJECT_ID_FIELD_NAME, foreignAutoRefresh=true, foreignAutoCreate=true)
	ProjectBean project;

	public Integer getID() {
		return ID;
	}

	public void setID(Integer ID) {
		this.ID = ID;
	}

	public ProjectBean getProject() {
		return project;
	}

	public void setProject(ProjectBean project) {
		this.project = project;
	}

	public UserDetailBean getUser() {
		return user;
	}

	public void setUser(UserDetailBean user) {
		this.user = user;
	}

	public UserProjectMapping(UserDetailBean user, ProjectBean project) {
		super();
		this.user = user;
		this.project = project;
	}
	
}
