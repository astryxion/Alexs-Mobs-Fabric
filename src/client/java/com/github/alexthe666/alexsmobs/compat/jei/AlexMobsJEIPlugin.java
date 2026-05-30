package com.github.alexthe666.alexsmobs.compat.jei;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.effect.ProperBrewingRecipe;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.CapsidRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JeiPlugin
public class AlexMobsJEIPlugin implements IModPlugin {
    public static final ResourceLocation MOD = new ResourceLocation("alexsmobs:alexsmobs");
    @Nullable
    private IRecipeCategory<CapsidRecipe> capsidCategory;

    public static final RecipeType<CapsidRecipe> CAPID_RECIPE_TYPE = RecipeType.create("alexsmobs", "capsid", CapsidRecipe.class);
    @Override
    public ResourceLocation getPluginUid() {
        return MOD;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(capsidCategory = new CapsidRecipeCategory(guiHelper));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CAPID_RECIPE_TYPE, AlexsMobs.PROXY.getCapsidRecipeManager().getCapsidRecipes());
        registration.addRecipes(RecipeTypes.BREWING, createCustomBrewingRecipes(registration));
        registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(
                new ItemStack(AMItemRegistry.SHIELD_OF_THE_DEEP)
        ));
    }

    private static List<IJeiBrewingRecipe> createCustomBrewingRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory recipeFactory = registration.getVanillaRecipeFactory();
        List<IJeiBrewingRecipe> recipes = new ArrayList<>();
        for (ProperBrewingRecipe recipe : ProperBrewingRecipe.CUSTOM_RECIPES) {
            ItemStack[] inputs = recipe.getInput().getItems();
            ItemStack[] ingredients = recipe.getIngredient().getItems();
            if (inputs.length == 0 || ingredients.length == 0) {
                continue;
            }
            recipes.add(recipeFactory.createBrewingRecipe(
                    Arrays.stream(ingredients).map(ItemStack::copy).toList(),
                    inputs[0].copy(),
                    recipe.getOutputTemplate()
            ));
        }
        return recipes;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AMBlockRegistry.CAPSID), CAPID_RECIPE_TYPE);

    }
}
