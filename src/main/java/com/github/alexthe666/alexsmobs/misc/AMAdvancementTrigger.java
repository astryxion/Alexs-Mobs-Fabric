package com.github.alexthe666.alexsmobs.misc;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AMAdvancementTrigger extends SimpleCriterionTrigger<AMAdvancementTrigger.Instance> {
    public final ResourceLocation resourceLocation;

    public AMAdvancementTrigger(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public ResourceLocation getId() {
        return this.resourceLocation;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    @Override
    public Instance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        return new Instance(player, this.resourceLocation);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(ContextAwarePredicate player, ResourceLocation id) {
            super(id, player);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            return super.serializeToJson(context);
        }
    }
}
