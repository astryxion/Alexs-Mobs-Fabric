package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

public class AMEffectRegistry {
    public static final MobEffect KNOCKBACK_RESISTANCE = registerEffect("knockback_resistance", new EffectKnockbackResistance());
    public static final MobEffect LAVA_VISION = registerEffect("lava_vision", new EffectLavaVision());
    public static final MobEffect SUNBIRD_BLESSING = registerEffect("sunbird_blessing", new EffectSunbird(false));
    public static final MobEffect SUNBIRD_CURSE = registerEffect("sunbird_curse", new EffectSunbird(true));
    public static final MobEffect POISON_RESISTANCE = registerEffect("poison_resistance", new EffectPoisonResistance());
    public static final MobEffect OILED = registerEffect("oiled", new EffectOiled());
    public static final MobEffect ORCAS_MIGHT = registerEffect("orcas_might", new EffectOrcaMight());
    public static final MobEffect BUG_PHEROMONES = registerEffect("bug_pheromones", new EffectBugPheromones());
    public static final MobEffect SOULSTEAL = registerEffect("soulsteal", new EffectSoulsteal());
    public static final MobEffect CLINGING = registerEffect("clinging", new EffectClinging());
    public static final MobEffect ENDER_FLU = registerEffect("ender_flu", new EffectEnderFlu());
    public static final MobEffect FEAR = registerEffect("fear", new EffectFear());
    public static final MobEffect TIGERS_BLESSING = registerEffect("tigers_blessing", new EffectTigersBlessing());
    public static final MobEffect DEBILITATING_STING = registerEffect("debilitating_sting", new EffectDebilitatingSting());
    public static final MobEffect EXSANGUINATION = registerEffect("exsanguination", new EffectExsanguination());
    public static final MobEffect EARTHQUAKE = registerEffect("earthquake", new EffectEarthquake());
    public static final MobEffect FLEET_FOOTED = registerEffect("fleet_footed", new EffectFleetFooted());
    public static final MobEffect POWER_DOWN = registerEffect("power_down", new EffectPowerDown());

    public static final MobEffect MOSQUITO_REPELLENT = registerEffect("mosquito_repellent", new EffectMosquitoRepellent());
    public static final Potion KNOCKBACK_RESISTANCE_POTION = registerPotion("knockback_resistance", new Potion("knockback_resistance", new MobEffectInstance(holder(KNOCKBACK_RESISTANCE), 3600)));
    public static final Potion LONG_KNOCKBACK_RESISTANCE_POTION = registerPotion("long_knockback_resistance", new Potion("long_knockback_resistance", new MobEffectInstance(holder(KNOCKBACK_RESISTANCE), 9600)));
    public static final Potion STRONG_KNOCKBACK_RESISTANCE_POTION = registerPotion("strong_knockback_resistance", new Potion("strong_knockback_resistance", new MobEffectInstance(holder(KNOCKBACK_RESISTANCE), 1800, 1)));
    public static final Potion LAVA_VISION_POTION = registerPotion("lava_vision", new Potion("lava_vision", new MobEffectInstance(holder(LAVA_VISION), 3600)));
    public static final Potion LONG_LAVA_VISION_POTION = registerPotion("long_lava_vision", new Potion("long_lava_vision", new MobEffectInstance(holder(LAVA_VISION), 9600)));
    public static final Potion SPEED_III_POTION = registerPotion("speed_iii", new Potion("speed_iii", new MobEffectInstance(MobEffects.SPEED, 2200, 2)));
    public static final Potion POISON_RESISTANCE_POTION = registerPotion("poison_resistance", new Potion("poison_resistance", new MobEffectInstance(holder(POISON_RESISTANCE), 3600)));
    public static final Potion LONG_POISON_RESISTANCE_POTION = registerPotion("long_poison_resistance", new Potion("long_poison_resistance", new MobEffectInstance(holder(POISON_RESISTANCE), 9600)));
    public static final Potion BUG_PHEROMONES_POTION = registerPotion("bug_pheromones", new Potion("bug_pheromones", new MobEffectInstance(holder(BUG_PHEROMONES), 3600)));
    public static final Potion LONG_BUG_PHEROMONES_POTION = registerPotion("long_bug_pheromones", new Potion("long_bug_pheromones", new MobEffectInstance(holder(BUG_PHEROMONES), 9600)));
    public static final Potion SOULSTEAL_POTION = registerPotion("soulsteal", new Potion("soulsteal", new MobEffectInstance(holder(SOULSTEAL), 3600)));
    public static final Potion LONG_SOULSTEAL_POTION = registerPotion("long_soulsteal", new Potion("long_soulsteal", new MobEffectInstance(holder(SOULSTEAL), 9600)));
    public static final Potion STRONG_SOULSTEAL_POTION = registerPotion("strong_soulsteal", new Potion("strong_soulsteal", new MobEffectInstance(holder(SOULSTEAL), 1800, 1)));
    public static final Potion CLINGING_POTION = registerPotion("clinging", new Potion("clinging", new MobEffectInstance(holder(CLINGING), 3600)));
    public static final Potion LONG_CLINGING_POTION = registerPotion("long_clinging", new Potion("long_clinging", new MobEffectInstance(holder(CLINGING), 9600)));

