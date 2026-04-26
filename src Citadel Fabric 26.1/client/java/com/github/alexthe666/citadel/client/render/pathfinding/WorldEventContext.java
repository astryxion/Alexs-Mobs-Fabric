package com.github.alexthe666.citadel.client.render.pathfinding;

import com.mojang.blaze3d.vertex.PoseStack;
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

    /**
     * In chunks
     */
    int clientRenderDist;

    private void setup() {
        this.bufferSource = WorldRenderMacros.getBufferSource();
        this.poseStack = new PoseStack();
        this.partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks();
        this.clientLevel = Minecraft.getInstance().level;
        this.clientPlayer = Minecraft.getInstance().player;
        this.mainHandItem = this.clientPlayer != null ? this.clientPlayer.getMainHandItem() : ItemStack.EMPTY;
        this.clientRenderDist = Minecraft.getInstance().options.renderDistance().get();

        final Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().position();
        this.poseStack.pushPose();
        this.poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
    }

    private void teardown() {
        this.poseStack.popPose();
    }

    public void renderWorldLastAfterOpaque() {
        setup();
        PathfindingDebugRenderer.render(this);
        this.bufferSource.endBatch();
        teardown();
    }

    public void renderWorldLastAfterTranslucent() {
        setup();
        this.bufferSource.endBatch();
        teardown();
    }
}
