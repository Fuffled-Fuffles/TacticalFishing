package fuffles.tactical_fishing.lib;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemTags 
{
	public static final TagKey<Item> RODS = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), Resources.TAG_FISHING_RODS);
}
