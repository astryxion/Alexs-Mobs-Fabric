package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelTasmanianDevil;
import com.github.alexthe666.alexsmobs.entity.EntityTasmanianDevil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderTasmanianDevil extends MobRenderer<EntityTasmanianDevil, LivingEntityRenderState, CitadelEntityModelBridge<EntityTasmanianDevil>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/tasmanian_devil.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.parse("alexsmobs:textures/entity/tasmanian_devil_angry.png");

    public RenderTasmanianDevil(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelTasmanianDevil()), 0.3F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityTasmanianDevil entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityTasmanianDevil t ? t : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.getAnimation() == EntityTasmanianDevil.ANIMATION_HOWL && entity.getAnimationTick() < 34 ? TEXTURE_ANGRY : TEXTURE;
    }
}
