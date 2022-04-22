package com.fuffles.tactical_fishing.common.advancements.criterion;

import javax.annotation.Nullable;

import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class RecipePredicate 
{
	private static final RecipePredicate.Fishing ANY = new RecipePredicate.Fishing(null, null, NonNullList.create(), null);
	
	private final ResourceLocation id;
	private final String group;
	private final NonNullList<Ingredient> ingredients;
	private final ItemStack result;
	
	private RecipePredicate(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, ItemStack result)
	{
		this.id = id;
		this.group = group;
		this.ingredients = ingredients;
		this.result = result;
	}
	
	protected boolean innerMatches(Recipe<? extends Container> recipe)
	{
		if (this.id != null && !this.id.equals(recipe.getId()))
		{
			return false;
		}
		else if (this.group != null && !this.group.equals(recipe.getGroup()))
		{
			return false;
		}
		else if (this.result != null && !ItemStack.matches(this.result, recipe.getResultItem()))
		{
			return false;
		}
		else
		{
			if (this.ingredients.size() > 0)
			{
				for (Ingredient predicateIngredient : this.ingredients)
				{
					if (!recipe.getIngredients().contains(predicateIngredient))
					{
						return false;
					}
				}
			}
			return true;
		}
	}
	
	public JsonElement serializeToJson()
	{
		if (this == ANY)
		{
			return JsonNull.INSTANCE;
		}
		else
		{
			JsonObject jsonObj = new JsonObject();
			if (this.id != null)
			{
				jsonObj.addProperty("id", this.id.toString());
			}
			if (this.group != null)
			{
				jsonObj.addProperty("group", this.group);
			}
			if (this.ingredients.size() > 0)
			{
				
			}
			if (this.result != null)
			{
				jsonObj.addProperty("result", this.result.getItem().getRegistryName().toString());
			}
			return jsonObj;
		}
	}
	
	public static class Fishing extends RecipePredicate
	{	
		public Fishing(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, ItemStack result)
		{
			super(id, group, ingredients, result);
		}
		
		public boolean matches(FishingRecipe recipe)
		{
			return this.innerMatches(recipe);
		}
		
		public static Fishing fromJson(@Nullable JsonElement json) 
		{
			if (json != null && !json.isJsonNull()) 
			{
				JsonObject jsonObj = GsonHelper.convertToJsonObject(json, "recipe");
				String rawId = GsonHelper.getAsString(jsonObj, "id", null);
				
				ResourceLocation id = rawId == null ? null : new ResourceLocation(rawId);
				String group = GsonHelper.getAsString(jsonObj, "group", null);
				
				@SuppressWarnings("unused")
				JsonArray rawIngredients = GsonHelper.getAsJsonArray(jsonObj, "ingredients", null);
				//lazy
				NonNullList<Ingredient> ingredients = NonNullList.create();
				
				JsonObject rawResult = GsonHelper.getAsJsonObject(jsonObj, "result", null);
				ItemStack result = rawResult == null ? null : ShapedRecipe.itemStackFromJson(rawResult);
				
				return new Fishing(id, group, ingredients, result);
			}
			return ANY;
		}
	}
}
