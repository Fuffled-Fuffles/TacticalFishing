package fuffles.tactical_fishing.common;

import fuffles.tactical_fishing.lib.CriteriaTriggers;
import fuffles.tactical_fishing.lib.RecipeSerializers;
import fuffles.tactical_fishing.lib.RecipeTypes;
import fuffles.tactical_fishing.lib.Resources;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class Registry 
{
	@SubscribeEvent
	public static void onRegister(RegisterEvent event)
	{
		event.register(ForgeRegistries.RECIPE_SERIALIZERS.getRegistryKey(), Resources.RECIPE_SERIALIZER_FISHING, () -> RecipeSerializers.FISHING);
		event.register(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), Resources.RECIPE_TYPE_FISHING, () -> RecipeTypes.FISHING);
		@SuppressWarnings("unused")
		CriterionTrigger<?> callOnly = CriteriaTriggers.FISHING_CRAFTING;
	}
}
