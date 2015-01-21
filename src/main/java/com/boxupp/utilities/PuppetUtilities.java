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
package com.boxupp.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.boxupp.ConfigurationGenerator;
import com.boxupp.FileManager;
import com.boxupp.dao.PuppetModuleDAOManager;
import com.boxupp.db.beans.PuppetModuleMapping;
import com.boxupp.db.beans.SearchModuleBean;
import com.boxupp.responseBeans.StatusBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class PuppetUtilities extends Utilities {
	
	private static Logger logger = LogManager.getLogger(PuppetUtilities.class.getName());
	private static PuppetUtilities puppetUtilities = null;
	private static String OSFileSeparator;
	
	private PuppetUtilities(){
		OSFileSeparator = osProperties.getOSFileSeparator();
	}
	
	public static PuppetUtilities getInstance(){
		if(puppetUtilities == null){
			try{
				puppetUtilities = new PuppetUtilities();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return puppetUtilities;
	}
	
	public void deletePuppetModule(String puppetModuleName){
		String modulesDir =  constructModuleDirectory() +OSFileSeparator;
		File file  = new File(modulesDir+puppetModuleName);
		Utilities.getInstance().deleteFile(file);
		
	}
	public String constructModuleDirectory(){
		return  osProperties.getUserHomeDirectory() + 
				osProperties.getOSFileSeparator() +
				"Boxupp" + osProperties.getOSFileSeparator()+osProperties.getModuleDirName();
	}
	
	public String constructManifestsDirectory(){
		return  osProperties.getUserHomeDirectory() + 
				osProperties.getOSFileSeparator() +
				"Boxupp" + osProperties.getOSFileSeparator()+osProperties.getManifestsDirName();
	}
	
	public StatusBean downloadModule(JsonNode moduleData){
		Gson searchModuleData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
		SearchModuleBean searchModuleBean = searchModuleData.fromJson(moduleData.toString(), SearchModuleBean.class);
		String fileURL = CommonProperties.getInstance().getPuppetForgeDownloadAPIPath()+searchModuleBean.getCurrent_release().getFile_uri();
		StatusBean statusBean = new StatusBean();
		URL url = null;
		int responseCode = 0;
		HttpURLConnection httpConn = null;
		String fileSeparator = OSProperties.getInstance().getOSFileSeparator();
		String moduleDirPath = constructModuleDirectory()+fileSeparator;
		checkIfDirExists(new File(constructManifestsDirectory()));
		checkIfDirExists(new File(moduleDirPath));
		try {
			url = new URL(fileURL);
			httpConn = (HttpURLConnection) url.openConnection();
			responseCode = httpConn.getResponseCode();

			// always check HTTP response code first
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String fileName = "";
				String disposition = httpConn.getHeaderField("Content-Disposition");
				String contentType = httpConn.getContentType();
				int contentLength = httpConn.getContentLength();

				if (disposition != null) {
					int index = disposition.indexOf("filename=");
					if (index > 0) {
						fileName = disposition.substring(index + 9,
								disposition.length());
					}
				} else {
					fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
				}
				InputStream inputStream = httpConn.getInputStream();
				String saveFilePath = moduleDirPath + fileName;
				FileOutputStream outputStream = new FileOutputStream(saveFilePath);

				int bytesRead = -1;
				byte[] buffer = new byte[4096];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.close();
				inputStream.close();
				extrectFile(saveFilePath, moduleDirPath, searchModuleBean.getModuleName());
				File file = new File(saveFilePath);
				file.delete();
			
			} else {
				logger.error("No file to download. Server replied HTTP code: "+ responseCode);
			}
			httpConn.disconnect();
			statusBean.setStatusCode(0);
			statusBean.setStatusMessage(" Module Downloaded successfully ");
		} catch (IOException e) {
			logger.error("Error in loading module :" +e.getMessage());
			statusBean.setStatusCode(1);
			statusBean.setStatusMessage("Error in loading module :" +e.getMessage());
		}
		statusBean =PuppetModuleDAOManager.getInstance().create(moduleData);
		return statusBean;
	}
	
	private void extrectFile(String saveFilePath, String moduleDirPath, String moduleName) {
		try {
			File file = new File(saveFilePath);
			FileInputStream fin = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fin);
			GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
			TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn);
			TarArchiveEntry entry = null;
			int length =0;
			File unzipFile = null;
			while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					if(length == 0){
						File f = new File(moduleDirPath + entry.getName());
						f.mkdirs();
						 unzipFile = f;
						 length++;
					}else{
						File f = new File(moduleDirPath + entry.getName());
						f.mkdirs();
					}
				} else {
					int count;
					byte data[] = new byte[4096];
					FileOutputStream fos;
					fos = new FileOutputStream(moduleDirPath + entry.getName());
					BufferedOutputStream dest = new BufferedOutputStream(fos, 4096);
					while ((count = tarIn.read(data, 0, 4096)) != -1) {
						dest.write(data, 0, count);
					}
					dest.close();
				}
				
			}
			File renamedFile= new File(moduleDirPath+"/"+moduleName);
			if(unzipFile.isDirectory()){
				unzipFile.renameTo(renamedFile);
			}
			tarIn.close();
			
		} catch (IOException e) {
			logger.error("Error in unzip the module file :"+e.getMessage());
		}
	}
	public List<SearchModuleBean> searchModule(HttpServletRequest request){
		List <SearchModuleBean> moduleList = new ArrayList<SearchModuleBean>();
		String module = request.getParameter("query");
		String url = "https://forgeapi.puppetlabs.com:443/v3/modules?query="+module;
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("owner", request.getParameter("owner"));
			con.setRequestProperty("tag", request.getParameter("tag"));
			con.setRequestProperty("show+deleted", request.getParameter("show+deleted"));
			con.setRequestProperty("sort_by", request.getParameter("sort_by"));
			con.setRequestProperty("operatingsystem", request.getParameter("operatingsystem"));
			con.setRequestProperty("supported", request.getParameter("supported"));
			con.setRequestProperty("pe_requirement", request.getParameter("pe_requirement"));
			con.setRequestProperty("puppet_requirement", request.getParameter("puppet_requirement"));
			con.setRequestProperty("limit", request.getParameter("limit"));
			con.setRequestProperty("offset", request.getParameter("offset"));
			con.setRequestProperty("If-Modified-Since", request.getParameter("If-Modified-Since"));
			con.setRequestProperty("Content-Type","application/json; charset=UTF-8");
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				JSONObject o = new JSONObject(response.toString());
				JSONArray jsonArray = o.getJSONArray("results");
					for(int i=0; i<jsonArray.length(); i++){
						SearchModuleBean searchModule = new SearchModuleBean();
						Gson moduleData = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss").create();
						searchModule = moduleData.fromJson(jsonArray.get(i).toString(),SearchModuleBean.class);
						moduleList.add(searchModule);
					}
					
			} catch (ProtocolException e) {
				logger.error("Error in searching module :"+e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Error in searching module :"+e.getMessage());
				e.printStackTrace();
			} catch (JSONException e) {
				logger.error("Error in searching module :"+e.getMessage());
				e.printStackTrace();
			}
		return moduleList;
	}
	public StatusBean refreshNodeTemplate( List<PuppetModuleMapping> puppetModuleData, String ProjectID){
		
		HashMap<String, ArrayList<String>> nodeConfigMap = new HashMap<String, ArrayList<String>>();
		for(PuppetModuleMapping puppetModule : puppetModuleData){
			if(puppetModule.getMachineConfig().getIsDisabled() == false && puppetModule.getPuppetModule().getIsDisabled() == false){
				if(nodeConfigMap.containsKey(puppetModule.getMachineConfig().getHostName())){
					nodeConfigMap.get(puppetModule.getMachineConfig().getHostName()).add(puppetModule.getPuppetModule().getModuleName());
				}else{
					ArrayList<String > moduleNameList = new ArrayList<String>();
					moduleNameList.add(puppetModule.getPuppetModule().getModuleName());
					nodeConfigMap.put(puppetModule.getMachineConfig().getHostName(), moduleNameList);
				}
			}
		}
		
		boolean configFileData = ConfigurationGenerator.generateNodeConfig(nodeConfigMap);
		StatusBean statusBean = new StatusBean();
		if(configFileData){
			logger.info("Started saving Nodes.pp file");
			FileManager fileManager = new FileManager();
			String renderedTemplate = ConfigurationGenerator.getVelocityFinalTemplate();
			statusBean = fileManager.writeNodeFileToDisk(renderedTemplate, ProjectID);
			logger.info("Nodes.pp file save completed");
		}
		else{
			logger.info("Failed to save Nodes.pp file !!");
		}
		
		return statusBean;
		
	}
}




















