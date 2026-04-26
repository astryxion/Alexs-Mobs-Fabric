package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelFroststalker;
import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderFroststalker extends MobRenderer<EntityFroststalker, LivingEntityRenderState, CitadelEntityModelBridge<EntityFroststalker>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/froststalker.png");
    private static final Identifier TEXTURE_NOSPIKES = Identifier.parse("alexsmobs:textures/entity/froststalker_nospikes.png");

    public RenderFroststalker(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelFroststalker()), 0.4F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    protected boolean isShaking(LivingEntityRenderState state) {
        EntityFroststalker entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityFroststalker f ? f : null;
        return entity != null && entity.isInWaterRainOrBubble() && !entity.hasSpikes();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityFroststalker entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityFroststalker f ? f : null;
        if (entity == null) {
            return TEXTURE_NOSPIKES;
        }
        return entity.hasSpikes() ? TEXTURE : TEXTURE_NOSPIKES;
    }
}
