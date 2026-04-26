package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelJerboa;
import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderJerboa extends MobRenderer<EntityJerboa, LivingEntityRenderState, CitadelEntityModelBridge<EntityJerboa>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/jerboa.png");
    private static final Identifier TEXTURE_SLEEPING = Identifier.parse("alexsmobs:textures/entity/jerboa_sleeping.png");

    public RenderJerboa(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelJerboa()), 0.1F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityJerboa entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityJerboa j ? j : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isSleeping() ? TEXTURE_SLEEPING : TEXTURE;
    }
}
