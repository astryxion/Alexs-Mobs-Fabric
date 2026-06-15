package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.model.NoAnimHumanoidModel;
import com.github.alexthe666.alexsmobs.client.render.AMArmorLayerUtil;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.EntityArmorModelCache;
import com.github.alexthe666.alexsmobs.client.render.RenderKangaroo;
import com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.joml.Quaternionf;

public class LayerKangarooArmor extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityKangaroo>> {

    private final EntityArmorModelCache armorModelCache;
    private final EquipmentAssetManager equipmentAssets;
    private final RenderKangaroo renderer;
    private final CustomArmorRenderProperties armorRenderProperties = new CustomArmorRenderProperties();

    public LayerKangarooArmor(RenderKangaroo render, EntityRendererProvider.Context context) {
        super(render);
        this.renderer = render;
        this.armorModelCache = new EntityArmorModelCache(context.getModelSet());
        this.equipmentAssets = context.getEquipmentAssets();
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityKangaroo roo = AlexsMobsClientKeys.getLiving(state) instanceof EntityKangaroo k ? k : null;
        if (roo == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        ArmorModelSet<NoAnimHumanoidModel> slotModels = this.armorModelCache.get(roo);
        matrixStackIn.pushPose();
        if (roo.isRoger()) {
            ItemStack haloStack = new ItemStack(AMItemRegistry.HALO);
            matrixStackIn.pushPose();
            translateToHead(matrixStackIn);
            float f = 0.1F * (float) Math.sin((roo.tickCount + partialTicks) * 0.1F) + (roo.isBaby() ? 0.2F : 0F);
            matrixStackIn.translate(0.0F, -0.75F - f, -0.2F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(90F));
            matrixStackIn.scale(1.3F, 1.3F, 1.3F);
            ItemModelResolver resolver = Minecraft.getInstance().getItemModelResolver();
            ItemStackRenderState rs = new ItemStackRenderState();
            resolver.updateForTopItem(rs, haloStack, ItemDisplayContext.GROUND, roo.level() instanceof ClientLevel cl ? cl : null, null, 0);
            rs.submit(matrixStackIn, collector, packedLightIn, OverlayTexture.NO_OVERLAY, 0);
            matrixStackIn.popPose();
        }
        if (!roo.isBaby()) {
            {
                matrixStackIn.pushPose();
                ItemStack itemstack = roo.getItemBySlot(EquipmentSlot.HEAD);
                if (!AMArmorLayerUtil.getDraws(this.equipmentAssets, itemstack, EquipmentClientInfo.LayerType.HUMANOID, EquipmentSlot.HEAD, roo).isEmpty()) {
                    HumanoidModel<?> armorModel = resolveHeadArmorModel(itemstack, slotModels);
                    translateToHead(matrixStackIn);
                    matrixStackIn.translate(0, 0.015F, -0.05F);
                    if (itemstack.is(AMItemRegistry.FEDORA)) {
                        matrixStackIn.translate(0, 0.05F, 0F);
                    }
                    matrixStackIn.scale(0.7F, 0.7F, 0.7F);
                    renderHelmetPose(armorModel);
                    boolean glint = itemstack.hasFoil();
                    for (AMArmorLayerUtil.ArmorDraw draw : AMArmorLayerUtil.getDraws(this.equipmentAssets, itemstack, EquipmentClientInfo.LayerType.HUMANOID, EquipmentSlot.HEAD, roo)) {
                        AMArmorLayerUtil.submitCustomHeadArmor(matrixStackIn, collector, state, packedLightIn, glint, armorModel, draw.color(), draw.texture());
                    }
                } else if (!itemstack.isEmpty()) {
                    translateToHead(matrixStackIn);
                    matrixStackIn.translate(0, -0.2, -0.1F);
                    matrixStackIn.mulPose((new Quaternionf()).rotateX(Mth.PI));
                    matrixStackIn.mulPose((new Quaternionf()).rotateY(Mth.PI));
                    ItemModelResolver resolver = Minecraft.getInstance().getItemModelResolver();
                    ItemStackRenderState rs = new ItemStackRenderState();
                    resolver.updateForTopItem(rs, itemstack, ItemDisplayContext.FIXED, roo.level() instanceof ClientLevel cl ? cl : null, null, 0);
                    rs.submit(matrixStackIn, collector, packedLightIn, OverlayTexture.NO_OVERLAY, 0);
                }
                matrixStackIn.popPose();
            }
            {
                matrixStackIn.pushPose();
                ItemStack itemstack = roo.getItemBySlot(EquipmentSlot.CHEST);
                Equippable chestEq = itemstack.get(DataComponents.EQUIPPABLE);
                if (chestEq != null && chestEq.slot() == EquipmentSlot.CHEST && !AMArmorLayerUtil.getDraws(this.equipmentAssets, itemstack, EquipmentClientInfo.LayerType.HUMANOID, EquipmentSlot.CHEST, roo).isEmpty()) {
                    HumanoidModel<?> armorModel = resolveChestArmorModel(itemstack, slotModels);
                    translateToChest(matrixStackIn);
                    matrixStackIn.translate(0, 0.25F, 0F);
                    renderChestplatePose(roo, armorModel);
                    boolean glint = itemstack.hasFoil();
                    for (AMArmorLayerUtil.ArmorDraw draw : AMArmorLayerUtil.getDraws(this.equipmentAssets, itemstack, EquipmentClientInfo.LayerType.HUMANOID, EquipmentSlot.CHEST, roo)) {
                        AMArmorLayerUtil.submitCustomChestplateArmor(matrixStackIn, collector, state, packedLightIn, glint, armorModel, draw.color(), draw.texture());
                    }
                }
                matrixStackIn.popPose();
            }
        }
        matrixStackIn.popPose();
    }

    private HumanoidModel<?> resolveHeadArmorModel(ItemStack itemstack, ArmorModelSet<NoAnimHumanoidModel> slotModels) {
        NoAnimHumanoidModel slotModel = slotModels.get(EquipmentSlot.HEAD);
        Model model = this.armorRenderProperties.getGenericArmorModel(itemstack, EquipmentClientInfo.LayerType.HUMANOID, slotModel);
        return model instanceof HumanoidModel<?> hm ? hm : slotModel;
    }

    private HumanoidModel<?> resolveChestArmorModel(ItemStack itemstack, ArmorModelSet<NoAnimHumanoidModel> slotModels) {
        NoAnimHumanoidModel slotModel = slotModels.get(EquipmentSlot.CHEST);
        Model model = this.armorRenderProperties.getGenericArmorModel(itemstack, EquipmentClientInfo.LayerType.HUMANOID, slotModel);
        return model instanceof HumanoidModel<?> hm ? hm : slotModel;
    }

    private void translateToHead(PoseStack matrixStackIn) {
        translateToChest(matrixStackIn);
        kangarooModel().neck.translateAndRotate(matrixStackIn);
        kangarooModel().head.translateAndRotate(matrixStackIn);
    }

    private void translateToChest(PoseStack matrixStackIn) {
        kangarooModel().root.translateAndRotate(matrixStackIn);
        kangarooModel().body.translateAndRotate(matrixStackIn);
        kangarooModel().chest.translateAndRotate(matrixStackIn);
    }

    private ModelKangaroo kangarooModel() {
        return (ModelKangaroo) this.getParentModel().citadel();
    }

    private void renderHelmetPose(HumanoidModel<?> modelIn) {
        this.renderer.getModel().copyPropertiesTo(modelIn);
        modelIn.head.xRot = 0F;
        modelIn.head.yRot = 0F;
        modelIn.head.zRot = 0F;
        modelIn.hat.xRot = 0F;
        modelIn.hat.yRot = 0F;
        modelIn.hat.zRot = 0F;
        modelIn.head.x = 0F;
        modelIn.head.y = 0F;
        modelIn.head.z = 0F;
        modelIn.hat.x = 0F;
        modelIn.hat.y = 0F;
        modelIn.hat.z = 0F;
    }

    private void renderChestplatePose(EntityKangaroo entity, HumanoidModel<?> modelIn) {
        this.renderer.getModel().copyPropertiesTo(modelIn);
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        modelIn.body.xRot = 90 * 0.017453292F;
        modelIn.body.yRot = 0;
        modelIn.body.zRot = 0;
        modelIn.body.x = 0;
        modelIn.body.y = 0.25F;
        modelIn.body.z = -7.6F;
        modelIn.rightArm.x = kangarooModel().arm_right.rotationPointX;
        modelIn.rightArm.y = kangarooModel().arm_right.rotationPointY;
        modelIn.rightArm.z = kangarooModel().arm_right.rotationPointZ;
        modelIn.rightArm.xRot = kangarooModel().arm_right.rotateAngleX;
        modelIn.rightArm.yRot = kangarooModel().arm_right.rotateAngleY;
        modelIn.rightArm.zRot = kangarooModel().arm_right.rotateAngleZ;
        modelIn.leftArm.x = kangarooModel().arm_left.rotationPointX;
        modelIn.leftArm.y = kangarooModel().arm_left.rotationPointY;
        modelIn.leftArm.z = kangarooModel().arm_left.rotationPointZ;
        modelIn.leftArm.xRot = kangarooModel().arm_left.rotateAngleX;
        modelIn.leftArm.yRot = kangarooModel().arm_left.rotateAngleY;
        modelIn.leftArm.zRot = kangarooModel().arm_left.rotateAngleZ;
        modelIn.leftArm.y = kangarooModel().arm_left.rotationPointY - 4 + (sitProgress * 0.25F);
        modelIn.rightArm.y = kangarooModel().arm_right.rotationPointY - 4 + (sitProgress * 0.25F);
        modelIn.leftArm.z = kangarooModel().arm_left.rotationPointZ - 0.5F;
        modelIn.rightArm.z = kangarooModel().arm_right.rotationPointZ - 0.5F;
    }
}
