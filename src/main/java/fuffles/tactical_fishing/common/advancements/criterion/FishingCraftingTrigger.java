package fuffles.tactical_fishing.common.advancements.criterion;

import com.google.gson.JsonObject;

import fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import fuffles.tactical_fishing.lib.Resources;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class FishingCraftingTrigger extends SimpleCriterionTrigger<FishingCraftingTrigger.Instance>
{
	@Override
	public ResourceLocation getId() 
	{
		return Resources.ADVANCEMENT_CRITERION_FISHING_CRAFTING;
	}

	@Override
	protected Instance createInstance(JsonObject json, ContextAwarePredicate composite, DeserializationContext context) 
	{
		return new Instance(composite, RecipePredicate.Fishing.fromJson(json.get("recipe")));
	}
	
	public void trigger(ServerPlayer player, FishingRecipe recipe) 
	{
		this.trigger(player, (inst) -> {
	         return inst.matches(recipe);
		});
	}
	
	public static class Instance extends AbstractCriterionTriggerInstance 
	{
		private final RecipePredicate.Fishing recipe;
		
		public Instance(ContextAwarePredicate composite, RecipePredicate.Fishing recipe) 
		{
			super(Resources.ADVANCEMENT_CRITERION_FISHING_CRAFTING, composite);
			this.recipe = recipe;
		}
		
		public boolean matches(FishingRecipe recipe)
		{
			return this.recipe.matches(recipe);
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext context) 
		{
			JsonObject jsonObj = super.serializeToJson(context);
			jsonObj.add("recipe", this.recipe.serializeToJson());
			return jsonObj;
		}
	}
}
