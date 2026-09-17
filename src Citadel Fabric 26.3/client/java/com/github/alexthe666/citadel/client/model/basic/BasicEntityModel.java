package com.github.alexthe666.citadel.client.model.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;

public abstract class BasicEntityModel<T extends Entity> {
    public int textureWidth = 64;
    public int textureHeight = 32;

    protected BasicEntityModel() {
    }

    public abstract void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLightIn, int packedOverlayIn, int color);

    public abstract Iterable<BasicModelPart> parts();

    public abstract void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
    }
}
