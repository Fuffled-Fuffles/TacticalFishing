package fuffles.tactical_fishing.integration.jei;

import fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import fuffles.tactical_fishing.lib.RecipeTypes;
import fuffles.tactical_fishing.lib.Resources;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class TacticalPlugin implements IModPlugin
{	
	public static final RecipeType<FishingRecipe> FISHING = new RecipeType<>(Resources.JEI_RECIPE_CATEGORY_FISHING, FishingRecipe.class);
	
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
		registry.addRecipes(this.fishingCategory.getRecipeType(), mc.level.getRecipeManager().getAllRecipesFor(RecipeTypes.FISHING));
	}

	@Override
	public ResourceLocation getPluginUid() 
	{
		return Resources.JEI_PLUGIN;
	}
}
