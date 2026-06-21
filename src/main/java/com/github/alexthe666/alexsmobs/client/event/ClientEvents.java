package com.github.alexthe666.alexsmobs.client.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelWanderingVillagerRider;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.model.ModelRockyChestplateRolling;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderVineLasso;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectClinging;
import com.github.alexthe666.alexsmobs.effect.EffectPowerDown;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityBlueJay;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.entity.IFalconry;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.client.event.EventGetFluidRenderType;
import com.github.alexthe666.citadel.client.event.EventGetOutlineColor;
import com.github.alexthe666.citadel.client.event.EventGetStarBrightness;
import com.github.alexthe666.citadel.client.event.EventPosePlayerHand;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.EntityHitResult;
public class ClientEvents {

    private static final ClientEvents INSTANCE = new ClientEvents();

    public static ClientEvents getInstance() {
        return INSTANCE;
    }

    public void registerFabricHandlers() {
        EventGetOutlineColor.EVENT.register(this::onOutlineEntityColor);
        EventGetStarBrightness.EVENT.register(this::onGetStarBrightness);
        EventPosePlayerHand.EVENT.register(this::onPoseHand);
        EventGetFluidRenderType.EVENT.register(this::onGetFluidRenderType);
        ClientTickEvents.START_CLIENT_TICK.register(client -> this.clientTick());
    }


    private static final Identifier ROCKY_CHESTPLATE_TEXTURE = Identifier.parse("alexsmobs:textures/armor/rocky_chestplate.png");
    private static final ModelRockyChestplateRolling ROCKY_CHESTPLATE_MODEL = new ModelRockyChestplateRolling();

    private boolean previousLavaVision = false;
    public long lastStaticTick = -1;
    public static int renderStaticScreenFor = 0;

