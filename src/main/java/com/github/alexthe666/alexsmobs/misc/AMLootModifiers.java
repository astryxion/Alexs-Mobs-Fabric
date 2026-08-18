package com.github.alexthe666.alexsmobs.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * Applies extra block/chest drops that used to be global loot modifiers.
 */
public final class AMLootModifiers {
    private static final LootItemCondition[] NONE = new LootItemCondition[0];
    private static final BananaLootModifier BANANA = new BananaLootModifier(NONE);
    private static final BlossomLootModifier BLOSSOM = new BlossomLootModifier(NONE);
    private static final PigshoesLootModifier PIGSHOES = new PigshoesLootModifier(NONE);
    private static final AncientDartLootModifier DART = new AncientDartLootModifier(NONE);

    private AMLootModifiers() {
    }

    public static void apply(LootContext context, ObjectArrayList<ItemStack> generatedLoot) {
        BANANA.apply(generatedLoot, context);
        BLOSSOM.apply(generatedLoot, context);
        PIGSHOES.apply(generatedLoot, context);
        DART.apply(generatedLoot, context);
    }
}
