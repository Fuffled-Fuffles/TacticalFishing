package com.fuffles.tactical_fishing.integration.jei;

import com.fuffles.tactical_fishing.TacticalFishing;
import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import com.fuffles.tactical_fishing.lib.Resources;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class FishingRecipeCategory implements IRecipeCategory<FishingRecipe>
{
	private static final ResourceLocation FISHING_ATLAS = new ResourceLocation(TacticalFishing.ID, "textures/gui/fishing_recipe_atlas.png");
	
	private final IDrawable icon;
	private final IDrawable bg;
	private final IDrawable bobber;
	
	public FishingRecipeCategory(IGuiHelper guiHelper)
	{
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, Items.FISHING_ROD.getDefaultInstance());
		this.bg = guiHelper.drawableBuilder(FISHING_ATLAS, 0, 0, 125, 41).setTextureSize(128, 64).build();
		this.bobber = guiHelper.drawableBuilder(FISHING_ATLAS, 0, 48, 8, 13).setTextureSize(128, 64).build();
	}
	
	@Override
	public Component getTitle() 
	{
		return new TranslatableComponent("jei.recipe_category.tactical_fishing.fishing");
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
	public void setRecipe(IRecipeLayoutBuilder builder, FishingRecipe recipe, IFocusGroup focuses) 
	{
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
		.addIngredients(recipe.getIngredient());
		
		builder.addSlot(RecipeIngredientRole.CATALYST, 50, 1)
		.addIngredients(recipe.getRod());
		
		builder.addSlot(RecipeIngredientRole.INPUT, 79, 24)
		.addIngredients(recipe.getCatch())
		.setOverlay(this.bobber, 4, -9);
		
		builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 1)
		.addItemStack(recipe.getResultItem());
	}
}
