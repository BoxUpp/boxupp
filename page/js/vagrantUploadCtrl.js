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
angular.module("boxuppApp").controller('VagrantFileUpload',function($scope){
	
	$scope.startUpload = function(){
		//e.preventDefault();
		var files = $('#vagrantFileSelector')[0].files;
		//var files = e.target.files;
		var formData = new FormData();
		for (var i = 0; i < files.length; i++) {
			  var file = files[i];
			  formData.append('dataFile', file, file.name);
		}
		var xhr = new XMLHttpRequest();
		xhr.open('POST', 'http://localhost:9000/services/uploadHandler/parseVagrantFile', true);
		xhr.onload = function () {
			if (xhr.status === 200) {
				populateMachinesData(xhr.response);
			} else {
				alert('An error occurred!');
			}
			
		};
		xhr.send(formData);
	};
	
	function populateMachinesData(data){
		var response = JSON.parse(data);
		response = JSON.parse(response.parsedData);
		var boxes = response.vmData;
		alert(boxes.length);
		for(counter in boxes){
			$scope.$parent.boxesData.splice(counter,1,boxes[counter]);
			if(boxes[counter].isPuppetMaster){
				$scope.$parent.selectedPuppetMaster = boxes[counter];
			}
		}
		$scope.$parent.nodeSelected = 0;
		$scope.$parent.selectNode(0);
		$scope.$parent.$digest();
		$scope.$parent.$apply();		
	}
	
	/*$('body').on('change','#vagrantFileSelector',function(e){
		event.preventDefault();
		var files = e.target.files;
		var formData = new FormData();
		for (var i = 0; i < files.length; i++) {
			  var file = files[i];
			  formData.append('dataFile', file, file.name);
		}
		var xhr = new XMLHttpRequest();
		xhr.open('POST', 'http://localhost:9000/services/uploadHandler/parseVagrantFile', true);
		xhr.onload = function () {
			if (xhr.status === 200) {
				alert('File uploaded successfully');
			} else {
				alert('An error occurred!');
			}
			
		};
		xhr.send(formData);
	});*/
	
	$('#vagrantForm').onsubmit = function(event){
		event.preventDefault();
		var files = $('#vagrantFileSelector').files;
		var formData = new FormData();
		for (var i = 0; i < files.length; i++) {
			  var file = files[i];
			  formData.append('dataFile[]', file, file.name);
		}
		var xhr = new XMLHttpRequest();
		xhr.open('POST', 'http://localhost:9000/services/uploadHandler/parseVagrantFile', true);
		xhr.onload = function () {
			if (xhr.status === 200) {
				alert('File uploaded successfully');
			} else {
				alert('An error occurred!');
			}
			xhr.send(formData);
		};
	}
	
	function UploadFile(file) {

			var xhr = new XMLHttpRequest();
			//if (xhr.upload && file.type == "image/jpeg" && file.size <= $id("MAX_FILE_SIZE").value) {
			// start upload
			xhr.open("post", 'http://localhost:9000/services/uploadHandler/parseVagrantFile', true);
			xhr.setRequestHeader("X_FILENAME", "dataFile");
			xhr.setRequestHeader("Content-Type", "multipart/form-data");
			//xhr.send(file);
			var formData = new FormData();
			formData.append("dataFile", file);
			xhr.send(file);

		//}

	}
	
	//action="http://localhost:8585/services/uploadHandler/parseVagrantFile"
});