package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.NoAnimHumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class EntityArmorModelCache {

    private final EntityModelSet modelSet;
    private final Map<UUID, ArmorModelSet<NoAnimHumanoidModel>> cache = new WeakHashMap<>();

    public EntityArmorModelCache(EntityModelSet modelSet) {
        this.modelSet = modelSet;
    }

    public ArmorModelSet<NoAnimHumanoidModel> get(LivingEntity entity) {
        return this.cache.computeIfAbsent(
                entity.getUUID(),
                ignored -> ArmorModelSet.bake(ModelLayers.ARMOR_STAND_ARMOR, this.modelSet, NoAnimHumanoidModel::new)
        );
    }
}
