package com.fuffles.tactical_fishing.common;

import com.fuffles.tactical_fishing.lib.CriteriaTriggers;
import com.fuffles.tactical_fishing.lib.RecipeSerializers;
import com.fuffles.tactical_fishing.lib.RecipeTypes;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Registry 
{
	private static void registerRecipeType(RecipeType<?> type)
	{
		ResourceLocation id = new ResourceLocation(type.toString());
		if (!net.minecraft.core.Registry.RECIPE_TYPE.containsKey(id))
		{
			net.minecraft.core.Registry.register(net.minecraft.core.Registry.RECIPE_TYPE, new ResourceLocation(type.toString()), type);
		}
	}
	
	@SubscribeEvent
	public static void onRecipeSerializerRegistry(RegistryEvent.Register<RecipeSerializer<?>> event)
	{
		event.getRegistry().register(RecipeSerializers.FISHING);
		Registry.registerRecipeType(RecipeTypes.FISHING);
		@SuppressWarnings("unused")
		CriterionTrigger<?> callOnly = CriteriaTriggers.FISHING_CRAFTING;
	}
}
