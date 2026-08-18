package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelGazelle;
import com.github.alexthe666.alexsmobs.entity.EntityGazelle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderGazelle extends MobRenderer<EntityGazelle, LivingEntityRenderState, CitadelEntityModelBridge<EntityGazelle>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/gazelle.png");

    public RenderGazelle(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelGazelle()), 0.4F);
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
        return TEXTURE;
    }
}
