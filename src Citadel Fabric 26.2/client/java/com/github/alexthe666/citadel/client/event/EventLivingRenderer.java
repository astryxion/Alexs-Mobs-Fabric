package com.github.alexthe666.citadel.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class EventLivingRenderer {
    private static final Map<net.minecraft.client.renderer.entity.state.LivingEntityRenderState, LivingEntity> ENTITY_BY_STATE =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static void mapState(net.minecraft.client.renderer.entity.state.LivingEntityRenderState state, LivingEntity entity) {
        if (state != null && entity != null) {
            ENTITY_BY_STATE.put(state, entity);
        }
    }

    public static LivingEntity getLiving(net.minecraft.client.renderer.entity.state.LivingEntityRenderState state) {
        return ENTITY_BY_STATE.get(state);
    }

    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(
            Listener.class,
            listeners -> event -> {
                for (Listener listener : listeners) {
                    listener.onEvent(event);
                }
            });

    private LivingEntity entity;
    private EntityModel model;
    private PoseStack poseStack;
    private float partialTicks;

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

    public void post() {
        EVENT.invoker().onEvent(this);
    }

    @FunctionalInterface
    public interface Listener {
        void onEvent(EventLivingRenderer event);
    }

    public static class SetupRotations extends EventLivingRenderer {
        private float bodyYRot;

        public SetupRotations(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks) {
            super(entity, model, poseStack, partialTicks);
            this.bodyYRot = bodyYRot;
        }

        public float getBodyYRot() {
            return bodyYRot;
        }
    }

    public static class AccessToBufferSource extends EventLivingRenderer {
        private float bodyYRot;
        private SubmitNodeCollector submitCollector;
        private int packedLight;

        public AccessToBufferSource(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, SubmitNodeCollector submitCollector, int packedLight) {
            super(entity, model, poseStack, partialTicks);
            this.bodyYRot = bodyYRot;
            this.submitCollector = submitCollector;
            this.packedLight = packedLight;
        }

        public float getBodyYRot() {
            return bodyYRot;
        }

        public SubmitNodeCollector getSubmitCollector() {
            return submitCollector;
        }

        public int getPackedLight() {
            return packedLight;
        }
    }

    public static class PreSetupAnimations extends AccessToBufferSource {

        public PreSetupAnimations(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, SubmitNodeCollector submitCollector, int packedLight) {
            super(entity, model, poseStack, bodyYRot, partialTicks, submitCollector, packedLight);
        }
    }

    public static class PostSetupAnimations extends AccessToBufferSource {

        public PostSetupAnimations(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, SubmitNodeCollector submitCollector, int packedLight) {
            super(entity, model, poseStack, bodyYRot, partialTicks, submitCollector, packedLight);
        }
    }

    public static class PostRenderModel extends AccessToBufferSource {

        public PostRenderModel(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, SubmitNodeCollector submitCollector, int packedLight) {
            super(entity, model, poseStack, bodyYRot, partialTicks, submitCollector, packedLight);
        }
    }
}
