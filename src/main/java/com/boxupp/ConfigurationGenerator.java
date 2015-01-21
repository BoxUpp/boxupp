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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.boxupp.db.beans.MachineConfigurationBean;
import com.boxupp.db.beans.PuppetModuleBean;
import com.boxupp.db.beans.PuppetModuleMapping;
import com.boxupp.db.beans.ShellScriptBean;
import com.boxupp.db.beans.ShellScriptMapping;
import com.boxupp.velocity.VelocityInit;


public class ConfigurationGenerator {	
	
	private static Logger logger = LogManager.getLogger(ConfigurationGenerator.class.getName());
	private static boolean configurationGenerated;
	private static String velocityFinalTemplate = "";
		
	public static boolean isConfigurationGenerated() {
		return configurationGenerated;
	}

	public static void setConfigurationGenerated(boolean configurationGenerated) {
		ConfigurationGenerator.configurationGenerated = configurationGenerated;
	}

	public static String getVelocityFinalTemplate() {
		return velocityFinalTemplate;
	}

	public static void setVelocityFinalTemplate(String velocityFinalTemplate) {
		ConfigurationGenerator.velocityFinalTemplate = velocityFinalTemplate;
	}

	//Note: The output after template merger can also be redirected to a file on disk //
	 public static boolean generateConfig(List<MachineConfigurationBean> machineConfigList,
			List<PuppetModuleBean> puppetModules,
			List<ShellScriptBean> shellScripts,
			List<ShellScriptMapping> scriptMappings,
			List<PuppetModuleMapping> moduleMappings, String provider, String projectID){
		
		VelocityEngine ve = VelocityInit.getVelocityInstance();
		Template template = VelocityInit.getTemplate(ve, provider);
		VelocityContext context = new VelocityContext();
		context.put("boxuppConfig", machineConfigList);
		context.put("scripts", shellScripts );
		context.put("modules", puppetModules);
		context.put("moduleMappings", moduleMappings);
		context.put("scriptMappings", scriptMappings);
		context.put("nodeFile", projectID+".pp");
		
		try{
			StringWriter stringWriter = new StringWriter();
			template.merge(context, stringWriter);
			velocityFinalTemplate = stringWriter.toString();
			setConfigurationGenerated(true);
		}
		catch(ResourceNotFoundException e){
			logger.error("Boxupp Template not found");
			setConfigurationGenerated(false);
		} 
		catch(ParseErrorException e){
			logger.error("Error parsing Boxupp template");
			setConfigurationGenerated(false);
		}
		return configurationGenerated;
	}
	 public static boolean generateNodeConfig(HashMap<String, ArrayList<String>> nodeConfigList){
			VelocityEngine ve = VelocityInit.getVelocityInstance();
			Template template = VelocityInit.getNodeTemplate(ve);
			VelocityContext context = new VelocityContext();
			context.put("puppetModules", nodeConfigList);
			try{
				StringWriter stringWriter = new StringWriter();
				template.merge(context, stringWriter);
				velocityFinalTemplate = stringWriter.toString();
				setConfigurationGenerated(true);
			}
			catch(ResourceNotFoundException e){
				logger.error("Node Template not found");
				setConfigurationGenerated(false);
			} 
			catch(ParseErrorException e){
				logger.error("Error parsing Node template");
				setConfigurationGenerated(false);
			}
			return configurationGenerated;
		}
	
}
