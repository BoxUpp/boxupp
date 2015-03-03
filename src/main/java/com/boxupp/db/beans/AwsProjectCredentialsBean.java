package com.boxupp.db.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="awsProjectCredentials")
public class AwsProjectCredentialsBean {

	public static final String ID_FIELD_NAME = "awsCredentialsID";

	@DatabaseField(canBeNull=false,generatedId=true,useGetSet=true,columnName=ID_FIELD_NAME)
	private Integer awsCredentialsID;
	
	@DatabaseField(canBeNull=false,useGetSet=true)
	private String awsAccessKeyId;
	
	@DatabaseField(canBeNull=false,useGetSet=true)
	private String awsSecretAccessKey;
	
	@DatabaseField(canBeNull=false,useGetSet=true)
	private String awsKeyPair;
	
	@DatabaseField(canBeNull=false,useGetSet=true)
	private String privateKeyPath;

	public Integer getAwsCredentialsID(){
		return awsCredentialsID;
	}
	
	public void setAwsCredentialsID(Integer awsCredentialsID){
		this.awsCredentialsID=awsCredentialsID;
	}
	
	public String getAwsAccessKeyId(){
		return awsAccessKeyId;
	}
	
	public void setAwsAccessKeyId(String awsAccessKeyId){
		this.awsAccessKeyId=awsAccessKeyId;
	}
	
	public String getAwsSecretAccessKey(){
		return awsSecretAccessKey;
	}
	
	public void setAwsSecretAccessKey(String awsSecretAccessKey){
		this.awsSecretAccessKey=awsSecretAccessKey;
	}
	
	public String getAwsKeyPair(){
		return awsKeyPair;
	}
	
	public void setAwsKeyPair(String awsKeyPair){
		this.awsKeyPair=awsKeyPair;
	}
	
	public String getPrivateKeyPath(){
		return privateKeyPath;
	}
	
	public void setPrivateKeyPath(String privateKeyPath){
		this.privateKeyPath=privateKeyPath;
	}
	
}
