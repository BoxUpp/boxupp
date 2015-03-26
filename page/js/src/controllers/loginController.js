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
var ngBoxuppApp = angular.module('boxuppApp');

ngBoxuppApp.controller('loginController',function($scope,$http,$location,User,$routeParams){

	/*$scope.loginCredentials = {
			loginId : '',
			password : ''
	};*/

	$scope.bodyStyle.applyDashBoardStyling = false;


	$scope.checkIfUserExists = function(mailID){
		if(angular.isDefined(mailID)){
			User.checkIfExists(mailID).then(function(response){
				if(response.statusCode === 0){
					$scope.disableUserReg = true;	
				}else{
					$scope.disableUserReg = false;	
				}
			});	
		}
	}

	$scope.checkEmailID = function(registrationForm){
		if(!registrationForm.emailID.$valid && registrationForm.emailID.$dirty){
			return true;
		}
	}

	$scope.checkUserLogin = function(){

		$scope.checkLogin = true;
		$scope.authError = false;
		try{
			var lc = $scope.loginCredentials;

			if( angular.isUndefined(lc && lc.loginID && lc.password)){
				throw "Empty Username/Password !";	
			} 
			User.login($scope.loginCredentials.loginID,$scope.loginCredentials.password).then(function(data){
				try{
					if(data.statusCode === 0){
						$scope.checkLogin = false;
						$location.path("/" + data.userID + "/projects/");
						ga('send', 'event', 'v1.0.0', 'login',lc.loginID);
					}else{
						$scope.authError = true;
						$scope.checkLogin = false;
						throw "Invalid user credentials";
					}
				}catch(err){
					$scope.errorMessage = err;
					$scope.authError = true;
					$scope.checkLogin = false;	
				}
			});
		}
		catch(err){
			$scope.errorMessage = err;
			$scope.authError = true;
			$scope.checkLogin = false;	
		}
	}

	$scope.registerNewUser = function(){
		$scope.startRegistration = true;
		try{
			var nu = $scope.newUserDetail;
			if( angular.isUndefined(nu && nu.mailID && nu.password && nu.firstName && nu.lastName)){
				throw "All fields should be filled";
			}
			User.signup($scope.newUserDetail).then(function(data){
				if(data.statusCode !== 0){
					$scope.showRegError = true;
					$scope.startRegistration = false;
				}else{
					$scope.startRegistration = false;
					$scope.showRegInfo = true;	
					$scope.regError = false;
				}
			});	
			ga('send', 'event', 'v1.0.0', 'register',nu.mailID);
		}catch(err){
			$scope.startRegistration = false;
			$scope.regErrorMessage = err;
			$scope.regError = true;
		}	
		
	}

	$scope.proceedToLogin = function(){
		$location.path('#/login/');
	}


});