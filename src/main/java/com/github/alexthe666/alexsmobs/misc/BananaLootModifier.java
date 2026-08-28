package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Fabric: 1:1 logic from Forge IGlobalLootModifier; "should add" used by AMLootRegistry pool conditions. */
public class BananaLootModifier {

    /** Returns true when this modifier would add a banana (for use by Fabric loot pool condition). */
    public static boolean shouldAdd(LootContext context) {
        if (!AMConfig.bananasDropFromLeaves) return false;
        ItemStack ctxTool = context.getParamOrNull(LootContextParams.TOOL);
        var enchantReg = context.getLevel().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
        if (ctxTool != null) {
            int silkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, ctxTool);
            if (silkTouch > 0 || ctxTool.getItem() instanceof ShearsItem) return false;
        }
        RandomSource random = context.getRandom();
        int bonusLevel = ctxTool != null ? EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, ctxTool) : 0;
        int bananaStep = (int) Math.floor(AMConfig.bananaChance * 0.1F);
        int bananaRarity = AMConfig.bananaChance - (bonusLevel * bananaStep);
        return bananaRarity < 1 || random.nextInt(bananaRarity) == 0;
    }

    /** Same logic as Forge doApply: add banana to list when conditions pass. */
    public static ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (shouldAdd(context)) {
            generatedLoot.add(new ItemStack(AMItemRegistry.BANANA));
        }
        return generatedLoot;
    }
}