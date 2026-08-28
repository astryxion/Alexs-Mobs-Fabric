package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.item.CitadelRecipes;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/List;")
    )
    private List<SmithingRecipe> citadel_getRecipesFor(RecipeManager recipeManager, RecipeType<SmithingRecipe> type, Container input, Level level) {
        List<SmithingRecipe> list = new ArrayList<>(recipeManager.getRecipesFor(type, input, level));
        if (type == RecipeType.SMITHING && !input.getItem(0).isEmpty() && !input.getItem(1).isEmpty()) {
            list.addAll(CitadelRecipes.getSmithingRecipes());
        }
        return list;
    }
}
