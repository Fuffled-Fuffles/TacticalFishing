package fuffles.tactical_fishing;

import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import fuffles.tactical_fishing.common.item.crafting.FishingRecipe;
import fuffles.tactical_fishing.lib.ItemTags;
import fuffles.tactical_fishing.lib.RecipeSerializers;
import fuffles.tactical_fishing.lib.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.ForgeRegistries;

public class DatapackWriter
{	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	private static File makeDir(File parent, String child)
	{
		File dir = new File(parent, child);
		if (!dir.exists())
		{
			dir.mkdir();
		}
		return dir;
	}
	
	private static File constructPath(File from, String... paths)
	{
		File last = from;
		for (String str : paths)
		{
			last = DatapackWriter.makeDir(last, str);
		}
		return last;
	}
	
	private static void writePackMcMeta(File in, int version, String desc)
	{
		File mcmeta = new File(in, "pack.mcmeta");
		try(JsonWriter writer = DatapackWriter.GSON.newJsonWriter(Files.newWriter(mcmeta, StandardCharsets.UTF_8)))
		{
			DatapackWriter.GSON.toJson(GsonUtil.newObject((obj) -> {
				obj.add("pack", GsonUtil.newObject((pack) -> {
					pack.addProperty("pack_format", version);
					pack.addProperty("description", desc);
					pack.add("mods", GsonUtil.newArray((mods) -> {
						for (ModInfo info : FMLLoader.getLoadingModList().getMods())
						{
							mods.add(info.getModId());
						}
					}));
				}));
			}), writer);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*private static JsonObject writeRecipe(String mod, ResourceLocation fishId, ResourceLocation bucketId)
	{
		return GsonUtil.newObject((obj) -> {
			obj.addProperty("type", Resources.RECIPE_SERIALIZER_FISHING.toString());
			obj.addProperty("group", Resources.RECIPE_GROUP_FISH_BUCKETS.toString());
			obj.add("rod", GsonUtil.newObject((rod) -> {
				rod.addProperty("tag", Resources.TAG_FISHING_RODS.toString());
			}));
			obj.add("ingredient", GsonUtil.newObject((ingredient) -> {
				ingredient.addProperty("item", ForgeRegistries.ITEMS.getKey(Items.WATER_BUCKET).toString());
			}));
			obj.add("catch", GsonUtil.newObject((fish) -> {
				fish.addProperty("item", fishId.toString());
			}));
			obj.add("result", GsonUtil.newObject((result) -> {
				result.addProperty("item", bucketId.toString());
			}));
			obj.add("conditions", GsonUtil.newArray((conditions) -> {
				conditions.add(GsonUtil.newObject((condition) -> {
					condition.addProperty("type", ModLoadedCondition.Serializer.INSTANCE.getID().toString());
					condition.addProperty("modid", mod);
				}));
			}));
		});
	}*/
	
	private static void writeTag(File in, List<ResourceLocation> rods)
	{
		File tag = new File(in, Resources.TAG_FISHING_RODS.getPath() + ".json");
		
		try (JsonWriter writer = DatapackWriter.GSON.newJsonWriter(Files.newWriter(tag, StandardCharsets.UTF_8)))
		{
			DatapackWriter.GSON.toJson(GsonUtil.newObject((obj) -> {
				obj.addProperty("replace", false);
				obj.add("values", GsonUtil.newArray((values) -> {
					if (!rods.isEmpty())
					{
						for (ResourceLocation rod : rods)
						{
							values.add(rod.toString());
						}
					}
				}));
			}), writer);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Deprecated
	public static List<Recipe<?>> write(File to, String key, int version, String desc)
	{
		DatapackWriter.writePackMcMeta(to, version, desc);
		File recipePath = DatapackWriter.constructPath(to, "data", key, "recipes", "inbuilt");
		File tagsPath = DatapackWriter.constructPath(to, "data", key, "tags", "items");
		List<ResourceLocation> rods = new ArrayList<>();
		List<Recipe<?>> recipes = new ArrayList<>();
		
		for (ResourceLocation registryKey : ForgeRegistries.ITEMS.getKeys())
		{
			if (ForgeRegistries.ITEMS.getValue(registryKey) instanceof FishingRodItem)
			{
				rods.add(registryKey);
				continue;
			}
			else if (!TacticalFishing.Config.COMMON.writeFishBucketRecipes.get() || !registryKey.getPath().endsWith("_bucket"))
			{
				continue;
			}
			String id = registryKey.getNamespace();
			ResourceLocation potentialFish = new ResourceLocation(id, registryKey.getPath().substring(0, registryKey.getPath().lastIndexOf("_")));
			ResourceLocation potentialRawFish = new ResourceLocation(id, "raw_" + potentialFish.getPath());
			Optional<Item> fish = Optional.of(ForgeRegistries.ITEMS.getValue(potentialFish)).or(() -> Optional.of(ForgeRegistries.ITEMS.getValue(potentialRawFish)));
			if (fish.isPresent())
			{
				FishingRecipe recipe = new FishingRecipe(new ResourceLocation(TacticalFishing.ID, "fishing_" + registryKey.toString().replace(':', '_')), Resources.RECIPE_GROUP_FISH_BUCKETS.toString(), Ingredient.of(ItemTags.RODS), Ingredient.of(Items.WATER_BUCKET), Ingredient.of(fish.get().getDefaultInstance()), ForgeRegistries.ITEMS.getValue(registryKey).getDefaultInstance());
				File recipeOut = new File(recipePath, recipe.getId().getPath());
				if (recipeOut.exists())
				{
					TacticalFishing.LOG.info("Skipping recipe 'inbuilt/" + recipe.getId().getPath() + ".json' as it already exists.");
					continue;
				}
				try(JsonWriter writer = DatapackWriter.GSON.newJsonWriter(Files.newWriter(recipeOut, StandardCharsets.UTF_8)))
				{
					DatapackWriter.GSON.toJson(GsonUtil.newObject((obj) -> {
						obj.addProperty("type", Resources.RECIPE_SERIALIZER_FISHING.toString());
						RecipeSerializers.FISHING.toJson(obj, recipe);
					}), writer);
					recipes.add(recipe);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		DatapackWriter.writeTag(tagsPath, rods);
		return recipes;
	}
	
	private static List<String> getPackMcMetaModList(File in)
	{
		List<String> modList = new ArrayList<>();
		
		File mcmeta = new File(in, "pack.mcmeta");
		if (mcmeta.exists())
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			try(Reader reader = Files.newReader(mcmeta, StandardCharsets.UTF_8))
			{
				JsonObject obj = gson.fromJson(reader, JsonObject.class);
				if (obj.has("pack") && obj.get("pack") instanceof JsonObject pack && pack.has("last_mods") && pack.get("last_mods") instanceof JsonArray mods)
				{
					for (JsonElement mod : mods)
					{
						if (!mod.isJsonNull() && mod.isJsonPrimitive())
						{
							modList.add(mod.getAsString());
						}
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			TacticalFishing.LOG.warn("Couldn't find pack.mcmeta file in '" + in.toString() + "', can't verify mod integrity!");
		}
		
		return modList;
	}
	
	@Deprecated
	public static boolean isPackOutdated(File in)
	{
		/*if (TacticalFishing.Config.COMMON.verifyDatapack.get() == false)
		{
			return false;
		}*/
		List<String> modList = DatapackWriter.getPackMcMetaModList(in);
		if (!modList.isEmpty())
		{
			for (ModInfo info : FMLLoader.getLoadingModList().getMods())
			{
				if (!modList.contains(info.getModId()))
				{
					return true;
				}
			}
		}
		return false;
	}
}
