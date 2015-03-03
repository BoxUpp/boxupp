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
package com.boxupp.velocity;

import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

public class VelocityInit {
	
	public static VelocityEngine getVelocityInstance(){
		VelocityEngine ve = new VelocityEngine();
		setVelocityProperties(ve);
		return ve;
	}
	
	static void setVelocityProperties(VelocityEngine ve){
		Properties prop = new Properties();
		prop.setProperty("resource.loader", "class");
		prop.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
		prop.setProperty("class.resource.loader.class", 
					  "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		prop.setProperty("math", "org.apache.velocity.tools.generic.MathTool");
		ve.init(prop);
	}
	
	public static Template getTemplate(VelocityEngine ve, String provider){
		Template template = null;
		if(provider.equalsIgnoreCase("virtualBox") ){
			template = ve.getTemplate("virtualBox.vm");
		}else if(provider.equalsIgnoreCase("docker")){
			template = ve.getTemplate("docker.vm");
		}
		else if(provider.equalsIgnoreCase("AWS")){
			template = ve.getTemplate("awsMachine.vm");
		}
		return template;
	}
	
	public static Template getNodeTemplate(VelocityEngine ve){
		return ve.getTemplate("node.vm");
	}
}

