package com.fuffles.tactical_fishing.lib;

import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class RecipeTypes 
{
	private static <T extends IRecipe<?>> IRecipeType<T> create(ResourceLocation id)
	{
		return Registry.register(Registry.RECIPE_TYPE, id, new IRecipeType<T>() {
			public String toString() { return id.toString(); }
		});
	}
	
	public static final IRecipeType<FishingRecipe> FISHING = RecipeTypes.create(Resources.RECIPE_TYPE_FISHING);
}
