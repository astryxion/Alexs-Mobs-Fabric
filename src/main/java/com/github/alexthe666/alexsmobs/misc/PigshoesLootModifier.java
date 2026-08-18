package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PigshoesLootModifier  {

    public static final Supplier<MapCodec<PigshoesLootModifier>> CODEC =
            () -> MapCodec.unit(() -> new PigshoesLootModifier(new net.minecraft.world.level.storage.loot.predicates.LootItemCondition[0]));

    // Hardcoded loot table ID since codec doesn't load conditions from JSON
    private static final Identifier PIGLIN_BARTERING = Identifier.withDefaultNamespace("gameplay/piglin_bartering");

    private final LootItemCondition[] conditions;

    public PigshoesLootModifier(LootItemCondition[] conditionsIn) {
        this.conditions = conditionsIn;
    }

    @NotNull
    public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // 1.21 LootContext no longer exposes queried table id; piglin entity context approximates bartering table use.
        net.minecraft.world.entity.Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity != null && entity.getType() == EntityType.PIGLIN) {
            return this.doApply(generatedLoot, context);
        }
        return generatedLoot;
    }

    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (AMConfig.addLootToChests) {
            if (context.getRandom().nextFloat() <= AMConfig.tusklinShoesBarteringChance) {
                generatedLoot.add(new ItemStack(AMItemRegistry.PIGSHOES));
            }
        }
        return generatedLoot;
    }

    
    }