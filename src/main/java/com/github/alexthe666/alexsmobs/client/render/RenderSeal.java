package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelSeal;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerSealItem;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RenderSeal extends MobRenderer<EntitySeal, LivingEntityRenderState, CitadelEntityModelBridge<EntitySeal>> {
    private static final Identifier TEXTURE_BROWN_0 = Identifier.parse("alexsmobs:textures/entity/seal/seal_brown_0.png");
    private static final Identifier TEXTURE_BROWN_1 = Identifier.parse("alexsmobs:textures/entity/seal/seal_brown_1.png");
    private static final Identifier TEXTURE_ARCTIC_0 = Identifier.parse("alexsmobs:textures/entity/seal/seal_arctic_0.png");
    private static final Identifier TEXTURE_ARCTIC_1 = Identifier.parse("alexsmobs:textures/entity/seal/seal_arctic_1.png");
    private static final Identifier TEXTURE_ARCTIC_BABY = Identifier.parse("alexsmobs:textures/entity/seal/seal_arctic_baby.png");
    private static final Identifier TEXTURE_TEARS = Identifier.parse("alexsmobs:textures/entity/seal/seal_crying.png");

    public RenderSeal(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelSeal()), 0.45F);
        this.addLayer(new LayerSealItem(this));
        this.addLayer(new SealTearsLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected boolean shouldShowName(EntitySeal seal, double distanceToCameraSq) {
        return super.shouldShowName(seal, distanceToCameraSq) || seal.isTearsEasterEgg();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntitySeal entity = AlexsMobsClientKeys.getLiving(state) instanceof EntitySeal s ? s : null;
        if (entity == null) {
            return TEXTURE_BROWN_0;
        }
        if (entity.isArctic()) {
            return entity.isBaby() ? TEXTURE_ARCTIC_BABY : entity.getVariant() == 1 ? TEXTURE_ARCTIC_1 : TEXTURE_ARCTIC_0;
        }
        return entity.getVariant() == 1 ? TEXTURE_BROWN_1 : TEXTURE_BROWN_0;
    }

    @Override
    protected void submitNameDisplay(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        EntitySeal seal = AlexsMobsClientKeys.getLiving(state) instanceof EntitySeal s ? s : null;
        if (seal != null && seal.isTearsEasterEgg() && state.nameTag != null && state.distanceToCameraSq < 4096.0) {
            poseStack.pushPose();
            if (state.scoreText != null) {
                submitNodeCollector.submitNameTag(
                    poseStack, state.nameTagAttachment, 0, state.scoreText, !state.isDiscrete, state.lightCoords, state.distanceToCameraSq, camera
                );
                poseStack.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
            }
            String[] split = state.nameTag.getString(512).split(" ");
            StringBuilder recombined = new StringBuilder();
            List<String> strings = new ArrayList<>();
            for (int wordIndex = 0; wordIndex < split.length; wordIndex++) {
                recombined.append(split[wordIndex]).append(" ");
                if (recombined.length() > 15 || wordIndex == split.length - 1) {
                    strings.add(recombined.toString());
                    recombined = new StringBuilder();
                }
            }
            int offset = 10 - 10 * strings.size();
            for (String print : strings) {
                Component line = Component.literal(print);
                submitNodeCollector.submitNameTag(
                    poseStack, state.nameTagAttachment, offset, line, !state.isDiscrete, state.lightCoords, state.distanceToCameraSq, camera
                );
                poseStack.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
                offset += 10;
            }
            poseStack.popPose();
            return;
        }
        super.submitNameDisplay(state, poseStack, submitNodeCollector, camera);
    }

    static class SealTearsLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntitySeal>> {

        public SealTearsLayer(RenderSeal p_i50928_1_) {
            super(p_i50928_1_);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntitySeal entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntitySeal s ? s : null;
            if (entitylivingbaseIn == null || !entitylivingbaseIn.isTearsEasterEgg()) {
                return;
            }
            this.getParentModel().setupAnim(state);
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            collector.submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(TEXTURE_TEARS), (pose, lead) ->
                this.getParentModel().renderCitadelToBuffer(matrixStackIn, lead, packedLightIn, overlay, -1)
            );
        }
    }
}
