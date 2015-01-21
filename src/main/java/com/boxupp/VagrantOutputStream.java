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
package com.boxupp;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.boxupp.responseBeans.VagrantOutput;
import com.boxupp.responseBeans.VagrantStreamError;
import com.boxupp.responseBeans.VagrantStreamOutput;
import com.boxupp.ws.OutputConsole;

public class VagrantOutputStream implements OutputConsole{
	private static LinkedList<VagrantOutput> output = new LinkedList<VagrantOutput>();
	
	public void pushOutput(String data){
		VagrantStreamOutput vagrantStreamOutput = new VagrantStreamOutput();
		vagrantStreamOutput.setOutput(data);
		output.add(vagrantStreamOutput);
	}
	
	public void flushData(){
		output.clear();
	}
	
	public void pushError(String data){
		VagrantStreamError vagrantStreamError = new VagrantStreamError();
		vagrantStreamError.setOutput(data);
		output.add(vagrantStreamError);
	}
	
	public void pushDataTermination(){
		VagrantStreamOutput vagrantStreamOutput = new VagrantStreamOutput();
		vagrantStreamOutput.setOutput("Execution Completed");
		vagrantStreamOutput.setDataEnd(true);
		output.add(vagrantStreamOutput);
	}
		
	public static VagrantOutput pop(){
		try{
			return output.remove();
		}
		catch(NoSuchElementException e){
			VagrantStreamOutput vagrantEmptyOutput = new VagrantStreamOutput();
			vagrantEmptyOutput.setOutput("");
			vagrantEmptyOutput.setType("empty");
			return vagrantEmptyOutput;
		}
	}
}
