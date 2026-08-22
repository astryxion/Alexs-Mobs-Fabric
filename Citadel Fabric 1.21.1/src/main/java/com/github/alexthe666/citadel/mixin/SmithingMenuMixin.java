package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.item.CitadelRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(SmithingMenu.class)
public class SmithingMenuMixin {

    @Redirect(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/List;")
    )
    private List<RecipeHolder<SmithingRecipe>> citadel_getRecipesFor(RecipeManager recipeManager, RecipeType<SmithingRecipe> type, RecipeInput input, Level level) {
        SmithingRecipeInput smithingInput = (SmithingRecipeInput) input;
        List<RecipeHolder<SmithingRecipe>> list = new ArrayList<>(recipeManager.getRecipesFor(type, smithingInput, level));
        if (type == RecipeType.SMITHING && !smithingInput.template().isEmpty() && !smithingInput.base().isEmpty()) {
            int i = 0;
            for (SmithingRecipe recipe : CitadelRecipes.getSmithingRecipes()) {
                list.add(new RecipeHolder<>(ResourceLocation.parse("citadel:smithing_custom_" + (i++)), recipe));
            }
        }
        return list;
    }
}