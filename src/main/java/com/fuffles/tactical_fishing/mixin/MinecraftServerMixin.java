package com.fuffles.tactical_fishing.mixin;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fuffles.tactical_fishing.DatapackWriter;
import com.fuffles.tactical_fishing.TacticalFishing;

import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.DataPackConfig;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin 
{
	@Inject(method = "configurePackRepository(Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/world/level/DataPackConfig;Z)Lnet/minecraft/world/level/DataPackConfig;", at = @At("HEAD"))
	private static void configurePackRepository(PackRepository packRepo, DataPackConfig config, boolean flag, CallbackInfoReturnable<DataPackConfig> cbr) 
	{
		if (TacticalFishing.Config.COMMON.writeDatapack.get() == true)
		{
			for (RepositorySource repo : packRepo.sources)
			{
				if (repo instanceof FolderRepositorySource folderRepo && folderRepo.packSource == PackSource.WORLD)
				{
					if (!folderRepo.folder.isDirectory())
					{
						folderRepo.folder.mkdirs();
					}
					File tacticalFishing = new File(folderRepo.folder, TacticalFishing.ID + " (in-built)");
					if (!tacticalFishing.exists() || DatapackWriter.isPackOutdated(tacticalFishing))
					{
						long timestamp = System.currentTimeMillis();
						if (!tacticalFishing.exists())
						{
							tacticalFishing.mkdir();
						}
						DatapackWriter.write(tacticalFishing, TacticalFishing.ID, PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()), "In-built fish bucket recipes for Tactical Fishing");
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
