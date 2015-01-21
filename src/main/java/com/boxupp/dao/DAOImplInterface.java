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
package com.boxupp.dao;

import org.codehaus.jackson.JsonNode;

import com.boxupp.responseBeans.StatusBean;

public interface DAOImplInterface {
	public StatusBean create(JsonNode data);
	
	public <T>T read(String ID);
	
	public StatusBean update(JsonNode updatedData);
	
	public StatusBean delete(String ID);
	
	/*public StatusBean createMappedDB(String MappedId, JsonNode newData);
	
	public <E> List<E> readAllDB();
	
	public <E> List<E> readMappedData(String MappedId);
	
	public <T>T populateMappingBean(T mappingBean, String...ids);*/
	
	
}
