package fuffles.tactical_fishing.lib;

import fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeTypes 
{	
	public static final RecipeType<FishingRecipe> FISHING = new RecipeType<FishingRecipe>() {
		public String toString() { return Resources.RECIPE_TYPE_FISHING.toString(); }
	};
}
