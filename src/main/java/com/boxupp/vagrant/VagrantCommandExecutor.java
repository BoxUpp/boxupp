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
package com.boxupp.vagrant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.boxupp.utilities.OSProperties;
import com.boxupp.utilities.Utilities;
import com.boxupp.ws.OutputConsole;


public class VagrantCommandExecutor {
	
	private static Logger logger = LogManager.getLogger(VagrantCommandExecutor.class.getName());
	
	public static String WIN_CMD_PROCESSOR = "cmd";
	public static String OPTION_REG_VALUENAME = " /v";
	public static String OPTION_RUN_AND_TERMINATE = " /C";
	public static String REG_QUERY_CMD = "reg query ";
	public static String VBOX_MANAGE_CMD = "VBoxManage";
	public static String VBOX_LIST_CMD = " list";
	public static String VBOX_LIST_VMS_CMD = " vms";
	public static String VBOX_LIST_RUNNINGVMS_CMD = " runningvms";
	public static String VBOX_LIST_HOSTINFO_CMD = " hostinfo";
	public static String VBOX_VM_INFO_CMD = " showvminfo";
	public static String REG_SOFTWARE_PATH_CMD = "HKLM\\Software";
	public String cmdExecDir;
	
	
	public String getCMDExecDir(){
		return cmdExecDir;
	}
	
	public void setCMDExecDir(String dir){
		cmdExecDir = dir;
	}
	
	public void bootVagrantMachine(OutputConsole console, Integer userID, String... commands ) throws IOException, InterruptedException{
		
		StringBuffer vagrantCommand = new StringBuffer();
		File vagrantFile = new File(Utilities.getInstance().fetchActiveProjectDirectory(userID) 
						   +OSProperties.getInstance().getOSFileSeparator()+
						   OSProperties.getInstance().getVagrantFileName());
		for(int counter=0; counter<commands.length; counter++){
			if(counter == commands.length-1){
				vagrantCommand.append(commands[counter]);
			}
			else{
				vagrantCommand.append(commands[counter]).append(" ");
			}
		}
		if(vagrantFile.exists()){
			ProcessBuilder processBuilder;
			String osType = OSProperties.getInstance().getOSName();
			
			String windowsCmd = "cmd /C" + vagrantCommand.toString();
			if(osType.indexOf("windows") != -1){
				String commandArray[] = windowsCmd.split(" ");
				processBuilder = new ProcessBuilder(commandArray);
			}else if((osType.indexOf("mac")!= -1) || (osType.indexOf("darwin")!= -1)){
				String command = vagrantCommand.toString().replaceAll("vagrant", "/Applications/Vagrant/bin/vagrant");
				processBuilder = new ProcessBuilder(command.split(" "));
			}else{
				String command = vagrantCommand.toString().replaceAll("vagrant", "/opt/vagrant/bin/vagrant");
				processBuilder = new ProcessBuilder(command.split(" "));
			}
			
			processBuilder.directory(new File(cmdExecDir));
			Process proc = processBuilder.start();
			InputStream is = proc.getInputStream();
			InputStream es = proc.getErrorStream();
			BufferedReader errReader = new BufferedReader(new InputStreamReader(es));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String data;
			console.pushOutput(cmdExecDir + ">" + vagrantCommand.toString());
			Integer commandLength = vagrantCommand.toString().split(" ").length;
			String vagrantID = vagrantCommand.toString().split(" ")[commandLength -1].equalsIgnoreCase("-f")? vagrantCommand.toString().split(" ")[commandLength -2]:vagrantCommand.toString().split(" ")[commandLength -1];
			File logdir = new File(Utilities.getInstance().fetchActiveProjectDirectory(userID)+
					OSProperties.getInstance().getOSFileSeparator()+
					OSProperties.getInstance().getLogDirName());
			if(!logdir.exists()){
				logdir.mkdir();
			}
			if(!reader.equals(null)){
				String logFileName = logdir+OSProperties.getInstance().getOSFileSeparator()+userID+"_"+vagrantID+"_"+getCurrentTime()+"_"+"success.log";
				File file = new File(logFileName);
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				while((data = reader.readLine())!=null){
					bw.write(data);
					console.pushOutput(data);
				}
				bw.close();
			}
			int exitCode = proc.waitFor();
			
			if(errReader.readLine() != null){
				String logFileName = logdir+OSProperties.getInstance().getOSFileSeparator()+userID+"_"+vagrantID+"_"+getCurrentTime()+"_"+"error.log";
				File file = new File(logFileName);
				
				
				if (!file.exists()) {
					file.createNewFile();
				}
				
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
//			if(exitCode != 0){
				while((data = errReader.readLine())!=null){
					bw.write(data);
					console.pushError(data);
				}
				bw.close();
			}
//			}
			console.pushDataTermination();
		}else{
			console.pushOutput("VagrantFile not found on your machine.. !!");
			console.pushDataTermination();
		}
	}
	
