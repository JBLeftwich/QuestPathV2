package com.jsu.cs521.questpath.buildingblock.object;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import blackboard.persist.Id;

/**
 * A QuestPathItem is any Course Content located that is a test, assignment, or item
 * that has adaptive rules attached or dependent on to the content being processed.
 * @author JBLeftwich
 *
 */
public class QuestPathItem {
	private DecimalFormat nf = new DecimalFormat("#0.00");
	private Id contentId;
	private float pointsPossible;
	private float pointsEarned;
	private float percentageEarned;
	private boolean firstQuestItem;
	private boolean lastQuestItem;
	private List<String> childContent = new ArrayList<String>();
	private List<String> parentContent = new ArrayList<String>();
	private boolean isLocked;
	private boolean isPassed;
	private boolean isAttempted;
	private boolean isUnLocked;
	private String name;
	private boolean isGradable;
	private String completeRule;
	private String unlockRule;

	public QuestPathItem() {
		super();
		this.contentId = null;
		this.pointsPossible = 0.0f;
		this.pointsEarned = 0.0f;
		this.percentageEarned = 0.0f;
		this.firstQuestItem = false;
		this.lastQuestItem = false;
		this.isLocked = false;
		this.isPassed = false;
		this.isAttempted = false;
		this.isUnLocked = false;
		this.name = "";
		this.isGradable = false;
		this.unlockRule = "";
		this.completeRule = "";
	}
	public Id getContentId() {
		return contentId;
	}
	public void setContentId(Id contenetId) {
		this.contentId = contenetId;
	}
	public float getPointsPossible() {
		return pointsPossible;
	}
	public void setPointsPossible(float poinstPossible) {
		this.pointsPossible = poinstPossible;
	}
	public float getPointsEarned() {
		return pointsEarned;
	}
	public void setPointsEarned(float pointsEarned) {
		this.pointsEarned = pointsEarned;
	}
	public float getPercentageEarned() {
		return percentageEarned;
	}
	public void setPercentageEarned(float percentageEarned) {
		this.percentageEarned = percentageEarned;
	}
	public boolean isFirstQuestItem() {
		return firstQuestItem;
	}
	public void setFirstQuestItem(boolean firstQuestItem) {
		this.firstQuestItem = firstQuestItem;
	}
	public boolean isLastQuestItem() {
		return lastQuestItem;
	}
	public void setLastQuestItem(boolean lastQuestItem) {
		this.lastQuestItem = lastQuestItem;
	}
	public List<String> getChildContent() {
		return childContent;
	}
	public void setChildContent(List<String> childContent) {
		this.childContent = childContent;
	}
	public List<String> getParentContent() {
		return parentContent;
	}
	public void setParentContent(List<String> parentContent) {
		this.parentContent = parentContent;
	}
	public boolean isLocked() {
		return isLocked;
	}
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}
	public boolean isPassed() {
		return isPassed;
	}
	public void setPassed(boolean isPassed) {
		this.isPassed = isPassed;
	}
	public boolean isAttempted() {
		return isAttempted;
	}
	public void setAttempted(boolean isAttempted) {
		this.isAttempted = isAttempted;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isGradable() {
		return isGradable;
	}
	public void setGradable(boolean isGradable) {
		this.isGradable = isGradable;
	}
	public boolean isUnLocked() {
		return isUnLocked;
	}
	public void setUnLocked(boolean isUnLocked) {
		this.isUnLocked = isUnLocked;
	}
	public String getCompleteRule() {
		return completeRule;
	}
	public void setCompleteRule(String completeRule) {
		this.completeRule = completeRule;
	}
	public String getUnlockRule() {
		return unlockRule;
	}
	public void setUnlockRule(String unlockRule) {
		this.unlockRule = unlockRule;
	}
	
	public String getPercentFormatted() {
		return nf.format(this.getPercentageEarned());
	}
	
	@Override
	public String toString() {
		return "QuestPathItem [contenetId=" + contentId + ", poinstPossible="
				+ pointsPossible + ", pointsEarned=" + pointsEarned
				+ ", percentageEarned=" + percentageEarned
				+ ", firstQuestItem=" + firstQuestItem + ", lastQuestItem="
				+ lastQuestItem + ", childContent=" + childContent
				+ ", parentContent=" + parentContent + ", isLocked=" + isLocked
				+ ", isPassed=" + isPassed + ", isAttempted=" + isAttempted
				+ ", name=" + name + ", isGradable=" + isGradable + "]";
	}
	
	
}
