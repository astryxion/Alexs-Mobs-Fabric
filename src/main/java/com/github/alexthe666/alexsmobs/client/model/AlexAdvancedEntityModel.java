package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Replaces {@code EntityModel#young} removed in Minecraft 26.1; Alex's Mobs Citadel models read {@link #young} for baby scaling.
 */
public abstract class AlexAdvancedEntityModel<T extends Entity> extends AdvancedEntityModel<T> {

    public boolean young = false;

    public AlexAdvancedEntityModel() {
        super();
    }

    /**
     * {@link SubmitNodeCollector#submitCustomGeometry} replays geometry using the {@link PoseStack.Pose} captured when the
     * submit was recorded. Citadel builds quads from {@link PoseStack#last()} at draw time, so the stack passed into
     * {@link AdvancedEntityModel#renderToBuffer} must carry that pose — not the renderer's live {@link PoseStack}, which
     * is often already popped when the replay runs (same pattern as vanilla map rendering with {@code addVertex(pose, ...)}).
     *
     * <p>Citadel {@code pushPose}/{@code popPose} must leave the scratch stack at the single root frame; if not, the next
     * {@link PoseStack#last()} is the wrong entry and transforms corrupt (random full-screen garbage while entities render).
     */
    public static void withCitadelSubmitPose(PoseStack.Pose submitPose, PoseStack scratch, Consumer<PoseStack> draw) {
        while (!scratch.isEmpty()) {
            scratch.popPose();
        }
        scratch.last().set(submitPose);
        draw.accept(scratch);
        while (!scratch.isEmpty()) {
            scratch.popPose();
        }
    }

    /**
     * Vanilla-{@link Model} counterpart to {@link CitadelEntityModelBridge#submitCitadel}: draws a model from a deferred
     * {@code submitCustomGeometry} replay using the captured submit {@code pose}. Pass the lambda's {@code pose}, never the
     * renderer's live {@link PoseStack} (which is usually already popped at replay time, leaving the model at the camera).
     */
    public static void submitModel(PoseStack.Pose submitPose, Model model, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        withCitadelSubmitPose(submitPose, new PoseStack(), s -> model.renderToBuffer(s, buffer, packedLight, packedOverlay, color));
    }

    /** Citadel-{@link AdvancedEntityModel} overload of {@link #submitModel(PoseStack.Pose, Model, VertexConsumer, int, int, int)}
     * (Citadel models do not extend vanilla {@link Model}). */
    public static void submitModel(PoseStack.Pose submitPose, AdvancedEntityModel<?> model, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        withCitadelSubmitPose(submitPose, new PoseStack(), s -> model.renderToBuffer(s, buffer, packedLight, packedOverlay, color));
    }

    /**
     * Carries the live entity for Citadel models during 26.1 render-state extraction; vanilla {@link LivingEntityRenderState}
     * does not retain entity references.
     */
    public static final class CitadelLivingRenderState extends LivingEntityRenderState {
        public LivingEntity citadelEntity;
    }

    /**
     * Minimal {@link EntityModel} bridge so {@link net.minecraft.client.renderer.entity.MobRenderer} satisfies generic bounds.
     * Actual mesh drawing uses Citadel {@link AdvancedEntityModel} via {@link SubmitNodeCollector#submitCustomGeometry}
     * in renderer {@code submit} (vanilla {@code submitModel} only walks empty {@link ModelPart} roots for Citadel models).
     * Use {@link #withCitadelSubmitPose} when drawing so vertices use the queued pose, not the live stack.
     */
    public static final class CitadelEntityModelBridge<T extends LivingEntity, M extends AlexAdvancedEntityModel<T>> extends EntityModel<CitadelLivingRenderState> {
        private final M delegate;

        public CitadelEntityModelBridge(M delegate) {
            super(new ModelPart(Collections.emptyList(), Map.of()), RenderTypes::entityCutout);
            this.delegate = delegate;
        }

        public M delegate() {
            return this.delegate;
        }

        @Override
        public void setupAnim(CitadelLivingRenderState state) {
            if (state.citadelEntity == null) {
                return;
            }
            if (!(state.citadelEntity instanceof LivingEntity living)) {
                return;
            }
            @SuppressWarnings("unchecked")
            T typed = (T) living;
            this.delegate.young = state.isBaby;
            this.delegate.setupAnim(
                typed,
                state.walkAnimationPos,
                state.walkAnimationSpeed,
                state.ageInTicks,
                state.yRot,
                state.xRot
            );
        }
    }
}