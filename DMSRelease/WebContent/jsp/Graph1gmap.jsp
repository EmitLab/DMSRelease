<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
 <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <style>
      html, body {
        height: 100%;
        width: 100%;
        margin: 0;
        padding: 0;
      }
      #map_canvas {
        width: 900px;
   		height: 600px;}
      }
      

    </style>
   <script src="/DMS/js/sys/jquery-3.1.1.min.js"></script>
   <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBDbMBHuxOjxuLNKqibX_XQhhcpeWxhrRQ&sensor=true"></script>
   <script src="/DMS/js/sys/Json2.js"></script>
   <script src="/DMS/js/dev/createXmlRequest.js"></script>
   <script type="text/javascript" src="/DMS/js/sys/infobox.js"></script>
   
<title>Graph 1</title>
<script type="text/javascript">

var interstate = "";

var statejson=""; 
var edge="";

var map;
var markers = new Array();
var j =0;
var redline;
var path;
var infowindow; 
var count = 0;
var infoWindowStatus = new Array();
var infoWindow = new Array();
var infobox = new Array();
var infoboxMarker;

function initMap(){
	 var myOptions = {
		        zoom: 4,
		        center: new google.maps.LatLng(39.5, -98.35),
		        mapTypeId: 'terrain'
		    };	
	map = new google.maps.Map($('#map_canvas')[0], myOptions);
	
	redline = new google.maps.Polyline({
	    strokeColor: "#FF0000",
	    strokeOpacity: 1.0,
	    strokeWeight: 5
    });

	//infowindow = new google.maps.InfoWindow();   

	createMarkers();
	createPolylines();
	
  }
      

function createMarkers(){
	var myMarkers = new Array();
	  for (var i = 0; i < statejson.length; i++) {
	       		var latlng = new google.maps.LatLng(statejson[i].pos.lat, statejson[i].pos.lng);
	       		if(statejson[i].interest==1){
	       			
	       			myMarkers[i] = new google.maps.Marker({
		                position: latlng,
		               
			         //label:""+statejson[i].name[0],
			         icon:"http://maps.google.com/mapfiles/ms/icons/green-dot.png",
			         myID: i,
			         //title:statejson[i].name,
		                map: map
		            });
	       		}else{
	       			myMarkers[i] = new google.maps.Marker({
		                position: latlng,
		                icon:"http://maps.google.com/mapfiles/ms/icons/red-dot.png",
			         //label:""+statejson[i].name[0],
			         myID: i,
		           //  title:statejson[i].name,
			        map: map
		            });
	       		}
	    
	       		myMarkers[i].addListener('mouseover', function() {
	       			var id=this.myID;
	       			var population = statejson[id].population ;
	       		 	var area = statejson[id].area;
	       		 	var contents = "Population: " + population + "\nArea: " + area ;
	       		 	var position = myMarkers[id].getPosition();
	       		 	
	       		 	
	     			printMarkerWindow(contents, position, id);
	       		 	
	   	    	 //alert("i: "+i);
	       		});
	       		myMarkers[i].addListener('mouseout', function() {
		       		infoboxMarker.close();
		       		});
	   
	 }
 
}

function printMarkerWindow(contents, latlong, i){
	
	var boxText = document.createElement("div");
   boxText.style.cssText = "background: #444; color: #FFF;";
   boxText.innerHTML = contents.toString();
   
   var myOptions = {
  		 content: boxText
  		,boxStyle: {
       	opacity: 0.75,
       	 border: "2px solid black",
       	 fontSize: "9pt",
       	 fontWeight: "bold",
       	 //font-family:"Arial, Helvetica, sans-serif",
       	 width: "60px",
       	textAlign: "center"
       }
   		,id: infobox
  		,disableAutoPan: true
  		,pixelOffset: new google.maps.Size(10, 10)
  		,position: latlong
  		,closeBoxURL: ""
  		,isHidden: false
  		,pane: "floatPane"
  		,enableEventPropagation: true
  		,alignBottom: true
  		
  	};
	infoboxMarker = new InfoBox(myOptions)
	infoboxMarker.open(map);
	
	

}

