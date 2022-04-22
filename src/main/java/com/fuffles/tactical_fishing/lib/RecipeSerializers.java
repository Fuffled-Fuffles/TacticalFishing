package com.fuffles.tactical_fishing.lib;

import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;

import net.minecraft.item.crafting.IRecipeSerializer;

public class RecipeSerializers 
{
	public static final IRecipeSerializer<FishingRecipe> FISHING = new FishingRecipe.Serializer();
}
