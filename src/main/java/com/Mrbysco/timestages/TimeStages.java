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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

@Mod(Reference.MOD_ID)
public class TimeStages {
	public static TimeStages INSTANCE;

	public static final Logger LOGGER = LogManager.getLogger();

	public static HashMap<String, StageInfo> timers = new HashMap<>();

	public TimeStages() {
		INSTANCE = this;
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void addTimerInfo(String uniqueID, String stage, String nextStage, int time, String amount, boolean removal, boolean removeOld) {
		// Check if the info doesn't already exist
		StageInfo timer_info = new StageInfo(uniqueID, stage, nextStage, time, amount, removal, removeOld);
		if(timers.containsValue(timer_info) || timers.containsKey(uniqueID) ) {
			return;
		} else {
			timers.put(uniqueID, timer_info);
		}
	}
    
    @SubscribeEvent
    public void playerTick(PlayerTickEvent event)
    {
		if(event.phase == TickEvent.Phase.END)
			return;

		final PlayerEntity player = event.player;
		if(!player.world.isRemote) {
			if (player.world.getGameTime() % 20 == 0) {
				if (PlayerUtils.isPlayerReal(event.player)) {
					ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
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

						if (removal) {
							if(GameStageHelper.hasStage(serverPlayer, requiredStage)) {
								if(getEntityTimeData(serverPlayer, uniqueID) != info.timer) {
									info.timer = getEntityTimeData(serverPlayer, uniqueID);
								}

								if(info.timer >= time) {
									info.timer = 0;
									setEntityTimeData(serverPlayer, uniqueID, 0);

									GameStageHelper.removeStage(serverPlayer, requiredStage);
									player.sendMessage(new TranslationTextComponent("stage.removal.message", requiredStage), Util.DUMMY_UUID);
								} else {
									++info.timer;
									setEntityTimeData(serverPlayer, uniqueID, info.timer);
								}
							} else {
								if (info.timer != 0) {
									info.timer = 0;
									setEntityTimeData(serverPlayer, uniqueID, 0);
								}
							}
						} else {
							if (GameStageHelper.hasStage(serverPlayer, requiredStage) && !GameStageHelper.hasStage(serverPlayer, nextStage)) {
								if(info.getAmount().contains("day")) {
									long worldAge = player.world.getGameTime() / 24000;
									if((int)worldAge >= time) {
										setEntityTimeData(serverPlayer, uniqueID, 0);
										GameStageHelper.addStage(serverPlayer, nextStage);
										if(removeOld) {
											GameStageHelper.removeStage(serverPlayer, requiredStage);
										}
										player.sendMessage(new TranslationTextComponent("stage.add.message", nextStage), Util.DUMMY_UUID);
									}
								} else {
									if(getEntityTimeData(serverPlayer, uniqueID) != info.timer) {
										info.timer = getEntityTimeData(serverPlayer, uniqueID);
									}

									if(info.timer >= time) {
										info.timer = 0;
										setEntityTimeData(serverPlayer, uniqueID, 0);

										GameStageHelper.addStage(serverPlayer, nextStage);
										if(removeOld) {
											GameStageHelper.removeStage(serverPlayer, requiredStage);
										}
										player.sendMessage(new TranslationTextComponent("stage.add.message", nextStage), Util.DUMMY_UUID);
									} else {
										++info.timer;
										setEntityTimeData(serverPlayer, uniqueID, info.timer);
									}
								}
							} else {
								if (info.timer != 0) {
									info.timer = 0;
									setEntityTimeData(serverPlayer, uniqueID, 0);
								}
							}
						}
					}
				}
			}
		}
    }
    
    public static void setEntityTimeData(ServerPlayerEntity player, String valueTag, int time) {
    	CompoundNBT playerData = player.getPersistentData();
		CompoundNBT data = getTag(playerData, PlayerEntity.PERSISTED_NBT_TAG);

    	data.putInt(valueTag, time);
    	playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
    }
    
    public static int getEntityTimeData(ServerPlayerEntity player, String valueTag) {
		CompoundNBT playerData = player.getPersistentData();
		CompoundNBT data = getTag(playerData, PlayerEntity.PERSISTED_NBT_TAG);
    	return data.getInt(valueTag);
    }
    
    public static CompoundNBT getTag(CompoundNBT tag, String key) {
		if(tag == null || !tag.contains(key)) {
			return new CompoundNBT();
		}
		return tag.getCompound(key);
    }

}
