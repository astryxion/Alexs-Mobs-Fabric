package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelWarpedToad;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerWarpedToadGlow;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderWarpedToad extends MobRenderer<EntityWarpedToad, LivingEntityRenderState, CitadelEntityModelBridge<EntityWarpedToad>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/warped_toad.png");
    private static final Identifier TEXTURE_BLINKING = Identifier.parse("alexsmobs:textures/entity/warped_toad_blink.png");
    private static final Identifier TEXTURE_PEPE = Identifier.parse("alexsmobs:textures/entity/warped_toad_pepe.png");
    private static final Identifier TEXTURE_PEPE_BLINKING = Identifier.parse("alexsmobs:textures/entity/warped_toad_pepe_blink.png");

    public RenderWarpedToad(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelWarpedToad()), 0.85F);
        this.addLayer(new LayerWarpedToadGlow(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(1.25F, 1.25F, 1.25F);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityWarpedToad entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityWarpedToad w ? w : null;
        if (entity == null) {
            return TEXTURE;
        }
        if (entity.isBased()) {
            return entity.isBlinking() ? TEXTURE_PEPE_BLINKING : TEXTURE_PEPE;
        } else {
            return entity.isBlinking() ? TEXTURE_BLINKING : TEXTURE;
        }
    }
}
