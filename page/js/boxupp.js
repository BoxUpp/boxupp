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
angular.module('boxuppApp',['ui.codemirror','app','ngAnimate', 'ngLoadScript','ngRoute','ngResource','ui.ace','ngMessages']).
	controller('boxuppAppController',function($scope,$http,$rootScope,$timeout,vagrantStatus,executeCommand,$q,$location,User){
		
	$scope.vagrantOutput = [{"type":"normal","output":"C:\\Users\\Paxcel Techn…second","dataEnd":false,"vagrantFileExists":true}];
	}).config(['$routeProvider','$httpProvider',
	           function($routeProvider,$httpProvider) {

		$routeProvider.when('/login/',{
			templateUrl: 'templates/login.html',
			controller: 'loginController'
		}).when('/:userID/projects/', {
			templateUrl: 'templates/projects.html',
			controller: 'projectController',
			resolve: {
				success: function (User) {
					return User.checkSession();
				}
			}
		}).when('/projects/:userID/:projectID/:providerType/',{
			templateUrl: 'templates/projectInit.html',
			controller: 'projectInitController',
			resolve:{ 
				success: function (User) {
					return User.checkSession();
				}
			}
		}).when('/projects/:userID/:projectID/:providerType/docker/',{
			templateUrl: 'templates/dockerDashboard.html',
			controller: 'vboxController',
			resolve : {
				provider : function(){
					return 'docker';
				},
				success: function (User) {
					return User.checkSession();
				}
			}
		}).when('/projects/:userID/:projectID/:providerType/virtualbox/',{
			templateUrl: 'templates/vboxDashboard.html',
			controller: 'vboxController',
			resolve : {
				provider : function(){
					return 'virtualbox';
				},
				success: function (User) {
					return User.checkSession();
				}
			}
		}).otherwise({
			redirectTo : '/login/'
		});

		$httpProvider.interceptors.push(function ($q,$location) {
			return {
				'responseError': function (rejection) {
					if (rejection.status === 401) {
						$location.path('/login/');
					}
					return $q.reject(rejection);
				}
			};
		});

	}
	])