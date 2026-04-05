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
import org.jetbrains.annotations.NotNull;

/** Fabric: 1:1 logic from Forge IGlobalLootModifier; "should add" used by AMLootRegistry pool conditions. */
public class BlossomLootModifier {

    public static boolean shouldAdd(LootContext context) {
        if (!AMConfig.acaciaBlossomsDropFromLeaves) return false;
        ItemStack ctxTool = context.getParamOrNull(LootContextParams.TOOL);
        var enchantReg = context.getLevel().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
        if (ctxTool != null) {
            int silkTouch = EnchantmentHelper.getItemEnchantmentLevel(enchantReg.getHolderOrThrow(Enchantments.SILK_TOUCH), ctxTool);
            if (silkTouch > 0 || ctxTool.getItem() instanceof ShearsItem) return false;
        }
        RandomSource random = context.getRandom();
        int bonusLevel = ctxTool != null ? EnchantmentHelper.getItemEnchantmentLevel(enchantReg.getHolderOrThrow(Enchantments.FORTUNE), ctxTool) : 0;
        int blossomStep = (int) Math.floor(AMConfig.acaciaBlossomChance * 0.1F);
        int blossomRarity = AMConfig.acaciaBlossomChance - (bonusLevel * blossomStep);
        return blossomRarity < 1 || random.nextInt(blossomRarity) == 0;
    }

    public static ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (shouldAdd(context)) {
            generatedLoot.add(new ItemStack(AMItemRegistry.ACACIA_BLOSSOM));
        }
        return generatedLoot;
    }
}