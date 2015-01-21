package com.boxupp.beans;

import java.sql.Time;
import java.util.Date;

public class LogBean {
	
	private Integer userID;
	private Integer projectID;
	private String vagrantID;
	private String status;
	private String time;
	private String filename;
	
	public Integer getUserID() {
		return userID;
	}
	public void setUserID(Integer userID) {
		this.userID = userID;
	}
	public Integer getProjectID() {
		return projectID;
	}
	public void setProjectID(Integer projectID) {
		this.projectID = projectID;
	}
	public String getVagrantID() {
		return vagrantID;
	}
	public void setVagrantID(String vagrantID) {
		this.vagrantID = vagrantID;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	

}
