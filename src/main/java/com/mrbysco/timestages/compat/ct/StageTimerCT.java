package com.mrbysco.timestages.compat.ct;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;

@ZenRegister
@Name("mods.timestages.Timers")
public class StageTimerCT {

	@Method
	public static void addTimer(String id, String stage, String nextStage, int time, String amount, boolean removeOld) {
		CraftTweakerAPI.apply(new ActionAddTimer(id, stage, nextStage, time, amount, false, removeOld, false));
	}

	@Method
	public static void addSilentTimer(String id, String stage, String nextStage, int time, String amount, boolean removeOld) {
		CraftTweakerAPI.apply(new ActionAddTimer(id, stage, nextStage, time, amount, false, removeOld, true));
	}

	@Method
	public static void addTimer(String id, String stage, String nextStage, int time, String amount) {
		CraftTweakerAPI.apply(new ActionAddTimer(id, stage, nextStage, time, amount, false, false, false));
	}

	@Method
	public static void addSilentTimer(String id, String stage, String nextStage, int time, String amount) {
		CraftTweakerAPI.apply(new ActionAddTimer(id, stage, nextStage, time, amount, false, false, true));
	}

	@Method
	public static void removalTimer(String id, String stage, int time, String amount) {
		CraftTweakerAPI.apply(new ActionAddTimer(id, stage, null, time, amount, true, false, false));
	}

	@Method
	public static void silentRemovalTimer(String id, String stage, int time, String amount) {
		CraftTweakerAPI.apply(new ActionAddTimer(id, stage, null, time, amount, true, false, true));
	}
}
