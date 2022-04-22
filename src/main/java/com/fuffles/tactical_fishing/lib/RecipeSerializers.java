package com.fuffles.tactical_fishing.lib;

import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeSerializers 
{
	public static final RecipeSerializer<FishingRecipe> FISHING = new FishingRecipe.Serializer();
}
