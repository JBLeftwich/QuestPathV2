package com.jsu.cs521.questpath.buildingblock.object;

import java.util.ArrayList;
import java.util.List;

/**
 * Class QuestPath represents a list of Quests grouped together.
 * The List<Integer> variables provide a way to locate Quest Path Items of
 * a specific type
 * @author JBLeftwich
 *
 */
public class QuestPath {
	private List<QuestPathItem> questPathItems = new ArrayList<QuestPathItem>();
	private String questName = "";
	private List<String> questItemNames = new ArrayList<String>();
	private List<Integer> passedQuests = new ArrayList<Integer>();
	private List<Integer> attemptedQuests = new ArrayList<Integer>();
	private List<Integer> unlockedQuests = new ArrayList<Integer>();
	private List<Integer> lockedQuests = new ArrayList<Integer>();
	private List<Integer> rewardItems = new ArrayList<Integer>();
	private List<Integer> lockedItems = new ArrayList<Integer>();

	public List<QuestPathItem> getQuestPathItems() {
		return questPathItems;
	}

	public void setQuestPathItems(List<QuestPathItem> questPathItems) {
		this.questPathItems = questPathItems;
	}

	public String getQuestName() {
		return questName;
	}

	public void setQuestName(String questName) {
		this.questName = questName;
	}

	public List<String> getQuestItemNames() {
		return questItemNames;
	}

	public void setQuestItemNames(List<String> questItemNames) {
		this.questItemNames = questItemNames;
	}

	public List<Integer> getPassedQuests() {
		return passedQuests;
	}

	public void setPassedQuests(List<Integer> passedQuests) {
		this.passedQuests = passedQuests;
	}

	public List<Integer> getAttemptedQuests() {
		return attemptedQuests;
	}

	public void setAttemptedQuests(List<Integer> attemptedQuests) {
		this.attemptedQuests = attemptedQuests;
	}

	public List<Integer> getUnlockedQuests() {
		return unlockedQuests;
	}

	public void setUnlockedQuests(List<Integer> unlockedQuests) {
		this.unlockedQuests = unlockedQuests;
	}

	public List<Integer> getLockedQuests() {
		return lockedQuests;
	}

	public void setLockedQuests(List<Integer> lockedQuests) {
		this.lockedQuests = lockedQuests;
	}

	public List<Integer> getRewardItems() {
		return rewardItems;
	}

	public void setRewardItems(List<Integer> rewardItems) {
		this.rewardItems = rewardItems;
	}

	public List<Integer> getLockedItems() {
		return lockedItems;
	}

	public void setLockedItems(List<Integer> lockedItems) {
		this.lockedItems = lockedItems;
	}

}
