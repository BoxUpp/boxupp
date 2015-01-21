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
$('body').on('keyup','#vagrantFileModal input',function(){
		var newLength = ($(this).val().length);
		$(this).prop('size', newLength);
});

$("#vagrantFileModal").on('show.bs.modal',function(){
	$("#vagrantFileModal input").each(function(){
		if($(this).val().length > 0){
			$(this).trigger('keyup');
		}
	});
});

function loaderIntro(){
	$('#lightBox').show();
	$('#loaderWindow').show();
	setTimeout(function(){
		$('#loaderWindow').hide();
		//$('#lightBox').animate({opacity:0},1500);
		$("#lightBox").animate({opacity:0}, {duration:1000, complete: function(){
			$('#lightBox').hide();
		}});
	},6000);
}

$('body').on('click','#invokeShellConsole,button.shellConsoleClose',function(){
	if($("#staticSection span").hasClass('closed')){
		$("#staticSection").click();
	}
});