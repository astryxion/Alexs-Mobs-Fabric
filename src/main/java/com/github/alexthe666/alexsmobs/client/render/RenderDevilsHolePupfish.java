package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelDevilsHolePupfish;
import com.github.alexthe666.alexsmobs.entity.EntityDevilsHolePupfish;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderDevilsHolePupfish extends MobRenderer<EntityDevilsHolePupfish, LivingEntityRenderState, CitadelEntityModelBridge<EntityDevilsHolePupfish>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/devils_hole_pupfish.png");

    public RenderDevilsHolePupfish(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelDevilsHolePupfish()), 0.2F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityDevilsHolePupfish entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityDevilsHolePupfish p ? p : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        float scale = entitylivingbaseIn.getPupfishScale();
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
