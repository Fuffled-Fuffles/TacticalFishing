package fuffles.tactical_fishing;

import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GsonUtil 
{
	public static JsonObject newObject(Consumer<JsonObject> fn)
	{
		JsonObject obj = new JsonObject();
		fn.accept(obj);
		return obj;
	}
	
	public static JsonArray newArray(Consumer<JsonArray> fn)
	{
		JsonArray arr = new JsonArray();
		fn.accept(arr);
		return arr;
	}
}
