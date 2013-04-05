package com.jsu.cs521.questpath.test;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.jsu.cs521.questpath.buildingblock.engine.Processor;
import com.jsu.cs521.questpath.buildingblock.object.GraphTier;
import com.jsu.cs521.questpath.buildingblock.object.QuestPath;
import com.jsu.cs521.questpath.buildingblock.object.QuestPathItem;
import com.jsu.cs521.questpath.buildingblock.util.QuestPathUtil;


public class testProcessor {

	public static void main(String args[]) {
		List<QuestPathItem> items = new ArrayList<QuestPathItem>();
		items.addAll(buildQuests("C"));
		items.addAll(buildQuests("D"));
		items.addAll(buildQuests("E"));
		items.addAll(buildQuests("F"));
		Processor proc = new Processor();
		QuestPathUtil util = new QuestPathUtil();
		items = util.setInitialFinal(items);
		List<QuestPath> questPaths = proc.buildQuests(items);

		List<List<GraphTier>> allTiers = new ArrayList<List<GraphTier>>();
		for (QuestPath path : questPaths) {
			List<GraphTier> tiers = proc.buildGraphTier(path.getQuestPathItems());
			allTiers.add(tiers);
			int i = 0;
			for (GraphTier tier : tiers) {
				for (String tierItem : tier.getTier()) {
					System.out.println("Tier " + i + " " + tierItem);					
				}
				i++;
			}
			System.out.println(tiers.size());
		}

		System.out.println("# of Tiers : " + allTiers.size());
		try {
			JSONArray jA = new JSONArray(allTiers);
			System.out.println(jA.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		for (QuestPath quest : questPaths) {
			for(String names : quest.getQuestItemNames()) {
				System.out.println(quest.getQuestName() + " " + names);
			}
		}

	}
	
	public static List<QuestPathItem> buildQuests(String suffix) {
		List<QuestPathItem> items = new ArrayList<QuestPathItem>();
		QuestPathItem item1 = new QuestPathItem();
		item1.setName(suffix + "P1");
		item1.setExtContentId(item1.getName());
		item1.getChildContent().add(suffix + "P2");
		items.add(item1);
		QuestPathItem item2 = new QuestPathItem();
		item2.setName(suffix + "P2");
		item2.setExtContentId(item2.getName());
		item2.getParentContent().add(suffix + "P1");
		item2.getChildContent().add(suffix + "P3");
		item2.getChildContent().add(suffix + "P4");
		items.add(item2);
		QuestPathItem item3 = new QuestPathItem();
		item3.setName(suffix + "P3");
		item3.setExtContentId(item3.getName());
		item3.getParentContent().add(suffix + "P1");
		item3.getParentContent().add(suffix + "P2");
		item2.getChildContent().add(suffix + "P5");
		item2.getChildContent().add(suffix + "P6");
		items.add(item3);
		QuestPathItem item4 = new QuestPathItem();
		item4.setName(suffix + "P4");
		item4.setExtContentId(item4.getName());
		item4.getParentContent().add(suffix + "P2");
		items.add(item4);
		QuestPathItem item5 = new QuestPathItem();
		item5.setName(suffix + "P5");
		item5.setExtContentId(item5.getName());
		item5.getParentContent().add(suffix + "P3");
		item5.getChildContent().add(suffix + "P7");
		items.add(item5);
		QuestPathItem item6 = new QuestPathItem();
		item6.setName(suffix + "P6");
		item6.setExtContentId(item6.getName());
		item6.getParentContent().add(suffix + "P4");
		item6.getChildContent().add(suffix + "P8");
		items.add(item6);
		QuestPathItem item7 = new QuestPathItem();
		item7.setName(suffix + "P7");
		item7.setExtContentId(item7.getName());
		item7.getParentContent().add(suffix + "P5");
		items.add(item7);
		QuestPathItem item8 = new QuestPathItem();
		item8.setName(suffix + "P8");
		item8.setExtContentId(item8.getName());
		item8.getParentContent().add(suffix + "P6");
		items.add(item8);
		return items;
	}

}
