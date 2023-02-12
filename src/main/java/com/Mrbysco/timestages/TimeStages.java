package com.mrbysco.timestages;

import com.mrbysco.timestages.util.TimeHelper;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Mod(Reference.MOD_ID)
public class TimeStages {
	public static TimeStages INSTANCE;

	public static ConcurrentHashMap<String, StageInfo> timers = new ConcurrentHashMap<>();

	public TimeStages() {
		INSTANCE = this;
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addTimerInfo(String uniqueID, String stage, String nextStage, int time, String amount, boolean removal, boolean removeOld) {
		// Check if the info doesn't already exist
		StageInfo timer_info = new StageInfo(uniqueID, stage, nextStage, time, amount, removal, removeOld);
		if (!timers.containsValue(timer_info) || !timers.containsKey(uniqueID)) {
			timers.put(uniqueID, timer_info);
		}
	}

	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.phase == Phase.START)
			return;

		final PlayerEntity player = event.player;
		if (!player.level.isClientSide && player.isAlive() && player.level.getGameTime() % 20 == 0) {
			if (PlayerUtils.isPlayerReal(event.player) && timers != null && !timers.isEmpty()) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
				for (HashMap.Entry<String, StageInfo> entry : timers.entrySet()) {
					StageInfo info = entry.getValue();
					if (info.getStage().isEmpty())
						return;

					final boolean removal = info.isRemoval();

					final String requiredStage = info.getStage();
					final String nextStage = info.getNextStage();
					final int time = TimeHelper.getProperTime(info.getTime(), info.getAmount());
					final boolean removeOld = info.isRemoveOld();
					final String uniqueID = info.getUniqueID();
					int timer = getEntityTimeData(serverPlayer, uniqueID);

					if (removal) {
						if (requiredStage.isEmpty() || GameStageHelper.hasStage(serverPlayer, requiredStage)) {
							if (timer >= time) {
								setEntityTimeData(serverPlayer, uniqueID, 0);

								if (!requiredStage.isEmpty()) {
									GameStageHelper.removeStage(serverPlayer, requiredStage);
									player.sendMessage(new TranslationTextComponent("stage.removal.message", requiredStage), Util.NIL_UUID);
								}
							} else {
								++timer;
								setEntityTimeData(serverPlayer, uniqueID, timer);
							}
						} else {
							if (timer != 0) {
								setEntityTimeData(serverPlayer, uniqueID, 0);
							}
						}
					} else {
						if ((requiredStage.isEmpty() || GameStageHelper.hasStage(serverPlayer, requiredStage)) && !GameStageHelper.hasStage(serverPlayer, nextStage)) {
							if (info.getAmount().contains("day")) {
								long worldAge = player.level.getGameTime() / 24000;
								if ((int) worldAge >= time) {
									setEntityTimeData(serverPlayer, uniqueID, 0);
									GameStageHelper.addStage(serverPlayer, nextStage);
									if (removeOld && !requiredStage.isEmpty()) {
										GameStageHelper.removeStage(serverPlayer, requiredStage);
									}
									player.sendMessage(new TranslationTextComponent("stage.add.message", nextStage), Util.NIL_UUID);
								}
							} else {
								if (timer >= time) {
									setEntityTimeData(serverPlayer, uniqueID, 0);

									GameStageHelper.addStage(serverPlayer, nextStage);
									if (removeOld && !requiredStage.isEmpty()) {
										GameStageHelper.removeStage(serverPlayer, requiredStage);
									}
									player.sendMessage(new TranslationTextComponent("stage.add.message", nextStage), Util.NIL_UUID);
								} else {
									++timer;
									setEntityTimeData(serverPlayer, uniqueID, timer);
								}
							}
						} else {
							if (timer != 0) {
								setEntityTimeData(serverPlayer, uniqueID, 0);
							}
						}
					}
				}
			}
		}
	}

	public static void setEntityTimeData(ServerPlayerEntity player, String valueTag, int time) {
		CompoundNBT playerData = player.getPersistentData();
		playerData.putInt(valueTag, time);
	}

	public static int getEntityTimeData(ServerPlayerEntity player, String valueTag) {
		CompoundNBT playerData = player.getPersistentData();
		return playerData.getInt(valueTag);
	}
}
