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
-->

<%@page import="blackboard.persist.content.impl.ContentDbPersisterImpl"%>
<%@page import="blackboard.persist.content.avlrule.AvailabilityRuleDbPersister"%>
<%@page import="blackboard.data.gradebook.impl.Grade"%>
<%@page import="blackboard.data.content.avlrule.GradeRangeCriteria"%>
<%@page import="blackboard.persist.gradebook.impl.OutcomeDefinitionDbLoader"%>
<%@page import="blackboard.data.gradebook.impl.OutcomeDefinition"%>
<%@page import="blackboard.data.gradebook.impl.Outcome"%>
<%@page import="blackboard.persist.gradebook.impl.OutcomeDbLoader"%>
<%@page import="blackboard.data.content.avlrule.GradeCompletedCriteria"%>
<%@page import="blackboard.data.content.avlrule.AvailabilityCriteria"%>
<%@page import="blackboard.data.content.avlrule.AvailabilityRule"%>
<%@page	import="blackboard.persist.content.avlrule.AvailabilityRuleDbLoader"%>
<%@page import="blackboard.data.content.AggregateReviewStatus"%>
<%@page import="blackboard.data.gradebook.Score"%>
<%@page import="blackboard.data.gradebook.Lineitem"%>
<%@page import="blackboard.persist.gradebook.LineitemDbLoader"%>
<%@page import="blackboard.platform.BbServiceManager"%>
<%@page import="blackboard.data.navigation.CourseToc"%>
<%@page import="blackboard.persist.content.impl.ContentDbLoaderImpl"%>
<%@page import="blackboard.persist.navigation.CourseTocDbLoader"%>
<%@page import="blackboard.platform.coursemap.CourseMapManagerFactory"%>
<%@page import="blackboard.platform.coursemap.impl.CourseMapManagerImpl"%>
<%@page import="blackboard.platform.coursemap.CourseMapManager"%>
<%@page	import="blackboard.platform.coursecontent.CourseContentManagerFactory"%>
<%@page import="blackboard.platform.coursecontent.CourseContentManager"%>
<%@page	import="blackboard.persist.content.avlrule.AvailabilityCriteriaDbLoader"%>
<%@page	import="blackboard.platform.coursecontent.impl.CourseContentManagerImpl"%>
<%@page import="blackboard.platform.content.ContentUserManagerImpl"%>
<%@page import="blackboard.data.content.Content"%>
<%@page import="blackboard.data.content.ContentManager"%>
<%@page import="blackboard.base.*"%>
<%@page import="blackboard.data.course.*"%>
<!-- for reading data -->
<%@page import="blackboard.data.user.*"%>
<!-- for reading data -->
<%@page import="blackboard.persist.*"%>
<!-- for writing data -->
<%@page import="blackboard.persist.course.*"%>
<!-- for writing data -->
<%@page import="blackboard.persist.content.*"%>
<!-- for writing data -->
<%@page import="blackboard.data.coursemap.impl.*"%>
<!-- for writing data -->
<%@page import="blackboard.platform.gradebook2.*"%>
<%@page import="blackboard.platform.gradebook2.impl.*"%>
<%@page import="java.util.*"%>
<!-- for utilities -->
<%@page import="blackboard.platform.plugin.PlugInUtil"%>
<%@page import="com.jsu.cs521.questpath.buildingblock.util.*" %>
<%@page import="com.jsu.cs521.questpath.buildingblock.object.*" %>
<%@page import="com.jsu.cs521.questpath.buildingblock.engine.*" %>
<%@page import="com.jsu.cs591.web.util.QPAttributes"%>
<!-- for utilities -->
<%@ taglib uri="/bbData" prefix="bbData"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<!-- for tags -->
<%-- <bbData:context id="ctx">  --%>
<bbNG:includedPage ctxId="ctx">
	<!-- to allow access to the session variables -->
	<%
		// get the current user
			User sessionUser = ctx.getUser();
			Id courseID = ctx.getCourseId();
			String sessionUserRole = ctx.getCourseMembership()
					.getRoleAsString();
			String sessionUserID = sessionUser.getId().toString();
			CourseManager cm1 = CourseManagerFactory.getInstance();
			Course course = cm1.getCourse(courseID);

			boolean isUserAnInstructor = false;
			if (sessionUserRole.trim().toLowerCase().equals("instructor")) {
				isUserAnInstructor = true;
			}

			String cssPath1 = PlugInUtil.getUri("dt", "questpathblock",	"css/chartDemo.css");
			String jQuery = PlugInUtil.getUri("dt", "questpathblock", "js/jquery.min.js");
			String jQueryui = PlugInUtil.getUri("dt", "questpathblock", "js/jquery-ui.min.js");
			String jsPlumb = PlugInUtil.getUri("dt", "questpathblock", "js/jquery.jsPlumb-1.3.16-all-min.js");
			String jsTouch = PlugInUtil.getUri("dt", "questpathblock", "js/jquery.ui.touch-punch.min.js");
			String questPath = PlugInUtil.getUri("dt", "questpathblock","js/questPath.js");
	%>

