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
angular.module("boxuppApp").controller('gitController', [ '$scope', '$routeParams','fetchVagrantFile','fetchListOfgitRepo',function($scope, $routeParams, fetchVagrantFile, fetchListOfgitRepo){
	$scope.githubConfig = {
		username:"",
		password:"",
		repoName:"trial",
		repoBranch:"",
		path:"local/VagrantFile",
		comment:"Vagrantfile commit"
	};

	$scope.buttonText = "Get Repos";
	$scope.loaderValid = false;
	$scope.statusText = "";
	$scope.fetchRepoListSuccess = false;
	$scope.branchValid = false;
	
	$scope.resetFlags = function(){
		$scope.fetchRepoListSuccess = false;
		$scope.fetchBranchListSuccess = false;
		$scope.branchValid = false;
		// $scope.githubConfig.username = "";
		// $scope.githubConfig.password = "";
	}	

	$scope.getGitRepoList = function(){
		$scope.checkRepoList = true;
		/*if( !$scope.githubConfig.password ){
			$scope.passValid = true ;
			return -1;

		}
		$scope.passValid = false ;*/
		var gC = $scope.githubConfig;
		
		var github = new Github({
			username: gC.username,
			password: gC.password,
			auth : 'basic'
		});
		
		/*$scope.loaderValid = true;*/
		
		$scope.userData = github.getUser();
		try{
			$scope.userData.repos(function(err, response){
				$scope.checkRepoList = false;
			if(err != null){
				$scope.errorCode = err.error;
				$scope.statusText = $scope.errorCode + ": Please check your credentials";
				$scope.$apply();
				// $scope.loaderValid = false;
				// $scope.loginValid = true;
				// $scope.githubConfig.username = "";
				// $scope.githubConfig.password = "";
				// return -1;
			}
			
			if(err == null){
				// $scope.loginValid = false;
				// $scope.loaderValid = false;
				$scope.statusText="";
				gC.gitRepoList = response;
				$scope.fetchRepoListSuccess = true;
				$scope.$apply();
			}
			
		});	
		}
		catch(err){
			console.error('Github error');
		}
		
		
	}
	$scope.getGitBranchList = function(){
		var gC = $scope.githubConfig;
		var github = new Github({
			username: gC.username,
			password: gC.password,
			auth : 'basic'
		});

		// $scope.loaderValid = true;
		
		github.getRepo(gC.username, gC.repoName).listBranches(function(err, response){

			if(err == null){
				gC.gitBranchList = response;
				$scope.fetchBranchListSuccess = true;
				// $scope.loaderValid = false;
				// $scope.repoValid = true;
				$scope.$apply();

			}
		});

	}
		
	$scope.commitToGithub = function(){
		var gC = $scope.githubConfig;
		var github = new Github({
			username: gC.username,
			password:gC.password,
			auth : 'basic'
		});
	
		var gitPath = gC.path;
		var repo = github.getRepo(gC.username, gC.repoName);
		var gitBranch = gC.repoBranch;
		var gitCommitMessage = gC.comment;
		/*GitRepo.save($scope.githubConfig,function(data){
				$scope.projects.push(angular.copy(data.beanData));
				//Reset New Project Modal Data
				$scope.newProject = {};
				//Reset form pristine state
				$scope.newProjectData.$setPristine();
			});*/
		fetchVagrantFile.content($routeParams.userID).then(function(response){
			
			var gitContent;
			if(response.statusCode === 0){
			gitContent = response.fileContent;
				repo.write(gitBranch, gitPath, gitContent, gitCommitMessage, function(err) {
					if(err !== null){
						var responseObj = JSON.parse(err.request.response);
						alert(responseObj.message);
					}
					else{
						alert('Vagrant File has been successfully committed to your repository');
					}
				});
			}else{
				alert('Vagrant file could not be fetched');
			}			
		});
		
	}
}]).factory('fetchVagrantFile',function($http,$q,$routeParams){
		return{
			content : function(userID){
				var completeURL = '/boxupp/getVagrantFile/';
				var deferred = $q.defer();
				var parameters = {"projectID" : $routeParams.projectID, "userID" : $routeParams.userID};
				$http({	
					method:'GET',
					headers:{'Content-Type':'application/json; charset=UTF-8'},
					params: parameters,
					url:completeURL
				}).
				success(function(response, status, headers, config) {
					deferred.resolve(response);
				}).
				error(function(data, status, headers, config) {
					console.log(" : Error fetching vagrant file : ");
				});
				return deferred.promise;
			}
	}
}).factory('fetchListOfgitRepo', function($http, $q){
	return{
		repoList: function(username){
			var repoURL = "https://api.github.com/"+username + "/repos";
			var deferred = $q.defer();
			$http({
				method: 'GET',
				headers: {'Content-Type':'application/json; charset=UTF-8'},
				url : repoURL
			}).
			success(function (response,status, headers, config) {
				deferred.resolve(response);
				console.log(response);
			}).error(function(data, status, headers, config){
				console.log(":Error geting Repo from github for a user");
			});
			return deferred.promise;
		}

	}
});
