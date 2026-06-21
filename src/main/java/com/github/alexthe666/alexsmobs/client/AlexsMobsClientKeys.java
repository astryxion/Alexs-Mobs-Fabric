package com.github.alexthe666.alexsmobs.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class AlexsMobsClientKeys {
    private static final Map<EntityRenderState, Entity> ENTITY_BY_STATE = Collections.synchronizedMap(new WeakHashMap<>());

    private AlexsMobsClientKeys() {
    }

    public static LivingEntity getLiving(LivingEntityRenderState state) {
        if (state instanceof com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel.CitadelLivingRenderState citadel) {
            return citadel.citadelEntity;
        }
        Entity mapped = ENTITY_BY_STATE.get(state);
        return mapped instanceof LivingEntity living ? living : null;
    }

    public static Entity getEntity(EntityRenderState state) {
        return ENTITY_BY_STATE.get(state);
    }

    public static void setEntity(EntityRenderState state, Entity entity) {
        if (state != null && entity != null) {
            ENTITY_BY_STATE.put(state, entity);
        }
    }
}