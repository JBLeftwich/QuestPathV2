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
-->
<%@page import="blackboard.persist.Id"%>
<%@page import="blackboard.data.course.Course"%>
<%@page import="blackboard.platform.BbServiceManager"%>
<%@page import="blackboard.persist.BbPersistenceManager"%>
<%@page import="blackboard.persist.content.ContentDbPersister"%>
<%@page import="blackboard.base.FormattedText"%>
<%@page import="java.util.ArrayList"%>
<%@page import="blackboard.data.content.Content"%>
<%@page import="blackboard.data.navigation.CourseToc"%>
<%@page import="java.util.List"%>
<%@page import="blackboard.persist.content.ContentDbLoader"%>
<%@page import="blackboard.persist.navigation.CourseTocDbLoader"%>
<%@ taglib uri="/bbData" prefix="bbData"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<!-- for tags -->
<%-- <bbData:context id="ctx">  --%>
<bbNG:includedPage ctxId="ctx">
<%              
			BbPersistenceManager bbPm = BbServiceManager.getPersistenceService().getDbPersistenceManager();			
			Id courseId = bbPm.generateId(Course.DATA_TYPE,request.getParameter("course_id"));  
			String testVar = request.getParameter("testVar");
			CourseTocDbLoader cTocLoader = CourseTocDbLoader.Default.getInstance();
			ContentDbLoader cntDbLoader = ContentDbLoader.Default.getInstance();
			List<CourseToc> tList = cTocLoader.loadByCourseId(courseId);
			List<Content> children = new ArrayList<Content>();
			for (CourseToc t : tList) {
				if (t.getTargetType() == CourseToc.Target.CONTENT) {
					children.addAll(cntDbLoader.loadChildren(t.getContentId(), false, null));
				}
			}
			for (Content c : children) {
				if (c.getTitle().equalsIgnoreCase("test") || c.getTitle().equalsIgnoreCase("test.txt")) {
					Content courseWork = cntDbLoader.loadById(c.getId());;
					FormattedText ft = new FormattedText(testVar, FormattedText.Type.PLAIN_TEXT);
					courseWork.setBody(ft);
					ContentDbPersister contentPersister =  ContentDbPersister.Default.getInstance(); 
       			   	contentPersister.persist(courseWork);
				}
			}
			
%>
<body>
<div id="questpathBlockContainer" class="mainDiv">
SAVED!!!
</div>
</body>
</bbNG:includedPage>