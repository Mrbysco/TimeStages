package com.mrbysco.timestages.compat.ct;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.mrbysco.timestages.TimeStages;

public class ActionAddTimer implements IRuntimeAction {
	private final String uniqueID;
	private final String stage;
	private final String nextStage;
	private final int time;
	private final String amount;
	private final boolean removal;
	private final boolean removeOld;
	private final boolean silent;

	public ActionAddTimer(String uniqueID, String stage, String nextStage, int time, String amount, boolean removal, boolean removeOld, boolean silent) {
		this.uniqueID = uniqueID;
		this.stage = stage;
		this.nextStage = nextStage;
		this.time = time;
		this.amount = amount;
		this.removal = removal;
		this.removeOld = removeOld;
		this.silent = silent;
	}

	@Override
	public void apply() {
		TimeStages.INSTANCE.addTimerInfo(uniqueID, stage, nextStage, time, amount, removal, removeOld, silent);
	}

	@Override
	public String describe() {
		if (this.removal)
			return String.format("%s will be locked in %d %s", this.stage, this.time, this.amount);
		else
			return String.format("%d %s has been added to unlock stage %s", this.time, this.amount, this.nextStage);
	}
}
