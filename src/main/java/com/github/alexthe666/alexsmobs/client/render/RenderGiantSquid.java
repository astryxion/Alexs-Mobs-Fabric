package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelGiantSquid;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquid;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquidPart;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class RenderGiantSquid extends MobRenderer<EntityGiantSquid, LivingEntityRenderState, CitadelEntityModelBridge<EntityGiantSquid>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/giant_squid.png");
    private static final Identifier TEXTURE_BLUE = Identifier.parse("alexsmobs:textures/entity/giant_squid_blue.png");
    private static final Identifier TEXTURE_DEPRESSURIZED = Identifier.parse("alexsmobs:textures/entity/giant_squid_depressurized.png");

    public RenderGiantSquid(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelGiantSquid()), 1F);
        this.addLayer(new LayerDepressurization(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected float getFlipDegrees() {
        return 0.0F;
    }

    @Override
    public boolean shouldRender(EntityGiantSquid livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (livingEntityIn.isCaptured() && livingEntityIn.isAlive()) {
            return false;
        }
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        }
        for (EntityGiantSquidPart part : livingEntityIn.allParts) {
            if (camera.isVisible(part.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityGiantSquid entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityGiantSquid s ? s : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isBlue() ? TEXTURE_BLUE : TEXTURE;
    }

    static class LayerDepressurization extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityGiantSquid>> {

        LayerDepressurization(RenderGiantSquid render) {
            super(render);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityGiantSquid squid = AlexsMobsClientKeys.getLiving(state) instanceof EntityGiantSquid s ? s : null;
            if (squid == null) {
                return;
            }
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            float alpha = squid.prevDepressurization + (squid.getDepressurization() - squid.prevDepressurization) * partialTicks;
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            this.getParentModel().setupAnim(state);
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(TEXTURE_DEPRESSURIZED), (pose, consumer) ->
                    this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, packedLightIn, overlay, AMColorUtil.packColor(1.0F, 1.0F, 1.0F, alpha)));
        }
    }
}
