package com.fuffles.tactical_fishing.common;

import com.fuffles.tactical_fishing.lib.CriteriaTriggers;
import com.fuffles.tactical_fishing.lib.RecipeSerializers;
import com.fuffles.tactical_fishing.lib.RecipeTypes;

import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Registry 
{
	private static void registerRecipeType(IRecipeType<?> type)
	{
		ResourceLocation id = new ResourceLocation(type.toString());
		if (net.minecraft.util.registry.Registry.RECIPE_TYPE.get(id) == null)
		{
			net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.RECIPE_TYPE, new ResourceLocation(type.toString()), type);
		} 
	}
	
	@SubscribeEvent
	public static void onRecipeSerializerRegistry(RegistryEvent.Register<IRecipeSerializer<?>> event)
	{
		event.getRegistry().register(RecipeSerializers.FISHING);
		Registry.registerRecipeType(RecipeTypes.FISHING);
		@SuppressWarnings("unused")
		AbstractCriterionTrigger<?> callOnly = CriteriaTriggers.FISHING_CRAFTING;
	}
}
