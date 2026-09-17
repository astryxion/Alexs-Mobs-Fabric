package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

/**
 * Simple advancement trigger for Alex's Mobs
 */
public class AMAdvancementTrigger extends SimpleCriterionTrigger<AMAdvancementTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        AlexsMobs.LOGGER.info("AMAdvancementTrigger.trigger() called for player: {}", player.getName().getString());
        this.trigger(player, (instance) -> true);
    }

    public record TriggerInstance(Optional<Holder<LootItemCondition>> player) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
            ).apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> instance() {
            return new AMAdvancementTrigger().createCriterion(new TriggerInstance(Optional.empty()));
        }
    }
}
