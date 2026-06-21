package com.github.alexthe666.alexsmobs.client.render.tile;

import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.Random;

public class RenderCapsid<T extends TileEntityCapsid> implements BlockEntityRenderer<T, RenderCapsid.CapsidRenderState> {

    private final ItemModelResolver itemModelResolver;

    public RenderCapsid(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this.itemModelResolver = rendererDispatcherIn.itemModelResolver();
    }

    protected int getModelCount(ItemStack stack) {
        int i = 1;
        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }

        return i;
    }

    public static final class CapsidRenderState extends BlockEntityRenderState {
        public ItemStack stack = ItemStack.EMPTY;
        public long randomSeed;
        public float floatProgress;
        public float yaw;
        public float displayAngleRad;
        public int modelCount;
        public boolean vibrating;
    }

    @Override
    public CapsidRenderState createRenderState() {
        return new CapsidRenderState();
    }

    @Override
    public void extractRenderState(T entity, CapsidRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, renderState, partialTick, cameraPos, crumblingOverlay);
        ItemStack stack = entity.getItem(0);
        renderState.stack = stack;
        if (stack.isEmpty()) {
            renderState.modelCount = 0;
            return;
        }
        int i = Item.getId(stack.getItem()) + stack.getDamageValue();
        renderState.randomSeed = (long) i;
        renderState.floatProgress = entity.prevFloatUpProgress + (entity.floatUpProgress - entity.prevFloatUpProgress) * partialTick;
        renderState.yaw = entity.prevYawSwitchProgress + (entity.yawSwitchProgress - entity.prevYawSwitchProgress) * partialTick;
        renderState.displayAngleRad = Maths.rad(entity.getBlockAngle() + renderState.yaw);
        renderState.modelCount = this.getModelCount(stack);
        renderState.vibrating = entity.vibratingThisTick && entity.getLevel() != null;
    }

    @Override
    public void submit(CapsidRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        ItemStack stack = state.stack;
        if (stack.isEmpty()) {
            return;
        }
        Random random = new Random(state.randomSeed);
        ClientLevel level = Minecraft.getInstance().level instanceof ClientLevel cl ? cl : null;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F + state.floatProgress, 0.5F);
        poseStack.mulPose(new Quaternionf().rotateY(state.displayAngleRad));
        poseStack.pushPose();
        poseStack.translate(0.0F, -0.1F, 0.0F);
        if (state.vibrating && Minecraft.getInstance().level != null) {
            float vibrate = 0.05F;
            net.minecraft.util.RandomSource r = Minecraft.getInstance().level.getRandom();
            poseStack.translate((r.nextFloat() - 0.5F) * vibrate, (r.nextFloat() - 0.5F) * vibrate, (r.nextFloat() - 0.5F) * vibrate);
        }
        poseStack.scale(1.3F, 1.3F, 1.3F);

        ItemStackRenderState probe = new ItemStackRenderState();
        this.itemModelResolver.updateForTopItem(probe, stack, ItemDisplayContext.GROUND, level, null, (int) state.randomSeed);
        AABB bounds = probe.getModelBoundingBox();
        double minExtent = Math.min(bounds.getXsize(), Math.min(bounds.getYsize(), bounds.getZsize()));
        boolean flag = minExtent > 0.0625D;

        if (!flag) {
            float f7 = -0.0F * (float) (state.modelCount - 1) * 0.5F;
            float f8 = -0.0F * (float) (state.modelCount - 1) * 0.5F;
            float f9 = -0.09375F * (float) (state.modelCount - 1) * 0.5F;
            poseStack.translate((double) f7, (double) f8, (double) f9);
        }

        for (int k = 0; k < state.modelCount; ++k) {
            poseStack.pushPose();
            if (k > 0) {
                if (flag) {
                    float f11 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f13 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f10 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    poseStack.translate(f11, f13, f10);
                } else {
                    float f12 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float f14 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    poseStack.translate(f12, f14, 0.0D);
                }
            }

            ItemStackRenderState itemState = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(itemState, stack, ItemDisplayContext.GROUND, level, null, (int) state.randomSeed);
            itemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            poseStack.popPose();
            if (!flag) {
                poseStack.translate(0.0, 0.0, 0.09375F);
            }
        }

        poseStack.popPose();
        poseStack.popPose();
    }
}
