package com.fuffles.tactical_fishing.integration.jei;

import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import com.fuffles.tactical_fishing.lib.RecipeTypes;
import com.fuffles.tactical_fishing.lib.Resources;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class TacticalPlugin implements IModPlugin
{	
	private IRecipeCategory<FishingRecipe> fishingCategory;
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) 
	{
		registry.addRecipeCategories(this.fishingCategory = new FishingRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registry) 
	{
		Minecraft mc = Minecraft.getInstance();
		registry.addRecipes(mc.level.getRecipeManager().getAllRecipesFor(RecipeTypes.FISHING), this.fishingCategory.getUid());
	}

	@Override
	public ResourceLocation getPluginUid() 
	{
		return Resources.JEI_PLUGIN;
	}
}
