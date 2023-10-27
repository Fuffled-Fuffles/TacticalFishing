package fuffles.tactical_fishing.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonElement;

import fuffles.tactical_fishing.GsonUtil;
import fuffles.tactical_fishing.TacticalFishing;
import fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import fuffles.tactical_fishing.lib.ItemTags;
import fuffles.tactical_fishing.lib.RecipeSerializers;
import fuffles.tactical_fishing.lib.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin 
{
	private static boolean mightBeValid(ResourceLocation key)
	{
		if (!key.getPath().endsWith("_bucket"))
			return false;
		for (ResourceLocation fluidKey : ForgeRegistries.FLUIDS.getKeys())
		{
			if (key.getPath().contains(fluidKey.getPath() + "_bucket"))
				return false;
		}
		return true;
	}
	
	@Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
			at = @At("HEAD"))
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resManager, ProfilerFiller profiler, CallbackInfo cb) 
	{
		if (TacticalFishing.Config.COMMON.writeDatapack.get() && TacticalFishing.Config.COMMON.writeFishBucketRecipes.get())
		{
			for (ResourceLocation registryKey : ForgeRegistries.ITEMS.getKeys())
			{
				if (!mightBeValid(registryKey))
				{
					continue;
				}
				String id = registryKey.getNamespace();
				ResourceLocation potentialFish = new ResourceLocation(id, registryKey.getPath().substring(0, registryKey.getPath().lastIndexOf("_")));
				ResourceLocation potentialRawFish = new ResourceLocation(id, "raw_" + potentialFish.getPath());
				Item fish = ForgeRegistries.ITEMS.getValue(potentialFish);
				if (fish == Items.AIR)
					fish = ForgeRegistries.ITEMS.getValue(potentialRawFish);
				if (fish != Items.AIR)
				{
					FishingRecipe recipe = new FishingRecipe(new ResourceLocation(TacticalFishing.ID, "fishing_" + registryKey.toString().replace(':', '_')), Resources.RECIPE_GROUP_FISH_BUCKETS.toString(), Ingredient.of(ItemTags.RODS), Ingredient.of(Items.WATER_BUCKET), Ingredient.of(fish.getDefaultInstance()), ForgeRegistries.ITEMS.getValue(registryKey).getDefaultInstance());
					map.put(recipe.getId(), GsonUtil.newObject((obj) -> {
						obj.addProperty("type", Resources.RECIPE_SERIALIZER_FISHING.toString());
						RecipeSerializers.FISHING.toJson(obj, recipe);
					}));
				}
			}
		}
	}
}
