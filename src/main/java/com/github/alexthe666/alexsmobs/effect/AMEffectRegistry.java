package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;

public class AMEffectRegistry {
    private static ResourceLocation id(String path) {
        return new ResourceLocation(AlexsMobs.MODID, path);
    }

    private static MobEffect registerEffect(String name, MobEffect effect) {
        return Registry.register(BuiltInRegistries.MOB_EFFECT, id(name), effect);
    }

    private static Potion registerPotion(String name, Potion potion) {
        return Registry.register(BuiltInRegistries.POTION, id(name), potion);
    }

    public static MobEffect KNOCKBACK_RESISTANCE;
    public static MobEffect LAVA_VISION;
    public static MobEffect SUNBIRD_BLESSING;
    public static MobEffect SUNBIRD_CURSE;
    public static MobEffect POISON_RESISTANCE;
    public static MobEffect OILED;
    public static MobEffect ORCAS_MIGHT;
    public static MobEffect BUG_PHEROMONES;
    public static MobEffect SOULSTEAL;
    public static MobEffect CLINGING;
    public static MobEffect ENDER_FLU;
    public static MobEffect FEAR;
    public static MobEffect TIGERS_BLESSING;
    public static MobEffect DEBILITATING_STING;
    public static MobEffect EXSANGUINATION;
    public static MobEffect EARTHQUAKE;
    public static MobEffect FLEET_FOOTED;
    public static MobEffect POWER_DOWN;
    public static MobEffect MOSQUITO_REPELLENT;

    public static Potion KNOCKBACK_RESISTANCE_POTION;
    public static Potion LONG_KNOCKBACK_RESISTANCE_POTION;
    public static Potion STRONG_KNOCKBACK_RESISTANCE_POTION;
    public static Potion LAVA_VISION_POTION;
    public static Potion LONG_LAVA_VISION_POTION;
    public static Potion SPEED_III_POTION;
    public static Potion POISON_RESISTANCE_POTION;
    public static Potion LONG_POISON_RESISTANCE_POTION;
    public static Potion BUG_PHEROMONES_POTION;
    public static Potion LONG_BUG_PHEROMONES_POTION;
    public static Potion SOULSTEAL_POTION;
    public static Potion LONG_SOULSTEAL_POTION;
    public static Potion STRONG_SOULSTEAL_POTION;
    public static Potion CLINGING_POTION;
    public static Potion LONG_CLINGING_POTION;

