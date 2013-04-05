package com.jsu.cs521.questpath.buildingblock.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import blackboard.data.content.Content;
import blackboard.data.content.avlrule.AvailabilityCriteria;
import blackboard.data.content.avlrule.AvailabilityRule;
import blackboard.data.content.avlrule.GradeRangeCriteria;
import blackboard.data.gradebook.Lineitem;
import blackboard.data.gradebook.Score;
import blackboard.data.gradebook.impl.OutcomeDefinition;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.content.avlrule.AvailabilityCriteriaDbLoader;
import blackboard.persist.gradebook.impl.OutcomeDefinitionDbLoader;
import blackboard.platform.context.Context;

import com.jsu.cs521.questpath.buildingblock.object.QuestPath;
import com.jsu.cs521.questpath.buildingblock.object.QuestPathItem;
import com.jsu.cs521.questpath.buildingblock.object.QuestRule;
import com.jsu.cs521.questpath.buildingblock.object.RuleCriteria;

/**
 * Utility Class to mold the QuestPathItems into a QuestPath
 * @author JBLeftwich
 *
 */
public class QuestPathUtil {

	Map<String, String> contentMap = new HashMap<String, String>();
	/**
	 * Remove any content found for the course that does not have a rule associated
	 * @param allItems
	 * @return
	 */
	public List<QuestPathItem> removeNonAdaptiveReleaseContent(List<QuestPathItem> allItems) {
		List<QuestPathItem> finalList = new ArrayList<QuestPathItem>();
		for (QuestPathItem qPI : allItems) {
			if (qPI.getChildContent().size() > 0 || qPI.getParentContent().size() > 0) 
			{
				finalList.add(qPI);
			}
		}
		return finalList;
	}

	/**
	 * Set QuestPathItems that are first task as well as last task
	 * This is done based on the size of child content and parent content
	 * Child Content applies to content which has rules dependent on the score received
	 * for the Quest Path Item
	 * Parent Content applies to content that a QuestPathItem is dependent upon
	 * For example if XP1 must be completed before XP2 can be assignable
	 * XP1 is a parent content to XP2
	 * XP2 is a child content to XP1 
	 * @param allItems
	 * @return
	 */
	public List<QuestPathItem> setInitialFinal(List<QuestPathItem> allItems) {
		for (QuestPathItem qPI : allItems) {
			if (qPI.getChildContent().size() > 0 && qPI.getParentContent().size() == 0) 
			{
				qPI.setFirstQuestItem(true);
			}
			if (qPI.getChildContent().size() == 0 && qPI.getParentContent().size() > 0) 
			{
				qPI.setLastQuestItem(true);
			}
		}
		return allItems;
	}

	/**
	 * Populate parent/child relationships based on QuestRules
	 * Child Content applies to content which has rules dependent on the score received
	 * for the Quest Path Item
	 * Parent Content applies to content that a QuestPathItem is dependent upon
	 * For example if XP1 must be completed before XP2 can be assignable
	 * XP1 is a parent content to XP2
	 * XP2 is a child content to XP1
	 * @param allItems
	 * @param allRules
	 * @return
	 */
	public List<QuestPathItem> setParentChildList(List<QuestPathItem> allItems, List<QuestRule> allRules) {
		for (QuestPathItem item : allItems) {
			for (QuestRule rule : allRules) {
				if (rule.getExtContentId().equals(item.getExtContentId())) {
					for (RuleCriteria crit : rule.getCriterias()) {
						item.getParentContent().add(crit.getParentContent());
						for(QuestPathItem item2 : allItems) {
							if(item2.getExtContentId().equals(crit.getParentContent())) {
								item2.getChildContent().add(item.getExtContentId());
							}
						}
					}
				}
			}
			if (item.getParentContent().size() > 0) {
				boolean subsequentRule = false;
				if (item.getUnlockRule().length() > 0) {subsequentRule = true;}
				StringBuilder sb = new StringBuilder();
				for (String pc : item.getParentContent()) {
					if (!subsequentRule) {
						sb.append(" Item will be unlocked when the following Quest Path Items are completed: " + contentMap.get(pc));
						subsequentRule = true;
					} else {
						sb.append("," + contentMap.get(pc));
					}
				}
				item.setUnlockRule(sb.toString());
			}
		}
		return allItems;
	}

