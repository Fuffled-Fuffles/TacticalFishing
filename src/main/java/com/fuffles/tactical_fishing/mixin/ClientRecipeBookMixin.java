package com.fuffles.tactical_fishing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fuffles.tactical_fishing.lib.RecipeTypes;

import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.item.crafting.IRecipe;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin 
{
	@Inject(method = "getCategory(Lnet/minecraft/item/crafting/IRecipe;)Lnet/minecraft/client/util/RecipeBookCategories;", at = @At("HEAD"), cancellable = true)
	private static void getCategory(IRecipe<?> recipe, CallbackInfoReturnable<RecipeBookCategories> cbr)
	{
		if (recipe.getType() == RecipeTypes.FISHING)
		{
			cbr.setReturnValue(RecipeBookCategories.UNKNOWN);
		}
	}
}