    public static ItemStack createPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }

    public static void init() {
        KNOCKBACK_RESISTANCE = registerEffect("knockback_resistance", new EffectKnockbackResistance());
        LAVA_VISION = registerEffect("lava_vision", new EffectLavaVision());
        SUNBIRD_BLESSING = registerEffect("sunbird_blessing", new EffectSunbird(false));
        SUNBIRD_CURSE = registerEffect("sunbird_curse", new EffectSunbird(true));
        POISON_RESISTANCE = registerEffect("poison_resistance", new EffectPoisonResistance());
        OILED = registerEffect("oiled", new EffectOiled());
        ORCAS_MIGHT = registerEffect("orcas_might", new EffectOrcaMight());
        BUG_PHEROMONES = registerEffect("bug_pheromones", new EffectBugPheromones());
        SOULSTEAL = registerEffect("soulsteal", new EffectSoulsteal());
        CLINGING = registerEffect("clinging", new EffectClinging());
        ENDER_FLU = registerEffect("ender_flu", new EffectEnderFlu());
        FEAR = registerEffect("fear", new EffectFear());
        TIGERS_BLESSING = registerEffect("tigers_blessing", new EffectTigersBlessing());
        DEBILITATING_STING = registerEffect("debilitating_sting", new EffectDebilitatingSting());
        EXSANGUINATION = registerEffect("exsanguination", new EffectExsanguination());
        EARTHQUAKE = registerEffect("earthquake", new EffectEarthquake());
        FLEET_FOOTED = registerEffect("fleet_footed", new EffectFleetFooted());
        POWER_DOWN = registerEffect("power_down", new EffectPowerDown());
        MOSQUITO_REPELLENT = registerEffect("mosquito_repellent", new EffectMosquitoRepellent());

        KNOCKBACK_RESISTANCE_POTION = registerPotion("knockback_resistance", new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE, 3600)));
        LONG_KNOCKBACK_RESISTANCE_POTION = registerPotion("long_knockback_resistance", new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE, 9600)));
        STRONG_KNOCKBACK_RESISTANCE_POTION = registerPotion("strong_knockback_resistance", new Potion(new MobEffectInstance(KNOCKBACK_RESISTANCE, 1800, 1)));
        LAVA_VISION_POTION = registerPotion("lava_vision", new Potion(new MobEffectInstance(LAVA_VISION, 3600)));
        LONG_LAVA_VISION_POTION = registerPotion("long_lava_vision", new Potion(new MobEffectInstance(LAVA_VISION, 9600)));
        SPEED_III_POTION = registerPotion("speed_iii", new Potion(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2200, 2)));
        POISON_RESISTANCE_POTION = registerPotion("poison_resistance", new Potion(new MobEffectInstance(POISON_RESISTANCE, 3600)));
        LONG_POISON_RESISTANCE_POTION = registerPotion("long_poison_resistance", new Potion(new MobEffectInstance(POISON_RESISTANCE, 9600)));
        BUG_PHEROMONES_POTION = registerPotion("bug_pheromones", new Potion(new MobEffectInstance(BUG_PHEROMONES, 3600)));
        LONG_BUG_PHEROMONES_POTION = registerPotion("long_bug_pheromones", new Potion(new MobEffectInstance(BUG_PHEROMONES, 9600)));
        SOULSTEAL_POTION = registerPotion("soulsteal", new Potion(new MobEffectInstance(SOULSTEAL, 3600)));
        LONG_SOULSTEAL_POTION = registerPotion("long_soulsteal", new Potion(new MobEffectInstance(SOULSTEAL, 9600)));
        STRONG_SOULSTEAL_POTION = registerPotion("strong_soulsteal", new Potion(new MobEffectInstance(SOULSTEAL, 1800, 1)));
        CLINGING_POTION = registerPotion("clinging", new Potion(new MobEffectInstance(CLINGING, 3600)));
        LONG_CLINGING_POTION = registerPotion("long_clinging", new Potion(new MobEffectInstance(CLINGING, 9600)));
    }

    /** Call after AMItemRegistry.init() so item refs are non-null (1:1 behavior). */
    public static void registerBrewing() {
        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.STRENGTH, Ingredient.of(AMItemRegistry.BEAR_FUR), KNOCKBACK_RESISTANCE_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(KNOCKBACK_RESISTANCE_POTION, Ingredient.of(Items.REDSTONE), LONG_KNOCKBACK_RESISTANCE_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(KNOCKBACK_RESISTANCE_POTION, Ingredient.of(Items.GLOWSTONE_DUST), STRONG_KNOCKBACK_RESISTANCE_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(LAVA_VISION_POTION, Ingredient.of(Items.REDSTONE), LONG_LAVA_VISION_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(POISON_RESISTANCE_POTION, Ingredient.of(AMItemRegistry.KOMODO_SPIT), LONG_POISON_RESISTANCE_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.STRONG_SWIFTNESS, Ingredient.of(AMItemRegistry.GAZELLE_HORN), SPEED_III_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(AMItemRegistry.COCKROACH_WING), BUG_PHEROMONES_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(BUG_PHEROMONES_POTION, Ingredient.of(Items.REDSTONE), LONG_BUG_PHEROMONES_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(AMItemRegistry.SOUL_HEART), SOULSTEAL_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(SOULSTEAL_POTION, Ingredient.of(Items.REDSTONE), LONG_SOULSTEAL_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(SOULSTEAL_POTION, Ingredient.of(Items.GLOWSTONE_DUST), STRONG_SOULSTEAL_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(AMItemRegistry.DROPBEAR_CLAW), CLINGING_POTION);
        FabricBrewingRecipeRegistry.registerPotionRecipe(CLINGING_POTION, Ingredient.of(Items.REDSTONE), LONG_CLINGING_POTION);
        ProperBrewingRecipe.registerCustomRecipes();
    }
}
