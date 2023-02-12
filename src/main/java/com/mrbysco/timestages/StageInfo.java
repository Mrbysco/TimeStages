package com.mrbysco.timestages;

public class StageInfo {
	private final String uniqueID;
	private final String stage;
	private final String nextStage;
	private final int time;
	private final String amount;
	private final boolean removal;
	private final boolean removeOld;
	private final boolean silent;

	public StageInfo(String ID, String stage, String nextStage, int time, String amount, boolean removal, boolean removeOld, boolean silent) {
		this.uniqueID = ID;
		this.stage = stage;
		this.nextStage = nextStage;
		this.time = time;
		this.amount = amount;
		this.removal = removal;
		this.removeOld = removeOld;
		this.silent = silent;
	}

	public String getStage() {
		return this.stage;
	}

	public String getNextStage() {
		return nextStage;
	}

	public int getTime() {
		return this.time;
	}

	public String getAmount() {
		return this.amount;
	}

	public boolean isRemoval() {
		return this.removal;
	}

	public boolean isRemoveOld() {
		return removeOld;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public boolean isSilent() {
		return silent;
	}
}
