package com.Mrbysco.TimeStages.compat.ct;

import com.Mrbysco.TimeStages.TimeStages;

import crafttweaker.IAction;

public class ActionAddTimer implements IAction {

	private final String stage;
	private final String nextStage;
	private final int time;
	private final String amount;  
	private final boolean removal;

	public ActionAddTimer(String stage, String nextStage, int time, String amount, boolean removal) {
		this.stage = stage;
		this.nextStage = nextStage;
		this.time = time;
		this.amount = amount;
		this.removal = removal;
	}
	
	@Override
	public void apply() {
		if (this.removal)
			TimeStages.addTimerInfo(stage, nextStage, time, amount, true);
		else
			TimeStages.addTimerInfo(stage, nextStage, time, amount, false);
	}

	@Override
	public String describe() {
		if (this.removal)
			return String.format(this.stage + "will be locked in %d %s", this.time, this.amount);	
		else
			return String.format("%d %s has been added to unlock stage " + this.nextStage, this.time, this.amount);	
	}

}