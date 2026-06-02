package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Map;

public final class CitadelEntityModelBridge<E extends LivingEntity> extends EntityModel<LivingEntityRenderState> {

    private final AdvancedEntityModel<E> citadel;

    public CitadelEntityModelBridge(AdvancedEntityModel<E> citadel) {
        super(new ModelPart(Collections.emptyList(), Map.of()), RenderTypes::entityCutout);
        this.citadel = citadel;
    }

    public AdvancedEntityModel<E> citadel() {
        return citadel;
    }

    /** Citadel mesh draw for layers and custom submit (same as {@link #renderToBuffer(PoseStack, VertexConsumer, int, int, int)}). */
    public void renderCitadelToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.citadel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, color);
    }

    /**
     * Draws the Citadel mesh from a deferred {@link net.minecraft.client.renderer.SubmitNodeCollector#submitCustomGeometry}
     * replay. The lambda MUST pass its {@code pose} (the captured submit pose) here, never the renderer's live
     * {@link PoseStack} — the live stack is usually already popped by the time the replay runs, so Citadel (which builds
     * quads from {@link PoseStack#last()} at draw time) would read a stale/identity transform and draw the mesh at the
     * camera/origin (stray "ghost" geometry across the screen). See {@link AlexAdvancedEntityModel#withCitadelSubmitPose}.
     */
    public void submitCitadel(PoseStack.Pose submitPose, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        AlexAdvancedEntityModel.withCitadelSubmitPose(submitPose, new PoseStack(), s ->
            this.citadel.renderToBuffer(s, buffer, packedLight, packedOverlay, color));
    }

    /**
     * Sets {@link AlexAdvancedEntityModel#young} on the wrapped Citadel model (replaces removed {@code EntityModel#young}).
     */
    public void setCitadelYoung(boolean young) {
        ((AlexAdvancedEntityModel<?>) this.citadel).young = young;
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        LivingEntity raw = AlexsMobsClientKeys.getLiving(state);
        if (raw == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        E entity = (E) raw;
        // Per-entity baby flag: vanilla 26.1 no longer sets EntityModel#young, and only a handful of
        // renderers call setCitadelYoung() in extractRenderState. Set it here for every Citadel model so
        // the shared model's young flag can't leak from the previously rendered entity (babies rendering
        // adult-sized, or vice versa). Mirrors AlexAdvancedEntityModel.CitadelEntityModelBridge#setupAnim.
        if (this.citadel instanceof AlexAdvancedEntityModel<?> alex) {
            alex.young = state.isBaby;
        }
        float limbSwing = state.walkAnimationPos;
        float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
        float ageInTicks = state.ageInTicks;
        // Vanilla LivingEntityRenderer.extractRenderState: state.yRot is already head yaw relative to body (wrapped).
        float netHeadYaw = state.yRot;
        float headPitch = state.xRot;
        citadel.prepareMobModel(entity, limbSwing, limbSwingAmount, ageInTicks);
        citadel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public void copyPropertiesTo(HumanoidModel<?> model) {
        if (citadel instanceof ModelKangaroo kangaroo) {
            kangaroo.copyArmorPoseToHumanoid(model);
        }
    }
}