    public static Holder<MobEffect> holder(MobEffect effect) {
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
    }

    public static ItemStack createPotion(Holder<Potion> potion){
        return PotionContents.createItemStack(Items.POTION, potion);
    }

    public static ItemStack createPotion(Potion potion){
        return PotionContents.createItemStack(Items.POTION, BuiltInRegistries.POTION.wrapAsHolder(potion));
    }

    private static MobEffect registerEffect(String name, MobEffect effect) {
        return Registry.register(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, name), effect);
    }

    private static Potion registerPotion(String name, Potion potion) {
        return Registry.register(BuiltInRegistries.POTION, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, name), potion);
    }

    public static void init() {
    }

    public static void registerBrewingRecipes() {
        try {
            AMBrewing.reset();
            AMBrewing.register(new ProperBrewingRecipe(createPotion(Potions.STRENGTH), Ingredient.of(AMItemRegistry.BEAR_FUR), createPotion(KNOCKBACK_RESISTANCE_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(Potions.LONG_STRENGTH), Ingredient.of(AMItemRegistry.BEAR_FUR), createPotion(LONG_KNOCKBACK_RESISTANCE_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(Potions.STRONG_STRENGTH), Ingredient.of(AMItemRegistry.BEAR_FUR), createPotion(STRONG_KNOCKBACK_RESISTANCE_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(KNOCKBACK_RESISTANCE_POTION), Ingredient.of(Items.REDSTONE), createPotion(LONG_KNOCKBACK_RESISTANCE_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(KNOCKBACK_RESISTANCE_POTION), Ingredient.of(Items.GLOWSTONE_DUST), createPotion(STRONG_KNOCKBACK_RESISTANCE_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(new ItemStack(AMItemRegistry.LAVA_BOTTLE), Ingredient.of(AMItemRegistry.BONE_SERPENT_TOOTH), createPotion(LAVA_VISION_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(LAVA_VISION_POTION), Ingredient.of(Items.REDSTONE), createPotion(LONG_LAVA_VISION_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(Potions.POISON), Ingredient.of(AMItemRegistry.RATTLESNAKE_RATTLE), new ItemStack(AMItemRegistry.POISON_BOTTLE)));
            AMBrewing.register(new ProperBrewingRecipe(new ItemStack(AMItemRegistry.POISON_BOTTLE), Ingredient.of(AMItemRegistry.CENTIPEDE_LEG), createPotion(POISON_RESISTANCE_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(new ItemStack(AMItemRegistry.KOMODO_SPIT_BOTTLE), Ingredient.of(AMItemRegistry.CENTIPEDE_LEG), createPotion(POISON_RESISTANCE_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(POISON_RESISTANCE_POTION), Ingredient.of(AMItemRegistry.KOMODO_SPIT), createPotion(LONG_POISON_RESISTANCE_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(Potions.STRONG_SWIFTNESS), Ingredient.of(AMItemRegistry.GAZELLE_HORN), createPotion(SPEED_III_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(Potions.AWKWARD), Ingredient.of(AMItemRegistry.COCKROACH_WING), createPotion(BUG_PHEROMONES_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(BUG_PHEROMONES_POTION), Ingredient.of(Items.REDSTONE), createPotion(LONG_BUG_PHEROMONES_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(Potions.AWKWARD), Ingredient.of(AMItemRegistry.SOUL_HEART), createPotion(SOULSTEAL_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(SOULSTEAL_POTION), Ingredient.of(Items.REDSTONE), createPotion(LONG_SOULSTEAL_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(SOULSTEAL_POTION), Ingredient.of(Items.GLOWSTONE_DUST), createPotion(STRONG_SOULSTEAL_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(Potions.AWKWARD), Ingredient.of(AMItemRegistry.DROPBEAR_CLAW), createPotion(CLINGING_POTION)));
            AMBrewing.register(new ProperBrewingRecipe(createPotion(CLINGING_POTION), Ingredient.of(Items.REDSTONE), createPotion(LONG_CLINGING_POTION)));
        } catch (Throwable t) {
            AMBrewing.reset();
            AlexsMobs.LOGGER.warn("Skipped a brewing-recipe registration pass; recipes are registered again when PotionBrewing is rebuilt.", t);
        }
    }
}
