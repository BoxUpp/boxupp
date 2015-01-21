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
var activeScript = 0;
var firstRun = true;
//var flatData;
var scriptMappings = {}

$('body').on('click','#staticSection, button[id*="editScript"]',function(i){
	var divPosition = $('.consoleContent').css('right');
	if(divPosition === '-545px'){
		$(".consoleContent").animate({ right:'61px' }, {duration:1500,specialEasing:{right:"easeInOutQuint"}, complete: function(){
			$("#staticSection span").removeClass('closed');
			$("#staticSection span").addClass('open');
		}});		
	}
	else{
		$(".consoleContent").animate({ right:'-545px' }, {duration:1500,specialEasing:{right:"easeInOutQuint"}, complete: function(){
			$("#staticSection span").removeClass('open');
			$("#staticSection span").addClass('closed');
		}});
	}
});

function animateArrow(){
	var effectCounter = 4;
	for(var counter=1; counter<= effectCounter; counter++){
		$(".graph-arrow").animate({'opacity':1},1000);
		$(".graph-arrow").animate({'opacity':0},400);
	}
	firstRun = false;
}

$('body').on('click','.provisioningControl',function(event){
	$('.provisioningControl').each(function(){
		$(this).removeClass('activeProvisionControl');
	});
	var sectionButtonClicked = $(this).attr('data-provSection');
	$(this).addClass('activeProvisionControl');
	selectProvisioningControlSec(sectionButtonClicked);

});

$("body").on('click','.boxuppNav',function(event){
	$('.boxuppNav').each(function(){
		$(this).removeClass('activeNav');	
	});
	var navButtonClicked = $(this).attr('data-navIndex');
	$(this).addClass('activeNav');
	selectBoxuppNavTab(navButtonClicked);
});

function selectBoxuppNavTab(navigateToTab){
	$('.container,#sidebar-content').children('.TabbedPanelsContent').each(function(i){
		$(this).css("display","none");
		$(this).removeClass('activeBoxuppNav');
	});

	$('#sidebar-content').children('.TabbedPanelsContent:eq('+ navigateToTab +')').each(function(i){
		$(this).css("display","block");
	});
	
	$('.container').children('.TabbedPanelsContent:eq('+ navigateToTab +')').each(function(i){
		$(this).css("display","block");
	});
	//refreshNodesWindow();
}

function selectProvisioningControlSec(sectionButtonClicked){
	$('.provisioningConsole').children('.provisioningConsoleSection').each(function(i){
		$(this).css("display","none");
	});
	$('.provisioningConsole').children('.provisioningConsoleSection:eq('+ sectionButtonClicked +')').each(function(i){
		$(this).css("display","block");
	});
	//refreshNodesWindow();
}
