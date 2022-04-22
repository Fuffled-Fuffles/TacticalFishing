package com.fuffles.tactical_fishing.mixin;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fuffles.tactical_fishing.DatapackWriter;
import com.fuffles.tactical_fishing.TacticalFishing;

import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.codec.DatapackCodec;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin 
{
	@Inject(method = "configurePackRepository(Lnet/minecraft/resources/ResourcePackList;Lnet/minecraft/util/datafix/codec/DatapackCodec;Z)Lnet/minecraft/util/datafix/codec/DatapackCodec;", at = @At("HEAD"))
	private static void configurePackRepository(ResourcePackList packRepo, DatapackCodec config, boolean flag, CallbackInfoReturnable<DatapackCodec> cbr) 
	{
		if (TacticalFishing.Config.COMMON.writeDatapack.get() == true)
		{
			for (IPackFinder repo : packRepo.sources)
			{
				if (repo instanceof FolderPackFinder && ((FolderPackFinder)repo).packSource == IPackNameDecorator.WORLD)
				{
					if (!((FolderPackFinder)repo).folder.isDirectory())
					{
						((FolderPackFinder)repo).folder.mkdirs();
					}
					File tacticalFishing = new File(((FolderPackFinder)repo).folder, TacticalFishing.ID + " (in-built)");
					if (!tacticalFishing.exists() || DatapackWriter.isPackOutdated(tacticalFishing))
					{
						long timestamp = System.currentTimeMillis();
						if (!tacticalFishing.exists())
						{
							tacticalFishing.mkdir();
						}
						
						DatapackWriter.write(tacticalFishing, TacticalFishing.ID, SharedConstants.getCurrentVersion().getPackVersion(), "In-built fish bucket recipes for Tactical Fishing");
						TacticalFishing.LOG.info("In-built datapack successfully created/updated in " + ((double)(System.currentTimeMillis() - timestamp) / 1000.0D) + "s");
					}
					else
					{
						TacticalFishing.LOG.info("In-built datapack up-to-date, no further actions needed");
					}
					break;
				}
			}
		}
		else
		{
			TacticalFishing.LOG.info("Skipping writing the in-built datapack as instructed in the config");
		}
	}
}
