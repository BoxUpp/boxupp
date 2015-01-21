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
angular.module('app', ['angularFileUpload'])

    // The example of the full functionality
    .controller('TestController', function ($scope, $fileUploader) {
        'use strict';

        // create a uploader with options
        var uploader = $scope.uploader = $fileUploader.create({
            scope: $scope,                          // to automatically update the html. Default: $rootScope
            url: $scope.serverAddress + '/services/uploadHandler/upload',
            formData: [
                { customKey: $scope.fetchUploadFolder() }
            ],
            filters: [
                function (item) {                    // first user filter
                    //console.info('filter1');
                    return true;
                }
            ]
        });


        // FAQ #1
        /*var item = {
            file: {
                name: 'Previously uploaded file',
                size: 1e6
            },
            progress: 100,
            isUploaded: true,
            isSuccess: true
        };*/
		var item={};
        item.remove = function() {
            uploader.removeFromQueue(this);
        };
        //uploader.queue.push(item);
        uploader.progress = 100;


        // ADDING FILTERS

        uploader.filters.push(function (item) { // second user filter
            //console.info('filter2');
            return true;
        });

        // REGISTER HANDLERS

        uploader.bind('afteraddingfile', function (event, item) {
            //console.info('After adding a file', item);
        });

        uploader.bind('whenaddingfilefailed', function (event, item) {
            //console.info('When adding a file failed', item);
        });

        uploader.bind('afteraddingall', function (event, items) {
            //console.info('After adding all files', items);
        });

        uploader.bind('beforeupload', function (event, item) {
            //console.info('Before upload', item);
        });

        uploader.bind('progress', function (event, item, progress) {
            //console.info('Progress: ' + progress, item);
        });

        uploader.bind('success', function (event, xhr, item, response) {
            //console.info('Success', xhr, item, response);
        });

        uploader.bind('cancel', function (event, xhr, item) {
            //console.info('Cancel', xhr, item);
        });

        uploader.bind('error', function (event, xhr, item, response) {
            //console.info('Error', xhr, item, response);
        });

        uploader.bind('complete', function (event, xhr, item, response) {
            //console.info('Complete', xhr, item, response);
        });

        uploader.bind('progressall', function (event, progress) {
            //console.info('Total progress: ' + progress);
        });

        uploader.bind('completeall', function (event, items) {
            //console.info('Complete all', items);
        });

    });