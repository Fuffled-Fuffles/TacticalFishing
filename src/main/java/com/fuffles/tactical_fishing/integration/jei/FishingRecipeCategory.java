package com.fuffles.tactical_fishing.integration.jei;

import java.util.Arrays;

import com.fuffles.tactical_fishing.TacticalFishing;
import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import com.fuffles.tactical_fishing.lib.Resources;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.fluids.FluidStack;

public class FishingRecipeCategory implements IRecipeCategory<FishingRecipe>
{
	private static final ResourceLocation FISHING_ATLAS = new ResourceLocation(TacticalFishing.ID, "textures/gui/fishing_recipe_atlas_pre18.png");
	
	private final IDrawable icon;
	private final IDrawable bg;
	
	public FishingRecipeCategory(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableIngredient(Items.FISHING_ROD.getDefaultInstance());
		this.bg = guiHelper.drawableBuilder(FISHING_ATLAS, 0, 0, 125, 41).setTextureSize(128, 64).build();
	}
	
	@Override
	public String getTitle() 
	{
		return LanguageMap.getInstance().getOrDefault("jei.recipe_category.tactical_fishing.fishing");
	}

	@Override
	public IDrawable getIcon() 
	{
		return this.icon;
	}
	
	@Override
	public IDrawable getBackground() 
	{
		return this.bg;
	}

	@Override
	public ResourceLocation getUid() 
	{
		return Resources.JEI_RECIPE_CATEGORY_FISHING;
	}

	@Override
	public Class<FishingRecipe> getRecipeClass() 
	{
		return FishingRecipe.class;
	}

	@Override
	public void setIngredients(FishingRecipe recipe, IIngredients ingredients) 
	{
		ingredients.setInputIngredients(Arrays.asList(recipe.getIngredient(), recipe.getRod(), recipe.getCatch()));
		ingredients.setInput(VanillaTypes.FLUID, FluidStack.EMPTY);
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}

	@Override
	public void setRecipe(IRecipeLayout layout, FishingRecipe recipe, IIngredients ingredients) 
	{
		IGuiItemStackGroup guiStacks = layout.getItemStacks();
		
		guiStacks.init(0, true, 0, 0);
		guiStacks.init(1, true, 49, 0);
		guiStacks.init(2, true, 78, 23);
		
		guiStacks.init(3, false, 107, 0);
		
		guiStacks.set(ingredients);
	}
}
