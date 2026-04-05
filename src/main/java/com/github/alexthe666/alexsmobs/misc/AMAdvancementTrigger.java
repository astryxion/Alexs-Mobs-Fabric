package com.github.alexthe666.alexsmobs.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class AMAdvancementTrigger extends SimpleCriterionTrigger<AMAdvancementTrigger.Instance> {
    public final ResourceLocation resourceLocation;

    public AMAdvancementTrigger(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public Codec<Instance> codec() {
        return RecordCodecBuilder.create(inst -> inst.group(
                ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(Instance::player)
        ).apply(inst, Instance::new));
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    public static class Instance implements SimpleCriterionTrigger.SimpleInstance {
        private final Optional<ContextAwarePredicate> player;

        public Instance(Optional<ContextAwarePredicate> player) {
            this.player = player;
        }

        public Instance(ContextAwarePredicate player) {
            this(Optional.of(player));
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return player;
        }
    }
}
