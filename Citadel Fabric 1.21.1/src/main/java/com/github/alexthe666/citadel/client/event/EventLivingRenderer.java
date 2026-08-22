package com.github.alexthe666.citadel.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventLivingRenderer {

    private final LivingEntity entity;
    private final EntityModel model;
    private final PoseStack poseStack;
    private final float partialTicks;

    public EventLivingRenderer(LivingEntity entity, EntityModel model, PoseStack poseStack, float partialTicks) {
        this.entity = entity;
        this.model = model;
        this.poseStack = poseStack;
        this.partialTicks = partialTicks;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public EntityModel getModel() {
        return model;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public static class SetupRotations extends EventLivingRenderer {
        private static final List<Consumer<SetupRotations>> LISTENERS = new ArrayList<>();
        private final float bodyYRot;

        public static void addListener(Consumer<SetupRotations> listener) { LISTENERS.add(listener); }
        public static void post(SetupRotations event) { for (Consumer<SetupRotations> l : LISTENERS) l.accept(event); }

        public SetupRotations(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks) {
            super(entity, model, poseStack, partialTicks);
            this.bodyYRot = bodyYRot;
        }

        public float getBodyYRot() {
            return bodyYRot;
        }
    }

    public static class AccessToBufferSource extends EventLivingRenderer {
        private final float bodyYRot;
        private final MultiBufferSource bufferSource;
        private final int packedLight;

        public AccessToBufferSource(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, MultiBufferSource bufferSource, int packedLight) {
            super(entity, model, poseStack, partialTicks);
            this.bodyYRot = bodyYRot;
            this.bufferSource = bufferSource;
            this.packedLight = packedLight;
        }

        public float getBodyYRot() {
            return bodyYRot;
        }

        public MultiBufferSource getBufferSource() {
            return bufferSource;
        }

        public int getPackedLight() {
            return packedLight;
        }
    }

    public static class PreSetupAnimations extends AccessToBufferSource {
        private static final List<Consumer<PreSetupAnimations>> LISTENERS = new ArrayList<>();
        public static void addListener(Consumer<PreSetupAnimations> listener) { LISTENERS.add(listener); }
        public static void post(PreSetupAnimations event) { for (Consumer<PreSetupAnimations> l : LISTENERS) l.accept(event); }

        public PreSetupAnimations(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, MultiBufferSource bufferSource, int packedLight) {
            super(entity, model, poseStack, bodyYRot, partialTicks, bufferSource, packedLight);
        }
    }

    public static class PostSetupAnimations extends AccessToBufferSource {
        private static final List<Consumer<PostSetupAnimations>> LISTENERS = new ArrayList<>();
        public static void addListener(Consumer<PostSetupAnimations> listener) { LISTENERS.add(listener); }
        public static void post(PostSetupAnimations event) { for (Consumer<PostSetupAnimations> l : LISTENERS) l.accept(event); }

        public PostSetupAnimations(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, MultiBufferSource bufferSource, int packedLight) {
            super(entity, model, poseStack, bodyYRot, partialTicks, bufferSource, packedLight);
        }
    }

    public static class PostRenderModel extends AccessToBufferSource {
        private static final List<Consumer<PostRenderModel>> LISTENERS = new ArrayList<>();
        public static void addListener(Consumer<PostRenderModel> listener) { LISTENERS.add(listener); }
        public static void post(PostRenderModel event) { for (Consumer<PostRenderModel> l : LISTENERS) l.accept(event); }

        public PostRenderModel(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, MultiBufferSource bufferSource, int packedLight) {
            super(entity, model, poseStack, bodyYRot, partialTicks, bufferSource, packedLight);
        }
    }
}
