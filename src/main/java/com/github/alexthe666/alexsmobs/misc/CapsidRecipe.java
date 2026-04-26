package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.citadel.client.model.container.JsonUtils;
import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.lang.reflect.Type;

public class CapsidRecipe {
    private final NonNullList<Ingredient> ingredients;
    private ItemStack result = ItemStack.EMPTY;
    private int time = 0;

    public CapsidRecipe(NonNullList<Ingredient> ingredients, ItemStack result, int time) {
        this.result = result;
        this.ingredients = ingredients;
        this.time = time;
    }

    private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        for (int i = 0; i < ingredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.CODEC.decode(com.mojang.serialization.JsonOps.INSTANCE, ingredientArray.get(i))
                    .getOrThrow(msg -> new JsonParseException(String.valueOf(msg)))
                    .getFirst();
            if (!ingredient.isEmpty()) {
                nonnulllist.add(ingredient);
            }
        }
        return nonnulllist;
    }

    public ItemStack getResult() {
        return result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public int getTime() {
        return time;
    }

    public boolean matches(ItemStack... stacks) {
        IntList taken = new IntArrayList();
        ItemStack[] copy = new ItemStack[stacks.length];
        for (int j = 0; j < copy.length; j++) {
            copy[j] = stacks[j].copy();
            for (int i = 0; i < ingredients.size(); i++) {
                if (ingredients.get(i).test(copy[j])) {
                    taken.add(j);
                    copy[j].shrink(1);
                }
            }
        }
        return taken.size() >= ingredients.size();
    }

    public static class Deserializer implements JsonDeserializer<CapsidRecipe> {

        @Override
        public CapsidRecipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = json.getAsJsonObject();
            int time = JsonUtils.getInt(jsonobject, "time");
            ItemStack result = ItemStack.EMPTY;
            if (jsonobject.has("result")) {
                JsonObject resultObj = JsonUtils.getJsonObject(jsonobject, "result");
                net.minecraft.resources.Identifier itemId = net.minecraft.resources.Identifier.parse(resultObj.get("item").getAsString());
                Item item = BuiltInRegistries.ITEM.get(itemId).map(Holder::value).orElse(Items.AIR);
                int count = resultObj.has("count") ? resultObj.get("count").getAsInt() : 1;
                result = new ItemStack(item, count);
            }
            NonNullList<Ingredient> nonnulllist = readIngredients(JsonUtils.getJsonArray(jsonobject, "ingredients"));
            return new CapsidRecipe(nonnulllist, result, time);
        }

    }
}
