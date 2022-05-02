package com.mrbysco.timestages.compat.ct;

import com.blamejared.crafttweaker.api.action.base.IRuntimeAction;
import com.mrbysco.timestages.TimeStages;

public class ActionAddTimer implements IRuntimeAction {
	private final String uniqueID;
	private final String stage;
	private final String nextStage;
	private final int time;
	private final String amount;
	private final boolean removal;
	private final boolean removeOld;

	public ActionAddTimer(String uniqueID, String stage, String nextStage, int time, String amount, boolean removal, boolean removeOld) {
		this.uniqueID = uniqueID;
		this.stage = stage;
		this.nextStage = nextStage;
		this.time = time;
		this.amount = amount;
		this.removal = removal;
		this.removeOld = removeOld;
	}

	@Override
	public void apply() {
		if (this.removal)
			TimeStages.INSTANCE.addTimerInfo(uniqueID, stage, nextStage, time, amount, true, removeOld);
		else
			TimeStages.INSTANCE.addTimerInfo(uniqueID, stage, nextStage, time, amount, false, false);
	}

	@Override
	public String describe() {
		if (this.removal)
			return String.format("%s will be locked in %d %s", this.stage, this.time, this.amount);
		else
			return String.format("%d %s has been added to unlock stage %s", this.time, this.amount, this.nextStage);
	}
}
