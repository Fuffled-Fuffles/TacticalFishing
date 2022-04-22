package com.fuffles.tactical_fishing.common.advancements.criterion;

import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import com.fuffles.tactical_fishing.lib.Resources;
import com.google.gson.JsonObject;

import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class FishingCraftingTrigger extends AbstractCriterionTrigger<FishingCraftingTrigger.Instance>
{
	@Override
	public ResourceLocation getId() 
	{
		return Resources.ADVANCEMENT_CRITERION_FISHING_CRAFTING;
	}

	@Override
	protected Instance createInstance(JsonObject json, EntityPredicate.AndPredicate composite, ConditionArrayParser context) 
	{
		return new Instance(composite, RecipePredicate.Fishing.fromJson(json.get("recipe")));
	}
	
	public void trigger(ServerPlayerEntity player, FishingRecipe recipe) 
	{
		this.trigger(player, (inst) -> {
	         return inst.matches(recipe);
		});
	}
	
	public static class Instance extends CriterionInstance 
	{
		private final RecipePredicate.Fishing recipe;
		
		public Instance(EntityPredicate.AndPredicate composite, RecipePredicate.Fishing recipe) 
		{
			super(Resources.ADVANCEMENT_CRITERION_FISHING_CRAFTING, composite);
			this.recipe = recipe;
		}
		
		public boolean matches(FishingRecipe recipe)
		{
			return this.recipe.matches(recipe);
		}
		
		@Override
		public JsonObject serializeToJson(ConditionArraySerializer context) 
		{
			JsonObject jsonObj = super.serializeToJson(context);
			jsonObj.add("recipe", this.recipe.serializeToJson());
			return jsonObj;
		}
	}
}
