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
angular.module("boxuppApp").controller('projectController',function($scope,$rootScope, Projects,AuthenticateAwsCred,Providers,User,$routeParams,$filter,$location,miscUtil,$http,$timeout){

	/*$scope.projects = Projects.query(function(){
		if($scope.projects.length === 0) $scope.noProjectsInfo = true;
		
	});*/

	$scope.awsProviderName = "AWS";
	 
	$scope.bodyStyle.applyDashBoardStyling = false;
	
	User.getProjects($routeParams.userID).then(function(projectsList){
			$scope.projects = projectsList;
	});

	$scope.providers = Providers.query(function(){
		console.log($scope.providers);
		//Queries all the providers stored in the database
		
	});

	$scope.selectProject = function(project){

		//alert('Project ID :-' + project.id + ' Project ProviderType :- '+project.providerType);
		//alert($routeParams.userID);
		
		$location.path("/projects/" + $routeParams.userID + "/" + project.projectID + "/" + project.providerType);
	}


	/*$scope.providers = [
						{id:1, name:'VirtualBox'},
						{id:2, name:'Docker'}
	];*/

	$scope.checkProjectInput = function(){
		     return !(!$scope.newProjectData.providerNames.$pristine && $scope.newProjectData.providerNames.$valid
		     		  && !$scope.newProjectData.projectTitle.$pristine && $scope.newProjectData.projectTitle.$valid
		     		  && !$scope.newProjectData.projectDesc.$pristine && $scope.newProjectData.projectDesc.$valid
		     		  && ($scope.newProject.providerType > 0) && $scope.checkAwsCredentialsInput() && $scope.authenticatCred );
	}
	
	$scope.checkAwsCredentialsInput = function(){
		providerName = $scope.getProviderName($scope.newProject.providerType);
		if(providerName==$scope.awsProviderName){
			return $scope.checkAwsCredentialsStatus();
		}
		else{
			$scope.authenticatCred = true;
			return true;
		}
	}
	
	$scope.checkAwsCredentialsStatus = function(){
		return $scope.newProjectData.awsAccessKeyId.$valid && $scope.newProjectData.awsSecretAccessKey.$valid &&
		$scope.newProjectData.awsKeyPair.$valid && $scope.newProjectData.privateKeyPath.$valid ;
	}
	
	$scope.authenticateAwsCredentials = function(){
		if($scope.checkAwsCredentialsInput()){
			$scope.authenticatCred=false;
			$scope.awsCred ={};
			$scope.awsCred.awsAccessKeyId = $scope.newProject.awsAccessKeyId;
			$scope.awsCred.awsSecretAccessKey = $scope.newProject.awsSecretAccessKey;
			$scope.awsCred.awsKeyPair =	$scope.newProject.awsKeyPair; 	
			$scope.awsCred.privateKeyPath = $scope.newProject.privateKeyPath;
			AuthenticateAwsCred.save($scope.awsCred,function(data){
				$scope.authenticatCred=!data.statusCode;
				$scope.awsAuthenticationStatus=true;
				$scope.awsAuthenticationMessage = data.statusMessage;
			});
		}
	}

	$scope.submitNewProjectData = function(){
			
			$scope.newProject.owner=$routeParams.userID;
			$scope.newProject.creationTime = miscUtil.fetchCurrentTime();								 
			$scope.newProject.isDisabled = false;
			Projects.save($scope.newProject,function(data){
				$scope.projects.push(angular.copy(data.beanData));
				//Reset New Project Modal Data
				$scope.newProject = {};
				//Reset form pristine state
				$scope.newProjectData.$setPristine();
				$scope.awsAuthenticationStatus=false;
				$scope.newProjectCreated = true;
				$timeout(function(){
					$scope.newProjectCreated = false;
				},3000);
			});
			
			
	}
	
	$scope.checkProviderType = function(providerType){
		providerName = $scope.getProviderName(providerType);
		if(providerName==$scope.awsProviderName){
			$scope.showAwsCredDiv=true;
		}else{
			$scope.showAwsCredDiv=false;
		}
	}
	
	$scope.getProviderName=function(providerType){
		for(e in $scope.providers){
			if($scope.providers[e].providerID==providerType){
				return $scope.providers[e].name;
			}
		}
	}

});