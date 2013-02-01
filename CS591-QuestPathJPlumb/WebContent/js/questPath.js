(function() {

	window.jsPlumbDemo = {

			init : function() {			

				var color = "black";
				jsPlumb.importDefaults({
					// notice the 'curviness' argument to this Bezier curve.  the curves on this page are far smoother
					// than the curves on the first demo, which use the default curviness value.			
					Connector : [ "Bezier", { curviness:40 } ],
					DragOptions : { cursor: "pointer", zIndex:2000 },
					PaintStyle : { strokeStyle:color, lineWidth:3 },
					EndpointStyle : { radius:3, fillStyle:color },
					HoverPaintStyle : {strokeStyle:"#ec9f2e" },
					EndpointHoverStyle : {fillStyle:"#ec9f2e" },			
					Anchors :  [ "AutoDefault", "AutoDefault" ]
				});


				// declare some common values:
				var arrowCommon = { foldback:0.7, fillStyle:color, width:14 },
				// use three-arg spec to create two different arrows with the common values:
				overlays = [
				            [ "Arrow", { location:0.5 }, arrowCommon ]
				            ];

				for (var i = 0; i < quests.length; i++) {
					for (var j = 0; j < quests[i].questPathItems.length; j++) {
						for (var k = 0; k < quests[i].questPathItems[j].childContent.length; k++) {
							jsPlumb.connect({source:i + '-' + quests[i].questPathItems[j].name,  
								target:i + "-" + quests[i].questPathItems[j].childContent[k], overlays:overlays});
						}
					}
				}

				jsPlumb.draggable(jsPlumb.getSelector(".window"));
			}
	};

})();

function waitForDependencies2() {    
	if (typeof jQueryLoaded === 'undefined' || typeof questsLoaded === 'undefined' || typeof jsPlumbLoaded === 'undefined'
		|| typeof uiMinLoaded === 'undefined' || typeof uiTouchLoaded === 'undefined') {        
		setTimeout(waitForDependencies2, 1);    }    
	else {
		//window.onload = function() { moveItems(); jsPlumbDemo.init(); };
		alert("Beginning Process");
		moveItems(); jsPlumbDemo.init();
	}
}

waitForDependencies2();

function moveItems() {
	var topX = [113, 176, 373, 214, 206, 333, 198, 375,  ];
	var leftX =  [293, 223, 310, 95, 485, 466, 788, 594 ];
	var k = 0;
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