package com.fuffles.tactical_fishing.common.item.crafting;

import com.fuffles.tactical_fishing.TacticalFishing;
import com.fuffles.tactical_fishing.lib.RecipeSerializers;
import com.fuffles.tactical_fishing.lib.RecipeTypes;
import com.fuffles.tactical_fishing.lib.Resources;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FishingRecipe implements Recipe<Container>
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
	public boolean matches(Container container, Level level) 
	{
		return false;
	}
	
	public boolean matches(String debugPhase, Inventory playerInv, NonNullList<ItemStack> fish)
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
	public ItemStack assemble(Container container) 
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
	public RecipeSerializer<FishingRecipe> getSerializer() 
	{
		return RecipeSerializers.FISHING;
	}

	@Override
	public RecipeType<?> getType()
	{
		return RecipeTypes.FISHING;
	}
	
	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FishingRecipe>
	{
		public Serializer()
		{
			this.setRegistryName(Resources.RECIPE_SERIALIZER_FISHING);
		}

		@Override
		public FishingRecipe fromJson(ResourceLocation id, JsonObject json) 
		{
			String group = GsonHelper.getAsString(json, "group", "");
			Ingredient rod = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "rod"));
			Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
			Ingredient fish = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "catch"));
			ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			return new FishingRecipe(id, group, rod, ingredient, fish, result);
		}

		@Override
		public FishingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) 
		{
			String group = buffer.readUtf();
			Ingredient rod = Ingredient.fromNetwork(buffer);
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			Ingredient fish = Ingredient.fromNetwork(buffer);
			ItemStack result = buffer.readItem();
			return new FishingRecipe(id, group, rod, ingredient, fish, result);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, FishingRecipe recipe) 
		{
			buffer.writeUtf(recipe.getGroup());
			recipe.rod.toNetwork(buffer);
			recipe.ingredient.toNetwork(buffer);
			recipe.fish.toNetwork(buffer);
			buffer.writeItem(recipe.getResultItem());
		}
	}
}
