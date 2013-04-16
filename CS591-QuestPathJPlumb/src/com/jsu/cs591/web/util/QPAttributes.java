package com.jsu.cs591.web.util;

import com.jsu.cs521.questpath.buildingblock.object.QuestPathItem;
/**
 * 
 * @author JBLeftwich
 *
 */
public class QPAttributes {
	
	private String title;
	private String statusClassName;
	
	public QPAttributes() {
		this.title = "";
		this.statusClassName = "";
	}
	
	public QPAttributes(QuestPathItem item) {
		super();
		
			if (item.isUnLocked() && item.isPassed()) {
				this.statusClassName = "passed";
				this.title = "Assignment - " + item.getName();
			}
			else if (item.isUnLocked() && !item.isGradable()) {
				this.statusClassName = "passed";
				this.title = "Reward - " + item.getName();
			}
			else if (item.isAttempted() && item.isUnLocked()) {
				this.statusClassName = "failed";
				this.title = item.getName() + " Score - " + item.getPointsEarned() + "/" + item.getPointsPossible() + 
						" ( " + item.getPercentFormatted()  + "%) " + item.getCompleteRule();
			}
			else if (item.isUnLocked()) {
				this.statusClassName = "unlocked";
				this.title = item.getName() + " Score - " + item.getPointsEarned() + "/" + item.getPointsPossible() + 
						" ( " + item.getPercentFormatted()  + "%) " + item.getCompleteRule();
			}
			else if (item.isGradable() && item.isLocked()){
				this.statusClassName = "locked";
				this.title = item.getName() + " Score - " + item.getPointsEarned() + "/" + item.getPointsPossible() + 
						" ( " + item.getPercentFormatted()  + "%) " + item.getCompleteRule();

			}
			else {
				this.statusClassName = "locked";
				this.title =  "Reward - " + item.getName() + item.getUnlockRule();
			}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatusClassName() {
		return statusClassName;
	}

	public void setStatusClassName(String statusClassName) {
		this.statusClassName = statusClassName;
	}

	
}
