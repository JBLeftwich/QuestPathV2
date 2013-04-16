/**
 * Main Script for Quest Path Building Block
 * Author: Jonathan Leftwich  Graduate Student at Jacksonville State University
 */
/*
 * Initial function to draw connectors after quest items have been put in place
 */
(function() {

	window.jsPlumbDemo = {

			init : function() {			
				//jsPlumb.setRenderMode(jsPlumb.VML);

				var color = "black";
				jsPlumb.importDefaults({
					Connector : [ "Bezier", { curviness:40 } ],
					DragOptions : { cursor: "pointer", zIndex:2000 },
					PaintStyle : { strokeStyle:color, lineWidth:3 },
					EndpointStyle : { radius:3, fillStyle:color },
					HoverPaintStyle : {strokeStyle:"black" },
					EndpointHoverStyle : {fillStyle:"#ec9f2e" },			
					Anchors :  [ "AutoDefault", "AutoDefault" ]
				});



				var arrowCommon = { foldback:1.0, fillStyle:color, width:14 },

				overlays = [[ "Arrow", { location:0.5 }, arrowCommon ]];

				var procQuests = new Array();
				for (var i = 0; i < quests.length; i++) {
					for (var j = 0; j < quests[i].questPathItems.length; j++) {
						if (jQueryAlias.inArray(quests[i].questPathItems[j].extContentId, procQuests) < 0) {
							for (var k = 0; k < quests[i].questPathItems[j].childContent.length; k++) {
								jsPlumb.connect({source:quests[i].questPathItems[j].extContentId,  
									target:quests[i].questPathItems[j].childContent[k], overlays:overlays});
							}
						}
						procQuests.push(quests[i].questPathItems[j].extContentId);
					}
				}
				
				if (questDraggable) {
					jsPlumb.draggable(jsPlumb.getSelector(".questItem"),{containment:"parent"});
				}
			}
	};

})();

/*
 * Move quest items based on saved layout
 */
function moveItems() {
	if (questLayout != null) {
		var initWidth = questLayout.width;
		var currentWidth = document.getElementById('questpathBlockContainer').offsetWidth;
		var widthRatio = currentWidth/initWidth;
		try {
			for (var i = 0; i < questLayout.qItemLayout.length; i++) {
				var x = document.getElementById(questLayout.qItemLayout[i].extContentId);
				x.style.top = (parseInt(questLayout.qItemLayout[i].top) * 1) + "px";
				x.style.left = (parseInt(questLayout.qItemLayout[i].left) * widthRatio) + "px";
			}
		} catch(exception) {initLayout();}//default to init layout if unable to build layout
	} else {
		initLayout();
	}
};

/*
 * Pause for other script files to be loaded
 */
function waitForDependencies() {    
	if (typeof jQueryLoaded === 'undefined' || typeof questsLoaded === 'undefined' || typeof jsPlumbLoaded === 'undefined'
		|| typeof uiMinLoaded === 'undefined' || typeof uiTouchLoaded === 'undefined') {        
		setTimeout(waitForDependencies, 0200);}    
	else {
		jsPlumb.bind("ready", function() {initLayout(); moveItems(); setTimeout(jsPlumbDemo.init, 0200);});
	}
}

waitForDependencies();

function openAssignment(link) {
	urlLoc = window.location;
	if (urlLoc.toString().indexOf('detach_module') !== -1) {
		window.location.href = '../../../blackboard/' + link;	
	}
	else {window.location.href = '../../' + link;}

}

/*
 * Set initial location, this is used by config page
 */
function setLocation() {
	var qLayout = new Object();
	qLayout.height = document.getElementById('questpathBlockContainer').offsetHeight;
	qLayout.width = document.getElementById('questpathBlockContainer').offsetWidth;
	qLayout.qItemLayout = new Array();
	var k = 0;
	for (var i = 0; i < quests.length; i++) {
		for (var j = 0; j < quests[i].questPathItems.length; j++) {
			var qItem = new Object();
			qItem.extContentId = quests[i].questPathItems[j].extContentId;
			//qItem.top = document.getElementById(i + '-' + quests[i].questPathItems[j].name).style.top;
			//qItem.left = document.getElementById(i + '-' + quests[i].questPathItems[j].name).style.left;
			qItem.top = document.getElementById(quests[i].questPathItems[j].extContentId).style.top;
			qItem.left = document.getElementById(quests[i].questPathItems[j].extContentId).style.left;
			qLayout.qItemLayout[k] = qItem;
			k++;
		}
	} 
	document.getElementById("questLayout").value = JSON.stringify(qLayout);
}

/*
 * Redraw as page is resized
 */
window.onresize=function(){moveItems(); jsPlumbDemo.init();};

/*
 * Build initial layout graph
 */
function initLayout() {
	init_height = document.getElementById('questpathBlockContainer').offsetHeight;
	init_width = document.getElementById('questpathBlockContainer').offsetWidth;
	pathWidth = init_width/questTier.length;
	for (var i = 0; i < questTier.length; i++) {
		for (var j = 0; j < questTier[i].length; j++) {
			tierWidth = (pathWidth/questTier[i][j].tier.length);
			tierWidthCenter = tierWidth/2;
			for (var k = 0; k < questTier[i][j].tier.length; k++) {
				var x = document.getElementById(questTier[i][j].tier[k]);
				x.style.left = pathWidth * i  + (tierWidth * (k +1)) - tierWidthCenter  - (init_width/20) + "px";
				x.style.top =  init_height/questTier[i].length * j + "px";
			}
		}
	}
}

//jsPlumb.bind("ready", function() {moveItems(); jsPlumbDemo.init();});