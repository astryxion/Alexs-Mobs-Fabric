package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AncientDartLootModifier  {

    public static final Supplier<MapCodec<AncientDartLootModifier>> CODEC =
            () -> MapCodec.unit(() -> new AncientDartLootModifier(new net.minecraft.world.level.storage.loot.predicates.LootItemCondition[0]));

    // Hardcoded loot table IDs since codec doesn't load conditions from JSON
    private static final Identifier JUNGLE_TEMPLE = Identifier.withDefaultNamespace("chests/jungle_temple");
    private static final Identifier JUNGLE_TEMPLE_DISPENSER = Identifier.withDefaultNamespace("chests/jungle_temple_dispenser");

    private final LootItemCondition[] conditions;

    public AncientDartLootModifier(LootItemCondition[] conditionsIn) {
        this.conditions = conditionsIn;
    }

    @NotNull
    public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // 1.21 LootContext no longer exposes queried table id; use structure-at-origin check.
        Holder<Structure> jungleTemple = context.getLevel().registryAccess()
                .lookupOrThrow(Registries.STRUCTURE)
                .getOrThrow(BuiltinStructures.JUNGLE_TEMPLE);
        net.minecraft.world.phys.Vec3 origin = context.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN);
        if (origin != null && context.getLevel().structureManager().getStructureWithPieceAt(net.minecraft.core.BlockPos.containing(origin), jungleTemple.value()).isValid()) {
            return this.doApply(generatedLoot, context);
        }
        return generatedLoot;
    }

    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (AMConfig.addLootToChests) {
            generatedLoot.add(new ItemStack(AMItemRegistry.ANCIENT_DART));
        }
        return generatedLoot;
    }


    
    
}