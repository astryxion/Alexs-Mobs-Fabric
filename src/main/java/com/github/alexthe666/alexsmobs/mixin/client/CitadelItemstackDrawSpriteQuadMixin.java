package com.github.alexthe666.alexsmobs.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Citadel 26.1 {@code drawSpriteQuad} omits overlay (UV1) and normal attributes required by the
 * {@link net.minecraft.client.renderer.rendertype.RenderTypes#entityCutout} format on Minecraft 26.1,
 * which crashes GUI item atlas baking (advancements screen, etc.).
 */
@Mixin(targets = "com.github.alexthe666.citadel.client.CitadelItemstackRenderer$CitadelStackSpecialRenderer")
public abstract class CitadelItemstackDrawSpriteQuadMixin {

    @Inject(method = "drawSpriteQuad", remap = false, at = @At("HEAD"), cancellable = true)
    private static void alexsmobs$drawSpriteQuadWithFullFormat(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            TextureAtlasSprite sprite,
            int light,
            CallbackInfo ci
    ) {
        var renderType = RenderTypes.entityCutout(sprite.atlasLocation());
        collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) -> {
            Matrix4f matrix = pose.pose();
            int c = 255;
            vertex(consumer, matrix, pose, sprite, 1.0F, 1.0F, 0.0F, sprite.getU1(), sprite.getV0(), c, light);
            vertex(consumer, matrix, pose, sprite, 0.0F, 1.0F, 0.0F, sprite.getU0(), sprite.getV0(), c, light);
            vertex(consumer, matrix, pose, sprite, 0.0F, 0.0F, 0.0F, sprite.getU0(), sprite.getV1(), c, light);
            vertex(consumer, matrix, pose, sprite, 1.0F, 0.0F, 0.0F, sprite.getU1(), sprite.getV1(), c, light);
        });
        ci.cancel();
    }

    private static void vertex(
            VertexConsumer consumer,
            Matrix4f matrix,
            PoseStack.Pose pose,
            TextureAtlasSprite sprite,
            float x,
            float y,
            float z,
            float u,
            float v,
            int c,
            int light
    ) {
        consumer.addVertex(matrix, x, y, z)
                .setUv(u, v)
                .setColor(c, c, c, 255)
                .setLight(light)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }
}
