(function() {

	window.jsPlumbDemo = {

			init : function() {			

				var color = "black";
				jsPlumb.importDefaults({
					Connector : [ "Bezier", { curviness:40 } ],
					DragOptions : { cursor: "pointer", zIndex:2000 },
					PaintStyle : { strokeStyle:color, lineWidth:3 },
					EndpointStyle : { radius:3, fillStyle:color },
					HoverPaintStyle : {strokeStyle:"#ec9f2e" },
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
								target:i + "-" + quests[i].questPathItems[j].childContent[k], paintStyle:{lineWidth:3, strokeStyle:"#25f300"}, overlays:overlays});
							} else {
								jsPlumb.connect({source:i + '-' + quests[i].questPathItems[j].name,  
									target:i + "-" + quests[i].questPathItems[j].childContent[k], paintStyle:{lineWidth:3, strokeStyle:"#f30e00"}, overlays:overlays});
							}
						}
					}
				}

				jsPlumb.draggable(jsPlumb.getSelector(".window"));
			}
	};

})();

function moveItems() {
	var topX = [113, 176, 373, 214, 206, 333, 198, 375];
	var leftX =  [293, 223, 310, 95, 485, 466, 788, 594 ];
	var k = 0;
//	alert(document.getElementById('questpathBlockContainer').offsetLeft);
//	alert(document.getElementById('questpathBlockContainer').offsetHeight);
	for (var i = 0; i < quests.length; i++) {
		for (var j = 0; j < quests[i].questPathItems.length; j++) {
				var x = document.getElementById(i + '-' + quests[i].questPathItems[j].name);
				x.style.top = topX[k].toString() + 'px';
				x.style.left = leftX[k].toString() + 'px';
				k++;
		}
	}
	var x = document.getElementById('QP0');
	x.style.top = '324px';
	x.style.left = '145px';
	var y = document.getElementById('QP1');
	y.style.top = '249px';
	y.style.left = '638px';
};

function waitForDependencies() {    
	if (typeof jQueryLoaded === 'undefined' || typeof questsLoaded === 'undefined' || typeof jsPlumbLoaded === 'undefined'
		|| typeof uiMinLoaded === 'undefined' || typeof uiTouchLoaded === 'undefined') {        
		setTimeout(waitForDependencies, 1);}    
	else {
		jsPlumb.bind("ready", function() {alert("2"); moveItems(); jsPlumbDemo.init();});
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
jsPlumb.bind("ready", function() {alert("1"); moveItems(); jsPlumbDemo.init();});