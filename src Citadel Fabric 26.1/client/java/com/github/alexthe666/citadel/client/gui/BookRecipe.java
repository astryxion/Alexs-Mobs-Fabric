package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.Citadel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads crafting JSON from the datapack so the animal dictionary can show recipes
 * even when the live recipe registry is empty in the book GUI.
 */
public class BookRecipe {
    private static final Map<String, BookRecipe> CACHE = new HashMap<>();
    private final List<ItemStack[]> ingredients;
    private final ItemStack result;
    private final boolean shapeless;

    private BookRecipe(List<ItemStack[]> ingredients, ItemStack result, boolean shapeless) {
        this.ingredients = ingredients;
        this.result = result;
        this.shapeless = shapeless;
    }

    public List<ItemStack[]> getIngredients() {
        return this.ingredients;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public boolean isShapeless() {
        return this.shapeless;
    }

    public static BookRecipe get(String id) {
        if (CACHE.containsKey(id)) {
            return CACHE.get(id);
        }
        BookRecipe recipe = null;
        try {
            recipe = load(id);
        } catch (Exception e) {
            Citadel.LOGGER.warn("Could not read book recipe {}", id, e);
        }
        CACHE.put(id, recipe);
        return recipe;
    }

    private static BookRecipe load(String id) throws Exception {
        Identifier res = Identifier.parse(id);
        InputStream stream = BookRecipe.class.getResourceAsStream("/data/" + res.getNamespace() + "/recipe/" + res.getPath() + ".json");
        if (stream == null) {
            stream = BookRecipe.class.getResourceAsStream("/data/" + res.getNamespace() + "/recipes/" + res.getPath() + ".json");
        }
        if (stream == null) {
            return null;
        }
        try (InputStream in = stream; InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String type = json.has("type") ? json.get("type").getAsString() : "";
            List<ItemStack[]> ingredients = new ArrayList<>();
            boolean shapeless;
            if ("minecraft:crafting_shaped".equals(type)) {
                shapeless = false;
                JsonArray pattern = json.getAsJsonArray("pattern");
                JsonObject key = json.getAsJsonObject("key");
                for (JsonElement rowElement : pattern) {
                    String row = rowElement.getAsString();
                    for (int i = 0; i < row.length(); i++) {
                        char c = row.charAt(i);
                        ingredients.add(c == ' ' ? new ItemStack[0] : resolve(key.get(String.valueOf(c))));
                    }
                }
            } else if ("minecraft:crafting_shapeless".equals(type)) {
                shapeless = true;
                for (JsonElement element : json.getAsJsonArray("ingredients")) {
                    ingredients.add(resolve(element));
                }
            } else {
                return null;
            }
            return new BookRecipe(ingredients, readResult(json.get("result")), shapeless);
        }
    }

    private static ItemStack readResult(JsonElement element) {
        if (element == null) {
            return ItemStack.EMPTY;
        }
        if (element.isJsonPrimitive()) {
            return stackOf(element.getAsString(), 1);
        }
        JsonObject object = element.getAsJsonObject();
        String itemId = object.has("id") ? object.get("id").getAsString() : object.get("item").getAsString();
        int count = object.has("count") ? object.get("count").getAsInt() : 1;
        return stackOf(itemId, count);
    }

    private static ItemStack[] resolve(JsonElement element) {
        List<ItemStack> stacks = new ArrayList<>();
        collect(element, stacks);
        return stacks.toArray(new ItemStack[0]);
    }

    private static void collect(JsonElement element, List<ItemStack> out) {
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collect(child, out);
            }
            return;
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("item")) {
                collect(object.get("item"), out);
            } else if (object.has("tag")) {
                collect(object.get("tag"), out);
            } else if (object.has("id")) {
                collect(object.get("id"), out);
            }
            return;
        }
        String value = element.getAsString();
        if (value.startsWith("#")) {
            TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse(value.substring(1)));
            for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
                out.add(new ItemStack((ItemLike) holder.value()));
            }
        } else {
            ItemStack stack = stackOf(value, 1);
            if (!stack.isEmpty()) {
                out.add(stack);
            }
        }
    }

    private static ItemStack stackOf(String id, int count) {
        return BuiltInRegistries.ITEM.get(Identifier.parse(id))
                .map(holder -> new ItemStack(holder.value(), count))
                .orElse(ItemStack.EMPTY);
    }
}
