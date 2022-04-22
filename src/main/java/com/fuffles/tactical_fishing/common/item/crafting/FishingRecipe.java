package com.fuffles.tactical_fishing.common.item.crafting;

import com.fuffles.tactical_fishing.TacticalFishing;
import com.fuffles.tactical_fishing.lib.RecipeSerializers;
import com.fuffles.tactical_fishing.lib.RecipeTypes;
import com.fuffles.tactical_fishing.lib.Resources;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FishingRecipe implements IRecipe<IInventory>
{
	private final ResourceLocation id;
	private final String group;
	private final Ingredient rod;
	private final Ingredient ingredient;
	private final Ingredient fish;
	private final ItemStack output;
	
	private NonNullList<Ingredient> serialized_ingredients;
	
	public FishingRecipe(ResourceLocation id, String group, Ingredient rod, Ingredient ingredient, Ingredient fish, ItemStack output)
	{
		this.id = id;
		this.group = group;
		this.rod = rod;
		this.ingredient = ingredient;
		this.fish = fish;
		this.output = output;
	}
	
	@Override
	public ResourceLocation getId()
	{
		return this.id;
	}
	
	@Override
	public String getGroup()
	{
		return this.group;
	}
	
	@Override
	@Deprecated
	public boolean matches(IInventory container, World level) 
	{
		return false;
	}
	
	public boolean matches(String debugPhase, PlayerInventory playerInv, NonNullList<ItemStack> fish)
	{
		ItemStack main = playerInv.getSelected();
		ItemStack off = playerInv.offhand.get(0);
		
		TacticalFishing.DEBUG("[" + this.getId() + "] [ + " + debugPhase + " + ] Pushing test match -> [" + main + ", " + off + "] + " + fish);
		
		if (main.isEmpty() && off.isEmpty()) 
		{ return TacticalFishing.DEBUG(false, "[" + this.getId() + "] [ + " + debugPhase + " + ] failure! Both hands empty."); }
		else if (main.isEmpty() || off.isEmpty()) 
		{ return TacticalFishing.DEBUG(false, "[" + this.getId() + "] [ + " + debugPhase + " + ] failure! One of the hands is empty."); }
		
		ItemStack rod = main.getItem() instanceof FishingRodItem ? main : off.getItem() instanceof FishingRodItem ? off : null;
		
		if (rod == null)
		{ return TacticalFishing.DEBUG(false, "[" + this.getId() + "] [ + " + debugPhase + " + ] failure! No fishing rod present."); }
		
		ItemStack other = ItemStack.matches(rod, main) ? off : main;
		
		boolean result = this.rod.test(rod) && this.ingredient.test(other) && fish.stream().anyMatch(this.fish);
		if (result) 
		{ TacticalFishing.DEBUG("[" + this.getId() + "] [ + " + debugPhase + " + ] success!"); }
		else
		{
			if (!this.rod.test(rod))
			{ TacticalFishing.DEBUG("[" + this.getId() + "] [ + " + debugPhase + " + ] failure! Rod does not match [" + Lists.newArrayList(this.rod.getItems()) + "]"); }
			if (!this.ingredient.test(other))
			{ TacticalFishing.DEBUG("[" + this.getId() + "] [ + " + debugPhase + " + ] failure! Other hand item does not match [" + Lists.newArrayList(this.ingredient.getItems()) + "]"); }
			else
			{ TacticalFishing.DEBUG("[" + this.getId() + "] [ + " + debugPhase + " + ] failure! Catch does not match [" + Lists.newArrayList(this.fish.getItems()) + "]"); }
		}
		return result;
	}
	
	@Override
	public ItemStack assemble(IInventory container) 
	{
		return this.getResultItem().copy();
	}

	@Override
	@Deprecated
	public boolean canCraftInDimensions(int x, int y) 
	{
		return true;
	}
	
	public Ingredient getRod()
	{
		return this.rod;
	}
	
	public Ingredient getIngredient()
	{
		return this.ingredient;
	}
	
	public Ingredient getCatch()
	{
		return this.fish;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients()
	{
		if (this.serialized_ingredients == null)
		{
			this.serialized_ingredients = NonNullList.of(Ingredient.EMPTY, this.rod, this.ingredient, this.fish);
		}
		
		return this.serialized_ingredients;
	}
	
	@Override
	public ItemStack getResultItem() 
	{
		return this.output;
	}

	@Override
	public ItemStack getToastSymbol() 
	{
	      return Items.FISHING_ROD.getDefaultInstance();
	}
	
	@Override
	public IRecipeSerializer<FishingRecipe> getSerializer() 
	{
		return RecipeSerializers.FISHING;
	}

	@Override
	public IRecipeType<?> getType()
	{
		return RecipeTypes.FISHING;
	}
	
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FishingRecipe>
	{
		public Serializer()
		{
			this.setRegistryName(Resources.RECIPE_SERIALIZER_FISHING);
		}

		@Override
		public FishingRecipe fromJson(ResourceLocation id, JsonObject json) 
		{
			String group = JSONUtils.getAsString(json, "group", "");
			Ingredient rod = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "rod"));
			Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "ingredient"));
			Ingredient fish = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "catch"));
			ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
			return new FishingRecipe(id, group, rod, ingredient, fish, result);
		}

		@Override
		public FishingRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) 
		{
			String group = buffer.readUtf();
			Ingredient rod = Ingredient.fromNetwork(buffer);
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			Ingredient fish = Ingredient.fromNetwork(buffer);
			ItemStack result = buffer.readItem();
			return new FishingRecipe(id, group, rod, ingredient, fish, result);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, FishingRecipe recipe) 
		{
			buffer.writeUtf(recipe.getGroup());
			recipe.rod.toNetwork(buffer);
			recipe.ingredient.toNetwork(buffer);
			recipe.fish.toNetwork(buffer);
			buffer.writeItem(recipe.getResultItem());
		}
	}
}
