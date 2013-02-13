<!DOCTYPE HTML>
<%@page import="java.util.*"%>
<%@page import="blackboard.platform.plugin.PlugInUtil"%>
<%@page import="com.jsu.cs521.questpath.buildingblock.util.*"%>
<%@page import="com.jsu.cs521.questpath.buildingblock.object.*"%>
<%@page import="com.jsu.cs521.questpath.buildingblock.engine.*"%>
<%@page import="com.jsu.cs591.web.util.QPAttributes"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<bbNG:modulePage type="personalize" ctxId="ctx">
<%
		Processor proc = new Processor();
		proc.QPDriver(ctx);
		String cssPath1 = PlugInUtil.getUri("dt", "questpathblock",	"css/questPath.css");
		String jQuery = PlugInUtil.getUri("dt", "questpathblock", "js/jquery.min.js");
		String jQueryui = PlugInUtil.getUri("dt", "questpathblock", "js/jquery-ui.min.js");
		String jsPlumb = PlugInUtil.getUri("dt", "questpathblock", "js/jquery.jsPlumb-1.3.16-all-min.js");
		String jsTouch = PlugInUtil.getUri("dt", "questpathblock", "js/jquery.ui.touch-punch.min.js");
		String questPath = PlugInUtil.getUri("dt", "questpathblock","js/questPath.js");
		String json2 = PlugInUtil.getUri("dt", "questpathblock","js/json2.js");
%>
<bbNG:cssFile href="<%=cssPath1%>" />
<bbNG:pageHeader>
<bbNG:pageTitleBar title="Questpath Configuration"></bbNG:pageTitleBar>
</bbNG:pageHeader>
<bbNG:form action="questpath_save.jsp" method="post" isSecure="false">
	<div id="questpathBlockContainer" class="mainDiv">
	<%
		int j = 0;
		for (QuestPath qp : proc.qPaths) {
	%>
	<div id="<%="QP" + j%>" class="questItem questName"><%=qp.getQuestName()%></div>
	<%
			for (QuestPathItem qpI : qp.getQuestPathItems()) {
				QPAttributes qpAtt = new QPAttributes(qpI);
	%>
			<div id="<%=j + "-"	+ qpI.getName().replace(" ", "_")%>"
				class="questItem <%=qpAtt.getStatusClassName()%>"
				title="<%=qpAtt.getTitle()%>" <%if (!qpI.isLocked()) {%>
			<%}%>><%=qpI.getName()%></div>
	<%
			}
			j++;
		}
	%>
<bbNG:jsBlock>
<script type="text/javascript">
	<%String questString = "";
	for (QuestPath quest : proc.qPaths) {
		for (QuestPathItem qItem : quest.getQuestPathItems()) {
			qItem.setContentId(null);
			qItem.setName(qItem.getName().replace(" ","_"));
			List<String> cc = new ArrayList<String>();
			for (String s : qItem.getChildContent()) {
				cc.add(s.replace(" ", "_"));
			}
			qItem.setChildContent(cc);
			List<String> pc = new ArrayList<String>();
			for (String s : qItem.getParentContent()) {
				pc.add(s.replace(" ", "_"));
			}
			qItem.setParentContent(pc);
		}
	}
	questString = proc.qpUtil.toJson(proc.qPaths);%>
	var quests = <%=questString%>;
	var questsLoaded = true;
</script>
</bbNG:jsBlock>
<bbNG:jsFile href="<%=jQuery%>"/>
<bbNG:jsFile href="<%=jQueryui%>"/>
<bbNG:jsFile href="<%=jsPlumb%>"/>
<bbNG:jsFile href="<%=jsTouch%>"/>
<bbNG:jsFile href="<%=json2%>"/>
<bbNG:jsFile href="<%=questPath%>"/>
<div class="legend">
	<h5>LEGEND</h5>
	<div class="legendColor passed">Passed</div>
	<div class="legendColor failed">Attempted</div>
	<div class="legendColor locked">Locked</div>
	<div class="legendColor unlocked">Unlocked</div>
</div>
<div class="saveButton">
<button type="button" onclick="setLocation();">Confirm Layout</button>
</div>
</div>
	<bbNG:dataCollection>
		<bbNG:step title="QuestPath Configuration 1" >
			<input type="text" id="testVar" name="testVar" value='<%=proc.qLayout%>' />
			<input type="hidden" name="course_id" value="<%=request.getParameter("course_id")%>" />
		</bbNG:step>
		<bbNG:stepSubmit />
	</bbNG:dataCollection>
</bbNG:form>
</bbNG:modulePage>