        public void onOutlineEntityColor(EventGetOutlineColor event) {
        if(event.getEntityIn() instanceof Enemy && AlexsMobs.PROXY.getSingingBlueJayId() != -1){
            Entity entity = event.getEntityIn().level().getEntity(AlexsMobs.PROXY.getSingingBlueJayId());
            if(entity instanceof EntityBlueJay jay && jay.isAlive() && jay.isMakingMonstersBlue()){
                event.setColor(0X4B95FE);
                event.setResult(TriState.TRUE);
            }
        }
        if (event.getEntityIn() instanceof ItemEntity && ((ItemEntity) event.getEntityIn()).getItem().is(AMTagRegistry.VOID_WORM_DROPS)){
            int fromColor = 0;
            int toColor = 0X21E5FF;
            float startR = (float) (fromColor >> 16 & 255) / 255.0F;
            float startG = (float) (fromColor >> 8 & 255) / 255.0F;
            float startB = (float) (fromColor & 255) / 255.0F;
            float endR = (float) (toColor >> 16 & 255) / 255.0F;
            float endG = (float) (toColor >> 8 & 255) / 255.0F;
            float endB = (float) (toColor & 255) / 255.0F;
            float f = (float) (Math.cos(0.4F * (event.getEntityIn().tickCount + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true))) + 1.0F) * 0.5F;
            float r = (endR - startR) * f + startR;
            float g = (endG - startG) * f + startG;
            float b = (endB - startB) * f + startB;
            int j = ((((int) (r * 255)) & 0xFF) << 16) |
                    ((((int) (g * 255)) & 0xFF) << 8) |
                    ((((int) (b * 255)) & 0xFF) << 0);
            event.setColor(j);
            event.setResult(TriState.TRUE);
        }
    }

        public void onGetStarBrightness(EventGetStarBrightness event) {
        if (Minecraft.getInstance().player.hasEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.POWER_DOWN))) {
            if (Minecraft.getInstance().player.getEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.POWER_DOWN)) != null) {
                MobEffectInstance instance = Minecraft.getInstance().player.getEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.POWER_DOWN));
                EffectPowerDown powerDown = (EffectPowerDown) instance.getEffect().value();
                int duration = instance.getDuration();
                float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
                float f = (Math.min(powerDown.getActiveTime(), duration) + partialTicks) * 0.1F;
                event.setBrightness(0);
                event.setResult(TriState.TRUE);
            }

        }
    }


        public void onPoseHand(EventPosePlayerHand event) {
        LivingEntity player = (LivingEntity) event.getEntityIn();
        float f = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        boolean leftHand = false;
        boolean usingLasso = player.isUsingItem() && player.getUseItem().is(AMItemRegistry.VINE_LASSO);
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.VINE_LASSO) {
            leftHand = player.getMainArm() == HumanoidArm.LEFT;
        } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.VINE_LASSO) {
            leftHand = player.getMainArm() != HumanoidArm.LEFT;
        }
        if (leftHand && event.isLeftHand() && usingLasso) {
            //float swing = (float) Math.sin(player.tickCount + f) * 0.5F;
            event.setResult(TriState.TRUE);
            event.getModel().leftArm.xRot = Maths.rad(-120F) + Mth.sin(player.tickCount + f) * 0.5F;
            event.getModel().leftArm.yRot = Maths.rad(-20F) + Mth.cos(player.tickCount + f) * 0.5F;
        }
        if (!leftHand && !event.isLeftHand() && usingLasso) {
            event.setResult(TriState.TRUE);
            event.getModel().rightArm.xRot = Maths.rad(-120F) + Mth.sin(player.tickCount + f) * 0.5F;
            event.getModel().rightArm.yRot = Maths.rad(20F) - Mth.cos(player.tickCount + f) * 0.5F;
        }
    }


    public <E extends Entity> void renderEntity(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, SubmitNodeCollector collector) {
        ClientProxy.submitEntityInWorld(entityIn, x, y, z, yaw, partialTicks, matrixStack, collector);
    }

    public void renderRockyRolling(LivingEntity entity, LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        poseStack.pushPose();
        try {
            float limbSwing = state.walkAnimationPos - state.walkAnimationSpeed * (1.0F - partialTick);
            float limbSwingAmount = state.walkAnimationSpeed;
            float yRot = state.bodyRot;
            float roll = state.walkAnimationPos;
            int packedLight = state.lightCoords;
            boolean foil = entity.getItemBySlot(EquipmentSlot.CHEST).hasFoil();
            poseStack.translate(0.0D, entity.getBbHeight() - entity.getBbHeight() * 0.5F, 0.0D);
            poseStack.mulPose(Axis.YN.rotationDegrees(180F + yRot));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(100F * roll));
            ROCKY_CHESTPLATE_MODEL.setupAnim(entity, limbSwing, limbSwingAmount, entity.tickCount + partialTick, 0, 0);
            collector.submitCustomGeometry(poseStack, RenderTypes.armorCutoutNoCull(ROCKY_CHESTPLATE_TEXTURE), (pose, vertexConsumer) -> {
                PoseStack local = new PoseStack();
                local.pushPose();
                local.last().set(pose);
                ROCKY_CHESTPLATE_MODEL.renderToBuffer(local, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
                local.popPose();
            });
            if (foil) {
                collector.submitCustomGeometry(poseStack, RenderTypes.armorEntityGlint(), (pose, vertexConsumer) -> {
                    PoseStack local = new PoseStack();
                    local.pushPose();
                    local.last().set(pose);
                    ROCKY_CHESTPLATE_MODEL.renderToBuffer(local, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
                    local.popPose();
                });
            }
        } finally {
            poseStack.popPose();
        }
    }

    public void clientTick() {
        AMItemstackRenderer.incrementTick();
        onRenderWorldLastEvent();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            for (EntityCrimsonMosquito mosquito : mc.level.getEntitiesOfClass(EntityCrimsonMosquito.class, mc.player.getBoundingBox().inflate(32.0D), m -> m.isAlive() && m.isLatched())) {
                mosquito.ensureLatchTick();
            }
        }
        if (renderStaticScreenFor > 0 && Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            if (Minecraft.getInstance().player.isAlive() && lastStaticTick != Minecraft.getInstance().level.getGameTime()) {
                renderStaticScreenFor--;
            }
            lastStaticTick = Minecraft.getInstance().level.getGameTime();
        }
    }

    public void onRenderWorldLastEvent() {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        if (!AMConfig.shadersCompat) {
            boolean hasLavaVision = Minecraft.getInstance().player.hasEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.LAVA_VISION));
            if (hasLavaVision != previousLavaVision) {
                updateAllChunks();
            }
            previousLavaVision = hasLavaVision;
        }
        if (Minecraft.getInstance().getCameraEntity() instanceof EntityBaldEagle eagle) {
            LocalPlayer playerEntity = Minecraft.getInstance().player;
            if (eagle.shouldHoodedReturn() || eagle.isRemoved()) {
                Minecraft.getInstance().setCameraEntity(playerEntity);
                Minecraft.getInstance().options.setCameraType(CameraType.values()[AlexsMobs.PROXY.getPreviousPOV()]);
            } else {
                float rotX = Mth.wrapDegrees(playerEntity.getYRot() + playerEntity.yHeadRot);
                float rotY = playerEntity.getXRot();
                Entity over = null;
                if (Minecraft.getInstance().hitResult instanceof EntityHitResult entityHitResult) {
                    over = entityHitResult.getEntity();
                } else {
                    Minecraft.getInstance().hitResult = null;
                }
                boolean loadChunks = playerEntity.level().getDefaultClockTime() % 10 == 0;
                eagle.directFromPlayer(rotX, rotY, false, over);
                AlexsMobs.sendMSGToServer(new com.github.alexthe666.alexsmobs.network.MessageUpdateEagleControls(
                        eagle.getId(), rotX, rotY, loadChunks, over == null ? -1 : over.getId()));
            }
        }
    }

    private void updateAllChunks() {
        if (Minecraft.getInstance().levelRenderer != null) {
            Minecraft.getInstance().levelRenderer.resetLevelRenderData();
        }
    }

    //     // public void onRenderNameplate(RenderNameTagEvent event) {
    //     if (Minecraft.getInstance().getCameraEntity() instanceof EntityBaldEagle && event.getEntity() == Minecraft.getInstance().player) {
    //         if (Minecraft.getInstance().hasSingleplayerServer()) {
    //             event.setResult(net.neoforged.neoforge.common.util.TriState.FALSE);
    //         }
    //     }
    // }


        public void onGetFluidRenderType(EventGetFluidRenderType event) {
        if (Minecraft.getInstance().player.hasEffect(net.minecraft.core.Holder.direct(AMEffectRegistry.LAVA_VISION)) && (event.getFluidState().is(Fluids.LAVA) || event.getFluidState().is(Fluids.FLOWING_LAVA))) {
            event.setRenderType(RenderTypes.translucentMovingBlock());
            event.setResult(TriState.TRUE);
        }
    }

}
