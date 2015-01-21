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
package com.boxupp.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;


public class MailManager {
	private static String apiKey = "key-";
	private static String webResourceURL = "https://api.mailgun.net/v2/support.boxupp.com/messages";
	
	public ClientResponse sendRegistrationMail(String emailID, String name, String password){
		Client client = Client.create();
	    client.addFilter(new HTTPBasicAuthFilter("api","key-"));
	    WebResource webResource = client.resource("https://api.mailgun.net/v2/support.boxupp.com/messages");
	    MultivaluedMapImpl formData = new MultivaluedMapImpl();
	    formData.add("from", "Boxupp Support <info@boxupp.com>");
	    formData.add("to", emailID);
	    formData.add("subject", "Welcome to Boxupp");
	    BufferedReader fileReader = null;

		InputStream iStream = getClass().getResourceAsStream("/data.html");
		InputStreamReader reader = new InputStreamReader(iStream);
		fileReader = new BufferedReader(reader);
		
	    StringBuffer buffer = new StringBuffer();
	    String data;
	    try {
			while((data = fileReader.readLine())!=null){
				buffer.append(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    data = buffer.toString();
	    data = data.replaceAll("<USERNAME>", emailID);
	    data = data.replaceAll("<PASSWORD>", password);
	    data = data.replaceAll("<NAME>", name);
	    formData.add("html", data);

	    return webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
	        post(ClientResponse.class, formData);
	}

}
