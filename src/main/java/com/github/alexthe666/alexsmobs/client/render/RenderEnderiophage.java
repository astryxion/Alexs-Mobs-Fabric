package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelEnderiophage;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class RenderEnderiophage extends MobRenderer<EntityEnderiophage, LivingEntityRenderState, CitadelEntityModelBridge<EntityEnderiophage>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/enderiophage.png");
    private static final Identifier TEXTURE_GLOW = Identifier.parse("alexsmobs:textures/entity/enderiophage_glow.png");
    private static final Identifier TEXTURE_OVERWORLD = Identifier.parse("alexsmobs:textures/entity/enderiophage_overworld.png");
    private static final Identifier TEXTURE_OVERWORLD_GLOW = Identifier.parse("alexsmobs:textures/entity/enderiophage_overworld_glow.png");
    private static final Identifier TEXTURE_NETHER = Identifier.parse("alexsmobs:textures/entity/enderiophage_nether.png");
    private static final Identifier TEXTURE_NETHER_GLOW = Identifier.parse("alexsmobs:textures/entity/enderiophage_nether_glow.png");

    public RenderEnderiophage(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelEnderiophage()), 0.5F);
        this.addLayer(new EnderiophageEyesLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Nullable
    @Override
    protected RenderType getRenderType(LivingEntityRenderState state, boolean normal, boolean invis, boolean outline) {
        Identifier tex = this.getTextureLocation(state);
        if (invis) {
            return RenderTypes.entityTranslucentCullItemTarget(tex);
        } else if (normal) {
            return RenderTypes.entityTranslucent(tex);
        } else {
            return outline ? RenderTypes.outline(tex) : null;
        }
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityEnderiophage entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityEnderiophage e ? e : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        float partialTickTime = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float scale = entitylivingbaseIn.prevEnderiophageScale + (entitylivingbaseIn.getPhageScale() - entitylivingbaseIn.prevEnderiophageScale) * partialTickTime;
        matrixStackIn.scale(0.8F * scale, 0.8F * scale, 0.8F * scale);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityEnderiophage entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityEnderiophage e ? e : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.getVariant() == 2 ? TEXTURE_NETHER : entity.getVariant() == 1 ? TEXTURE_OVERWORLD : TEXTURE;
    }

    static class EnderiophageEyesLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityEnderiophage>> {

        public EnderiophageEyesLayer(RenderEnderiophage p_i50928_1_) {
            super(p_i50928_1_);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityEnderiophage entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityEnderiophage e ? e : null;
            if (entitylivingbaseIn == null) {
                return;
            }
            this.getParentModel().setupAnim(state);
            RenderType glowType = AMRenderTypes.getGhost(entitylivingbaseIn.getVariant() == 2 ? TEXTURE_NETHER_GLOW : entitylivingbaseIn.getVariant() == 1 ? TEXTURE_OVERWORLD_GLOW : TEXTURE_GLOW);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            PoseStack citadelPoseStack = new PoseStack();
            collector.submitCustomGeometry(matrixStackIn, glowType, (pose, ivertexbuilder) ->
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.getParentModel().renderCitadelToBuffer(scratch, ivertexbuilder, 15728640, overlay, -1))
            );
        }
    }
}
