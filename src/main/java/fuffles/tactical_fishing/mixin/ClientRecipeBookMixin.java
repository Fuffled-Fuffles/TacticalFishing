package fuffles.tactical_fishing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fuffles.tactical_fishing.lib.RecipeTypes;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.Recipe;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin 
{
	@Inject(method = "getCategory(Lnet/minecraft/world/item/crafting/Recipe;)Lnet/minecraft/client/RecipeBookCategories;", at = @At("HEAD"), cancellable = true)
	private static void getCategory(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookCategories> cbr)
	{
		if (recipe.getType() == RecipeTypes.FISHING)
		{
			cbr.setReturnValue(RecipeBookCategories.UNKNOWN);
		}
	}
}
