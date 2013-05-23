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
<%@page import="blackboard.persist.PersistenceException"%>
<%@page import="blackboard.data.ValidationException"%>
<%@page import="blackboard.data.content.CourseDocument"%>
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
<bbNG:includedPage ctxId="ctx">
<%              
			String errorMsg = "";
			BbPersistenceManager bbPm = BbServiceManager.getPersistenceService().getDbPersistenceManager();			
			Id courseId = bbPm.generateId(Course.DATA_TYPE,request.getParameter("course_id"));
			String testVar = request.getParameter("questLayout");
			CourseTocDbLoader cTocLoader = CourseTocDbLoader.Default.getInstance();
			ContentDbLoader cntDbLoader = ContentDbLoader.Default.getInstance();
			List<CourseToc> tList = cTocLoader.loadByCourseId(courseId);
			List<Content> children = new ArrayList<Content>();
			String label = "";
			String type = "";
			for (CourseToc t : tList) {
				//label = label + t.getLabel() + ";";
				//type = type + t.getTargetType() + ";";
				if (t.getTargetType() == CourseToc.Target.CONTENT) {
					children.addAll(cntDbLoader.loadChildren(t.getContentId(), false, null));
				}
			}
			boolean contentExists = false;
			for (Content c : children) {
				if (c.getTitle().equalsIgnoreCase("QuestPath")) {
					Content courseWork = cntDbLoader.loadById(c.getId());;
					FormattedText ft = new FormattedText(testVar, FormattedText.Type.PLAIN_TEXT);
					courseWork.setBody(ft);
					ContentDbPersister contentPersister =  ContentDbPersister.Default.getInstance(); 
       			   	contentPersister.persist(courseWork);
       			   	contentExists = true;
       			   	break;
				}
			}
			Content courseDoc = new Content();
			boolean questConfigCreated = false;
			try {
 			if (!contentExists) {
 				for (CourseToc t : tList) {
 					if (t.getTargetType() == CourseToc.Target.CONTENT) {
 		 				//CourseToc toc = blackboard.persist.navigation.CourseTocDbLoader.Default.getInstance().loadByCourseIdAndLabel(courseId, "COURSE_DEFAULT.Content.CONTENT_LINK.label");
 						courseDoc.setTitle("QuestPath");
 		 				courseDoc.setCourseId(courseId);
 		 				String strMainData = request.getParameter("questLayout");
 		 				FormattedText text = new FormattedText(strMainData,FormattedText.Type.PLAIN_TEXT);
 		 				courseDoc.setParentId(t.getContentId());
 		 				courseDoc.setBody( text );
 		 				courseDoc.setCourseId(courseId);
 		 				courseDoc.setIsAvailable(false);
 		 				courseDoc.setIsTracked(false);
 		 				courseDoc.setIsDescribed(false);
 		 				courseDoc.setLaunchInNewWindow(false);
 		 	            courseDoc.setAllowGuests(false);
 		 	            courseDoc.setAllowObservers(false);
 		 	            try {
 		 	                blackboard.persist.content.ContentDbPersister.Default.getInstance().persist(courseDoc);
 		 	            } catch (ValidationException ex) {
 		 	                 errorMsg = ex.getLocalizedMessage();
 		 	            } catch (PersistenceException ex) {
 		 	            	errorMsg = ex.getLocalizedMessage();
 		 	            }
 		 	            break;
 					}
 					
 				}
 			}
			}
			catch (Exception e) {
				errorMsg = e.getLocalizedMessage();
			}
			
%>
<body>
<div id="questpathBlockContainer" class="mainDiv">
<%=errorMsg %>
</div>
<script type="text/javascript">
var errorMsg = '<%=errorMsg%>';
if (errorMsg.length > 0) {
	alert(errorMsg);
}
history.go(-2);
</script>
</body>
</bbNG:includedPage>