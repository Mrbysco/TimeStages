package com.mrbysco.timestages.compat.ct;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.timestages.Timers")
public class StageTimerCT {

	@ZenCodeType.Method
    public static void addTimer(String id, String stage, String nextStage, int time, String amount, boolean removeOld) {
        CraftTweakerAPI.apply(new ActionAddTimer(id, stage, nextStage, time, amount, false, removeOld));
	}

	@ZenCodeType.Method
	public static void addTimer(String id, String stage, String nextStage, int time, String amount) {
		CraftTweakerAPI.apply(new ActionAddTimer(id, stage, nextStage, time, amount, false, false));
	}

	@ZenCodeType.Method
	public static void removalTimer(String id, String stage, int time, String amount) {
		CraftTweakerAPI.apply(new ActionAddTimer(id, stage, null, time, amount, true, false));
	}
}
