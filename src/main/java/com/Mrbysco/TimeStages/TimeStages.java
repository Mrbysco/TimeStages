package com.mrbysco.timestages;

import java.util.ArrayList;

import com.mrbysco.timestages.proxy.CommonProxy;
import com.mrbysco.timestages.util.CurrentMilliTime;
import com.mrbysco.timestages.util.TimeHelper;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public class TimeStages {
	
	@Instance(Reference.MOD_ID)
	public static TimeStages instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;
	
	public static final LoggingHelper LOG = new LoggingHelper(Reference.MOD_NAME);
		
	public static ArrayList<StageInfo> timers = new ArrayList<>();
	
	private static StageInfo timer_info;
	
	public static void addTimerInfo(String stage, String nextStage, int time, String amount, boolean removal, boolean removeOld)
	{
		// Check if the info doesn't already exist
		timer_info = new StageInfo(stage, nextStage, time, amount, removal, removeOld);
		if(timers.contains(timer_info))
			return;
		else
			timers.add(timer_info);
	}
	
    @EventHandler
    public void Preinit(FMLPreInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
	public CurrentMilliTime milliTime;

    public TimeStages() {
		milliTime = new CurrentMilliTime();
	}
    
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event)
    {   
    	if (event.phase.equals(TickEvent.Phase.START) && event.side.isServer())
		{
    		if (PlayerUtils.isPlayerReal(event.player)) {
                final EntityPlayer player = (EntityPlayer) event.player;
            	if (System.currentTimeMillis() > milliTime.getMilliTime() + 1000L)
				{
					milliTime.setMilliTimeToCurrent();

					for (StageInfo info : this.timers) {
						if (info.getStage().isEmpty())
							return;
                	
						final boolean removal = info.isRemoval();

	                	final String stage = info.getStage();
	                	final String nextStage = info.getNextStage();
	                	final int time = TimeHelper.getProperTime(info.getTime(), info.getAmount());
	                	final String amount = info.getAmount();
	                	final boolean removeOld = info.isRemoveOld();
	                	
	                	if (removal)
	                	{
	            			String TimedName = "remove" + capitalizeFirstLetter(stage);

	                		if(GameStageHelper.hasStage(player, stage))
	                		{
	                			
	                			if(getEntityTimeData(player, TimedName) != info.timer)
	                			{
	                				info.timer = getEntityTimeData(player, TimedName);
	                			}
	                			
	                			if(info.timer >= time)
	        			        {
	                				info.timer = 0;
	                				setEntityTimeData(player, TimedName, 0);
	                				
	                				GameStageHelper.removeStage(player, stage);
	                    			player.sendMessage(new TextComponentTranslation("stage.removal.message", new Object[] {stage}));
	        			        }
	                			else
	                			{
	                				++info.timer;
	                				setEntityTimeData(player, TimedName, info.timer);
	                			}
	                		}
	                		else
	                		{
	                   			if (info.timer != 0)
	                   			{
	                   				info.timer = 0;
	                				setEntityTimeData(player, TimedName, 0);
	                   			}
	                		}
	                	}
	                	else
	                	{
	            			String TimedName = "add" + capitalizeFirstLetter(stage) + capitalizeFirstLetter(nextStage);

	                		if (GameStageHelper.hasStage(player, stage) && !GameStageHelper.hasStage(player, nextStage))
	                    	{

	                			if(getEntityTimeData(player, TimedName) != info.timer)
	                			{
	                				info.timer = getEntityTimeData(player, TimedName);
	                			}
	                			
	                    		if(info.timer >= time)
	        			        {
	                    			info.timer = 0;
	                    			setEntityTimeData(player, TimedName, 0);
	                        		
	                				if(removeOld)
	                				{
	                					GameStageHelper.addStage(player, nextStage);
	                					GameStageHelper.removeStage(player, stage);
	                				}
	                				else
	                				{
	                					GameStageHelper.addStage(player, nextStage);
	                				}
	                    			player.sendMessage(new TextComponentTranslation("stage.add.message", new Object[] {stage}));

	        			        }
	                    		else
	                    		{
	                    			++info.timer;
	                    			setEntityTimeData(player, TimedName, info.timer);
	                    		}
	                    	}
	                		else
	                		{
	                			if (info.timer != 0)
	                   			{
	                   				info.timer = 0;
	                   				setEntityTimeData(player, TimedName, 0);
	                   			}
	                		}
	                	}
					}
                }
        	}
    	}
    }
    
    public static void setEntityTimeData(EntityPlayer player, String valueTag, int time)
    {
    	NBTTagCompound playerData = player.getEntityData();
    	NBTTagCompound data = getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
    	
    	data.setInteger(valueTag, time);
    	playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
    }
    
    public static int getEntityTimeData(EntityPlayer player, String valueTag)
    {
    	NBTTagCompound playerData = player.getEntityData();
    	NBTTagCompound data = getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
    	return data.getInteger(valueTag);
    }
    
    public static NBTTagCompound getTag(NBTTagCompound tag, String key) {
		if(tag == null || !tag.hasKey(key)) {
			return new NBTTagCompound();
		}
		return tag.getCompoundTag(key);
    }
    
    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
