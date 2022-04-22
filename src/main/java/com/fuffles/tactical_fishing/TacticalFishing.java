package com.fuffles.tactical_fishing;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fuffles.tactical_fishing.common.Registry;
import com.fuffles.tactical_fishing.common.entity.FishingVisual;
import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import com.fuffles.tactical_fishing.lib.RecipeTypes;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(TacticalFishing.ID)
public class TacticalFishing implements Proxy
{
	public static final String ID = "tactical_fishing";
	public static final Logger LOG = LogManager.getLogger();
	
	public static void DEBUG(String str) { if (TacticalFishing.Config.COMMON.debug.get()) { TacticalFishing.LOG.debug(str); } }
	public static boolean DEBUG(boolean bool, String str) { TacticalFishing.DEBUG(str); return bool; }
	
    public TacticalFishing()
    {
    	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC, TacticalFishing.ID + ".toml");
        this.init();
    }
    
    @Override
	public void init() 
    {
    	this.getFMLBus().register(Registry.class);
    	
		this.getForgeBus().addListener(this::onItemFished);
	}
    
    private Optional<FishingRecipe> tryMatch(FishingRecipe recipe, ServerPlayer player, NonNullList<ItemStack> fish)
    {
    	return recipe.matches("INITIAL", player.getInventory(), fish) ? Optional.of(recipe) : Optional.empty();
    }
    
    private Optional<FishingRecipe> getFishingRecipe(ServerPlayer player, NonNullList<ItemStack> fish)
    {
    	return player.getServer().getRecipeManager().byType(RecipeTypes.FISHING).values().stream().flatMap((recipe) -> {
    		return this.tryMatch((FishingRecipe)recipe, player, fish).stream();
    	}).findFirst();
    }
    
    public void onItemFished(ItemFishedEvent event)
    {
    	Player player = event.getPlayer();
    	if (player != null && !player.level.isClientSide && !event.isCanceled())
    	{
    		ServerPlayer sPlayer = (ServerPlayer)player;
    		Optional<FishingRecipe> matches = this.getFishingRecipe(sPlayer, event.getDrops());
    		if (matches.isPresent())
    		{
    			FishingRecipe recipe = matches.get();
    			FishingHook hook = event.getHookEntity();
    			for (ItemStack fish : event.getDrops())
    			{
    				if (recipe.getCatch().test(fish))
    				{
    					ItemEntity visual = new ItemEntity(hook.level, hook.getX(), hook.getY(), hook.getZ(), fish);
    					double dx = player.getX() - hook.getX();
    					double dy = player.getY() - hook.getY();
    					double dz = player.getZ() - hook.getZ();
    					double vel = 0.1D;
    					visual.setDeltaMovement(dx * vel, dy * vel + Math.sqrt(Math.sqrt(dx * dx + dy * dy + dz * dz)) * 0.08D, dz * 0.1D);
    					if (visual instanceof FishingVisual fv)
    					{
    						fv.setFishingRecipe(recipe);
    					}
    					hook.level.addFreshEntity(visual);
    					break;
    				}
    			}
    			event.setCanceled(true);
    		}
    	}
    }
    
    public static class Config
    {
    	public static class Common
    	{
    		public final BooleanValue debug;
    		public final BooleanValue writeDatapack;
    		public final BooleanValue verifyDatapack;
    		public final BooleanValue writeFishBucketRecipes;
    		
    		private Common(ForgeConfigSpec.Builder builder) 
    		{
    			builder.push("general");
    			
    			this.debug = Config.build(builder, "Debug", false,
    				"When true writes a step-by-step feedback to your log file when the mod is testing if a certain combination matches a recipe's requirement; Default: false"
    			);
    			
    			this.writeDatapack = Config.build(builder, "WriteDatapack", true, 
    				"When true writes the in-built datapack on world creation; Default: true",
    				"Keep in mind if you set this to false there will be no 'tactical_fishing:fishing_rods' tag nor any of the fish bucket recipes"
    			);
    			builder.pop();
    			builder.push("datapack");
    			
    			this.verifyDatapack = Config.build(builder, "VerifyDatapack", true, 
    				"When true makes sure to update the datapack if your mod list changes; Default: true"
    			);
    			
    			this.writeFishBucketRecipes = Config.build(builder, "WriteFishBucketRecipes", true,
    				"When true writes .json files for each possible fish bucket; Default: true",
    				"If you want to use this mod for the fishing crafting without the fish buckets, you should set this to false"
    			);
    			
    			builder.pop();
    		}
    	}
    	
    	private static final ForgeConfigSpec COMMON_SPEC;
        public static final Common COMMON;
        static 
        {
            Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
            COMMON_SPEC = specPair.getRight();
            COMMON = specPair.getLeft();
        }
    	
    	private static BooleanValue build(ForgeConfigSpec.Builder builder, String key, boolean def, String... comments)
		{
			return builder.comment(comments).define(key, def);
		}
    }
}
