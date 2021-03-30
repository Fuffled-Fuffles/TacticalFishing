package com.fuffles.tactical_fishing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("tactical_fishing")
public class TacticalFishing
{
	public static final Logger LOG = LogManager.getLogger();
	
    public TacticalFishing()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onItemFished);
    }

    public void onItemFished(ItemFishedEvent event)
    {
    	PlayerEntity player = event.getPlayer();
    	if (player != null)
    	{
    		Hand bucket_carrier = player.getItemInHand(Hand.MAIN_HAND).getItem().equals(Items.WATER_BUCKET) ? Hand.MAIN_HAND : 
    			player.getItemInHand(Hand.OFF_HAND).getItem().equals(Items.WATER_BUCKET) ? Hand.OFF_HAND : 
    			null;
    		if (bucket_carrier != null)
    		{
    			for (ItemStack drop : event.getDrops())
    			{
    				String key = drop.getItem().getRegistryName().getPath();
    				if (key.contains("raw_"))
    				{
    					key = key.substring(4);
    				}
    				Item possible_bucket = ForgeRegistries.ITEMS.getValue(new ResourceLocation(drop.getItem().getRegistryName().getNamespace(), key + "_bucket"));
    				if (possible_bucket != null && !possible_bucket.equals(Items.AIR))
    				{
    					player.setItemInHand(bucket_carrier, possible_bucket.getDefaultInstance());
    					event.setCanceled(true);
    					break;
    				}
    			}
    		}
    	}
    }
}