	public StringBuffer checkVagrantStatusCMD(String... commands){
		
		StringBuffer vagrantCommand = new StringBuffer();
		StringBuffer commandOutput = new StringBuffer();
		try{

			ProcessBuilder processBuilder;
			String osType = OSProperties.getInstance().getOSName();
			
			if(osType.indexOf("windows") != -1){
				for(int counter=0; counter<commands.length; counter++){
					if(counter == commands.length-1){
						vagrantCommand.append(commands[counter]);
					}else{
						vagrantCommand.append(commands[counter]).append(" ");
					}
				}
				vagrantCommand.insert(0, "/C ");
				vagrantCommand.insert(0, "cmd ");
				String commandArray[] = vagrantCommand.toString().split(" ");
				processBuilder = new ProcessBuilder(commandArray);
			}else if((osType.indexOf("mac")!= -1) || (osType.indexOf("darwin")!= -1)){
				for(int counter=0; counter<commands.length; counter++){
					if(counter == commands.length-1){
						vagrantCommand.append(commands[counter]);
					}
					else{
						vagrantCommand.append(commands[counter]).append(" ");
					}
				}
				String command = vagrantCommand.toString().replaceAll("vagrant", "/Applications/Vagrant/bin/vagrant");
				processBuilder = new ProcessBuilder(command.split(" "));
				
			}
			else{
				
				for(int counter=0; counter<commands.length; counter++){
					if(counter == commands.length-1){
						vagrantCommand.append(commands[counter]);
					}
					else{
						vagrantCommand.append(commands[counter]).append(" ");
					}
				}
				String command = vagrantCommand.toString().replaceAll("vagrant", "/opt/vagrant/bin/vagrant");
				processBuilder = new ProcessBuilder(command.split(" "));
			}
			processBuilder.directory(new File(cmdExecDir));
			Process proc = processBuilder.start();
			InputStream is = proc.getInputStream();
			InputStream es = proc.getErrorStream();
			BufferedReader errReader = new BufferedReader(new InputStreamReader(es));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String data;
			while((data = reader.readLine())!=null){
				commandOutput.append(data);
			}
			int exitCode = proc.waitFor();
			if(exitCode != 0){
				while((data = errReader.readLine())!=null){
					commandOutput.append(data);
				}
			}
		}catch(IOException e){
			logger.error("Error reading command output : "+e.getMessage());
		}
		catch(InterruptedException e){
			logger.error("Interruption occured while waiting for command execution process : "+e.getMessage());
		}
		return commandOutput;
	}

	
	public  String getCurrentTime()
    {
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
    }
	public static void main(String args[]) throws IOException, InterruptedException{
		VagrantCommandExecutor executor = new VagrantCommandExecutor();
		String[] array = {"vagrant","status"};
		executor.setCMDExecDir(OSProperties.getInstance().getUserHomeDirectory());
		executor.checkVagrantStatusCMD(array);
		System.out.println("completed");
	}
}
	
	
