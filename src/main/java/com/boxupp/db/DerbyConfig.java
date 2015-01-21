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
package com.boxupp.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.boxupp.utilities.OSProperties;

public class DerbyConfig {
	 
	public static final String JDBC_URL = "jdbc:derby:"+ getDatabaseLocation() +";create=true";
	
	private static Logger logger = LogManager.getLogger(DerbyConfig.class.getName());
	
	public static String getDatabaseLocation(){
		return OSProperties.getInstance().getUserHomeDirectory() + 
						   OSProperties.getInstance().getOSFileSeparator() + 
						   OSProperties.getInstance().getPrimaryFolderName() +
						   OSProperties.getInstance().getOSFileSeparator() + 
						   OSProperties.getInstance().getDatabaseFolderName();
	}
}


