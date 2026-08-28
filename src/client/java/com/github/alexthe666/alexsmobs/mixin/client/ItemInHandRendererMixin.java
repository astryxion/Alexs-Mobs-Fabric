package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$renderHand(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand,
                                      float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack,
                                      MultiBufferSource buffers, int packedLight, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        final boolean[] canceled = {false};
        ClientEvents.getInstance().onRenderHand(new ClientEvents.RenderHandEvent() {
            @Override
            public InteractionHand getHand() {
                return hand;
            }

            @Override
            public PoseStack getPoseStack() {
                return poseStack;
            }

            @Override
            public float getPartialTick() {
                return partialTick;
            }

            @Override
            public MultiBufferSource getMultiBufferSource() {
                return buffers;
            }

            @Override
            public int getPackedLight() {
                return packedLight;
            }

            @Override
            public ItemStack getItemStack() {
                return stack;
            }

            @Override
            public void setCanceled(boolean b) {
                canceled[0] = b;
            }
        });
        if (canceled[0]) {
            ci.cancel();
        }
    }
}
