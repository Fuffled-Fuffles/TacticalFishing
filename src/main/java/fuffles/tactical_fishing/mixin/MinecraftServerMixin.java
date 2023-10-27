package fuffles.tactical_fishing.mixin;

import java.io.File;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fuffles.tactical_fishing.DatapackWriter;
import fuffles.tactical_fishing.TacticalFishing;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin 
{
	@Shadow
	public abstract RecipeManager getRecipeManager();
	
	@SuppressWarnings("deprecation")
	@Inject(method = "configurePackRepository(Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/world/level/DataPackConfig;ZLnet/minecraft/world/flag/FeatureFlagSet;)Lnet/minecraft/world/level/WorldDataConfiguration;", at = @At("HEAD"))
	private static void configurePackRepository(PackRepository packRepo, DataPackConfig config, boolean flag, FeatureFlagSet flagSet, CallbackInfoReturnable<WorldDataConfiguration> cbr) 
	{
		if (TacticalFishing.Config.COMMON.writeDatapack.get() == true)
		{
			for (RepositorySource repo : packRepo.sources)
			{
				if (repo instanceof FolderRepositorySource folderRepo && folderRepo.packSource == PackSource.WORLD)
				{
					if (!folderRepo.folder.toFile().isDirectory())
					{
						folderRepo.folder.toFile().mkdirs();
					}
					File tacticalFishing = new File(folderRepo.folder.toFile(), TacticalFishing.ID + " (in-built)");
					boolean setup = !tacticalFishing.exists();
					if (setup || DatapackWriter.isPackOutdated(tacticalFishing))
					{
						long timestamp = System.currentTimeMillis();
						if (setup)
						{
							tacticalFishing.mkdir();
						}
						
						List<Recipe<?>> toUpdate = DatapackWriter.write(tacticalFishing, TacticalFishing.ID, SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA), "In-built fish bucket recipes for Tactical Fishing");
						TacticalFishing.LOG.info("In-built datapack successfully created/updated in " + ((double)(System.currentTimeMillis() - timestamp) / 1000.0D) + "s");
						if (!toUpdate.isEmpty())
						{
							
						}
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
