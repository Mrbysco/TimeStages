package com.mrbysco.timestages;

import com.mrbysco.timestages.util.TimeHelper;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod(Reference.MOD_ID)
public class TimeStages {
	public static TimeStages INSTANCE;

	public static final Map<String, StageInfo> timers = new ConcurrentHashMap<>();

	public TimeStages() {
		INSTANCE = this;
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addTimerInfo(String uniqueID, String stage, String nextStage, int time, String amount, boolean removal, boolean removeOld, boolean silent) {
		// Check if the info doesn't already exist
		StageInfo timer_info = new StageInfo(uniqueID, stage, nextStage, time, amount, removal, removeOld, silent);
		if (!timers.containsValue(timer_info) || !timers.containsKey(uniqueID)) {
			timers.put(uniqueID, timer_info);
		}
	}

	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.phase == Phase.START) return;

		final Player player = event.player;
		if (!player.level().isClientSide && player.isAlive() && player.level().getGameTime() % 20 == 0) {
			if (isPlayerReal(event.player) && timers != null && !timers.isEmpty()) {
				ServerPlayer serverPlayer = (ServerPlayer) player;
				for (HashMap.Entry<String, StageInfo> entry : timers.entrySet()) {
					StageInfo info = entry.getValue();
					if (info.getStage().isEmpty()) return;

					final boolean removal = info.isRemoval();
					final boolean silent = info.isSilent();

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
									if (!silent)
										player.displayClientMessage(Component.translatable("timestages.stage.removal.message", requiredStage), false);
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
								long worldAge = player.level().getGameTime() / 24000;
								if ((int) worldAge >= time) {
									setEntityTimeData(serverPlayer, uniqueID, 0);
									GameStageHelper.addStage(serverPlayer, nextStage);
									if (removeOld && !requiredStage.isEmpty()) {
										GameStageHelper.removeStage(serverPlayer, requiredStage);
									}
									if (!silent)
										player.displayClientMessage(Component.translatable("timestages.stage.add.message", nextStage), false);
								}
							} else {
								if (timer >= time) {
									setEntityTimeData(serverPlayer, uniqueID, 0);

									GameStageHelper.addStage(serverPlayer, nextStage);
									if (removeOld && !requiredStage.isEmpty()) {
										GameStageHelper.removeStage(serverPlayer, requiredStage);
									}
									if (!silent)
										player.displayClientMessage(Component.translatable("timestages.stage.add.message", nextStage), false);
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

	public static boolean isPlayerReal(Entity player) {
		return player != null && player.level() != null && player.getClass() == ServerPlayer.class;
	}

	public static void setEntityTimeData(ServerPlayer player, String valueTag, int time) {
		CompoundTag playerData = player.getPersistentData();
		playerData.putInt(valueTag, time);
	}

	public static int getEntityTimeData(ServerPlayer player, String valueTag) {
		CompoundTag playerData = player.getPersistentData();
		return playerData.getInt(valueTag);
	}
}
