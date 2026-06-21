package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelSnowLeopard;
import com.github.alexthe666.alexsmobs.entity.EntitySnowLeopard;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderSnowLeopard extends MobRenderer<EntitySnowLeopard, LivingEntityRenderState, CitadelEntityModelBridge<EntitySnowLeopard>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/snow_leopard.png");
    private static final Identifier TEXTURE_SLEEPING = Identifier.parse("alexsmobs:textures/entity/snow_leopard_sleeping.png");

    public RenderSnowLeopard(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelSnowLeopard()), 0.4F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntitySnowLeopard entity = AlexsMobsClientKeys.getLiving(state) instanceof EntitySnowLeopard s ? s : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isSleeping() ? TEXTURE_SLEEPING : TEXTURE;
    }
}