<bbNG:cssFile href="<%=cssPath1%>"/>
<body>
<div id="questpathBlockContainer" class="mainDiv">
		<%
			//Load Course Table of Content
				CourseTocDbLoader cTocLoader = CourseTocDbLoader.Default
						.getInstance();
				ContentDbLoader cntDbLoader = ContentDbLoader.Default
						.getInstance();

				List<CourseToc> tList = cTocLoader.loadByCourseId(ctx
						.getCourseId());

				//Create an ArrayList of Content based on the TOC
				List<Content> children = new ArrayList<Content>();
				for (CourseToc t : tList) {
					if (t.getTargetType() == CourseToc.Target.CONTENT) {
						children.addAll(cntDbLoader.loadChildren(
								t.getContentId(), false, null));
					}
				}
				String testString = "";
				for (Content c : children) {
					if (c.getTitle().equalsIgnoreCase("test")
							|| c.getTitle().equalsIgnoreCase("test.txt")) {
						testString = c.getTitle() + " " + c.getBody().getText();
					}
				}

				//Load grades for gradable Lineitems
				LineitemDbLoader lineItemDbLoader = LineitemDbLoader.Default
						.getInstance();
				List<Lineitem> lineitems = lineItemDbLoader.loadByCourseId(ctx
						.getCourseId());

				QuestPathUtil qpUtil = new QuestPathUtil();
				List<QuestPathItem> itemList = qpUtil.buildInitialList(ctx,
						children, lineitems);

				//Create Loaders for Availability Rules, Criteria and Outcome
				//These loaders will allow us to capture Adaptive Release Information
				AvailabilityRuleDbLoader avRuleLoader = AvailabilityRuleDbLoader.Default
						.getInstance();
				AvailabilityCriteriaDbLoader avCriLoader = AvailabilityCriteriaDbLoader.Default
						.getInstance();
				OutcomeDefinitionDbLoader defLoad = OutcomeDefinitionDbLoader.Default
						.getInstance();
				//Load ADAPTIVE RELEASE rules
				List<AvailabilityRule> rules = avRuleLoader
						.loadByCourseId(courseID);
				List<QuestRule> questRules = qpUtil.buildQuestRules(rules,
						avCriLoader, defLoad);

				itemList = qpUtil.setParentChildList(itemList, questRules);
				itemList = qpUtil.setInitialFinal(itemList);
				itemList = qpUtil.removeNonAdaptiveReleaseContent(itemList);
				itemList = qpUtil.setGradableQuestPathItemStatus(itemList,
						questRules);
				itemList = qpUtil.setLockOrUnlocked(itemList, questRules);

				Processor proc = new Processor();
				List<QuestPath> qPaths = proc.buildQuests(itemList);

				//if (!isUserAnInstructor) {
				for (QuestPath quest : qPaths) {
					quest = qpUtil.setQuest(quest);
				}
		%>

			<%
				int j = 0;
				int top = 0;
				int left = 0;
				for (QuestPath qp : qPaths) { %>
				<div id="<%="QP" + j%>" class="window questName"><%=qp.getQuestName() %></div>
				<%
						for (QuestPathItem qpI : qp.getQuestPathItems()) {
							QPAttributes qpAtt = new QPAttributes(qpI);
				%>
			<div id="<%=j + "-" + qpI.getName().replace(" ", "_")%>" class="window <%=qpAtt.getStatusClassName() %>"
				title="<%=qpAtt.getTitle()%>" 
				<% if (!qpI.isLocked()) { %>
				ondblclick="openAssignment('execute/uploadAssignment?content_id=<%=qpI.getContentId().getExternalString()%>&course_id=<%=ctx.getCourseId().toExternalString()%>&assign_group_id=&mode=view');"
				<%} %>
			>
				<%=qpI.getName()%>
			</div>
			<%
							top = top + 30;
							left = left + 30;
						}
						j++;
					}
			%>
<script type="text/javascript">
<%String questString = "";
				for (QuestPath quest : qPaths) {
					for (QuestPathItem qItem : quest.getQuestPathItems()) {
						qItem.setContentId(null);
						qItem.setName(qItem.getName().replace(" ", "_"));
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
				questString = qpUtil.toJson(qPaths);%>
var quests = <%=questString%>;
var questsLoaded = true;
</script>
<bbNG:jsFile href="<%=jQuery%>"/>
<bbNG:jsFile href="<%=jQueryui%>"/>
<bbNG:jsFile href="<%=jsPlumb%>"/>
<bbNG:jsFile href="<%=jsTouch%>"/>
<bbNG:jsFile href="<%=questPath%>"/>
<div class="legend"><h5>LEGEND</h5>
<div class="legendColor passed">Passed</div>
<div class="legendColor failed">Attempted</div>
<div class="legendColor locked">Locked</div>
<div class="legendColor unlocked">Unlocked</div>
</div>
</div>
</body>
</bbNG:includedPage>