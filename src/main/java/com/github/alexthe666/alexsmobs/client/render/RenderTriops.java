package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelTriops;
import com.github.alexthe666.alexsmobs.entity.EntityTriops;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderTriops extends MobRenderer<EntityTriops, LivingEntityRenderState, CitadelEntityModelBridge<EntityTriops>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/triops.png");

    public RenderTriops(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelTriops()), 0.2F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityTriops entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityTriops t ? t : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        float scale = entitylivingbaseIn.getTriopsScale();
        if (entitylivingbaseIn.isBaby()) {
            scale *= 0.65F;
        }
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
