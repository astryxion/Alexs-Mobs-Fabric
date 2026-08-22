package com.github.alexthe666.citadel.client.model.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

public abstract class BasicEntityModel<T extends Entity> extends EntityModel<T> {
    public int textureWidth = 64;
    public int textureHeight = 32;

    protected BasicEntityModel() {
        this(RenderType::entityCutoutNoCull);
    }

    protected BasicEntityModel(Function<ResourceLocation, RenderType> p_102613_) {
        super(p_102613_);
    }

    @Override
    public void renderToBuffer(PoseStack p_103013_, VertexConsumer p_103014_, int p_103015_, int p_103016_, int p_103017_) {
        float r = ((p_103017_ >> 16) & 0xFF) / 255.0F;
        float g = ((p_103017_ >> 8) & 0xFF) / 255.0F;
        float b = (p_103017_ & 0xFF) / 255.0F;
        float a = ((p_103017_ >> 24) & 0xFF) / 255.0F;
        this.parts().forEach((p_103030_) -> {
            p_103030_.render(p_103013_, p_103014_, p_103015_, p_103016_, r, g, b, a);
        });
    }

    public abstract Iterable<BasicModelPart> parts();

    @Override
    public abstract void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_);

    @Override
    public void prepareMobModel(T p_102614_, float p_102615_, float p_102616_, float p_102617_) {
    }
}