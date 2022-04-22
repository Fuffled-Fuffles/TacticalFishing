package com.fuffles.tactical_fishing.lib;

import com.fuffles.tactical_fishing.TacticalFishing;

import net.minecraft.resources.ResourceLocation;

public class Resources 
{
	private static ResourceLocation tactId(String str)
	{
		return new ResourceLocation(TacticalFishing.ID, str);
	}
	
	public static final ResourceLocation ADVANCEMENT_CRITERION_FISHING_CRAFTING = tactId("fishing_crafting");
	
	public static final ResourceLocation JEI_PLUGIN = tactId("jei_plugin");
	
	public static final ResourceLocation JEI_RECIPE_CATEGORY_FISHING = tactId("fishing");
	
	public static final ResourceLocation RECIPE_GROUP_FISH_BUCKETS = tactId("fish_buckets");
	
	public static final ResourceLocation RECIPE_SERIALIZER_FISHING = tactId("fishing");
	
	public static final ResourceLocation RECIPE_TYPE_FISHING = tactId("fishing");
	
	public static final ResourceLocation TAG_FISHING_RODS = tactId("fishing_rods");
}
