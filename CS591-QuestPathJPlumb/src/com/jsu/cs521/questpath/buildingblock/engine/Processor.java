package com.jsu.cs521.questpath.buildingblock.engine;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import blackboard.data.content.Content;
import blackboard.data.content.avlrule.AvailabilityCriteria;
import blackboard.data.content.avlrule.AvailabilityRule;
import blackboard.data.content.avlrule.GradeRangeCriteria;
import blackboard.data.gradebook.Lineitem;
import blackboard.data.gradebook.impl.OutcomeDefinition;
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

import com.jsu.cs521.questpath.buildingblock.object.GraphTier;
import com.jsu.cs521.questpath.buildingblock.object.QuestPath;
import com.jsu.cs521.questpath.buildingblock.object.QuestPathItem;
import com.jsu.cs521.questpath.buildingblock.object.QuestRule;
import com.jsu.cs521.questpath.buildingblock.util.QuestPathUtil;

public class Processor {
	
	private int i = 1;
	public boolean isUserAnInstructor = false;
	public String qLayout = "";
	public String questTier = "";
	public List<QuestPath> qPaths = new ArrayList<QuestPath>();
	public QuestPathUtil qpUtil = new QuestPathUtil();
	List<List<GraphTier>> allTiers = new ArrayList<List<GraphTier>>();
	public String debugString = "";
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
				//newPath.getQuestItemNames().add(item.getName());
				newPath.getQuestItemNames().add(item.getExtContentId());
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
							//qp.getQuestItemNames().add(item.getName());
							qp.getQuestItemNames().add(item.getExtContentId());
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
	
	public List<GraphTier>  buildGraphTier (List<QuestPathItem> items) {
		List<GraphTier> graphTier = new ArrayList<GraphTier>();
		int tier = 0;
		boolean settingTier = true;
		List<String> tierFound = new ArrayList<String>();
		GraphTier tier1 = new GraphTier();
		for (QuestPathItem item : items) {
			//TODO change to External Content ID
			if (item.isFirstQuestItem()) {
//				tier1.getTier().add(item.getName());
//				tierFound.add(item.getName());
				tier1.getTier().add(item.getExtContentId());
				tierFound.add(item.getExtContentId());
			}
		}
		graphTier.add(tier1);
		while (settingTier) {
			tier++;
			GraphTier nextTier = new GraphTier();
			for (QuestPathItem item : items) {
				for (String parent : item.getParentContent()) {
					//TODO change to External Content ID					
					//if (graphTier.get(tier - 1).getTier().contains(parent) && !graphTier.get(tier - 1).getTier().contains(item.getName())) {
					if (graphTier.get(tier - 1).getTier().contains(parent) && !graphTier.get(tier - 1).getTier().contains(item.getExtContentId())) {
						//TODO change to External Content ID
						//nextTier.getTier().add(item.getName());
						//tierFound.add(item.getName());
						nextTier.getTier().add(item.getExtContentId());
						tierFound.add(item.getExtContentId());
						break;
					}
				}
			}
			if (nextTier.getTier().size() == 0) {
				settingTier = false;
			}
			else {
			graphTier.add(nextTier);
			}
		}
		GraphTier lastTier = new GraphTier();
		for (QuestPathItem item : items) {
			//TODO change to External Content ID			
			//if (!tierFound.contains(item.getName())) {
			//		lastTier.getTier().add(item.getName());
			//}
			if (!tierFound.contains(item.getExtContentId())) {
				lastTier.getTier().add(item.getExtContentId());
			}
		}
		if (lastTier.getTier().size() > 0) {
			graphTier.add(lastTier);
		}
		return graphTier;
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

//			for(AvailabilityRule rule : rules) {
//				List<AvailabilityCriteria> criterias = avCriLoader.loadByRuleId(rule.getId());
//				for (AvailabilityCriteria criteria : criterias) {
//					if(criteria.getRuleType().equals(AvailabilityCriteria.RuleType.GRADE_RANGE)) {
//						GradeRangeCriteria gcc = (GradeRangeCriteria) criteria;
//						OutcomeDefinition definition = defLoad.loadById(gcc.getOutcomeDefinitionId());
//						debugString += ",def1=" + definition.getTitle() + definition.getContentId();
//					}
//					if(criteria.getRuleType().equals(AvailabilityCriteria.RuleType.GRADE_RANGE_PERCENT)) {
//						GradeRangeCriteria gcc = (GradeRangeCriteria) criteria;
//						OutcomeDefinition definition = defLoad.loadById(gcc.getOutcomeDefinitionId());
//						debugString += ",def2=" + definition.getTitle() + definition.getContentId();
//					}
//				}
//			}
//			
//			for (QuestPath questPath1 : qPaths) {
//				for (QuestPath questPath2: qPaths) {
//					if (!questPath1.getQuestName().equals(questPath2.getQuestName())) {
//						for (QuestPathItem item1 :questPath1.getQuestPathItems()) {
//							if (!item1.isDuplicate()) {
//								for (QuestPathItem item2 : questPath2.getQuestPathItems()) 
//								{	
//									if (item1.getName().equals(item2.getName())) {
//										debugString += item2.getName() + " ! " ;
//										item2.setDuplicate(true);
//										//item1.getChildContent().addAll(item2.getChildContent());
//										//item1.getParentContent().addAll(item2.getParentContent());
//										//TODO set passed,locked,unlocked,attempted
//									}
//								}	
//							}
//						}
//						
//					}
//				}
//			}
			
			//if (!isUserAnInstructor) {
			for (QuestPath quest : qPaths) {
				quest = qpUtil.setQuest(quest);
				List<GraphTier> tiers = buildGraphTier(quest.getQuestPathItems());
				allTiers.add(tiers);
			}
			if (allTiers.size() > 0) {
				//JSONArray jA = new JSONArray(allTiers);
				//questTier = (jA.toString().replace(" ", "_").replace(".", "_").replace(")", "_").replace("(", "_"));
				questTier = new JSONArray(allTiers).toString();
			}
			else {
				questTier = "null";
			}

		} catch (PersistenceException e) {
			e.printStackTrace();
			throw e;
		}
		
	}
}
