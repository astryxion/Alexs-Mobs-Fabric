package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BananaLootModifier  {

    public static final Supplier<MapCodec<BananaLootModifier>> CODEC =
            () -> MapCodec.unit(() -> new BananaLootModifier(new net.minecraft.world.level.storage.loot.predicates.LootItemCondition[0]));

    // Hardcoded loot table ID since codec doesn't load conditions from JSON
    private static final Identifier JUNGLE_LEAVES = Identifier.withDefaultNamespace("blocks/jungle_leaves");

    private final LootItemCondition[] conditions;

    public BananaLootModifier(LootItemCondition[] conditionsIn) {
        this.conditions = conditionsIn;
    }

    @NotNull
    public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // 1.21 LootContext no longer exposes queried table id; gate by block state parameter.
        net.minecraft.world.level.block.state.BlockState state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
        if (state != null && state.is(Blocks.JUNGLE_LEAVES)) {
            return this.doApply(generatedLoot, context);
        }
        return generatedLoot;
    }

    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context){
        if (AMConfig.bananasDropFromLeaves){
            ItemInstance toolInstance = context.getOptionalParameter(LootContextParams.TOOL);
            ItemStack ctxTool = toolInstance instanceof ItemStack is ? is : ItemStack.EMPTY;
            RandomSource random = context.getRandom();
            if (!ctxTool.isEmpty()) {
                int silkTouch = EnchantmentHelper.getItemEnchantmentLevel(context.getLevel().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), ctxTool);
                if(silkTouch > 0 || ctxTool.getItem() instanceof ShearsItem){
                    return generatedLoot;
                }
            }
            int bonusLevel = !ctxTool.isEmpty() ? 0 /* TODO 1.21: Enchantments are data-driven */ : 0;
            int bananaStep = (int)Math.floor(AMConfig.bananaChance * 0.1F);
            int bananaRarity = AMConfig.bananaChance - (bonusLevel * bananaStep);
            if (bananaRarity < 1 || random.nextInt(bananaRarity) == 0) {
                generatedLoot.add(new ItemStack(AMItemRegistry.BANANA));
            }
        }
        return generatedLoot;
    }

    
    }