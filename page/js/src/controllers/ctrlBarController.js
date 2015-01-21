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
angular.module('boxuppApp').controller('ctrlBarController',function($scope,shellScript,$routeParams,miscUtil,MachineConfig){

	

	$scope.saveNewScript = function(newShellScriptData){
		newShellScriptData.isDisabled = false;
		newShellScriptData.userID = $routeParams.userID;
		newShellScriptData.description = "v1 script";
		newShellScriptData.creationTime = miscUtil.fetchCurrentTime();
		newShellScriptData.ProjectID = $routeParams.projectID;
		shellScript.save(newShellScriptData,function(data){
			if(data.statusCode === 0){
				console.info('Shell Script has been saved successfully');
				$scope.shellScripts.push(data.beanData);
				newShellScriptData.scriptName = "";
				newShellScriptData.scriptContent = "";
				$scope.rawScriptForm.$setPristine();
			}
			
		});
	}

	$scope.setShellChangeFlag = function(){
		$scope.boxuppConfig.shellChangeFlag = 1;	
	}
	$scope.$watch('shellScripts',function(newValue,oldValue){
		if(newValue.length !== oldValue.length){
			//shell scripts deleted or added//
			$scope.setShellChangeFlag();
		}
		//Only handle script name change events//
		if((newValue !== oldValue) && ($scope.scriptSelected !== -1)){
		$scope.setShellChangeFlag();
		console.log($scope.boxuppConfig.shellChangeFlag);
		/*To handle empty scripts case*/
		if(((typeof newValue[$scope.scriptSelected]) !== 'undefined') && 
			((typeof oldValue[$scope.scriptSelected]) !== 'undefined')){
			if(newValue[$scope.scriptSelected].scriptName !== oldValue[$scope.scriptSelected].scriptName){
				var newScriptName = newValue[$scope.scriptSelected].scriptName;
				var oldScriptName = oldValue[$scope.scriptSelected].scriptName;
				for(index in $scope.boxesData){
					var oldScriptNameIndex = $scope.boxesData[index].linkedScripts.indexOf(oldScriptName);
					if( oldScriptNameIndex > -1){
						$scope.boxesData[index].linkedScripts.splice(oldScriptNameIndex,1);
						$scope.boxesData[index].linkedScripts.push(newScriptName);
					}
				}
			}
		}
	}},true);


	$scope.projectData.defaultSettings = {
		"cpuExecCap":30,
		"memory":512,
	};

	$scope.createBoxes = function(boxData){
		$('#boxModal').modal('hide');
		$scope.quickBoxCommitLoader = true;
		$scope.toBeCreatedBox = angular.copy(boxData);
		$scope.toBeCreatedBox.projectID = $routeParams.projectID;
		$scope.toBeCreatedBox.providerID = $routeParams.providerID;
		$scope.toBeCreatedBox.providerType = $scope.providerType;
		$scope.toBeCreatedBox.isDisabled = false;
		MachineConfig.save($scope.toBeCreatedBox,function(data){
			$scope.boxesData.push(data.beanData);
			$scope.quickBox = {};
			$scope.quickBoxForm.$setPristine();
			$scope.deployBox(data.beanData);
		});
		$scope.quickBoxForm.$setPristine();
		$scope.quickBoxCommitLoader = false;
		$scope.modals.close.box();
	}
	$scope.createContainerBoxes = function(boxData){
		$('#boxModal').modal('hide');
		$scope.quickContainerBoxCommitLoader = true;
		$scope.toBeCreatedBox = angular.copy(boxData);
		$scope.toBeCreatedBox = $scope.dockerLinkMappingForBackend($scope.toBeCreatedBox);
		if(!$scope.toBeCreatedBox.username){
			$scope.toBeCreatedBox.username = "root";
		}
		if(!$scope.toBeCreatedBox.password){
			$scope.toBeCreatedBox.password = "root123";
		}
		$scope.toBeCreatedBox.projectID = $routeParams.projectID;
		$scope.toBeCreatedBox.providerType = $scope.providerType;
		$scope.toBeCreatedBox.isDisabled = false;
		MachineConfig.save($scope.toBeCreatedBox,function(data){
			$scope.boxesData.push($scope.dockerLinkMappingForFrontend(data.beanData));
			$scope.quickBox = {};
			$scope.containerQuickBoxForm.$setPristine();
			$scope.deployBox(data.beanData);
		});
		$scope.containerQuickBoxForm.$setPristine();
		$scope.quickContainerBoxCommitLoader = false;
		$scope.modals.close.containerBox();
	}
	
	$scope.createQuickBox = function(boxData){
		
		$scope.toBeCreatedBox = angular.copy(boxData);
		$scope.projectData.defaultSettings.networkIP = $scope.generateNewIP();
		angular.extend($scope.toBeCreatedBox,$scope.projectData.defaultSettings);

		$scope.toBeCreatedBox.projectID = $routeParams.projectID;
		$scope.toBeCreatedBox.providerID = $routeParams.providerID
		$scope.toBeCreatedBox.providerType = $scope.providerType;
		$scope.toBeCreatedBox.isDisabled = false;
		MachineConfig.save($scope.toBeCreatedBox,function(data){
			$scope.boxesData.push(data.beanData);
			$scope.quickBox = {};
			$scope.quickBoxForm.$setPristine();
			$scope.deployBox(data.beanData);
		});
		$scope.quickBoxCommitLoader = false;	
	}
	$scope.createContainerQuickBox = function(boxData){
		
		$scope.toBeCreatedBox = angular.copy(boxData);
		angular.extend($scope.toBeCreatedBox,$scope.projectData.defaultSettings);
		if(!$scope.toBeCreatedBox.username){
			$scope.toBeCreatedBox.username = "root";
		}
		if(!$scope.toBeCreatedBox.password){
			$scope.toBeCreatedBox.password = "root123";
		}
		$scope.toBeCreatedBox.projectID = $routeParams.projectID;
		$scope.toBeCreatedBox.providerType = $scope.providerType;
		$scope.toBeCreatedBox.isDisabled = false;
		MachineConfig.save($scope.toBeCreatedBox,function(data){
			$scope.boxesData.push(data.beanData);
			$scope.quickBox = {};
			$scope.containerQuickBoxForm.$setPristine();
			$scope.deployBox(data.beanData);
		});
		$scope.containerQuickBoxCommitLoader = false;	
	}
	$scope.cloneBoxData = function(cloneBox){

		$('#boxModal').modal('show');

		$scope.toBeClonedBox = angular.copy(cloneBox);
		if($scope.toBeClonedBox.networkIP != null){
			$scope.toBeClonedBox.networkIP = null;
		}
		$scope.toBeClonedBox.vagrantID = null;
		$scope.toBeClonedBox.hostName = null;
		angular.extend($scope.rawBox,$scope.toBeClonedBox);
		
		$scope.rawBoxForm.basicSettings.vagrantID.$setViewValue($scope.toBeClonedBox.vagrantID);
		$scope.rawBoxForm.basicSettings.boxType.$setViewValue($scope.toBeClonedBox.boxType);		
		$scope.rawBoxForm.basicSettings.boxUrl.$setViewValue($scope.toBeClonedBox.boxUrl);
		$scope.rawBoxForm.basicSettings.hostName.$setViewValue($scope.toBeClonedBox.hostName);

		$scope.rawBoxForm.basicSettings.vagrantID.$render();
		$scope.rawBoxForm.basicSettings.boxType.$render();
		$scope.rawBoxForm.basicSettings.boxUrl.$render();
		$scope.rawBoxForm.basicSettings.hostName.$render();

	}
	$scope.cloneContainerBoxData = function(cloneBox){

		$('#boxModal').modal('show');

		$scope.toBeClonedBox = angular.copy(cloneBox);
		$scope.toBeClonedBox.vagrantID = null;
		$scope.toBeClonedBox.hostName = null;
		angular.extend($scope.rawBox,$scope.toBeClonedBox);
		
		$scope.containerRawBoxForm.basicSettings.vagrantID.$setViewValue($scope.toBeClonedBox.vagrantID);
		$scope.containerRawBoxForm.basicSettings.hostName.$setViewValue($scope.toBeClonedBox.hostName);

		$scope.containerRawBoxForm.basicSettings.vagrantID.$render();
		$scope.containerRawBoxForm.basicSettings.hostName.$render();

	}


	$scope.generateNewIP = function(){
		var presentIP ="";
		var size = $scope.boxesData.length;
		if(angular.isDefined($scope.boxesData) && size!== 0){
			
			presentIP = $scope.boxesData[size-1].networkIP;
		}else{
			presentIP = "192.168.111.1";
		}
		
		var ipValues = presentIP.split(".");
		var newIP;
		if(parseInt(ipValues[3]) === 255){
			newIP = ipValues[0]+"."+ipValues[1]+"."+(parseInt(ipValues[2]) + 1)+"."+0;
		}else{
			newIP = ipValues[0]+"."+ipValues[1]+"."+ipValues[2]+"."+(parseInt(ipValues[3]) + 1);
		}
		return newIP;
	}

	$scope.checkFormStates = {

		vmQuickBox : function(){

				return !(!$scope.quickBoxForm.vagrantID.$pristine && $scope.quickBoxForm.vagrantID.$valid &&
				    !$scope.quickBoxForm.hostName.$pristine && $scope.quickBoxForm.hostName.$valid &&
				    !$scope.quickBoxForm.boxType.$pristine && $scope.quickBoxForm.boxType.$valid &&
				    !$scope.quickBoxForm.boxUrl.$pristine && $scope.quickBoxForm.boxUrl.$valid && $scope.quickBoxForm.$valid);
		},
		vmRawBox : function(){

				return !(!$scope.rawBoxForm.basicSettings.vagrantID.$pristine && $scope.rawBoxForm.basicSettings.vagrantID.$valid &&
				    !$scope.rawBoxForm.basicSettings.hostName.$pristine && $scope.rawBoxForm.basicSettings.hostName.$valid &&
				    !$scope.rawBoxForm.basicSettings.boxType.$pristine && $scope.rawBoxForm.basicSettings.boxType.$valid &&
				    !$scope.rawBoxForm.basicSettings.boxUrl.$pristine && $scope.rawBoxForm.basicSettings.boxUrl.$valid &&
					!$scope.rawBoxForm.networkSettings.networkIP.$pristine && $scope.rawBoxForm.networkSettings.networkIP.$valid);
		},
		containerQuickBox : function(){
				return !(!$scope.containerQuickBoxForm.vagrantID.$pristine && $scope.containerQuickBoxForm.vagrantID.$valid &&
				    !$scope.containerQuickBoxForm.hostName.$pristine && $scope.containerQuickBoxForm.hostName.$valid &&
				    $scope.containerQuickBoxForm.imageName.$valid);								
				// !$scope.containerQuickBoxForm.imageName.$pristine &&
		},
		containerRawBox : function(){
				return !(!$scope.containerRawBoxForm.basicSettings.vagrantID.$pristine && $scope.containerRawBoxForm.basicSettings.vagrantID.$valid &&
				    !$scope.containerRawBoxForm.basicSettings.hostName.$pristine && $scope.containerRawBoxForm.basicSettings.hostName.$valid &&
				    $scope.containerRawBoxForm.basicSettings.imageName.$valid );
		},
		vmRawBoxUpdate : function(){
				return angular.equals($scope.rawBox,$scope.activeVM);
		},
		vmRawScript : function(rawScriptForm){
				return !(!rawScriptForm.shellScriptName.$pristine && rawScriptForm.shellScriptName.$valid); 
				//&& !$scope.rawScript.scriptContent.$pristine && $scope.rawScript.scriptContent.$valid );
		},
		vmRawScriptUpdate : function(){
			return angular.equals($scope.rawScript,$scope.activeScript);
		}
	}	

	$scope.modals = {
		close : {
			script : function(rawScriptForm){
				$scope.projectData.scriptsState.update = false;
				rawScriptForm.$setPristine();
				$scope.rawScript.scriptName = "";
				$scope.rawScript.scriptContent = "";
				$('#scriptModal').modal('hide');
			},
			box : function(){
				$scope.projectData.boxesState.update = false;
				
				$scope.rawBoxForm.basicSettings.boxType.$setViewValue("");
				$scope.rawBoxForm.basicSettings.boxUrl.$setViewValue("");
				$scope.rawBoxForm.basicSettings.hostName.$setViewValue("");
				$scope.rawBoxForm.basicSettings.vagrantID.$setViewValue("");
				$scope.rawBoxForm.networkSettings.networkIP.$setViewValue("");

				$scope.rawBoxForm.basicSettings.boxType.$render();
				$scope.rawBoxForm.basicSettings.boxUrl.$render();
				$scope.rawBoxForm.basicSettings.hostName.$render();
				$scope.rawBoxForm.basicSettings.vagrantID.$render();
				$scope.rawBoxForm.networkSettings.networkIP.$render();

				$scope.rawBoxForm.basicSettings.$setPristine();
				$scope.rawBoxForm.networkSettings.$setPristine();
				$('#boxModal').modal('hide');
			},
			containerBox : function(){
				$scope.projectData.boxesState.update = false;
				$scope.containerRawBoxForm.basicSettings.vagrantID.$setViewValue("");
				$scope.containerRawBoxForm.basicSettings.hostName.$setViewValue("");
				$scope.containerRawBoxForm.basicSettings.vagrantID.$render();
				$scope.containerRawBoxForm.basicSettings.hostName.$render();
				
				$scope.containerRawBoxForm.basicSettings.$setPristine();
				$('#boxModal').modal('hide');
			}	
		}
	};

});