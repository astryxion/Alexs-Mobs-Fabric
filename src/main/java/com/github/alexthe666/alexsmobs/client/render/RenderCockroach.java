package com.github.alexthe666.alexsmobs.client.render;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCockroachMaracas;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderCockroach extends MobRenderer<EntityCockroach, LivingEntityRenderState, CitadelEntityModelBridge<EntityCockroach>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/cockroach.png");

    public RenderCockroach(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelCockroach()),  0.3F);
        this.addLayer(new LayerCockroachMaracas(this, renderManagerIn));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public void extractRenderState(EntityCockroach entity, LivingEntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        this.getModel().setCitadelYoung(entity.isBaby());
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
