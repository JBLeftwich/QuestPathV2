package com.jsu.cs521.questpath.buildingblock.engine;

import java.util.ArrayList;
import java.util.List;

import blackboard.data.content.Content;
import blackboard.data.content.avlrule.AvailabilityRule;
import blackboard.data.gradebook.Lineitem;
import blackboard.data.navigation.CourseToc;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.persist.content.ContentDbLoader;
import blackboard.persist.content.avlrule.AvailabilityCriteriaDbLoader;
import blackboard.persist.content.avlrule.AvailabilityRuleDbLoader;
import blackboard.persist.gradebook.LineitemDbLoader;
import blackboard.persist.gradebook.impl.OutcomeDefinitionDbLoader;
import blackboard.persist.navigation.CourseTocDbLoader;
import blackboard.platform.context.Context;

import com.jsu.cs521.questpath.buildingblock.object.QuestPath;
import com.jsu.cs521.questpath.buildingblock.object.QuestPathItem;
import com.jsu.cs521.questpath.buildingblock.object.QuestRule;
import com.jsu.cs521.questpath.buildingblock.util.QuestPathUtil;

public class Processor {
	
	private int i = 1;
	public boolean isUserAnInstructor = false;
	public String qLayout = "";
	public List<QuestPath> qPaths = new ArrayList<QuestPath>();
	public QuestPathUtil qpUtil = new QuestPathUtil();
/**
 * This method builds a QuestPath for each initial Quest Path Item
 * It then loops through the remaining Quest Path Items to apply them
 * to the appropriate Quest Path.
 * @param items - A list of Quest Path Items
 * @return
 */
	public List<QuestPath> buildQuests(List<QuestPathItem> items) {
		List<QuestPathItem> tempListA = new ArrayList<QuestPathItem>();
		tempListA.addAll(items);
		List<QuestPathItem> tempListB = new ArrayList<QuestPathItem>();
		tempListB.addAll(items);
		List<QuestPath> paths = new ArrayList<QuestPath>();
		for (QuestPathItem item : items) {
			if (item.isFirstQuestItem()) {
				QuestPath newPath = new QuestPath();
				newPath.getQuestPathItems().add(item);
				newPath.getQuestItemNames().add(item.getName());
				newPath.setQuestName("Quest Path - " + i);
				tempListB.remove(item);
				i++;
				paths.add(newPath);
			}
		}
		tempListA.clear(); 
		tempListA.addAll(tempListB);
		boolean process = true;
		int prevSize = tempListA.size();
		while (tempListA.size() > 0 && process) {
			for (QuestPathItem item : tempListA) {
				for (QuestPath qp : paths) {
					for (String parentItem : item.getParentContent()) {
						if (qp.getQuestItemNames().contains(parentItem)) {
							qp.getQuestItemNames().add(item.getName());
							qp.getQuestPathItems().add(item);
							tempListB.remove(item);
							break;
						}
					}
				}
			}
			if (tempListB.size() == prevSize) {
				process = false;
			}
			tempListA.clear(); 
			tempListA.addAll(tempListB);
			prevSize = tempListA.size();
		}
		return paths;
	}
	
	public void QPDriver (Context ctx) throws PersistenceException {
		try {
			Id courseID = ctx.getCourseId();
			String sessionUserRole = ctx.getCourseMembership().getRoleAsString();
			//String sessionUserID = sessionUser.getId().toString();
			//User sessionUser = ctx.getUser();
			//Course course = cm1.getCourse(courseID);
			//CourseManager cm1 = CourseManagerFactory.getInstance();
			if (sessionUserRole.trim().toLowerCase().equals("instructor")) {
				isUserAnInstructor = true;
			}
			CourseTocDbLoader cTocLoader = CourseTocDbLoader.Default.getInstance();
			ContentDbLoader cntDbLoader = ContentDbLoader.Default.getInstance();

			List<CourseToc> tList = cTocLoader.loadByCourseId(ctx.getCourseId());

			//Create an ArrayList of Content based on the TOC
			List<Content> children = new ArrayList<Content>();
			for (CourseToc t : tList) {
				if (t.getTargetType() == CourseToc.Target.CONTENT) {
					children.addAll(cntDbLoader.loadChildren(t.getContentId(), false, null));
				}
			}
			for (Content c : children) {
				if (c.getTitle().equalsIgnoreCase("QuestPath")) {
					qLayout = c.getBody().getText();
				}
			}
			if (qLayout.isEmpty()) {
				qLayout = "null";
			}

			//Load grades for gradable Lineitems
			LineitemDbLoader lineItemDbLoader = LineitemDbLoader.Default.getInstance();
			List<Lineitem> lineitems = lineItemDbLoader.loadByCourseId(ctx.getCourseId());

			List<QuestPathItem> itemList = qpUtil.buildInitialList(ctx, children, lineitems);

			//Create Loaders for Availability Rules, Criteria and Outcome
			//These loaders will allow us to capture Adaptive Release Information
			AvailabilityRuleDbLoader avRuleLoader = AvailabilityRuleDbLoader.Default.getInstance();
			AvailabilityCriteriaDbLoader avCriLoader = AvailabilityCriteriaDbLoader.Default.getInstance();
			OutcomeDefinitionDbLoader defLoad = OutcomeDefinitionDbLoader.Default.getInstance();
			//Load ADAPTIVE RELEASE rules
			List<AvailabilityRule> rules = avRuleLoader.loadByCourseId(courseID);
			List<QuestRule> questRules = qpUtil.buildQuestRules(rules, avCriLoader, defLoad);

			itemList = qpUtil.setParentChildList(itemList, questRules);
			itemList = qpUtil.setInitialFinal(itemList);
			itemList = qpUtil.removeNonAdaptiveReleaseContent(itemList);
			itemList = qpUtil.setGradableQuestPathItemStatus(itemList,questRules);
			itemList = qpUtil.setLockOrUnlocked(itemList, questRules);
			qPaths = this.buildQuests(itemList);

			//if (!isUserAnInstructor) {
			for (QuestPath quest : qPaths) {
				quest = qpUtil.setQuest(quest);
			}

		} catch (PersistenceException e) {
			e.printStackTrace();
			throw e;
		}



		
	}
}
