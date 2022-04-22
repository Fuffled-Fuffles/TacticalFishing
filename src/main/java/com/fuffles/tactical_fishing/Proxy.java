package com.fuffles.tactical_fishing;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public interface Proxy 
{
	abstract void init();
	
	default IEventBus getFMLBus()
	{
		return FMLJavaModLoadingContext.get().getModEventBus();
	}
	
	default IEventBus getForgeBus()
	{
		return MinecraftForge.EVENT_BUS;
	}
}