	/**
	 * A list of content for a course is provided to the method as will as the Line Items of the Gradebook
	 * It is as this step we can see if content is gradable, as well as capture the most recent score for 
	 * the content. 
	 * @param context
	 * @param contentItems
	 * @param lineitems
	 * @return
	 */
	public List<QuestPathItem> buildInitialList(Context context, List<Content> contentItems, List<Lineitem> lineitems) {
		List<QuestPathItem> initialList = new ArrayList<QuestPathItem>();
		for (Content c : contentItems) {
			QuestPathItem newQP = new QuestPathItem();
			newQP.setName(c.getTitle());
			newQP.setExtContentId(c.getId().getExternalString());
			for(Lineitem li : lineitems) {
				if (li.getName().equals(newQP.getName())) {
					newQP.setGradable(true);
					if (li.getType().equals("Assignment") || li.getType().equals("Test")) {
						newQP.setPointsPossible(li.getPointsPossible());
						for (Score score : li.getScores()) {
							if (score.getCourseMembershipId().equals(context.getCourseMembership().getId())) {
								if (score.getOutcome().getScore() > newQP.getPointsEarned()) {
									newQP.setPointsEarned(score.getOutcome().getScore());
								}
							}
						}
						if (newQP.getPointsPossible() > 0) {
							newQP.setPercentageEarned(newQP.getPointsEarned()/newQP.getPointsPossible() * 100);
						}
					}
				}
			}
			initialList.add(newQP);
			contentMap.put(c.getId().getExternalString(), c.getTitle());
		}
		return initialList;
	}

	/**
	 * Adaptive Release Rules are retrieved
	 * @param rules
	 * @param avCriLoader
	 * @param defLoad
	 * @return
	 * @throws KeyNotFoundException
	 * @throws PersistenceException
	 */
	public List<QuestRule> buildQuestRules(List<AvailabilityRule> rules, AvailabilityCriteriaDbLoader avCriLoader, OutcomeDefinitionDbLoader defLoad ) throws KeyNotFoundException, PersistenceException {
		List<QuestRule> questRules = new ArrayList<QuestRule>();
		for(AvailabilityRule rule : rules) {
			boolean load = false;
			QuestRule questRule = new QuestRule();
			List<AvailabilityCriteria> criterias = avCriLoader.loadByRuleId(rule.getId());
			questRule.setExtContentId(rule.getContentId().getExternalString());
			questRule.setRuleId(rule.getId());
			for (AvailabilityCriteria criteria : criterias) {
				RuleCriteria ruleCrit = new RuleCriteria();
				if(criteria.getRuleType().equals(AvailabilityCriteria.RuleType.GRADE_RANGE)) {
					GradeRangeCriteria gcc = (GradeRangeCriteria) criteria;
					ruleCrit.setGradeRange(true);
					if(gcc.getMaxScore() != null ) {ruleCrit.setMaxScore(gcc.getMaxScore());}
					if(gcc.getMinScore() != null ) {ruleCrit.setMinScore(gcc.getMinScore());}
					OutcomeDefinition definition = defLoad.loadById(gcc.getOutcomeDefinitionId());
					if (definition.getContentId() != null) {
						ruleCrit.setParentContent(definition.getContentId().getExternalString());
						load = true;
						questRule.getCriterias().add(ruleCrit);
					}
				}
				if(criteria.getRuleType().equals(AvailabilityCriteria.RuleType.GRADE_RANGE_PERCENT)) {
					GradeRangeCriteria gcc = (GradeRangeCriteria) criteria;
					ruleCrit.setGradePercent(true);
					if(gcc.getMaxScore() != null ) {ruleCrit.setMaxScore(gcc.getMaxScore());}
					if(gcc.getMinScore() != null ) {ruleCrit.setMinScore(gcc.getMinScore());}
					OutcomeDefinition definition = defLoad.loadById(gcc.getOutcomeDefinitionId());
					if (definition.getContentId() != null) {
						ruleCrit.setParentContent(definition.getContentId().getExternalString());
						load = true;
						questRule.getCriterias().add(ruleCrit);
					}
				}
			}
			if (load) {
				questRules.add(questRule);
			}
		}
		return questRules;
	}

