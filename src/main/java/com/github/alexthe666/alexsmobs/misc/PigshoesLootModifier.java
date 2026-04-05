package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

/** Fabric: 1:1 logic from Forge IGlobalLootModifier; "should add" used by AMLootRegistry pool conditions. */
public class PigshoesLootModifier {

    public static boolean shouldAdd(LootContext context) {
        return AMConfig.addLootToChests && context.getRandom().nextFloat() <= AMConfig.tusklinShoesBarteringChance;
    }

    public static ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (shouldAdd(context)) {
            generatedLoot.add(new ItemStack(AMItemRegistry.PIGSHOES));
        }
        return generatedLoot;
    }
}