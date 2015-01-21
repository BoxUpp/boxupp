package com.boxupp.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.boxupp.beans.LogBean;


public class VagrantUtilities {
	private static Logger logger = LogManager.getLogger(VagrantUtilities.class.getName());

	private static VagrantUtilities vagrantUtilities = null;

	public VagrantUtilities(){
		
	}
	
	public static VagrantUtilities getInstance(){
		if(vagrantUtilities == null){
			try{
				vagrantUtilities = new VagrantUtilities();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return vagrantUtilities;
	}
	
	public List<LogBean> getVagrantLogs(HttpServletRequest request){
		Integer userID = Integer.parseInt(request.getParameter("userID") != null?request.getParameter("userID"):null);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		List<LogBean> logFileList = new ArrayList<LogBean>();
		try {
			Date fromDate = convertStringInDate(dateFormat.format(dateFormat.parse(request.getParameter("fromDate"))));
			Date toDate = convertStringInDate(dateFormat.format(dateFormat.parse(request.getParameter("toDate"))));
			String logdir= Utilities.getInstance().fetchActiveProjectDirectory(userID)+OSProperties.getInstance().getOSFileSeparator()+OSProperties.getInstance().getLogDirName();
			File[] files = new File(logdir).listFiles();
			if (files == null){ return logFileList;}
			for ( File file : files) {
				
				if (file.isFile()) {
					LogBean logBean = new LogBean();
					String fileName = file.getName();
					Date fileDate=convertStringInDate(fileName.split("_")[2]);
					if((fileDate.after(fromDate)||fileDate.equals(fromDate))&&(fileDate.before(toDate)||fileDate.equals(toDate))){
						logBean.setFilename(fileName);
						logBean.setVagrantID(fileName.split("_")[1] != null?fileName.split("_")[1]:null);
						logBean.setUserID(Integer.parseInt(fileName.split("_")[0] != null?fileName.split("_")[0]:null));
						logBean.setStatus(fileName.split("_")[fileName.split("_").length-1].split("\\.")[0]);
						logBean.setTime(fileName.split("_")[2]);
						logFileList.add(logBean);
					}
				}
				}
				
			
		} catch (ParseException e) {
			logger.error("Error in fetching vagrant log files: "+e.getMessage());
		}
		return logFileList;
	}
	
	public String getVagrantLogContent(HttpServletRequest request){
		Integer userID = Integer.parseInt(request.getParameter("userID") != null?request.getParameter("userID"):null);
		String fileName = request.getParameter("fileName") != null? request.getParameter("fileName"):null;
		
		String logFilePath= Utilities.getInstance().fetchActiveProjectDirectory(userID)+OSProperties.getInstance().getOSFileSeparator()+OSProperties.getInstance().getLogDirName()+OSProperties.getInstance().getOSFileSeparator()+fileName;
		StringBuilder contents = new StringBuilder();
		   try {
	            BufferedReader input =  new BufferedReader(new FileReader(logFilePath));
	            try {
	                String line = null;
	                while (( line = input.readLine()) != null) {
	                    contents.append(line);
	                    contents.append(System.getProperty("line.separator"));
	                }
	            }
	            finally {
	                input.close();
	            }
	        }
	        catch (IOException ex){
	        	logger.error("Error in fetching content of vagrant log file: "+ex.getMessage());
	        }
		return contents.toString();
	
	}
	
	public Date convertStringInDate(String dateInString){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			 date = formatter.parse(dateInString);
		} catch (ParseException e) {
			logger.error("Error in parsing date : "+e.getMessage());
		}
		return date;
    }
}
