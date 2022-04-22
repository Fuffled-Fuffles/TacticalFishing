package com.fuffles.tactical_fishing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fuffles.tactical_fishing.common.entity.FishingVisual;
import com.fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import com.fuffles.tactical_fishing.lib.CriteriaTriggers;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements FishingVisual
{
	private FishingRecipe fishingRecipe = null;

	private ItemEntityMixin(EntityType<?> arg, Level arg2) { super(arg, arg2); }
	
	@Shadow
	public ItemStack getItem() { return ItemStack.EMPTY; }
	
	@Override
	public boolean isFishingVisual() 
	{
		return this.fishingRecipe != null;
	}

	@Override
	public void setFishingRecipe(FishingRecipe recipe) 
	{
		this.fishingRecipe = recipe;
	}

	@Override
	public FishingRecipe getFishingRecipe() 
	{
		return this.fishingRecipe;
	}

	@Inject(method = "isMergable()Z", at = @At("HEAD"), cancellable = true)
	private void isMergable(CallbackInfoReturnable<Boolean> cbr) 
	{
		if (this.isFishingVisual())
		{
			cbr.setReturnValue(false);
		}
	}
	
	@Inject(method = "playerTouch(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"), cancellable = true)
	public void playerTouch(Player player, CallbackInfo cb) 
	{
		if (!this.level.isClientSide) 
		{
			if (this.isFishingVisual())
    		{
    			FishingRecipe recipe = this.getFishingRecipe();
    			if (recipe.matches("VERIFY", player.getInventory(), NonNullList.of(ItemStack.EMPTY, this.getItem())))
    			{
    				InteractionHand targetHand = recipe.getIngredient().test(player.getItemInHand(InteractionHand.MAIN_HAND)) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        			player.setItemInHand(targetHand, recipe.assemble(null));
        			player.swing(targetHand, true);
        			player.level.playLocalSound(player.getX(), player.getY() + 1D, player.getZ(), SoundEvents.GENERIC_SPLASH, player.getSoundSource(), 0.8F, 0.8F + player.level.random.nextFloat() * 0.4F, false);
        			player.level.addFreshEntity(new ExperienceOrb(player.level, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, this.random.nextInt(6) + 1));
        			CriteriaTriggers.FISHING_CRAFTING.trigger((ServerPlayer)player, recipe);
        			this.discard();
        			cb.cancel();
    			}
    			else
    			{
    				this.setFishingRecipe(null);
    			}
    		}
		}
	}
}
