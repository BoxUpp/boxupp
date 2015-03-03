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

ngBoxuppApp.controller('projectInitController',function($scope,$routeParams,Providers,$location,$http,$timeout){


    $scope.bodyStyle.applyDashBoardStyling = false;
    
    $scope.$watch('selectedProvider',function(newVal, oldVal){
        if(newVal === oldVal) return;
        console.info('watch triggered');
        if(newVal.name === 'Docker') {
            //$location.path("#/projects/{{$routeParams.projectID}}/docker");
            var userID = $routeParams.userID;
            var projectID = $routeParams.projectID;
            var providerID = $routeParams.providerType;
            $timeout(function(){
                $location.path("/projects/" + userID + "/" + projectID + "/" + providerID +"/docker/");
            },3000);
        }
        else if(newVal.name === 'AWS') {
            //$location.path("#/projects/{{$routeParams.projectID}}/docker");
            var userID = $routeParams.userID;
            var projectID = $routeParams.projectID;
            var providerID = $routeParams.providerType;
            $timeout(function(){
                $location.path("/projects/" + userID + "/" + projectID + "/" + providerID +"/AWS/");
            },3000);
        }
        else{

            var userID = $routeParams.userID;
            var projectID = $routeParams.projectID;
            var providerID = $routeParams.providerType;
            
            $timeout(function(){
                $location.path("/projects/" + userID + "/" + projectID + "/" + providerID +"/virtualbox/");    
            },3000);
        }
    },true);
    /*
    $http({method: 'GET', url: '/services/resources/providers/1'}).
      success(function(data, status, headers, config) {
            
            $scope.selectedProvider = data;
      }).
      error(function(data, status, headers, config) {
            console.error("Error");
      });*/

	$scope.selectedProvider = Providers.get({id : $routeParams.providerType});
    
    // $scope.providerID = $routeParams.providerType;
    // $scope.selectedProvider = Providers.get({ id: $scope.providerID }, function() {
    //     console.log($scope.selectedProvider);
    // });

    
});