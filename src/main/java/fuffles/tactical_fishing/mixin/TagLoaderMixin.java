package fuffles.tactical_fishing.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.gson.JsonArray;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import fuffles.tactical_fishing.GsonUtil;
import fuffles.tactical_fishing.TacticalFishing;
import fuffles.tactical_fishing.lib.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.FishingRodItem;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(TagLoader.class)
public abstract class TagLoaderMixin 
{
	@Final
	@Shadow
	private String directory;
	
	@Inject(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/FileToIdConverter;json(Ljava/lang/String;)Lnet/minecraft/resources/FileToIdConverter;", ordinal = 0, shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void load(ResourceManager manager, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cbr, Map<ResourceLocation, List<TagLoader.EntryWithSource>> map) 
	{
		if (TacticalFishing.Config.COMMON.writeDatapack.get() && this.directory.equals("tags/items"))
		{
			JsonArray array = new JsonArray();
			for (ResourceLocation registryKey : ForgeRegistries.ITEMS.getKeys())
			{
				if (ForgeRegistries.ITEMS.getValue(registryKey) instanceof FishingRodItem)
				{
					array.add(registryKey.toString());
				}
			}
			List<TagLoader.EntryWithSource> list = map.computeIfAbsent(Resources.TAG_FISHING_RODS, (val) -> new ArrayList<>());
			TagFile file = TagFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, GsonUtil.newObject((obj) -> {
					obj.add("values", array);
				}))).getOrThrow(false, TacticalFishing.LOG::error);
			for (TagEntry entry : file.entries())
			{
				list.add(new TagLoader.EntryWithSource(entry, TacticalFishing.ID));
			}
		}
	}
}