var array = new Array();
function createPolylines()
{		
	
	var neighbour =new Array();
    var myPolyline= new Array();

for(var i=0 ; i<edge.length;i++){
var sourcelat = statejson[edge[i].source].pos.lat;
var sourcelng = statejson[edge[i].source].pos.lng;
var targetlat = statejson[edge[i].target].pos.lat;
var targetlng = statejson[edge[i].target].pos.lng;
neighbour[0]=new google.maps.LatLng(sourcelat,sourcelng );
neighbour[1]=new google.maps.LatLng(targetlat,targetlng);
	  myPolyline[i] = new google.maps.Polyline({
		    path: neighbour,
		    geodesic: false,
		    strokeColor: "#0000CD",
		    strokeOpacity: .5,
		    strokeWeight: 6,
		    myID: i
		  });
	  myPolyline[i].setMap(map);
	  
	infoWindowStatus[i] = 0; 

	  google.maps.event.addListener(myPolyline[i],'mouseover',function(){
    	var i = this.myID;
    	myPolyline[i].setOptions({strokeOpacity: 1.0});
 
    });
	  google.maps.event.addListener(myPolyline[i],'mouseout',function(){
	    	var i = this.myID;
	    	myPolyline[i].setOptions({strokeOpacity: .5});
	 
	    });
    google.maps.event.addListener(myPolyline[i],'click',function(){
    	var i = this.myID;
    	var sourcelat = statejson[edge[i].source].pos.lat;
    	var sourcelng = statejson[edge[i].source].pos.lng;
    	var targetlat = statejson[edge[i].target].pos.lat;
    	var targetlng = statejson[edge[i].target].pos.lng;
    	 var mypointlng = (sourcelng + targetlng)/2;
    	 var mypointlat = (sourcelat + targetlat)/2;
    	 var midlatlong =new google.maps.LatLng(mypointlat,mypointlng);
    if(infoWindowStatus[i] == 0)
    {
   
    	//weight in infoBox 
    	var contents = edge[i].weight 
    	printwindow(contents,midlatlong,i);
    	myPolyline[i].setOptions({strokeColor: "#ff0000"});
    	infoWindowStatus[i] = 1;
    }
    else if(infobox[i].close() == true)
    {
    	myPolyline[i].setOptions({strokeColor: "#ff0000"});
    	infoWindowStatus[i] = 1;
    }
    else
    {   
    	myPolyline[i].setOptions({strokeColor: "#0000CD"});
    	infobox[i].close();
    	infoWindowStatus[i] = 0;
   	}
    });
}

}
function lineClick(line)
{
    alert("Line clicked with myID=" + line.myID);
}

function printwindow(contents, midlatlong,i){
	
	var boxText = document.createElement("div");
    boxText.style.cssText = "background: #444; color: #FFF;";
    boxText.innerHTML = contents.toString();
    
    var myOptions = {
   		 content: boxText
   		,boxStyle: {
   			//background: "url('http://google-maps-utility-library-v3.googlecode.com/svn/trunk/infobox/examples/tipbox.gif') no-repeat",
        	opacity: 0.75,
        	 border: "2px solid black",
        	 fontSize: "12pt",
        	 fontWeight: "bold",
        	 //font-family:"Arial, Helvetica, sans-serif",
        	 width: "50px",
        	textAlign: "center"
        }
   		,disableAutoPan: true
   		,pixelOffset: new google.maps.Size(-25, 10)
   		,position: midlatlong
   		,closeBoxURL: ""
   		,isHidden: false
   		,pane: "floatPane"
   		,enableEventPropagation: true
   		,alignBottom: true
   		
   	};
	infobox[i] = new InfoBox(myOptions);
   	infobox[i].open(map);
	

}


function catchGraph(graphfile1){
	
	 var par = window.location.search.substring(1).split("=")[1];
	 
	 reqgraph= CreateXmlHttpReq(graphHandler);
		var GetMap = '/DMS/GetMap?ClusterRequest=map&graph=' + graphfile1+"&intState="+par;	//https://hive.asu.edu:8443https://hive.asu.edu:8443
		reqgraph.open("GET", GetMap);
		reqgraph.send(null);
	//initMap();
}

function graphHandler() {
	  if (reqgraph.readyState == 4 && reqgraph.status == 200) {
		  
		  var JSONrsp = JSON.parse(reqgraph.responseText);
		 
		  statejson = JSONrsp.statejson1;
		  edge = JSONrsp.edge;
		  initMap();
	  }
}

</script>
</head>
<body onload="catchGraph('Graph1.json')">
<div id="map_canvas"></div>
</body>
</html>