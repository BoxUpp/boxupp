package com.boxupp.db.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="projectAwsCredentialsMapping")
public class ProjectAwsCredentialsMapping {

	public final static String CREDENTIALS_ID_FIELD_NAME = "awsCredentialsID";
	public final static String PROJECT_ID_FIELD_NAME = "projectID";

	public ProjectAwsCredentialsMapping(ProjectBean projectId,AwsProjectCredentialsBean awsCredentialsId){
		super();
		this.projectID = projectId;
		this.awsCredentialsID=awsCredentialsId;
	}
	public ProjectAwsCredentialsMapping(){
		
	}
	
	@DatabaseField(canBeNull=false,useGetSet=true,generatedId=true)
	private Integer ID;
	
	@DatabaseField(foreign = true, useGetSet = true, columnName =PROJECT_ID_FIELD_NAME, foreignAutoRefresh=true, foreignAutoCreate=true)
	private ProjectBean projectID;
	
	@DatabaseField(foreign = true, useGetSet = true, columnName = CREDENTIALS_ID_FIELD_NAME, foreignAutoRefresh=true, foreignAutoCreate=true)
	private AwsProjectCredentialsBean awsCredentialsID;
	
	public Integer getID(){
		return ID;
	}
	
	public void setID(Integer ID){
		this.ID=ID;
	}
	
	public ProjectBean getProjectID(){
		return projectID;
	}
	
	public void setProjectID(ProjectBean projectId){
		this.projectID=projectId;
	}
	
	public AwsProjectCredentialsBean getAwsCredentialsID(){
		return awsCredentialsID;
	}
	
	public void setAwsCredentialsID(AwsProjectCredentialsBean awsCredentialsId){
		this.awsCredentialsID=awsCredentialsId;
	}
}
