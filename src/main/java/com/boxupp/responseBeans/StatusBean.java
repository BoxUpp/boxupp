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
package com.boxupp.responseBeans;
public class StatusBean{
		public String statusMessage;
		public Integer statusCode;
		public Object beanData;
		
		public Object getData() {
			return beanData;
		}
		public void setData(Object data) {
			this.beanData = data;
		}
		public String getStatusMessage() {
			return statusMessage;
		}
		public void setStatusMessage(String statusMessage) {
			this.statusMessage = statusMessage;
		}
		public Integer getStatusCode() {
			return statusCode;
		}
		public void setStatusCode(Integer statusCode) {
			this.statusCode = statusCode;
		}
	}