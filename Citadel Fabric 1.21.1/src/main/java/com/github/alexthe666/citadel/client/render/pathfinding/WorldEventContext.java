package com.github.alexthe666.citadel.client.render.pathfinding;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class WorldEventContext {
    public static final WorldEventContext INSTANCE = new WorldEventContext();

    private WorldEventContext() {
    }

    public MultiBufferSource.BufferSource bufferSource;
    public PoseStack poseStack;
    public float partialTicks;
    public ClientLevel clientLevel;
    public LocalPlayer clientPlayer;
    public ItemStack mainHandItem;
    int clientRenderDist;

    public void renderWorldLastEvent(final WorldRenderContext context) {
        bufferSource = WorldRenderMacros.getBufferSource();
        poseStack = context.matrixStack();
        partialTicks = context.tickCounter().getGameTimeDeltaPartialTick(false);
        clientLevel = Minecraft.getInstance().level;
        clientPlayer = Minecraft.getInstance().player;
        mainHandItem = clientPlayer != null ? clientPlayer.getMainHandItem() : ItemStack.EMPTY;
        clientRenderDist = Minecraft.getInstance().options.renderDistance().get();

        final Vec3 cameraPos = context.camera().getPosition();
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());

        PathfindingDebugRenderer.render(this);
        bufferSource.endBatch();

        poseStack.popPose();
    }
}
