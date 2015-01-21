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
ScriptMapping = function(data){
	$("#scriptMapping").empty();
	this.data = data;
	color = d3.scale.category10();
var width = 600,
    height = 150;

var cluster = d3.layout.cluster()
    .size([height, width - 160]);

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });

var svg = d3.select("#scriptMapping").append("svg")
    .attr("width", width)
    .attr("height", height)
  .append("g")
    .attr("transform", "translate(40,0)");


//d3.json("js/src/graphFiles/a.json", function(error, root) {
  var nodes = cluster.nodes(data),
      links = cluster.links(nodes);

  var link = svg.selectAll(".link")
      .data(links)
    .enter().append("path")
      .attr("class", "link")
      .attr("d", diagonal);
    

  var node = svg.selectAll(".node")
      .data(nodes)
   	  .enter().append("g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });
     

  node.append("circle")
      .attr("r", 4.5)
      .on('mouseover', function(d) {if(d.name){showData(this, d.name);}else{showData(this, d.vagrantID+"<br />"+d.hostName);}})
      .on('mouseout', function(d) {hideData();});
      

  node.append("text")
      .attr("dx", function(d) { return d.children ? -8 : 8; })
      .attr("dy", 3)
      .style("text-anchor", function(d) { return d.children ? "end" : "start"; })
      .text(function(d) { if(d.name){return d.name;}else{return d.vagrantID;} });
//});

d3.select(self.frameElement).style("height", height + "px");
$("#scriptMapping").append("<div class='infobox' style='display:none;'></div>");

}
ModuleMapping = function(data){
	$("#moduleMapping").empty();
	this.data = data;
var width = 500,
    height = 150;

var cluster = d3.layout.cluster()
    .size([height, width - 160]);

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });

var svg = d3.select("#moduleMapping").append("svg")
    .attr("width", width)
    .attr("height", height)
  .append("g")
    .attr("transform", "translate(40,0)");

//d3.json("js/src/graphFiles/a.json", function(error, root) {
  var nodes = cluster.nodes(data),
      links = cluster.links(nodes);

  var link = svg.selectAll(".link")
      .data(links)
    .enter().append("path")
      .attr("class", "link")
      .attr("d", diagonal);

  var node = svg.selectAll(".node")
      .data(nodes)
   	  .enter().append("g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
      .on('mouseover', function(d) {if(d.name){showData(this, d.name+"<br />"+d.moduleID);}else{showData(this, d.vagrantID+"<br />"+d.hostName);}})
      .on('mouseout', function(d) {hideData();});

  node.append("circle")
      .attr("r", 4.5);

  node.append("text")
      .attr("dx", function(d) { return d.children ? -8 : 8; })
      .attr("dy", 3)
      .style("text-anchor", function(d) { return d.children ? "end" : "start"; })
      .text(function(d) { if(d.name){return d.name;}else{return d.vagrantID;} });
//});

d3.select(self.frameElement).style("height", height + "px");

$("#moduleMapping").append("<div class='infobox' style='display:none;'></div>");
}
function showData(obj, d) {
	var coord = d3.mouse(obj);
 	var infobox = d3.select(".infobox");
 	// now we just position the infobox roughly where our mouse is
 	infobox.style("left", ((coord[0])+ 250) +"px" );
 	infobox.style("top",  ((coord[1]) + 175) + "px");
 	$(".infobox").html(d);
 	$(".infobox").show();
}
 
function hideData() {
 	$(".infobox").hide();
} 