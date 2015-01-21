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
/*
 *  Angular LoadScript
 *
 *  Let angular load and execute lazy javascript from partials!
 *  
 *  This module is the result of  this issue: "1.2.0rc1 regression: script tags not loaded via ngInclude"
 *  Issue url: https://github.com/angular/angular.js/issues/3756
 * 
 *  As of Angular 1.2.0 the ngInclude scripts does not permit execution of javascript from included partials.
 *  This little module execute code inside script tags with "javascript-lazy" attribute after partial loading,
 *  thus re-enabling this feature.
 *
 *  ( please have a look at the issue comments, this angular feature was never planned nor included properly,
 *  was only a drawback of using jQuery for partial inclusion )
 * 
 *  This angular module have been created by @subudeepak(https://github.com/subudeepak) based on the code posted by @endorama (https://github.com/endorama) 
 *  (based upon the code
 *  posted by @olostan (https://github.com/olostan) )
 * 
 *  Simply add this file, load ngLoadScript module as application dependency and use type="text/javascript-lazy"
 *  as type for script you which to load lazily in partials.
 * 
 * License: 2013 - released to the Public Domain.
 */
 
/*global angular */
(function (ng) {
  'use strict';
 
  var app = ng.module('ngLoadScript', []);
 
  app.directive('script', function() {
    return {
      restrict: 'E',
      scope: false,
      link: function(scope, elem, attr) 
      {
        
        if (attr.type==='text/javascript-lazy') 
        {
          var s = document.createElement("script");
          s.type = "text/javascript";                
          var src = elem.attr('src');
          if(src!==undefined)
          {
              s.src = src;
          }
          else
          {
              var code = elem.text();
              s.text = code;
          }
          document.head.appendChild(s);
          elem.remove();
        }
      }
    };
  });
 
}(angular));