package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderKangaroo;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemModArmor;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class LayerKangarooArmor extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityKangaroo>> {

    private static final Map<String, Identifier> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
    private final HumanoidModel<HumanoidRenderState> defaultBipedModel;
    private final RenderKangaroo renderer;

    public LayerKangarooArmor(RenderKangaroo render, EntityRendererProvider.Context context) {
        super(render);
        defaultBipedModel = new HumanoidModel<>(context.bakeLayer(AMModelLayers.KANGAROO_ARMOR_BASE));
        this.renderer = render;
    }

    /**
     * Legacy-compatible armor texture path (vanilla and mods that follow the {@code *_layer_1[_overlay].png} layout).
     */
    public static Identifier getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        Identifier id = ARMOR_TEXTURE_RES_MAP.get(cacheKey(stack, slot, type));
        if (id == null) {
            id = resolveArmorTexture(stack, slot, type, entity instanceof LivingEntity le ? le : null);
            if (id != null) {
                ARMOR_TEXTURE_RES_MAP.put(cacheKey(stack, slot, type), id);
            }
        }
        return id;
    }

    private static String cacheKey(ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        return stack.getItem().toString() + "|" + slot + "|" + type;
    }

    private static Identifier resolveArmorTexture(ItemStack stack, EquipmentSlot slot, @Nullable String overlaySuffix, @Nullable LivingEntity entity) {
        Equippable eq = stack.get(DataComponents.EQUIPPABLE);
        if (eq == null || eq.slot() != slot) {
            return null;
        }
        if (stack.getItem() instanceof ItemModArmor modArmor) {
            return modArmor.getArmorTexture(stack, entity, slot, overlaySuffix, false);
        }
        Identifier fallback = vanillaStyleArmorTexture(eq, overlaySuffix);
        if (fallback == null) {
            return null;
        }
        return fallback;
    }

    private static Identifier vanillaStyleArmorTexture(Equippable eq, @Nullable String overlaySuffix) {
        Optional<ResourceKey<EquipmentAsset>> assetKey = eq.assetId();
        if (assetKey.isEmpty()) {
            return null;
        }
        Identifier loc = assetKey.get().identifier();
        String ns = loc.getNamespace();
        String matPath = loc.getPath();
        String extra = overlaySuffix == null ? "" : "_" + overlaySuffix;
        return Identifier.fromNamespaceAndPath(ns, "textures/models/armor/" + matPath + "_layer_1" + extra + ".png");
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityKangaroo roo = AlexsMobsClientKeys.getLiving(state) instanceof EntityKangaroo k ? k : null;
        if (roo == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
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
                Equippable headEq = itemstack.get(DataComponents.EQUIPPABLE);
                if (headEq != null && headEq.slot() == EquipmentSlot.HEAD) {
                    Model model = new com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties().getHumanoidArmorModel(itemstack, EquipmentClientInfo.LayerType.HUMANOID, defaultBipedModel);
                    HumanoidModel<?> a = model instanceof HumanoidModel<?> hm ? hm : defaultBipedModel;
                    boolean notAVanillaModel = a != defaultBipedModel;
                    this.setModelSlotVisible(a, EquipmentSlot.HEAD);
                    translateToHead(matrixStackIn);
                    matrixStackIn.translate(0, 0.015F, -0.05F);
                    if (itemstack.is(AMItemRegistry.FEDORA)) {
                        matrixStackIn.translate(0, 0.05F, 0F);
                    }
                    matrixStackIn.scale(0.7F, 0.7F, 0.7F);
                    final boolean flag1 = itemstack.hasFoil();
                    int clampedLight = packedLightIn;
                    if (itemstack.has(DataComponents.DYED_COLOR)) {
                        int i = itemstack.get(DataComponents.DYED_COLOR).rgb();
                        final float cr = (float) (i >> 16 & 255) / 255.0F;
                        final float cg = (float) (i >> 8 & 255) / 255.0F;
                        final float cb = (float) (i & 255) / 255.0F;
                        renderHelmet(roo, matrixStackIn, collector, clampedLight, flag1, a, cr, cg, cb, getArmorResource(roo, itemstack, EquipmentSlot.HEAD, null), notAVanillaModel);
                        renderHelmet(roo, matrixStackIn, collector, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlot.HEAD, "overlay"), notAVanillaModel);
                    } else {
                        renderHelmet(roo, matrixStackIn, collector, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlot.HEAD, null), notAVanillaModel);
                    }
                } else if (!itemstack.isEmpty()) {
                    translateToHead(matrixStackIn);
                    matrixStackIn.translate(0, -0.2, -0.1F);
                    matrixStackIn.mulPose((new Quaternionf()).rotateX(Mth.PI));
                    matrixStackIn.mulPose((new Quaternionf()).rotateY(Mth.PI));
                    matrixStackIn.scale(1.0F, 1.0F, 1.0F);
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
                if (chestEq != null && chestEq.slot() == EquipmentSlot.CHEST) {
                    Model model = new com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties().getHumanoidArmorModel(itemstack, EquipmentClientInfo.LayerType.HUMANOID, defaultBipedModel);
                    HumanoidModel<?> a = model instanceof HumanoidModel<?> hm ? hm : defaultBipedModel;
                    boolean notAVanillaModel = a != defaultBipedModel;
                    this.setModelSlotVisible(a, EquipmentSlot.CHEST);
                    translateToChest(matrixStackIn);
                    matrixStackIn.translate(0, 0.25F, 0F);
                    matrixStackIn.scale(1F, 1F, 1F);
                    boolean flag1 = itemstack.hasFoil();
                    int clampedLight = packedLightIn;
                    if (itemstack.has(DataComponents.DYED_COLOR)) {
                        int i = itemstack.get(DataComponents.DYED_COLOR).rgb();
                        float cr = (float) (i >> 16 & 255) / 255.0F;
                        float cg = (float) (i >> 8 & 255) / 255.0F;
                        float cb = (float) (i & 255) / 255.0F;
                        renderChestplate(roo, matrixStackIn, collector, clampedLight, flag1, a, cr, cg, cb, getArmorResource(roo, itemstack, EquipmentSlot.CHEST, null), notAVanillaModel);
                        renderChestplate(roo, matrixStackIn, collector, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlot.CHEST, "overlay"), notAVanillaModel);
                    } else {
                        renderChestplate(roo, matrixStackIn, collector, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(roo, itemstack, EquipmentSlot.CHEST, null), notAVanillaModel);
                    }
                }
                matrixStackIn.popPose();
            }
        }
        matrixStackIn.popPose();
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
        return (ModelKangaroo) ((CitadelEntityModelBridge<?>) this.renderer.getModel()).citadel();
    }

    private void renderChestplate(EntityKangaroo entity, PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, boolean glintIn, HumanoidModel<?> modelIn, float red, float green, float blue, Identifier armorResource, boolean notAVanillaModel) {
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
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.armorCutoutNoCull(armorResource), (pose, vc) -> {
            PoseStack stack = new PoseStack();
            stack.pushPose();
            stack.last().set(pose);
            modelIn.body.visible = false;
            modelIn.renderToBuffer(stack, vc, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            modelIn.body.visible = true;
            modelIn.rightArm.visible = false;
            modelIn.leftArm.visible = false;
            stack.pushPose();
            stack.scale(1.1F, 1.65F, 1.1F);
            modelIn.renderToBuffer(stack, vc, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            stack.popPose();
            modelIn.rightArm.visible = true;
            modelIn.leftArm.visible = true;
            stack.popPose();
        });
        if (glintIn) {
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.armorEntityGlint(), (pose, vc) -> {
                PoseStack stack = new PoseStack();
                stack.pushPose();
                stack.last().set(pose);
                modelIn.body.visible = false;
                modelIn.renderToBuffer(stack, vc, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
                modelIn.body.visible = true;
                modelIn.rightArm.visible = false;
                modelIn.leftArm.visible = false;
                stack.pushPose();
                stack.scale(1.1F, 1.65F, 1.1F);
                modelIn.renderToBuffer(stack, vc, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
                stack.popPose();
                modelIn.rightArm.visible = true;
                modelIn.leftArm.visible = true;
                stack.popPose();
            });
        }
    }

    private void renderHelmet(EntityKangaroo entity, PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, boolean glintIn, HumanoidModel<?> modelIn, float red, float green, float blue, Identifier armorResource, boolean notAVanillaModel) {
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
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.armorCutoutNoCull(armorResource), (pose, vc) -> {
            PoseStack stack = new PoseStack();
            stack.pushPose();
            stack.last().set(pose);
            modelIn.renderToBuffer(stack, vc, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            stack.popPose();
        });
        if (glintIn) {
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.armorEntityGlint(), (pose, vc) -> {
                PoseStack stack = new PoseStack();
                stack.pushPose();
                stack.last().set(pose);
                modelIn.renderToBuffer(stack, vc, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
                stack.popPose();
            });
        }
    }

    protected void setModelSlotVisible(HumanoidModel<?> p_188359_1_, EquipmentSlot slotIn) {
        this.setModelVisible(p_188359_1_);
        switch (slotIn) {
            case HEAD -> {
                p_188359_1_.head.visible = true;
                p_188359_1_.hat.visible = true;
            }
            case CHEST -> {
                p_188359_1_.body.visible = true;
                p_188359_1_.rightArm.visible = true;
                p_188359_1_.leftArm.visible = true;
            }
            case LEGS -> {
                p_188359_1_.body.visible = true;
                p_188359_1_.rightLeg.visible = true;
                p_188359_1_.leftLeg.visible = true;
            }
            case FEET -> {
                p_188359_1_.rightLeg.visible = true;
                p_188359_1_.leftLeg.visible = true;
            }
            default -> {
            }
        }
    }

    protected void setModelVisible(HumanoidModel<?> model) {
        model.head.visible = false;
        model.hat.visible = false;
        model.body.visible = false;
        model.rightArm.visible = false;
        model.leftArm.visible = false;
        model.rightLeg.visible = false;
        model.leftLeg.visible = false;
    }

    protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> model) {
        Model basicModel = new com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties().getHumanoidArmorModel(itemStack, EquipmentClientInfo.LayerType.HUMANOID, model);
        return basicModel instanceof HumanoidModel<?> hm ? hm : model;
    }
}
