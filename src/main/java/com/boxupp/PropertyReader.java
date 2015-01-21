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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyReader {
	private static Logger logger = LogManager.getLogger(PropertyReader.class.getName());
	private static Properties propertyFile = null;
	
	public static Properties getInstance(){
		if(propertyFile == null){
			try{
				new PropertyReader();
			}
			catch(Exception e){
				logger.error("Error reading config.properties file");
			}
		}
		return propertyFile;
	}
	public PropertyReader() throws IOException{
		propertyFile = new Properties();
		InputStream propertiesStream = getClass().getResourceAsStream("/config.properties");
		propertyFile.load(propertiesStream);
	}
	public static void main(String args[]) throws IOException{
		PropertyReader.getInstance();
		
	}
}


