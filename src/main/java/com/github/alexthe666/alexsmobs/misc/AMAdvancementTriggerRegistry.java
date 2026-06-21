package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * Registry for Alex's Mobs advancement triggers
 */
public class AMAdvancementTriggerRegistry {

    public static final AMAdvancementTrigger MOSQUITO_SICK = register("mosquito_sick");
    public static final AMAdvancementTrigger EMU_DODGE = register("emu_dodge");
    public static final AMAdvancementTrigger STOMP_LEAFCUTTER_ANTHILL = register("stomp_leafcutter_anthill");
    public static final AMAdvancementTrigger BALD_EAGLE_CHALLENGE = register("bald_eagle_challenge");
    public static final AMAdvancementTrigger VOID_WORM_SUMMON = register("void_worm_summon");
    public static final AMAdvancementTrigger VOID_WORM_SPLIT = register("void_worm_split");
    public static final AMAdvancementTrigger VOID_WORM_SLAY_HEAD = register("void_worm_kill");
    public static final AMAdvancementTrigger SEAGULL_STEAL = register("seagull_steal");
    public static final AMAdvancementTrigger LAVIATHAN_FOUR_PASSENGERS = register("laviathan_four_passengers");
    public static final AMAdvancementTrigger TRANSMUTE_1000_ITEMS = register("transmute_1000_items");
    public static final AMAdvancementTrigger UNDERMINE_UNDERMINER = register("undermine_underminer");
    public static final AMAdvancementTrigger ELEPHANT_SWAG = register("elephant_swag");
    public static final AMAdvancementTrigger SKUNK_SPRAY = register("skunk_spray");
    public static final AMAdvancementTrigger OPEN_ANIMAL_DICTIONARY = register("open_animal_dictionary");

    private static AMAdvancementTrigger register(String id) {
        AMAdvancementTrigger trigger = new AMAdvancementTrigger();
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, id), trigger);
    }

    public static void init() {
    }
}
