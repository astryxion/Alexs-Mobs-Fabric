package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.client.model.ModelUnderminerDwarf;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerUnderminerItem;
import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RenderUnderminer extends MobRenderer<EntityUnderminer, HumanoidRenderState, CitadelEntityModelBridge<EntityUnderminer>> {
    private static final Identifier TEXTURE_DWARF = Identifier.parse("alexsmobs:textures/entity/underminer_dwarf.png");
    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/underminer_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/underminer_1.png");
    public static final List<Identifier> BREAKING_LOCATIONS = IntStream.range(0, 10).mapToObj((destroyStage) -> Identifier.parse("alexsmobs:textures/block/ghostly_pickaxe/destroy_stage_" + destroyStage + ".png")).collect(Collectors.toList());
    private static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(AMRenderTypes::getGhostCrumbling).collect(Collectors.toList());
    public static boolean renderWithPickaxe = false;

    private final ModelUnderminerDwarf dwarfModel;
    private final HumanoidModel<HumanoidRenderState> normalModel;
    private final ModelBlockRenderer blockRenderer;

    public RenderUnderminer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelUnderminerDwarf()), 0.4F);
        this.dwarfModel = (ModelUnderminerDwarf) this.model.citadel();
        this.normalModel = new HumanoidModel<>(renderManagerIn.bakeLayer(AMModelLayers.UNDERMINER));
        this.blockRenderer = new ModelBlockRenderer(true, true, Minecraft.getInstance().getBlockColors());
        this.addLayer(new LayerUnderminerItem(this));
    }

    @Nullable
    private EntityUnderminer resolveUnderminer(HumanoidRenderState state) {
        if (AlexsMobsClientKeys.getLiving(state) instanceof EntityUnderminer underminer) {
            return underminer;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }
        Integer entityId = extractRenderStateEntityId(state);
        if (entityId != null && minecraft.level.getEntity(entityId) instanceof EntityUnderminer underminer) {
            return underminer;
        }
        return null;
    }

    @Nullable
    private static Integer extractRenderStateEntityId(HumanoidRenderState state) {
        try {
            Field idField = state.getClass().getField("id");
            Object value = idField.get(state);
            return value instanceof Integer i ? i : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }

    @Override
    public void extractRenderState(EntityUnderminer entity, HumanoidRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state, partialTicks, this.itemModelResolver);
        state.leftArmPose = getUnderminerArmPose(entity, HumanoidArm.LEFT);
        state.rightArmPose = getUnderminerArmPose(entity, HumanoidArm.RIGHT);
    }

    private static HumanoidModel.ArmPose getUnderminerArmPose(EntityUnderminer mob, HumanoidArm arm) {
        ItemStack itemHeldByArm = mob.getItemHeldByArm(arm);
        SwingAnimation anim = itemHeldByArm.get(DataComponents.SWING_ANIMATION);
        if (anim != null && anim.type() == SwingAnimationType.STAB && mob.swinging) {
            return HumanoidModel.ArmPose.SPEAR;
        } else {
            return itemHeldByArm.is(ItemTags.SPEARS) ? HumanoidModel.ArmPose.SPEAR : HumanoidModel.ArmPose.EMPTY;
        }
    }

    public void translateUnderminerHand(EntityUnderminer entity, HumanoidArm arm, PoseStack poseStack, HumanoidRenderState state) {
        if (entity.isDwarf()) {
            this.dwarfModel.translateToHand(arm, poseStack);
        } else {
            this.normalModel.translateToHand(state, arm, poseStack);
        }
    }

    @Override
    protected void scale(HumanoidRenderState state, PoseStack matrixStackIn) {
        LivingEntity living = AlexsMobsClientKeys.getLiving(state);
        if (living instanceof EntityUnderminer) {
            matrixStackIn.scale(0.925F, 0.925F, 0.925F);
        }
    }

    @Override
    public boolean shouldRender(EntityUnderminer livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntityIn.getMiningPos() != null) {
                BlockPos pos = livingEntityIn.getMiningPos();
                Vec3 vector3d = Vec3.atLowerCornerOf(pos);
                Vec3 vector3dCorner = Vec3.atLowerCornerOf(pos).add(1, 1, 1);
                return camera.isVisible(new AABB(vector3d.x, vector3d.y, vector3d.z, vector3dCorner.x, vector3dCorner.y, vector3dCorner.z));
            }
            return false;
        }
    }

    @Override
    protected float getFlipDegrees() {
        return 0.0F;
    }

    @Override
    protected float getShadowRadius(HumanoidRenderState state) {
        EntityUnderminer entity = resolveUnderminer(state);
        if (entity == null) {
            return 0.0F;
        }
        boolean bodyVisible = this.isBodyVisible(state);
        boolean forceTransparent = !bodyVisible && !state.isInvisibleToPlayer;
        RenderType renderType = this.getRenderType(state, bodyVisible, forceTransparent, state.appearsGlowing());
        if (renderType == null || entity.isFullyHidden()) {
            return 0.0F;
        }
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float hide = (entity.prevHidingProgress + (entity.hidingProgress - entity.prevHidingProgress) * partialTick) * 0.1F;
        float alpha = (1F - hide) * 0.6F;
        return 0.9F * alpha;
    }

    @Override
    public void submit(HumanoidRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraState) {
        EntityUnderminer entityIn = resolveUnderminer(state);
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

        poseStack.pushPose();
        if (state.hasPose(Pose.SLEEPING)) {
            net.minecraft.core.Direction bedOrientation = state.bedOrientation;
            if (bedOrientation != null) {
                float headOffset = state.eyeHeight - 0.1F;
                poseStack.translate(-bedOrientation.getStepX() * headOffset, 0.0F, -bedOrientation.getStepZ() * headOffset);
            }
        }

        float scale = state.scale;
        poseStack.scale(scale, scale, scale);
        this.setupRotations(state, poseStack, state.bodyRot, scale);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(state, poseStack);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        boolean flag = this.isBodyVisible(state);
        boolean flag1 = !flag && !state.isInvisibleToPlayer;
        boolean flag2 = state.appearsGlowing();
        RenderType rendertype = this.getRenderType(state, flag, flag1, flag2);
        if (rendertype != null && entityIn != null && !entityIn.isFullyHidden()) {
            int i = LivingEntityRenderer.getOverlayCoords(state, this.getWhiteOverlayProgress(state));

            float hide = (entityIn.prevHidingProgress + (entityIn.hidingProgress - entityIn.prevHidingProgress) * partialTick) * 0.1F;
            float alpha = flag1 ? 0.15F : Mth.clamp((1.0F - hide) * 0.6F, 0.0F, 1.0F);
            int tint = ARGB.multiply(ARGB.color(Mth.clamp((int) (alpha * 255.0F), 0, 255), 255, 255, 255), this.getModelTint(state));
            if (entityIn.isDwarf()) {
                this.syncDwarfModelFields(entityIn, state, partialTick);
                this.model.setupAnim(state);
                int overlay = i;
                int modelTint = tint;
                PoseStack citadelPoseStack = new PoseStack();
                submitNodeCollector.submitCustomGeometry(poseStack, rendertype, (pose, consumer) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.dwarfModel.renderToBuffer(scratch, consumer, state.lightCoords, overlay, modelTint))
                );
            } else {
                this.normalModel.setupAnim(state);
                int overlay = i;
                int modelTint = tint;
                PoseStack citadelPoseStack = new PoseStack();
                submitNodeCollector.submitCustomGeometry(poseStack, rendertype, (pose, consumer) ->
                    AlexAdvancedEntityModel.withCitadelSubmitPose(pose, citadelPoseStack, scratch -> this.normalModel.renderToBuffer(scratch, consumer, state.lightCoords, overlay, modelTint))
                );
            }
        }

        if (this.shouldRenderLayers(state) && !this.layers.isEmpty() && entityIn != null && !entityIn.isSpectator()) {
            if (entityIn.isDwarf()) {
                this.syncDwarfModelFields(entityIn, state, partialTick);
                this.model.setupAnim(state);
            } else {
                this.normalModel.setupAnim(state);
            }
            for (RenderLayer<HumanoidRenderState, CitadelEntityModelBridge<EntityUnderminer>> layerrenderer : this.layers) {
                layerrenderer.submit(poseStack, submitNodeCollector, state.lightCoords, state, state.yRot, state.xRot);
            }
        }

        poseStack.popPose();

        if (entityIn != null) {
            BlockPos miningPos = entityIn.getMiningPos();
            if (miningPos != null) {
                float partialTicks = partialTick;
                poseStack.pushPose();
                double d0 = Mth.lerp(partialTicks, entityIn.xo, entityIn.getX());
                double d1 = Mth.lerp(partialTicks, entityIn.yo, entityIn.getY());
                double d2 = Mth.lerp(partialTicks, entityIn.zo, entityIn.getZ());

                poseStack.translate((double) miningPos.getX() - d0, (double) miningPos.getY() - d1, (double) miningPos.getZ() - d2);
                int progress = Math.round((DESTROY_TYPES.size() - 1) * Mth.clamp(entityIn.getMiningProgress(), 0F, 1.0F));
                BlockState blockState = entityIn.level().getBlockState(miningPos);
                try {
                    java.util.ArrayList<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts = new java.util.ArrayList<>();
                    net.minecraft.util.RandomSource random = net.minecraft.util.RandomSource.create();
                    random.setSeed(blockState.getSeed(miningPos));
                    Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState).collectParts(random, parts);
                    submitNodeCollector.submitBreakingBlockModel(poseStack, java.util.List.copyOf(parts), progress);
                } catch (Throwable ignored) {
                }
                poseStack.popPose();
            }
        }

        // Same tail as {@link net.minecraft.client.renderer.entity.EntityRenderer#submit} — do not call
        // {@link LivingEntityRenderer#submit}: we already drew body + layers; vanilla would draw the Citadel bridge + layers again (white duplicate + doubled pickaxe).
        if (state.leashStates != null) {
            for (EntityRenderState.LeashState leashState : state.leashStates) {
                submitNodeCollector.submitLeash(poseStack, leashState);
            }
        }
        this.submitNameDisplay(state, poseStack, submitNodeCollector, cameraState);
    }

    private void syncDwarfModelFields(EntityUnderminer entityIn, HumanoidRenderState state, float partialTicks) {
        boolean shouldSit = entityIn.isPassenger() && entityIn.getVehicle() != null;
        this.dwarfModel.riding = shouldSit;
        this.dwarfModel.young = entityIn.isBaby();
        this.dwarfModel.attackTime = entityIn.getAttackAnim(partialTicks);
        this.dwarfModel.crouching = entityIn.isCrouching();
        this.dwarfModel.swimAmount = entityIn.getSwimAmount(partialTicks);
        this.dwarfModel.leftArmPose = state.leftArmPose;
        this.dwarfModel.rightArmPose = state.rightArmPose;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(HumanoidRenderState state, boolean normal, boolean invis, boolean outline) {
        EntityUnderminer entity = resolveUnderminer(state);
        if (entity == null) {
            return null;
        }
        Identifier texture = this.getTextureLocation(state);
        return outline ? RenderTypes.outline(texture) : AMRenderTypes.getUnderminer(texture);
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        EntityUnderminer entity = resolveUnderminer(state);
        if (entity == null) {
            return TEXTURE_0;
        }
        return entity.isDwarf() ? TEXTURE_DWARF : entity.getVariant() == 0 ? TEXTURE_0 : TEXTURE_1;
    }
}
