package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.ClientLayerRegistry;
import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow
    @Final
    protected M model;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void alexsmobs$addRainbowLayer(CallbackInfo ci) {
        ClientLayerRegistry.addLayerToRenderer((LivingEntityRenderer<?, ?>) (Object) this);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void alexsmobs$setBabyModelFlag(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (entity.getType().builtInRegistryHolder().key().location().getNamespace().equals(AlexsMobs.MODID)) {
            this.model.young = entity.isBaby();
        }
    }

    /** Fabric: replaces NeoForge {@code RenderLivingEvent.Pre}. */
    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void alexsmobs$preRenderLiving(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        LivingEntityRenderer<T, M> renderer = (LivingEntityRenderer<T, M>) (Object) this;
        ClientEvents.RenderLivingEventPre event = new ClientEvents.RenderLivingEventPre() {
            private boolean canceled;

            @Override
            public LivingEntity getEntity() {
                return entity;
            }

            @Override
            public PoseStack getPoseStack() {
                return poseStack;
            }

            @Override
            public float getPartialTick() {
                return partialTicks;
            }

            @Override
            public int getPackedLight() {
                return packedLight;
            }

            @Override
            public MultiBufferSource getMultiBufferSource() {
                return buffer;
            }

            @Override
            public LivingEntityRenderer<?, ?> getRenderer() {
                return renderer;
            }

            @Override
            public void setCanceled(boolean b) {
                this.canceled = b;
            }

            @Override
            public boolean isCanceled() {
                return canceled;
            }
        };
        ClientEvents.getInstance().onPreRenderEntity(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    /** Fabric: replaces NeoForge {@code RenderLivingEvent.Post} (vine lasso leash, effect pose cleanup, etc.). */
    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN")
    )
    private void alexsmobs$postRenderLiving(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        ClientEvents.getInstance().onPostRenderEntity(new ClientEvents.RenderLivingEventPost() {
            @Override
            public LivingEntity getEntity() {
                return entity;
            }

            @Override
            public PoseStack getPoseStack() {
                return poseStack;
            }

            @Override
            public float getPartialTick() {
                return partialTicks;
            }

            @Override
            public MultiBufferSource getMultiBufferSource() {
                return buffer;
            }

            @Override
            public int getPackedLight() {
                return packedLight;
            }
        });
    }
}
