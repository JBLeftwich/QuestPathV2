<!DOCTYPE HTML>
<!-- 
	Gamegogy Quest Path 1.0
    Copyright (C) 2012  David Thornton

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Author: Jonathan Leftwich  Graduate Student at Jacksonville State University
-->
<%@page import="blackboard.platform.plugin.PlugInUtil"%>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<%@page import="com.jsu.cs521.questpath.buildingblock.util.*" %>
<%@page import="com.jsu.cs521.questpath.buildingblock.object.*" %>
<%@page import="com.jsu.cs521.questpath.buildingblock.engine.*" %>
<%@page import="com.jsu.cs591.web.util.QPAttributes"%>
<%@ taglib uri="/bbData" prefix="bbData"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<!-- for tags -->
<%-- <bbData:context id="ctx">  --%>
<bbNG:includedPage ctxId="ctx">
	<!-- to allow access to the session variables -->
	<%
			String cssPath1 = PlugInUtil.getUri("dt", "questpathblock",	"css/questPath.css");
			String htcPath1 = PlugInUtil.getUri("dt", "questpathblock",	"htc/PIE.htc");
			Processor proc = new Processor();
			proc.QPDriver(ctx);
	%>
<bbNG:cssFile href="<%=cssPath1%>"/>
<bbNG:cssBlock>
<style>
.questItem {
	behavior: url(<%=htcPath1 %>);
	}
</style>
</bbNG:cssBlock>
<%-- <jsp:include page="ScriptFile.jsp" /> --%>
<body>
<div id="questpathBlockContainer" class="mainDiv">
<%
	int j = 0;
	List<String> procQI = new ArrayList<String>();
	for (QuestPath qp : proc.qPaths) { 
		for (QuestPathItem qpI : qp.getQuestPathItems()) {
			if (!procQI.contains(qpI.getExtContentId())) {
			QPAttributes qpAtt = new QPAttributes(qpI);
%>
			<div id="<%=qpI.getExtContentId()%>" class="questItem <%=qpAtt.getStatusClassName() %>"
			title="<%=qpAtt.getTitle()%>" 
			<% if (!qpI.isLocked()) { %>
			ondblclick="openAssignment('execute/uploadAssignment?content_id=<%=qpI.getExtContentId()%>&course_id=<%=ctx.getCourseId().toExternalString()%>&assign_group_id=&mode=view');"
			<%} %>><%=qpI.getName()%></div>
		<%
			procQI.add(qpI.getExtContentId());
			}
		}
		j++;
	}
%>
<bbNG:jsBlock>
<script type="text/javascript">
<%
String questString = proc.qpUtil.toJson(proc.qPaths);%>
var quests = <%=questString%>;
var questLayout = <%=proc.qLayout%>;
var questTier = <%=proc.questTier%>;
var questsLoaded = true;
var questDraggable = false;
</script>
<script type="text/javascript">
<jsp:include page="js/jquery.min.js" />
<jsp:include page="ScriptFile.jsp" />
<jsp:include page="js/jquery.jsPlumb-1.3.16-all-min.js" />
<jsp:include page="js/jquery.ui.touch-punch.min.js" />
<jsp:include page="js/questPath.js" />
</script>
</bbNG:jsBlock>
<div class="legend"><h5>LEGEND</h5>
<div class="legendColor passed">Passed</div>
<div class="legendColor unlockedLegend">Unlocked</div>
<div class="legendColor locked">Locked</div>
</div>
</div>
</body>
</bbNG:includedPage>