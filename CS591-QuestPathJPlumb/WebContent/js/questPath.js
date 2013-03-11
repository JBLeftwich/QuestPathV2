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

				for (var i = 0; i < quests.length; i++) {
					for (var j = 0; j < quests[i].questPathItems.length; j++) {
						for (var k = 0; k < quests[i].questPathItems[j].childContent.length; k++) {
							if ((quests[i].questPathItems[j].passed && quests[i].questPathItems[j].unLocked) || 
									(!quests[i].questPathItems[j].gradable && quests[i].questPathItems[j].unLocked)) {
								jsPlumb.connect({source:i + '-' + quests[i].questPathItems[j].name,  
									target:i + "-" + quests[i].questPathItems[j].childContent[k], overlays:overlays});
							} else {
								jsPlumb.connect({source:i + '-' + quests[i].questPathItems[j].name,  
									target:i + "-" + quests[i].questPathItems[j].childContent[k], overlays:overlays});
							}
						}
					}
				}

				if (questDraggable) {
					jsPlumb.draggable(jsPlumb.getSelector(".questItem"),{containment:"parent"});
				}
			}
	};

})();

function moveItems() {
	if (questLayout != null) {
		var initWidth = questLayout.width;
		var currentWidth = document.getElementById('questpathBlockContainer').offsetWidth;
		var widthRatio = currentWidth/initWidth;
		try {
			for (var i = 0; i < questLayout.qItemLayout.length; i++) {
				try {
					var x = document.getElementById(questLayout.qItemLayout[i].name);
					x.style.top = (parseInt(questLayout.qItemLayout[i].top) * 1) + "px";
					x.style.left = (parseInt(questLayout.qItemLayout[i].left) * widthRatio) + "px";
				} catch(exception) {continue;}
			}
		} catch(exception) {}
	} else {
		initLayout();
	}
};

function waitForDependencies() {    
	if (typeof jQueryLoaded === 'undefined' || typeof questsLoaded === 'undefined' || typeof jsPlumbLoaded === 'undefined'
		|| typeof uiMinLoaded === 'undefined' || typeof uiTouchLoaded === 'undefined') {        
		setTimeout(waitForDependencies, 1);}    
	else {
		jsPlumb.bind("ready", function() {moveItems(); jsPlumbDemo.init();});
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

function setLocation() {
	var qLayout = new Object();
	qLayout.height = document.getElementById('questpathBlockContainer').offsetHeight;
	qLayout.width = document.getElementById('questpathBlockContainer').offsetWidth;
	qLayout.qItemLayout = new Array();
	var k = 0;
	for (var i = 0; i < quests.length; i++) {
		var qItem = new Object();
		qItem.name = "QP" + i;
		qItem.top = document.getElementById("QP" + i).style.top;
		qItem.left = document.getElementById("QP" + i).style.left;
		qLayout.qItemLayout[k] = qItem;
		k++;
		for (var j = 0; j < quests[i].questPathItems.length; j++) {
			var qItem = new Object();
			qItem.name = i + '-' + quests[i].questPathItems[j].name;
			qItem.top = document.getElementById(i + '-' + quests[i].questPathItems[j].name).style.top;
			qItem.left = document.getElementById(i + '-' + quests[i].questPathItems[j].name).style.left;
			qLayout.qItemLayout[k] = qItem;
			k++;
		}
	} 
	document.getElementById("testVar").value = JSON.stringify(qLayout);
//	alert(document.getElementById("testVar").value);
}

window.onresize=function(){moveItems(); jsPlumbDemo.init();};

function initLayout() {
	init_height = document.getElementById('questpathBlockContainer').offsetHeight;
	init_width = document.getElementById('questpathBlockContainer').offsetWidth;
	pathWidth = init_width/questTier.length;
	for (var i = 0; i < questTier.length; i++) {
		var x = document.getElementById("QP" + i);
		x.style.left = pathWidth * i + "px";
		x.style.top = 40 + "px";  //TODO change to calculate 1/20 of height
		for (var j = 0; j < questTier[i].length; j++) {
			tierWidth = (pathWidth/questTier[i][j].tier.length);
			tierWidthCenter = tierWidth/2;
			for (var k = 0; k < questTier[i][j].tier.length; k++) {
				var x = document.getElementById(i + "-" + questTier[i][j].tier[k]);
				x.style.left = pathWidth * i  + (tierWidth * (k +1)) - tierWidthCenter  - (init_width/20) + "px";
				x.style.top =  init_height/questTier[i].length * j + "px";
				//alert(x.style.left + ":" + x.style.top);
			}
		}
	}
}

//jsPlumb.bind("ready", function() {moveItems(); jsPlumbDemo.init();});