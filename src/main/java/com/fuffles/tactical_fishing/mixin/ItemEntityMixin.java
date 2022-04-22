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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements FishingVisual
{
	private FishingRecipe fishingRecipe = null;

	private ItemEntityMixin(EntityType<?> arg, World arg2) { super(arg, arg2); }
	
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
	
	@Inject(method = "playerTouch(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"), cancellable = true)
	public void playerTouch(PlayerEntity player, CallbackInfo cb) 
	{
		if (!this.level.isClientSide) 
		{
			if (this.isFishingVisual())
    		{
    			FishingRecipe recipe = this.getFishingRecipe();
    			if (recipe.matches("VERIFY", player.inventory, NonNullList.of(ItemStack.EMPTY, this.getItem())))
    			{
    				Hand targetHand = recipe.getIngredient().test(player.getItemInHand(Hand.MAIN_HAND)) ? Hand.MAIN_HAND : Hand.OFF_HAND;
        			player.setItemInHand(targetHand, recipe.assemble(null));
        			player.swing(targetHand, true);
        			player.level.playLocalSound(player.getX(), player.getY() + 1D, player.getZ(), SoundEvents.GENERIC_SPLASH, player.getSoundSource(), 0.8F, 0.8F + player.level.random.nextFloat() * 0.4F, false);
        			player.level.addFreshEntity(new ExperienceOrbEntity(player.level, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, this.random.nextInt(6) + 1));
        			CriteriaTriggers.FISHING_CRAFTING.trigger((ServerPlayerEntity)player, recipe);
        			this.remove();
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
