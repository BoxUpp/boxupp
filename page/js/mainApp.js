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

angular.module('boxuppApp').controller('vboxController',function($scope,$interval,$q,$http,$rootScope,$routeParams,$filter,$timeout,MachineConfig,HostName,ResourcesData,vagrantStatus,executeCommand,retrieveMappings,puppetModule,miscUtil,shellScript,provider,User,$location,puppetModuleResource, boxFunctionality, loggerFunctionality,ValidateAmiId){

	$scope.projectData = {
		boxesState : {
			update : false,
		},
		scriptsState : {
			update : false
		}
	};
	


/*$('#datepicker-example7-start').Zebra_DatePicker({
  direction: true,
  pair: $('#datepicker-example7-end')
});

$('#datepicker-example7-end').Zebra_DatePicker({
  direction: 1
});*/
	$scope.showConsole = function(){
		$('#console').modal('show');
	}
	$rootScope.expandedCtrlBar = "";
	$scope.consoleTrial = "Hello";
	$scope.vagrantOutput = [];
	$scope.moduleMappingData = null;
    $scope.scriptMappingTree = {};
	$scope.boxuppMappings = {};
	$scope.serverAddress = "http://"+window.location.host;
	$scope.serverWSAddress = "ws://"+window.location.host;
	$scope.deployCommand = "";
	$scope.vagrantOptions = 0;
	$scope.apiHitInterval = 500; //0.5 second
	$scope.activeVM = null;
	$scope.activeScript = null;
	$scope.activeModule = null;
	$scope.projectData.activeModule = null;
	$scope.outputConsole = {};
	$scope.outputConsole.boxuppExecuting = false;
	$scope.searchingModules = false;
	$scope.outputConsole.boxuppOutputWindow = false;
	$scope.providerValidation = false;
	$scope.bodyStyle.applyDashBoardStyling = true;
	$scope.quickBox = {};
	$scope.moduleResults=[];
	$scope.rawBox = {};
	$scope.rawScript = {};
	$scope.rawBoxForm = {};
	$scope.rawScriptForm = {};
	$scope.rawBoxFormNetworkSettings = {};
	$scope.containerRawBoxForm ={};
	$scope.moduleProvMappings = {};
	$scope.shellProvMappings = {};
	$scope.boxesSize = 0;
	$scope.boxCounter = 0;
	$scope.providerType = provider;
	$scope.moduleMappingTree= {};
	$scope.fileName="1_t_2015-01-07-14:31:05_success.log";
	$scope.server = {
		connect : function(promise) {
			
			var location = $scope.serverWSAddress + "/vagrantConsole/";
			this._ws = new WebSocket(location);
			this._ws.onopen = this._onopen;
			this._ws.onmessage = this._onmessage;
			this._ws.onclose = this._onclose;
			this._ws.promise = promise;
		},

		_onopen : function() {
			//server._send('websockets are open for communications!');
			console.info('WebSocket connection initiated');
		},
		
		checkReadyState : function(){
			return this._ws.readyState;				
		},
		
		_send : function(message) {
			if (this._ws)
				this._ws.send(message);
		},

		send : function(text) {
			if (text != null && text.length > 0)
				this._send(text);
		},

		_onmessage : function(message) {
			$scope.vagrantOutput.push(message.data);
			var data = JSON.parse(message.data);
			if(data.dataEnd === false){
				$scope.activeOutputSnippet = {};
				$scope.activeOutputSnippet.dataEnd = data.dataEnd;
				$scope.activeOutputSnippet.type = data.type;
				$scope.activeOutputSnippet.output = data.output;
				$scope.activeOutputSnippet.vagrantFileExists = data.vagrantFileExists;
				if(data.type !== 'empty'){
					if((data.output.indexOf('rogress') !== -1)){
						$scope.vagrantOutput.splice($scope.vagrantOutput.length-1,1);
						$scope.vagrantOutput.push($scope.activeOutputSnippet);
						$scope.$apply();
					}else{
						$scope.vagrantOutput.push($scope.activeOutputSnippet);
						$scope.$apply();
					}
					$("#consoleOutput").scrollTop(1500000);
				}
			}else{
				
				$scope.activeOutputSnippet = {};
				$scope.activeOutputSnippet.dataEnd = data.dataEnd;
				$scope.activeOutputSnippet.type = data.type;
				$scope.activeOutputSnippet.output = data.output;
				$scope.vagrantOutput.push($scope.activeOutputSnippet);
				$scope.outputConsole.boxuppExecuting = false;
				$("#consoleOutput").scrollTop(1500000);
				this.close();
			}
		},

		_onclose : function(m) {
			this.promise.resolve("Done");
			this._ws = null;
			console.log('connection has been closed');
		}
	};
	
	
	$scope.fromDate = new Date();
	$scope.toDate = new Date();
	/*$scope.fromDate = $filter('date')(new Date(),'yyyy-MM-dd');
	$scope.toDate = $filter('date')(new Date(),'yyyy-MM-dd');*/
	loggerFunctionality.getLogFiles($routeParams.userID, $scope.fromDate, $scope.toDate).then(function(response){
		$scope.logFiles = response;
	});
	$scope.getLogFiles = function(fromDate, toDate){
		loggerFunctionality.getLogFiles($routeParams.userID,fromDate, toDate).then(function(response){
			$scope.logFiles = response;
		
	});
	}
	$scope.getLogFileContent = function(fileName){
		loggerFunctionality.getLogFileContent($routeParams.userID, fileName).then(function(response){
			$scope.logFileContent = response;
		});
	}
	$scope.checkMachineFlags = function(machine){
		if(machine !== null){
			if(machine.configChangeFlag || machine.scriptChangeFlag || machine.moduleChangeFlag){
				return true;
			}else{
				return false;
			}
		}
		
	}

	$scope.checkUpdatedMachineCount = function(){
		var count = 0;
		angular.forEach($scope.boxesData,function(box){
			if(box.configChangeFlag || box.scriptChangeFlag || box.moduleChangeFlag){
				count = count + 1;
			}
		});
		return count;
	}

	$scope.switchWorkspace = function(){
		$location.path("/" + $routeParams.userID + "/projects/");
	}

	$scope.checkScriptMappings = function(activeVM){
		if(activeVM !== null){
			var machineID = activeVM.machineID;
			if($scope.shellProvMappings.hasOwnProperty(machineID) && $scope.shellProvMappings[machineID].length > 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	$scope.checkModuleMappings = function(activeVM){
		if(activeVM !== null){
			var machineID = activeVM.machineID;
			if($scope.moduleProvMappings.hasOwnProperty(machineID) && $scope.moduleProvMappings[machineID].length > 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	$scope.fetchMappedScriptNames = function(activeVM){
		var scriptNames = [];

		if(activeVM !== null){
			var machineID = activeVM.machineID;
			
			var scriptMappings = $scope.shellProvMappings[machineID];
			angular.forEach(scriptMappings,function(scriptID){
				angular.forEach($scope.shellScripts,function(value){
					if(value.scriptID === scriptID){
						scriptNames.push(value.scriptName);
					}
				})
			});	
		}
		
		return scriptNames;
	}

	$scope.fetchMappedModuleNames = function(activeVM){
		var moduleNames = [];

		if(activeVM !== null){
			var machineID = activeVM.machineID;
			
			var moduleMappings = $scope.moduleProvMappings[machineID];
			angular.forEach(moduleMappings,function(moduleID){
				angular.forEach($scope.projectData.modules,function(value){
					if(value.puppetID === moduleID){
						moduleNames.push(value.moduleName);
					}
				})
			});	
		}
		
		return moduleNames;
	}


	$scope.deployEnvironment = function(){
		$scope.createVagrantFile().then(function(){
			var elements = [];
			angular.forEach($scope.boxesData,function(box){
				if(box.configChangeFlag || box.scriptChangeFlag || box.moduleChangeFlag){
					elements.push(box);
				}
			});
			
			$scope.boxesSize = elements.length;
			$scope.boxesSize > 0 ? $scope.triggerDeployment(elements[0],elements) : console.log('No boxes to deploy');
			
		});
	}
	
	$scope.stopBox = function(vmConfig){
		boxFunctionality.stopBox(vmConfig).then(function(response){
			vmConfig.underExecution = false;	
			console.log('Box stop successfully!');
		});
	}
	$scope.reloadBox = function(vmConfig){
		boxFunctionality.reloadBox(vmConfig).then(function(response){
			vmConfig.underExecution = false;	
			console.log('Box reload successfully!');
		});
	}

	$scope.deployBox = function(vmConfig){
		$scope.createVagrantFile().then(function(){
			$scope.startDeployment(vmConfig).then(function(response){
				vmConfig.underExecution = false;	
				$scope.resetFlags(vmConfig);
				$scope.commitBoxChanges(vmConfig);
				$scope.setAwsMachineHostName(vmConfig);
				console.log('Box execution completed now !');
			});
		});
	}

	$scope.setAwsMachineHostName = function(machineData){
		if(machineData.providerType=="AWS" && (machineData.hostName==null || machineData.hostName=="" )){
			machineData.userID=$routeParams.userID;
			machineData.projectID=$routeParams.projectID;
			HostName.update(machineData,function(data){
				$scope.fetchBoxList();
			});
		}
	}

	$scope.triggerDeployment = function(box,elements){
		promise = $scope.startDeployment(box).then(function(response){
			box.underExecution = false;	
			$scope.resetFlags(box);
			$scope.commitBoxChanges(box);
			console.log('Box execution completed now !');
		});
		$scope.boxCounter++;
		promise.then(function(){
			if($scope.boxCounter < $scope.boxesSize){
				$scope.triggerDeployment(elements[$scope.boxCounter],elements);
			}else{
				console.log('All Boxes deployed');
				$scope.boxCounter = 0; //RESET
			}		
		});
	}

	$scope.createVagrantFile = function(){
		var deferred = $q.defer();
		executeCommand.createVagrantFile($routeParams.projectID, $routeParams.userID).then(function(response){
			if(response){
				deferred.resolve(response);
				console.log('Vagrant file created for project  : ' + $routeParams.projectID);
			}else{
				deferred.reject('Vagrant file could not be created');
			}
		});
		return deferred.promise;
	}

	
	
	$scope.startDeployment = function(vmConfig){
		var deferred = $q.defer();
		// $scope.outputConsole.boxuppExecuting = true;
		// $scope.outputConsole.boxuppOutputWindow = true;
		// var optionSelected = $scope.vagrantOptions;
		// if(optionSelected === 0){
		// 	$scope.pushCustomMessage();
		vmConfig.underExecution = true;
			vagrantStatus.checkMachineStatus($routeParams.userID, vmConfig.vagrantID).then(function(response){
				console.log(response);
				vmConfig.machineStatusFlag = response.statusCode;
				var commandForMachine = $scope.chooseBestDeployOption(vmConfig);
				executeCommand.triggerVagrantCommand($scope,commandForMachine,deferred);
			});
		// }else{
		// 	var customCmd = prompt("Enter your Vagrant command","vagrant");
		// 	if(customCmd !== null){
		// 		$scope.deployCommand = customCmd;
		// 		executeCommand.triggerVagrant($scope.serverAddress,$scope);
		// 	}else{
		// 		$scope.outputConsole.boxuppExecuting = false;
		// 		$scope.outputConsole.boxuppOutputWindow = false;
		// 		return false;
		// 	}
		// }
		return deferred.promise;
		
	}
	

	$scope.chooseBestDeployOption = function(vmConfig){
		var flagStatesCombination = vmConfig.configChangeFlag + "" +vmConfig.moduleChangeFlag + "" +
									vmConfig.scriptChangeFlag + "" +vmConfig.machineStatusFlag + "";	
			if(vmConfig.providerType === "virtualbox"){						
				return ($scope.fetchVagrantCommandForVirtualBox(flagStatesCombination)+" " + vmConfig.vagrantID);
			}
			else if(vmConfig.providerType==="docker"){
				return ($scope.fetchVagrantCommandForDocker(flagStatesCombination)+" " + vmConfig.vagrantID);
			}
			else{
				return ($scope.fetchVagrantCommandForVirtualBox(flagStatesCombination)+" " + vmConfig.vagrantID);
			}
	}

	$scope.userSignout = function(){
		User.signout();
	}
	$scope.dockerLinkMappingForBackend = function(linkData){
		
		$scope.containerData = linkData;
		if(linkData.dockerLinks){
			var updatedData = linkData.dockerLinks;
			$scope.containerData.dockerLinks=[];
			for(var i=0; i<updatedData.length; i++){
				var links = {"linkedMachineID":updatedData[i]};
				$scope.containerData.dockerLinks.push(links);
			}
		}
		return $scope.containerData;
	}

	$scope.dockerLinkMappingForFrontend = function(linkData){
		$scope.containerData = linkData;
		if(linkData.dockerLinks){
			var machineIDs = [];
			var links = linkData.dockerLinks;
			for(var i=0; i<links.length; i++){
				machineIDs.push(links[i].linkedMachineID);	
			}
			$scope.containerData.dockerLinks = machineIDs;
		}
		return $scope.containerData;
	}
	$scope.deleteActiveBox = function(){
		alert(" Are you sure you want to remove this box.");
		MachineConfig.delete({id:$scope.activeVM.machineID},function(){			
			var boxCounter = 0;

			angular.forEach($scope.boxesData,function(box){
				if(box.machineID === $scope.activeVM.machineID){
					$scope.boxesData.splice(boxCounter,1);
				}
				boxCounter++;
			});
			$scope.activeVM = null;
		});

		/*$scope.machine = new MachineConfig({id : $scope.activeVM.machineID },function(){
			$scope.machine.$delete(function(){
				alert('Machine deleted');
			});
		});*/
	}

	$scope.deleteActiveScript = function(){
		shellScript.delete({id:$scope.activeScript.scriptID},function(){
			var scriptCounter = 0;
			angular.forEach($scope.shellScripts,function(script){
				if(script.scriptID === $scope.activeScript.scriptID){
					$scope.shellScripts.splice(scriptCounter,1);
				}
				scriptCounter++;
			});

			$scope.activeScript = null;
		});
	}

	$scope.deleteActiveModule = function(){
		puppetModuleResource.delete({id:$scope.activeModule.puppetID},function(){
			var moduleCounter = 0;
			angular.forEach($scope.projectData.modules,function(module){
				if(module.puppetID === $scope.activeModule.puppetID){
					$scope.projectData.modules.splice(moduleCounter,1);
				}
				moduleCounter++;
			});

			$scope.activeModule = null;
		});
	}

	$scope.editActiveBox = function(){
		if($scope.activeVM.providerType=="AWS"){
			$scope.onCategoryChange($scope.activeVM.instanceCategory);
		}
		var toBeEditedBox = angular.copy($scope.activeVM);
		$scope.rawBox = toBeEditedBox;
		$('#boxModal').modal('show');
		$scope.projectData.boxesState.update = true;
	}

	$scope.editActiveScript = function(){
		var toBeEditedScript = angular.copy($scope.activeScript);
		$scope.rawScript = toBeEditedScript;
		$('#scriptModal').modal('show');
		$scope.projectData.scriptsState.update = true;
	}

	$scope.updateBox = function(){
		var updatedContent = $scope.rawBox;
		$scope.entry = MachineConfig.get({id:updatedContent.machineID},function(){
			angular.extend($scope.entry,updatedContent);
			$scope.entry.configChangeFlag =1;
			$scope.entry.$update(function(){
				angular.forEach($scope.boxesData,function(box){
					if(box.machineID === $scope.entry.beanData.machineID){
						angular.extend(box,$scope.entry.beanData);
						box.configChangeFlag = 1;
						return;
					}
				});
			});
		});
	}

	$scope.commitBoxChanges = function(box){
		var updatedContent = box;
		$scope.entry = MachineConfig.get({id:updatedContent.machineID},function(){
			angular.extend($scope.entry,updatedContent);
			$scope.entry.$update(function(){
				angular.forEach($scope.boxesData,function(box){
					if(box.machineID === $scope.entry.beanData.machineID){
						angular.extend(box,$scope.entry.beanData);
						return;
					}
				});
			});
		});
	}

	$scope.updateContainerBox = function(){
		var updatedContent = $scope.dockerLinkMappingForBackend($scope.rawBox);
		
		$scope.entry = MachineConfig.get({id:updatedContent.machineID},function(){
			angular.extend($scope.entry,updatedContent);
			$scope.entry.$update(function(){
				angular.forEach($scope.boxesData,function(box){
					if(box.machineID === $scope.entry.beanData.machineID){
						angular.extend(box,$scope.dockerLinkMappingForFrontend($scope.entry.beanData));
						box.configChangeFlag = 1;
						return;
					}
				});
			});
		});
	}
	$scope.updateScript = function(){
		var updatedContent = $scope.rawScript;
		$scope.entry = shellScript.get({id:updatedContent.scriptID},function(){
			angular.extend($scope.entry,updatedContent);
			$scope.entry.creationTime = miscUtil.fetchCurrentTime();
			$scope.entry.userID = $routeParams.userID;
			$scope.entry.$update(function(){
				var beanScriptID = $scope.entry.beanData.scriptID;
				angular.forEach($scope.shellScripts,function(script){
					if(script.scriptID === beanScriptID){
						angular.extend(script,$scope.entry.beanData);
						$scope.triggerScriptChangeFlag(script);
						return;
					}
				});
			});
		});
	}

	$scope.triggerScriptChangeFlag = function(script){
		var scriptID = script.scriptID;
		angular.forEach($scope.shellProvMappings,function(mapping,key){
			if($scope.shellProvMappings[key].indexOf(scriptID) != -1){
				angular.forEach($scope.boxesData,function(box){
					if(box.machineID == key){
						$scope.setScriptFlagForBox(box);
					}
				});	
			}
		});
	}

	$scope.setScriptFlagForBox = function(box){
		var updatedContent = angular.copy(box);
		$scope.entry = MachineConfig.get({id:box.machineID},function(){
							angular.extend($scope.entry,updatedContent);
							$scope.entry.scriptChangeFlag = 1;
							$scope.entry.$update(function(){
								box.scriptChangeFlag = 1;		
							});
						});
	}

	$scope.setPuppetFlagForBox = function(box){
		var updatedContent = angular.copy(box);
		$scope.entry = MachineConfig.get({id:box.machineID},function(){
							angular.extend($scope.entry,updatedContent);
							$scope.entry.moduleChangeFlag = 1;
							$scope.entry.$update(function(){
								box.moduleChangeFlag = 1;		
							});
						});	
	}


	// $scope.selectedProvMachine = {};

	$scope.selectModule = function(module){
		$scope.activeModule = module;
		retrieveMappings.fetchPuppetMappings().then(function(mappings){
			$scope.moduleMappingData = mappings;
		});
		$scope.convertModuleMappingStructureForGraph($scope.moduleMappingData);
		$("#moduleMapping").empty();
		if($scope.moduleMappingTree.children.length != 0 ){
			$scope.moduleMapping = new ModuleMapping($scope.moduleMappingTree );
		}
		
	}
	$scope.convertModuleMappingStructureForGraph = function(mappings){
		
		$scope.moduleMappingTree = {"name": $scope.activeModule.moduleName,"children":[]};
		var machineList =[];
		angular.forEach(mappings, function(map){
			if($scope.activeModule.puppetID === map.puppetModule.puppetID){
				machineList.push(map.machineConfig);
			}
		});
		
		$scope.moduleMappingTree.children = machineList;
		
	}
	$scope.selectScript = function(script){		
		$scope.activeScript = script;
		retrieveMappings.fetchScriptMappings().then(function(mappings){

			$scope.scriptMappingData = mappings;
		});
		$scope.convertScriptMappingStructureForGraph($scope.scriptMappingData);
		$("#scriptMapping").empty();
		if($scope.scriptMappingTree.children.length != 0 ){

			$scope.scriptMapping = new ScriptMapping($scope.scriptMappingTree );
		}
	}

	$scope.listOfSSHImages=[
		{
			"path":"boxupp/centos-dev:V1.4",
			"iconSrc":"img/centos-32.png"
		},
		{
			"path":"boxupp/debian-base",
			"iconSrc" : "img/debian-32.png"
		},
		{
			"path":"boxupp/ubuntu-base",
			"iconSrc" : "img/ubuntu-32.png"
		}
	];
	/*{
			"path":"boxupp/redhat-base",
			"iconSrc" : "img/redhat-32.png"
		},*/
	$scope.searchNewModule = function(moduleSearchText){
		$scope.searchingModules = true;
		puppetModule.searchPuppetModule($scope,moduleSearchText).then(function(response){
			$scope.moduleResults = response;	
			$scope.searchingModules = false;
		});
	}

	$scope.downloadNewModule = function(toBeDownloadedModule){
		toBeDownloadedModule.downloadButtonText = 'Downloading';
		toBeDownloadedModule.downloading = true;
		puppetModule.downloadPuppetModule(toBeDownloadedModule).then(function(response){
			toBeDownloadedModule.downloadButtonText = 'Download';
			toBeDownloadedModule.downloading = false;
			$scope.projectData.modules.push(response.beanData);
		});
	}
	
	
	$scope.resetCtrlBarSecNav = function(){
		$('ul.ctrl-bar-sec-list li').removeClass('active');
	}
	$scope.resetBoxInControlBar = function(){
		$('ul.ctrl-bar-main-list .liForScript').removeClass('active');
		$('ul.ctrl-bar-main-list .liForModule').removeClass('active');
		$('ul.ctrl-bar-main-list .liForBox').addClass('active');
		$scope.resetCtrlBarSecNav();
		$('#ctrl-bar-module-sec-nav').removeClass('in active');
		$('#ctrl-bar-script-sec-nav').removeClass('in active');
		$('#box-quick').removeClass('active in');
		$('#box-clone').removeClass('active in');
		$('#ctrl-bar-box-sec-nav').addClass('in active');
		$('#ctrl-bar-empty-play-area').addClass('in active');
		
	}
	$scope.resetScriptInControlBar = function(){
		$('ul.ctrl-bar-main-list .liForBox').removeClass('active');
		$('ul.ctrl-bar-main-list .liForModule').removeClass('active');
		$('ul.ctrl-bar-main-list .liForScript').addClass('active');
		$scope.resetCtrlBarSecNav();
		$('#ctrl-bar-box-sec-nav').removeClass('in active');
		$('#ctrl-bar-module-sec-nav').removeClass('in active');
		$('#box-quick').removeClass('active in');
		$('#box-clone').removeClass('active in');
		$('#ctrl-bar-script-sec-nav').addClass('in active');
		$('#ctrl-bar-empty-play-area').addClass('in active');

		
	}
	$scope.resetModuleInControlBar = function(){
		$('ul.ctrl-bar-main-list .liForScript').removeClass('active');
		$('ul.ctrl-bar-main-list .liForBox').removeClass('active');
		$('ul.ctrl-bar-main-list .liForModule').addClass('active');
		$scope.resetCtrlBarSecNav();
		$('#ctrl-bar-box-sec-nav').removeClass('in active');
		$('#ctrl-bar-script-sec-nav').removeClass('in active');
		$('#box-quick').removeClass('active in');
		$('#box-clone').removeClass('active in');
		$('#ctrl-bar-module-sec-nav').addClass('in active');
		$('#ctrl-bar-empty-play-area').addClass('in active');
	}
		
	$scope.vagrantCommands = {
		0:"Choose what's best"
	};
	$scope.selectProviderPage = function(){
		$scope.providerValidation = true;
	}

	$scope.fetchBoxList = function(){
		ResourcesData.fetchBoxList($routeParams.projectID).then(function(response){
			if(response.length > 0){
				$scope.boxesData = response;
			}			
		});	
	}
	

	$scope.fetchScriptList = function(){
		ResourcesData.fetchScriptList($routeParams.projectID).then(function(response){
			$scope.shellScripts = response;
		});		
	}
	
	$scope.fetchModuleList = function(){
		ResourcesData.fetchModuleList($routeParams.projectID).then(function(response){
			$scope.projectData.modules = response;
		});			
	}

	$scope.markActiveProject = function(){
		miscUtil.selectActiveProject().then(function(response){
			$scope.selectedProject = response.data;
		});
	}

	$scope.fetchShellScriptMappings = function(){
		retrieveMappings.fetchScriptMappings().then(function(mappings){

			$scope.scriptMappingData = mappings;
			$scope.shellProvMappings = {};
			$scope.shellProvMappings = $scope.convertScriptMappingsStructure(mappings);
		});
	}
	$scope.convertScriptMappingStructureForGraph = function(mappings){
		$scope.scriptMappingTree ={};
		$scope.scriptMappingTree = {"name": $scope.activeScript.scriptName,"children":[]};
		var machineList =[];
		angular.forEach(mappings, function(map){
			if($scope.activeScript.scriptID === map.script.scriptID){
				machineList.push(map.machineConfig);
			}
		});
		
		$scope.scriptMappingTree.children = machineList;
		
	}
	$scope.convertScriptMappingsStructure = function(mappings){
		var newMappings = {};
		angular.forEach(mappings,function(map){
			var machineID = map.machineConfig.machineID;
			var scriptID = map.script.scriptID;
			if(newMappings.hasOwnProperty(machineID)){
				newMappings[machineID].push(scriptID);
			}else{
				newMappings[machineID] = [];
				newMappings[machineID].push(scriptID);
			}
		});
		return newMappings;
	}

	$scope.convertPuppetMappingsStructure = function(mappings){
		var newMappings = {};
		angular.forEach(mappings,function(map){
			var machineID = map.machineConfig.machineID;
			var puppetID = map.puppetModule.puppetID;
			if(newMappings.hasOwnProperty(machineID)){
				newMappings[machineID].push(puppetID);
			}else{
				newMappings[machineID] = [];
				newMappings[machineID].push(puppetID);
			}
		});
		return newMappings;
	}

	$scope.fetchPuppetMappings = function(){
		retrieveMappings.fetchPuppetMappings().then(function(mappings){
			$scope.moduleProvMappings = {};
			$scope.moduleMappingData = mappings;
			$scope.moduleProvMappings = $scope.convertPuppetMappingsStructure(mappings);
		});
	}
	
	$scope.instanceCategory = {
			"General Purpose":"General Purpose",
			"M3":"M3",
			"Compute Optimized C4":"Compute Optimized C4",
			"C3":"C3",
			"Memory Optimized R3":"Memory Optimized R3",
			"GPU G2":"GPU G2",
			"Storage Optimized I2":"Storage Optimized I2",
			"HS1":"HS1",
	};
	$scope.instanceCategoryTypes = {
			"General Purpose":{"Micro":"t2.micro","Small":"t2.small","Medium":"t2.medium"},
			"M3":{"Medium":"m3.medium","Large":"m3.large","xLarge":"m3.xlarge","2xLarge":"m3.2xlarge"},
			"Compute Optimized C4":{"Large":"c4.large","xLarge":"c4.xlarge","2xLarge":"c4.2xlarge","4xLarge":"c4.4xlarge","8xLarge":"c4.8xlarge"},
			"C3":{"Large":"c3.large","xLarge":"c3.xlarge","2xLarge":"c3.2xlarge","4xLarge":"c3.4xlarge","8xLarge":"c3.8xlarge"},
			"Memory Optimized R3":{"Large":"r3.large","xLarge":"r3.xlarge","2xLarge":"r3.2xlarge","4xLarge":"r3.4xlarge","8xLarge":"r3.8xlarge"},
			"GPU G2":{"2xLarge":"g2.2xlarge"},
			"Storage Optimized I2":{"xLarge":"i2.xlarge","2xLarge":"i2.2xlarge","4xLarge":"i2.4xlarge","8xLarge":"i2.8xlarge"},
			"HS1":{"8xLarge":"hs1.8xlarge"},
	};
	
	$scope.onCategoryChange=function(data){
		$scope.instanceTypes = $scope.instanceCategoryTypes[data];
	}


	$scope.fetchBoxList();
	$scope.fetchScriptList();
	$scope.fetchModuleList();
	$scope.markActiveProject();
	$scope.fetchShellScriptMappings();
	$scope.fetchPuppetMappings();
	
	
	/*$scope.server = {
		connect : function() {
			
			var location = $scope.serverWSAddress + "/vagrantConsole/";
			this._ws = new WebSocket(location);
			this._ws.onopen = this._onopen;
			this._ws.onmessage = this._onmessage;
			this._ws.onclose = this._onclose;
		},

		_onopen : function() {
			//server._send('websockets are open for communications!');
			console.info('WebSocket connection initiated');
		},
		
		checkReadyState : function(){
			return this._ws.readyState;				
		},
		
		_send : function(message) {
			if (this._ws)
				this._ws.send(message);
		},

		send : function(text) {
			if (text != null && text.length > 0)
				this._send(text);
		},

		_onmessage : function(message) {
			var data = JSON.parse(message.data);
			if(data.dataEnd === false){
				$scope.activeOutputSnippet = {};
				$scope.activeOutputSnippet.dataEnd = data.dataEnd;
				$scope.activeOutputSnippet.type = data.type;
				$scope.activeOutputSnippet.output = data.output;
				$scope.activeOutputSnippet.vagrantFileExists = data.vagrantFileExists;
				if(data.type !== 'empty'){
					if((data.output.indexOf('rogress') !== -1)){
						$scope.vagrantOutput.splice($scope.vagrantOutput.length-1,1);
						$scope.vagrantOutput.push($scope.activeOutputSnippet);
					}else{
						$scope.vagrantOutput.push($scope.activeOutputSnippet);
					}
					$("#consoleOutput").scrollTop(1500000);
				}
			}
			else{
				$scope.activeOutputSnippet = {};
				$scope.activeOutputSnippet.dataEnd = data.dataEnd;
				$scope.activeOutputSnippet.type = data.type;
				$scope.activeOutputSnippet.output = data.output;
				$scope.vagrantOutput.push($scope.activeOutputSnippet);
				$scope.outputConsole.boxuppExecuting = false;
				$("#consoleOutput").scrollTop(1500000);
			}
		},

		_onclose : function(m) {
			this._ws = null;
		}
	};*/
			
	$scope.defaultConfigurations = {
			"vagrantID":"",
			"hostName":"",
			"boxType":"Ubuntu",
			"boxUrl":"http://cloud-images.ubuntu.com/vagrant/precise/current/precise-server-cloudimg-i386-vagrant-disk1.box",
			"networkIP":"192.168.111.24",
			"syncFolders":[{
							"hostFolder":"",
							"vmFolder":""
			}],
			"portMappings":[{
							"hostPort":"",
							"vmPort":""
			}],
			"providerType":"virtualbox",
			"linkedScripts":[],
			"isPuppetMaster":false,
			"provisionerName":"",
			"cpuExecCap":"",
			"memory":"",
			"bootTimeout":"300",
			"guiMode":false
	};	
	
	$scope.$watch('vagrantSelection.$valid',function(newVal,oldVal){
		if($scope.activeVM !== null){
			$scope.activeVM.isValid = $scope.vagrantSelection.$valid;
		}
	});
	$scope.uploadFolder = "modules";
	$scope.fetchUploadFolder = function(){
		return $scope.uploadFolder;
	};
	
	/*$scope.validations = {
		"vagrantID":"",
		"networkIP":"^(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))$",
	}*/	
		
	$scope.settingsExec = function(option){
		if(option === 0){
			$scope.startProductTour();
		}else if(option === 1){
			$scope.toggleTooltipStatus();
		}
	}
	
	$scope.startProductTour = function(){
		var boxuppIntro = introJs();
		boxuppIntro.setOption("showStepNumbers", false);
		var targetElement = $("div.provisionSec");
		boxuppIntro.onbeforechange(function(targetElement) {  
			var nextStep = $(targetElement).data('step');
			if(nextStep === 1){
				$('#vagrant').click();
				$('#Box1').click();
			}
			if(nextStep === 5){
				$('#provision').click();
			}
			
		});
		boxuppIntro.start();	
	}
	
	$scope.toggleTooltipStatus = function(){
		$scope.tooltipStatus = ($scope.tooltipStatus === 'On') ? 'Off' : 'On';
	}
	
	$scope.$watch('tooltipStatus',function(newVal,oldVal){
		if(newVal === 'On'){
			$('input').tooltip();
		}else{
			$('input').tooltip('destroy');
		}
	});
	
	$scope.$watch('activeVM',function(newVal,oldVal){
		// console.log("new val "+newVal +" : old val "+oldVal);// console.log("new val "+newVal +" : old val "+oldVal);
	});

	$scope.setVagrantChangeFlag = function(){
		$scope.boxuppConfig.vagrantChangeFlag = 1;
	}
	$scope.editorOptions = {
        lineWrapping : true,
		lineNumbers  : true,
		mode:'puppet',        
    };
	
	$scope.scriptEditorOptions = {
		lineWrapping : true,
		lineNumbers  : true,
		mode:'shell',	
	};
	
	
	$scope.getVagrantBlockID = function(num){
		if($scope.boxesData[num].isPuppetMaster){
			return "master";
		}else{
			return $scope.boxesData[num].vagrantID;
		}
	}
	
	$scope.shellScripts = [];
	$scope.boxesData = [];
	/*{
							"vagrantID":"trialvm",
							"hostName":"boxupp.test.machine",
							"boxType":"Ubuntu",
							"boxUrl":"http://cloud-images.ubuntu.com/vagrant/precise/current/precise-server-cloudimg-i386-vagrant-disk1.box",
							"networkIP":"192.168.111.23",
							"syncFolders":[{
											"hostFolder":"",
											"vmFolder":""
							}],
							"portMappings":[{
											"hostPort":"8080",
											"vmPort":"8787"
							}],
							"providerType":"virtualbox",
							"linkedScripts":[],
							"isPuppetMaster":false,
							"provisionerName":"testMachine",
							"cpuExecCap":"15",
							"memory":"512",
							"bootTimeout":"300",
							"guiMode":false
						}*/
	$scope.projectData.modules = [];
	$scope.puppet = {
		"manifests":[{"moFileName":"nodes.pp",
					  "moFileSource":"# Sample nodes.pp file\n# Add master node \n# node \"puppet.vagrant.master.com\"\n# {\n# }\n# Add agent node \n# node \"puppet.vagrant.mysql.com\"\n# {\n    #Include modules to be added on the node in this format include module_name refer below e.g\n    #include haproxy\n# }",
					  "editName":false
					},
					{"moFileName":"site.pp",
					 "moFileSource":"# Sample site.pp file \n# import 'nodes.pp'\n# filebucket { 'main':\n#  \t\tserver => 'puppet',\n#  \t\tpath   => false,\n# }\n# File { backup => 'main' }\n\n# node default {}\n\n# Add agent definitions below\n# node 'agent' {\n# notify {'agent':}\n\n# create a simple hostname and ip host entry\n# host { 'mysql':\n#    \tip => '192.168.111.20',\n# }",
					 "editName":false
					}],
		"modules":[],
		"files":[]
	};
	
	$scope.minimizeConsole = function(){
		$scope.outputConsole.boxuppOutputWindow = false;
	}
	
	$scope.limitLength = function(){
		if(!$scope.vagrantSelection.vagrantID.$error.pattern && 
			!$scope.vagrantSelection.vagrantID.$error.maxlength && 
			!$scope.vagrantSelection.vagrantID.$error.required){
		}
	}
	
	
	$scope.activeOutputSnippet = {};
		
	$scope.urlInfo = {};
	$scope.urlInfoAvailable = false;
	
	$scope.resetBoxURLChangeStatus = function(){	
		$scope.urlInfo = {};
		$scope.urlInfoAvailable = false;
	}
	
	$scope.successTrials = {
		"dataPersistance":5,
		"vagrantOutputFetch":5
	}
	$scope.commitBoxuppData = function(){
		var boxuppMappings = {"vmData":$scope.boxesData,"shellScripts":$scope.shellScripts,"puppetData":$scope.puppet,"defaultConfigurations":$scope.defaultConfigurations};
		var completeURL = $scope.serverAddress + "/services/persistData";
		$http({	
				method:'POST',
				headers:{'Content-Type':'application/json; charset=UTF-8'},
				url:completeURL,
				data:boxuppMappings
			}).
		  success(function(data, status, headers, config) {
			console.info('Boxupp data persisted');
			$scope.successTrials.dataPersistance = 5;
			$timeout($scope.commitBoxuppData,10000);
		  }).
		  error(function(data, status, headers, config) {
			console.log("Error persisting data on the server ");
			if($scope.successTrials.dataPersistance > 0){
				$scope.successTrials.dataPersistance = $scope.successTrials.dataPersistance - 1;
				console.warn("Data persistance trial : "+$scope.successTrials.dataPersistance);
				$timeout($scope.commitBoxuppData,15000);
			}
		  });
	}
	
	
	$scope.updateURLInformation = function(){
		$scope.urlInfoAvailable = false;
		var completeURL = $scope.serverAddress + "/services/checkURL?boxURL=" + $scope.activeVM.boxUrl;
		var boxURL = $scope.activeVM.boxUrl;
		//To avoid empty URL triggering event//
		if(boxURL === " " || boxURL === undefined || boxURL === ""){
			$scope.resetBoxURLChangeStatus();
			return;
		}
		
		$http({		
				method:'GET',
				headers:{'Content-Type':'application/json; charset=UTF-8'},
				url:completeURL
			}).
		  success(function(data, status, headers, config) {
				$scope.urlInfo.contentLength = data.contentLength;
				$scope.urlInfo.statusCode = data.statusCode;
				$scope.urlInfoAvailable = true;
		  }).
		  error(function(data, status, headers, config) {
			console.log('Problem in fetching information for box URL. Probably, internet is not working');
		  });
	}
	
	$scope.fetchCommand = function(num){
		return $scope.vagrantCommands[num];
	}
	
	/*$scope.$watch('boxesData',function(newVal,oldVal){
		if(newVal !== oldVal){
			$scope.setVagrantChangeFlag();
		}
	},true);*/
	
	$scope.$watch('boxesData.length',function(newVal,oldVal){
		$('#nodesContainer').perfectScrollbar('update');
	},true);
	
	$scope.setVagrantChangeFlag = function(){
		$scope.boxuppConfig.vagrantChangeFlag = 1;
	}
	
	$scope.boxuppConfig = {
		activeVM:1,
		totalVM:1,
		activeNode:1,
		activeProvisioner:"shell",
		vagrantChangeFlag:0,
		shellChangeFlag:0,
		puppetChangeFlag:0,
		cookbooksChangeFlag:0,
		vagrantExecutionFlag:0,
		manifestDefaults:{
			"content":"Hello"
		}
	};
	
	$scope.flushVagrantOutputConsole = function(){
		$scope.vagrantOutput = [];
	}

	$scope.toggleConsoleWindow = function(){
		if($scope.outputConsole.boxuppOutputWindow){
			if(!$('#puppetEditor').hasClass('maximized')){
				$scope.outputConsole.boxuppOutputWindow = false;
			}
			return;
		}
		$scope.outputConsole.boxuppOutputWindow = true;
	}

	$scope.fetchVagrantCommandForVirtualBox = function(combination){
		console.log("Environment state : " + combination);
		var commands = {
			"0000":"vagrant up",
			"0001":"vagrant reload --provision",
			"0010":"vagrant up --provision",
			"0011":"vagrant provision --provision-with shell",
			"0100":"vagrant up --provision",
			"0101":"vagrant provision",
			"0110":"vagrant up --provision",
			"0111":"vagrant up --provision",
			"1000":"vagrant up",
			"1001":"vagrant reload",
			"1010":"vagrant reload --provision",
			"1011":"vagrant reload --provision",
			"1100":"vagrant reload --provision",
			"1101":"vagrant reload --provision",
			"1110":"vagrant up --provision",
			"1111":"vagrant reload --provision",
			"0003":"vagrant up --provision",
			"0013":"vagrant up --provision",
			"0103":"vagrant up --provision",
			"0113":"vagrant up --provision",
			"1003":"vagrant up --provision",
			"1013":"vagrant up --provision",
			"1103":"vagrant up --provision",
			"1113":"vagrant up --provision"
			
		};
		return commands[combination];	
	}
	$scope.fetchVagrantCommandForDocker = function(combination){
		console.log("Environment state : " + combination);
		var commands = {
			"0000":"vagrant up --provider=docker",
			"0001":"vagrant reload --provision",
			"0010":"vagrant up --provision --provider=docker",
			"0011":"vagrant provision --provision-with shell",
			"0100":"vagrant up --provision --provider=docker",
			"0101":"vagrant provision",
			"0110":"vagrant up --provision --provider=docker",
			"0111":"vagrant provision",
			"1000":"vagrant up --provider=docker",
			"1001":"vagrant reload",
			"1010":"vagrant up --provision --provider=docker",
			"1011":"vagrant reload --provision",
			"1100":"vagrant reload --provision",
			"1101":"vagrant reload --provision",
			"1110":"vagrant up --provision --provider=docker",
			"1111":"vagrant reload --provision",
			"0003":"vagrant up --provision --provider=docker",
			"0013":"vagrant up --provision --provider=docker",
			"0103":"vagrant up --provision --provider=docker",
			"0113":"vagrant up --provision --provider=docker",
			"1003":"vagrant up --provision --provider=docker",
			"1013":"vagrant up --provision --provider=docker",
			"1103":"vagrant up --provision --provider=docker",
			"1113":"vagrant up --provision --provider=docker"
			
		};
		return commands[combination];	
	}
	$scope.fetchVagrantOutput = function(){
		$scope.outputConsole.boxuppExecuting = true;
		var completeURL = $scope.serverAddress + "/services/getStream";
		$http({		
				method:'GET',
				headers:{'Content-Type':'application/json; charset=UTF-8'},
				url:completeURL
			}).
		  success(function(data, status, headers, config) {
					$scope.successTrials.vagrantOutputFetch = 5;
					if(data.dataEnd === false){
						$scope.activeOutputSnippet = {};
						$scope.activeOutputSnippet.dataEnd = data.dataEnd;
						$scope.activeOutputSnippet.type = data.type;
						$scope.activeOutputSnippet.output = data.output;
						$scope.activeOutputSnippet.vagrantFileExists = data.vagrantFileExists;
						if(data.type !== 'empty'){
							if((data.output.indexOf('rogress') !== -1)){
								$scope.vagrantOutput.splice($scope.vagrantOutput.length-1,1);
								$scope.vagrantOutput.push($scope.activeOutputSnippet);
							}else{
								$scope.vagrantOutput.push($scope.activeOutputSnippet);
							}
							$("#consoleOutput").scrollTop(1500000);
						}
						
						setTimeout($scope.fetchVagrantOutput,$scope.apiHitInterval);	
					}
					else{
						$scope.activeOutputSnippet = {};
						$scope.activeOutputSnippet.dataEnd = data.dataEnd;
						$scope.activeOutputSnippet.type = data.type;
						$scope.activeOutputSnippet.output = data.output;
						$scope.vagrantOutput.push($scope.activeOutputSnippet);
						$scope.outputConsole.boxuppExecuting = false;
						$("#consoleOutput").scrollTop(1500000);
					}
		  }).
		  error(function(data, status, headers, config) {
			console.log("Error : fetching output stream" + data);
			$scope.outputConsole.boxuppExecuting = false;
		  });
	}
	
	$scope.resetFlags = function(vmConfig){
		vmConfig.configChangeFlag = 0;
		vmConfig.moduleChangeFlag = 0;
		vmConfig.scriptChangeFlag = 0;
	}
	
	$scope.boxuppStateChanged = function(){
		var configData = $scope.boxuppConfig;
		return (
				configData.vagrantChangeFlag || 
				configData.shellChangeFlag || 
				configData.puppetChangeFlag || 
				configData.cookbooksChangeFlag
			);
	}
	
	$scope.pushNewVM = function(id,name,type,url,ip){
		/*var newVMProps = {};
		newVMProps.vagrantID = id;
		newVMProps.hostName = name;
		newVMProps.boxType = type;
		newVMProps.boxUrl = url;
		newVMProps.networkIP = ip;
		newVMProps.syncFolders = [{"hostFolder":"","vmFolder":""}];
		newVMProps.portMappings = [{"hostPort":"","vmPort":""}];
		newVMProps.providerType= "virtualbox";
		newVMProps.linkedScripts = [];
		newVMProps.isPuppetMaster = false;
		newVMProps.isValid = true;*/
		$scope.boxesData.push(newVMProps);
	}
	$scope.addNewVM = function(){
		//$scope.pushNewVM("boxuppTest","boxupp.test.machine","Ubuntu","http://www.google.com","192.168.111.23");
		var newVM = angular.copy($scope.defaultConfigurations);
		$scope.boxesData.push(newVM);
		$scope.presetNextIP();
		
	}
	$scope.cloneVM = function(num){
		var newVM = angular.copy($scope.boxesData[num]);
		newVM.vagrantID = "";
		newVM.hostName = "";
		newVM.networkIP = "";
		newVM.portMappings = [];
		newVM.isPuppetMaster = false;
		newVM.provisionerName = "";
		/*var toBeClonedNode = $scope.boxesData[num];
		$scope.pushNewVM(
								toBeClonedNode.vagrantID,
								toBeClonedNode.hostName,
								toBeClonedNode.boxType,
								toBeClonedNode.boxUrl,
								toBeClonedNode.networkIP
							);*/
		$scope.boxesData.push(newVM);
		$scope.presetNextIP();
	}
	$scope.presetNextIP = function(){
		var presentIP = $scope.defaultConfigurations.networkIP;
		var ipValues = presentIP.split(".");
		var newIP;
		if(parseInt(ipValues[3]) === 255){
			newIP = ipValues[0]+"."+ipValues[1]+"."+(parseInt(ipValues[2]) + 1)+"."+0;
		}else{
			newIP = ipValues[0]+"."+ipValues[1]+"."+ipValues[2]+"."+(parseInt(ipValues[3]) + 1);
		}
		$scope.defaultConfigurations.networkIP = newIP;
	}
	
	$scope.removeVM = function(num){
		/*splice method removes elements and returns the removed elements,
		Delete on the other hand, replaces the element with 'undefined'*/
		/*if($scope.boxesData[num].vagrantID === $scope.activeVM.vagrantID){
			$scope.activeVM = null;
		}*/
		if($scope.boxesData.length === 1){
			alert('All boxes cannot be removed');
			return;
		}
		$scope.boxesData.splice(num,1);
		if($scope.boxesData.length === 1){
			$scope.activeVM = $scope.boxesData[0];
		}
	}
	$scope.selectNode = function(box){
		$scope.activeVM = $scope.dockerLinkMappingForFrontend(box);
	}
	
	$scope.deleteFolderMapping = function(mappingNumber){
		$scope.rawBox.syncFolders.splice(mappingNumber,1);
	}
	
	$scope.delDefaultFolderMapping = function(mappingNumber){
		$scope.defaultConfigurations.syncFolders.splice(mappingNumber,1);
	}
	
	$scope.deletePortMapping = function(mappingNumber){
		$scope.rawBox.portMappings.splice(mappingNumber,1);
	}
	$scope.pushSyncFolderMapping = function(hostFolder,vmFolder){
		var syncFolderMapping = {hostFolder:"",
								 vmFolder:""};
		syncFolderMapping.hostFolder = hostFolder;
		syncFolderMapping.vmFolder = vmFolder;
		if($scope.rawBox !== null){
			if(!angular.isArray($scope.rawBox.syncFolders)){
				$scope.rawBox["syncFolders"] = [];
			}
			$scope.rawBox.syncFolders.push(syncFolderMapping);
		}
	}
	$scope.pushDefaultSyncFolderMapping = function(hostFolder,vmFolder){
		var syncFolderMapping = {hostFolder:"",
								 vmFolder:""};
		syncFolderMapping.hostFolder = hostFolder;
		syncFolderMapping.vmFolder = vmFolder;
		if($scope.defaultConfigurations !== null){
			$scope.defaultConfigurations.syncFolders.push(syncFolderMapping);
		}
	}
	$scope.addSyncFolderMapping = function(){
		$scope.pushSyncFolderMapping("","");
	}
	$scope.addDefaultSyncFolderMapping = function(){
		$scope.pushDefaultSyncFolderMapping("","");
	}
	$scope.addPortMapping = function(){
		var portMapping = {hostPort:"",vmPort:""};
		if(!angular.isArray($scope.rawBox.portMappings)){
			$scope.rawBox.portMappings = [];
		}
		$scope.rawBox.portMappings.push(portMapping);
	}
	$scope.addDockerLink=function(){
		var dockerLink ={linkContainer:""};
		if(!angular.isArray($scope.rawBox.dockerLinks)){
			$scope.rawBox.dockerLinks=[];
		}
		$scope.rawBox.dockerLinks.push(dockerLink);
	}
	$scope.deleteDockerLink = function(mappingNumber){
		$scope.rawBox.dockerLinks.splice(mappingNumber,1);
	}
	$scope.pushCustomMessage = function(){
		$scope.activeOutputSnippet = {};
		$scope.activeOutputSnippet.dataEnd = false;
		$scope.activeOutputSnippet.type = "normal";
		$scope.activeOutputSnippet.output = "Checking Vagrant Status...";
		$scope.activeOutputSnippet.vagrantFileExists = true;
		$scope.vagrantOutput.push($scope.activeOutputSnippet);
	}
	
	
	/*$scope.checkDataValidity = function(){
		for(box in $scope.boxesData){
			if($scope.boxesData[box].isValid === false){
				alert('Please check the box configurations for errors first');
				return false;
			}
		}
		return true;
	}*/
	
	
	
	$scope.waitForWSConnection = function(callback){
		setTimeout(
        function () {
            if ($scope.server.checkReadyState() === 1) {
                if(callback != null){
                    callback();
                }
                return;
            } else {
                console.log("waiting for connection...")
                waitForSocketConnection(callback);
            }
        }, 500);
	}

	$scope.checkValidity = {
		vagrantID : function(form){
			if(!$scope.projectData.boxesState.update){
				var keepgoing = true;
				angular.forEach($scope.boxesData,function(box){
					if(keepgoing){
						if(box.vagrantID === form.vagrantID.$modelValue){
							form.vagrantID.$setValidity('alreadyExists',false);
							keepgoing = false;
						}else{
							form.vagrantID.$setValidity('alreadyExists',true);				
						}	
					}
				});
			}
		},
		scriptName : function(form){
			if(!$scope.projectData.scriptsState.update){
				var keepgoing = true;
				angular.forEach($scope.shellScripts,function(script){
					if(keepgoing){
						if(script.scriptName === form.shellScriptName.$modelValue){
							form.shellScriptName.$setValidity('alreadyExists',false);
							keepgoing = false;
						}else{
							form.shellScriptName.$setValidity('alreadyExists',true);				
						}	
					}
					
				});		
			}
		}		
	}	

});

	
		