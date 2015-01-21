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
angular.module("boxuppApp").
	controller('shellController',function($scope,provision,$q,retrieveMappings){	
	
	$scope.selectedProvMachine = {};
	

	$scope.checkProvState = function(){
		return _.isEmpty($scope.shellProvMappings);
	}

	$scope.commitShellScriptProvisioning = function(){
		$scope.triggerScriptProvisioningFlagChanges().then(function(){
			provision.commitShellMappings($scope.shellProvMappings).then(function(){
				console.log('Shell Script Mappings have been committed');
			});
		});
	}

	$scope.triggerScriptProvisioningFlagChanges = function(){
		var defferred = $q.defer();
		retrieveMappings.fetchScriptMappings().then(function(mappings){
			var newMappings = $scope.convertScriptMappingsStructure(mappings);
			

			/*angular.forEach(newMappings,function(value,key){
				//Check is same machine is mapped in current mappings, only then flag will be set
				if($scope.shellProvMappings.hasOwnProperty(key) && $scope.shellProvMappings[key].length > 0){
					//Difference between previously mapped and present mapping array for a particular box
					var arrayDifference = _.difference($scope.shellProvMappings[key],value);
					if(arrayDifference.length>0){
						angular.forEach($scope.boxesData,function(box){
							if(box.machineID == key){
								$scope.setScriptFlagForBox(box);
							}	
						});
						
					}
				}
			});*/
			

			angular.forEach($scope.shellProvMappings,function(value,key){
				//Check is same machine is mapped in current mappings, only then flag will be set
				if(value.length > 0){
					//Difference between previously mapped and present mapping array for a particular box
					var arrayDifference = _.difference(value,newMappings[key]);
					if(arrayDifference.length>0){
						angular.forEach($scope.boxesData,function(box){
							if(box.machineID == key){
								$scope.setScriptFlagForBox(box);
							}	
						});
						
					}
				}
			});

			/*angular.forEach($scope.shellProvMappings,function(value,key){
				if(!newMappings.hasOwnProperty(key) && $scope.shellProvMappings[key].length > 0){
					$scope.setScriptFlagForBox(box);
				}
			});*/


			defferred.resolve();
		});
		return defferred.promise;
	}



	$scope.updateShellProvMapping = function(selectedProvMachine){
		if(selectedProvMachine.machineID){
			var checkedScripts = [];
			angular.forEach($scope.shellScripts, function(value){
				if(value.shellProvChecked){
					checkedScripts.push(value.scriptID);
				}
			});
			var selectedMachine = selectedProvMachine.machineID;
			$scope.shellProvMappings[selectedProvMachine.machineID] = checkedScripts;
		}
	}

	$scope.shellProvMachineSelectedList = [];

	$scope.$watch('selectedProvMachine',function(newVal,oldVal){
		angular.forEach($scope.shellScripts,function(value){
			value.shellProvChecked = false;
		});
		var selectedArray = $scope.shellProvMappings[$scope.selectedProvMachine.machineID];
		if(!angular.isUndefined(selectedArray)){
			angular.forEach($scope.shellScripts,function(value){
				var scriptID = value.scriptID;
				if(selectedArray.indexOf(scriptID) !== -1){
					value.shellProvChecked = true;
				}
			});	
		}
		
	});

	$scope.activeScript = {};
	$scope.nodeSelectionDisabled = true;
	$scope.invokeShellConsole = function(){
		$scope.activeScript = {};
		$scope.activeScript.scriptName = "";
		$scope.nodeSelectionDisabled = true;
		$scope.scriptSelected = -1;
		$scope.showShellEditor = !$scope.showShellEditor;
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
	
	$scope.pushNewScript = function(name,source){
		if((typeof name !== 'undefined') && (name !== "")){
			var newScript = {};
			newScript.scriptName = name;
			if(typeof source === 'undefined' || source.length === 0)
				source = " ";
			newScript.scriptSource = source;
			var scriptStatus = $scope.checkIfScriptExists(name);
			
			if(scriptStatus.exists && scriptStatus.index > -1){
				var userInput = confirm("You already have a script by that name. Do you want to overwrite?");
				if(userInput){
					$scope.shellScripts.splice(scriptStatus.index,1);
					$scope.shellScripts.splice(scriptStatus.index,0,newScript);
				}	
			}else{
				$scope.shellScripts.push(newScript);
			}
		}
	}
	
	$scope.checkIfScriptExists = function(name){
		var scriptStatus = {"exists":false,index:-1};
		for(script in $scope.shellScripts){
			if($scope.shellScripts[script].scriptName === name){
				scriptStatus.exists = true;
				scriptStatus.index = script;
			}
		}
		return scriptStatus;
	}
	
	
	
	$scope.deleteScript = function(num){
		$scope.removeMappings($scope.shellScripts[num].scriptName);
		$scope.shellScripts.splice(num,1);
	}
	
	$scope.editScript = function(num){
		$scope.activeScript = $scope.shellScripts[num];
		$scope.scriptSelected = num;
	}
	
	$scope.removeMappings = function(nameOfScript){
		for(index in $scope.boxesData){
			var boxScripts = $scope.boxesData[index].linkedScripts;
			var scriptIndex = boxScripts.indexOf(nameOfScript);
			if( scriptIndex > -1){
				$scope.boxesData[index].linkedScripts.splice(scriptIndex,1);
			}
		}
	}
	
	
	
	$scope.toggleScriptSelection = function(num){
		var index = $scope.boxesData[num].linkedScripts.indexOf($scope.activeScript.scriptName);
		if( index > -1){
			$scope.boxesData[num].linkedScripts.splice(index,1);
		}else{
				$scope.boxesData[num].linkedScripts.push($scope.activeScript.scriptName);
		}
		if($scope.boxesData[num].linkedScripts.length === 0){
			$scope.boxesData[num].isShellMapped = false;
		}else{
			$scope.boxesData[num].isShellMapped = true;
		}
	}
		
});