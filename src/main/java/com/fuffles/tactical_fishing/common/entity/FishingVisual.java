package com.fuffles.tactical_fishing.common.entity;

import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;

public interface FishingVisual
{
	abstract boolean isFishingVisual();
	
	abstract void setFishingRecipe(FishingRecipe recipe);
	
	abstract FishingRecipe getFishingRecipe();
}