	/**
	 * Set gradable items as passed or attempted
	 * If a grade is not found for the item (0.0 points earned) bypass setting status
	 * If a Quest Path Item is the last quest item (meaning no child content) and a score of 80% is received or higher
	 * mark as passed, otherwise mark attempted if a grade is found.
	 * @param items
	 * @param rules
	 * @return
	 */
	public List<QuestPathItem> setGradableQuestPathItemStatus(List<QuestPathItem> items, List<QuestRule> rules) {
		for (QuestPathItem item : items) {
			boolean passed = false;
			boolean attempted = false;
			if (item.isGradable()) {
				if (item.isLastQuestItem()) {
					if(item.getPercentageEarned() > 80) {
						passed = true;
					} else if(item.getPointsEarned() > 0) {
						attempted = true;					
						item.setCompleteRule(" Quest Path Item will be complete when a score of 80% or higher is scored.");
					}

				}
				else {
					for(QuestRule rule : rules) {
						int i = 1;
						for (RuleCriteria ruleC : rule.getCriterias()) {
							if(ruleC.getParentContent().equals(item.getExtContentId())) {
								if (ruleC.isGradePercent()) {
									if (item.getPointsEarned() > 0 && item.getPercentageEarned() < ruleC.getMinScore()) {
										attempted = true;
										item.setCompleteRule(" Rule " + i + " Quest Path Item will be complete when a score of " + ruleC.getMinScore() + "%" + " or higher is scored.");
										i++;
									}
									else if (item.getPercentageEarned() >= ruleC.getMinScore()) {
										passed = true;
									}
									else {
										item.setCompleteRule(" Rule " + i + " Quest Path Item will be complete when a score of " + ruleC.getMinScore() + "%" + " or higher is scored.");
										i++;
									}
								}
								if (ruleC.isGradeRange()) {
									if (item.getPointsEarned() > 0 &&  item.getPointsEarned() < ruleC.getMinScore()) {
										attempted = true;
										item.setCompleteRule("Rule " + i + " Quest Path Item will be complete when a score of " + ruleC.getMinScore() +  " or higher is scored.");
										i++;
									}
									else if (item.getPointsEarned() >= ruleC.getMinScore()) {
										passed = true;
									}
									else {
										item.setCompleteRule(" Rule " + i + " Quest Path Item will be complete when a score of " + ruleC.getMinScore() + " or higher is scored.");
										i++;
									}

								}
							}
						}
					}
				}
				if(attempted) {
					item.setAttempted(true);
				} 
				else 
				{
					if(passed) {
						item.setPassed(true);
					}
				}
			}
		}
		return items;
	}

	/**
	 * Decide if QuestPathItems are locked or unlocked
	 * @param items
	 * @param rules
	 * @return
	 */
	public List<QuestPathItem> setLockOrUnlocked(List<QuestPathItem> items, List<QuestRule> rules) {
		for (QuestPathItem item : items) {
			boolean locked = false;
			boolean unlocked = false;
			if (item.getParentContent().size() > 0) {
				for(QuestRule rule : rules) {
					if (rule.getExtContentId().equals(item.getExtContentId())) {
						for (RuleCriteria ruleCrit : rule.getCriterias()) {
							for (QuestPathItem item2 : items) {
								if (ruleCrit.getParentContent().equals(item2.getExtContentId())) {
									if (item2.isPassed()) {
										unlocked = true;
									}
									else {
										locked = true;
									}
								}
							}
						}

					}
				}
			}
			else 
			{
				unlocked = true;
			}
			if(locked) {
				item.setLocked(true);
			} 
			else 
			{
				if(unlocked) {
					item.setUnLocked(true);
				}
			}
		}
		return items;
	}

	/**
	 * Finalize Quest and determine if gradable content is part of the Passed, Attempted, Unlocked, or Locked List
	 * Also, determine if non-gradable items are Passed or Locked
	 * @param qp
	 * @return
	 */
	public QuestPath setQuest(QuestPath qp) {
		for (QuestPathItem item : qp.getQuestPathItems()) {
			if (item.isUnLocked() && item.isPassed()) {
				qp.getPassedQuests().add(qp.getQuestPathItems().indexOf(item));
			}
			else if (item.isUnLocked() && !item.isGradable()) {
				qp.getRewardItems().add(qp.getQuestPathItems().indexOf(item));
			}
			else if (item.isAttempted() && item.isUnLocked()) {
				qp.getAttemptedQuests().add(qp.getQuestPathItems().indexOf(item));
			}
			else if (item.isUnLocked()) {
				qp.getUnlockedQuests().add(qp.getQuestPathItems().indexOf(item));
			}
			else if (item.isGradable() && item.isLocked()){
				qp.getLockedQuests().add(qp.getQuestPathItems().indexOf(item));
			}
			else {
				qp.getLockedItems().add(qp.getQuestPathItems().indexOf(item));
			}

		}
		return qp;
	}
	
	public String toJson(List<QuestPath> qPaths) {
		try {
		JSONArray jA = new JSONArray(qPaths);
		return jA.toString();
		}
		catch (Exception e) {
			return e.getLocalizedMessage();
		}
	}
}
