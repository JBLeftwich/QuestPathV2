package com.jsu.cs521.questpath.buildingblock.object;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import blackboard.data.content.Content;
import blackboard.persist.Id;

import com.jsu.cs521.questpath.buildingblock.object.QuestPath;
import com.jsu.cs521.questpath.buildingblock.object.QuestPathItem;

public class QuestPathTestObject {
	
	private List<QuestPath> qPaths = new ArrayList<QuestPath>();
	
	public QuestPathTestObject() {
		QuestPath questPath = new QuestPath();
		List<Integer> attemptedQuests = new ArrayList<Integer>();
		attemptedQuests.add(1);
		questPath.setAttemptedQuests(attemptedQuests);
		
		List<Integer> lockedItems  = new ArrayList<Integer>();
		questPath.setLockedItems(lockedItems);
		
		List<Integer> lockedQuests = new ArrayList<Integer>();
		questPath.setLockedQuests(lockedQuests);
		
		List<Integer> passedQuests = new ArrayList<Integer>();
		passedQuests.add(0);
		questPath.setPassedQuests(passedQuests);
		
		List<String> questItemNames  = new ArrayList<String>();
		questItemNames.add("QI1");
		questPath.setQuestItemNames(questItemNames);
		
		questPath.setQuestName("Demo Quest Path");
		List<QuestPathItem> questPathItems = new ArrayList<QuestPathItem>(); //TODO Add Quest Path Items
		QuestPathItem qp1 = new QuestPathItem();
		qp1.setName("XP1");
		qp1.setPointsPossible(100);
		qp1.setPointsEarned(100);
		qp1.setPercentageEarned(100);
		List<String> cc = new ArrayList<String>();
		cc.add("XP2");
		cc.add("XP3");
		qp1.setChildContent(cc);
		qp1.setParentContent(new ArrayList<String>());
		questPathItems.add(qp1);

		QuestPathItem qp2 = new QuestPathItem();
		qp2.setName("XP2");
		qp2.setPointsPossible(100);
		qp2.setPointsEarned(79);
		qp2.setPercentageEarned(100);
		List<String> pc = new ArrayList<String>();
		pc.add("XP1");
		List<String> cc2 = new ArrayList<String>();
		cc2.add("XP3");
		qp2.setParentContent(pc);
		qp2.setChildContent(cc2);
		questPathItems.add(qp2);
		
		QuestPathItem qp3 = new QuestPathItem();
		qp3.setName("XP3");
		qp3.setPointsPossible(100);
		qp3.setPointsEarned(79);
		qp3.setPercentageEarned(100);
		List<String> pc2 = new ArrayList<String>();
		pc2.add("XP1");
		pc2.add("XP2");
		qp3.setParentContent(pc);
		qp3.setChildContent(new ArrayList<String>());
		questPathItems.add(qp3);		
		
		questPath.setQuestPathItems(questPathItems);
		qPaths.add(questPath);
		
	}
	
	public String toJson() { 
		JSONArray jA = new JSONArray(qPaths);
		return jA.toString();		
	}

	public List<QuestPath> getqPaths() {
		return qPaths;
	}

	public void setqPaths(List<QuestPath> qPaths) {
		this.qPaths = qPaths;
	}

}
