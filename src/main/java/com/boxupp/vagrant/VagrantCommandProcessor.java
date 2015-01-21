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

import java.io.IOException;

import com.boxupp.responseBeans.VagrantStatus;
import com.boxupp.ws.OutputConsole;

public class VagrantCommandProcessor {
	private static VagrantCommandExecutor shellExec = new VagrantCommandExecutor();
	private static VagrantCommandParser shellParser = new VagrantCommandParser();
	
	public VagrantStatus checkVagrantStatus(String location){
		shellExec.setCMDExecDir(location);
		StringBuffer cmdOutput;
		cmdOutput = shellExec.checkVagrantStatusCMD("vagrant","status");
		return shellParser.parseVagrantStatusCMD(cmdOutput);
	}
	
	public VagrantStatus checkMachineStatus(String location, String vagrantID){
		shellExec.setCMDExecDir(location);
		StringBuffer cmdOutput;
		cmdOutput = shellExec.checkVagrantStatusCMD("vagrant","status",vagrantID);
		return shellParser.parseVagrantStatusCMD(cmdOutput);
	}
	
	public String executeVagrantFile(String location, String command, Integer userID, OutputConsole consoleType) throws IOException, InterruptedException{
		
//		VagrantOutputStream.flushData();
		command = filterCommand(command);
		shellExec.setCMDExecDir(location);
		shellExec.bootVagrantMachine(consoleType, userID, command.split(" "));
		return "";
		
	}
	
	public String filterCommand(String command){
		//vagrant destroy
		StringBuilder stringConcat = new StringBuilder();
		int indexOfDestroy = command.indexOf("destroy");
		int indexOfForce = command.indexOf("--force");
		if((indexOfDestroy > -1) && (indexOfForce == -1)){
			stringConcat.append(command.substring(0, indexOfDestroy + 7));
			stringConcat.append(" --force");
			stringConcat.append(command.substring(indexOfDestroy + 7, command.length()));
			command = stringConcat.toString();
		}
		return command;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{
		VagrantCommandProcessor proc = new VagrantCommandProcessor();
		System.out.println(proc.filterCommand("vagrant destroy mysql")+"'");
	}
}
