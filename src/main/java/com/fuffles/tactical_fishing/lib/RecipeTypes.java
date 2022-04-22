package com.fuffles.tactical_fishing.lib;

import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeTypes 
{
	private static <T extends Recipe<?>> RecipeType<T> create(ResourceLocation id)
	{
		return Registry.register(Registry.RECIPE_TYPE, id, new RecipeType<T>() {
			public String toString() { return id.toString(); }
		});
	}
	
	public static final RecipeType<FishingRecipe> FISHING = RecipeTypes.create(Resources.RECIPE_TYPE_FISHING);
}
