package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class AMAdvancementTriggerRegistry {

    public static final AMAdvancementTrigger MOSQUITO_SICK = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "mosquito_sick"));
    public static final AMAdvancementTrigger EMU_DODGE = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "emu_dodge"));
    public static final AMAdvancementTrigger STOMP_LEAFCUTTER_ANTHILL = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "stomp_leafcutter_anthill"));
    public static final AMAdvancementTrigger BALD_EAGLE_CHALLENGE = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "bald_eagle_challenge"));
    public static final AMAdvancementTrigger VOID_WORM_SUMMON = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "void_worm_summon"));
    public static final AMAdvancementTrigger VOID_WORM_SPLIT = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "void_worm_split"));
    public static final AMAdvancementTrigger VOID_WORM_SLAY_HEAD = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "void_worm_kill"));
    public static final AMAdvancementTrigger SEAGULL_STEAL = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "seagull_steal"));
    public static final AMAdvancementTrigger LAVIATHAN_FOUR_PASSENGERS = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "laviathan_four_passengers"));
    public static final AMAdvancementTrigger TRANSMUTE_1000_ITEMS = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "transmute_1000_items"));
    public static final AMAdvancementTrigger UNDERMINE_UNDERMINER = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "undermine_underminer"));

    public static final AMAdvancementTrigger ELEPHANT_SWAG = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "elephant_swag"));
    public static final AMAdvancementTrigger SKUNK_SPRAY = new AMAdvancementTrigger(ResourceLocation.fromNamespaceAndPath("alexsmobs", "skunk_spray"));

    public static void init(){
        CriteriaTriggers.register(MOSQUITO_SICK.resourceLocation.toString(), MOSQUITO_SICK);
        CriteriaTriggers.register(EMU_DODGE.resourceLocation.toString(), EMU_DODGE);
        CriteriaTriggers.register(STOMP_LEAFCUTTER_ANTHILL.resourceLocation.toString(), STOMP_LEAFCUTTER_ANTHILL);
        CriteriaTriggers.register(BALD_EAGLE_CHALLENGE.resourceLocation.toString(), BALD_EAGLE_CHALLENGE);
        CriteriaTriggers.register(VOID_WORM_SUMMON.resourceLocation.toString(), VOID_WORM_SUMMON);
        CriteriaTriggers.register(VOID_WORM_SPLIT.resourceLocation.toString(), VOID_WORM_SPLIT);
        CriteriaTriggers.register(VOID_WORM_SLAY_HEAD.resourceLocation.toString(), VOID_WORM_SLAY_HEAD);
        CriteriaTriggers.register(SEAGULL_STEAL.resourceLocation.toString(), SEAGULL_STEAL);
        CriteriaTriggers.register(LAVIATHAN_FOUR_PASSENGERS.resourceLocation.toString(), LAVIATHAN_FOUR_PASSENGERS);
        CriteriaTriggers.register(TRANSMUTE_1000_ITEMS.resourceLocation.toString(), TRANSMUTE_1000_ITEMS);
        CriteriaTriggers.register(UNDERMINE_UNDERMINER.resourceLocation.toString(), UNDERMINE_UNDERMINER);
        CriteriaTriggers.register(ELEPHANT_SWAG.resourceLocation.toString(), ELEPHANT_SWAG);
        CriteriaTriggers.register(SKUNK_SPRAY.resourceLocation.toString(), SKUNK_SPRAY);
    }

}
