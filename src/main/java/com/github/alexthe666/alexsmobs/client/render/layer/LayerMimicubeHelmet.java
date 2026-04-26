package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelMimicube;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderMimicube;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.joml.Quaternionf;

import java.util.Map;
import java.util.Optional;

public class LayerMimicubeHelmet extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityMimicube>> {

    private static final Map<String, Identifier> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
    private static final Identifier FALLBACK_ARMOR = Identifier.parse("minecraft:textures/models/armor/leather_layer_1.png");
    private final HumanoidModel<HumanoidRenderState> defaultBipedModel;
    private final RenderMimicube renderer;

    public LayerMimicubeHelmet(RenderMimicube render, EntityRendererProvider.Context renderManagerIn) {
        super(render);
        this.renderer = render;
        this.defaultBipedModel = new HumanoidModel<>(renderManagerIn.bakeLayer(AMModelLayers.KANGAROO_ARMOR_BASE));
    }

    public static Identifier getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @javax.annotation.Nullable String type) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return FALLBACK_ARMOR;
        }
        Optional<ResourceKey<EquipmentAsset>> assetKey = equippable.assetId();
        if (assetKey.isEmpty()) {
            return FALLBACK_ARMOR;
        }
        Identifier assetLoc = assetKey.get().identifier();
        String domain = assetLoc.getNamespace();
        String texture = assetLoc.getPath();
        String s1 = String.format("%s:textures/models/armor/%s_layer_1%s.png", domain, texture, type == null ? "" : String.format("_%s", type));
        return ARMOR_TEXTURE_RES_MAP.computeIfAbsent(s1, Identifier::parse);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityMimicube cube = AlexsMobsClientKeys.getLiving(state) instanceof EntityMimicube m ? m : null;
        if (cube == null) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        matrixStackIn.pushPose();
        ItemStack itemstack = cube.getItemBySlot(EquipmentSlot.HEAD);
        float helmetSwap = Mth.lerp(partialTicks, cube.prevHelmetSwapProgress, cube.helmetSwapProgress) * 0.2F;
        if (HumanoidArmorLayer.shouldRender(itemstack, EquipmentSlot.HEAD)) {
            HumanoidModel<?> a = defaultBipedModel;
            a = getArmorModelHook(cube, itemstack, EquipmentSlot.HEAD, a);
            boolean notAVanillaModel = a != defaultBipedModel;

            this.setModelSlotVisible(a, EquipmentSlot.HEAD);
            mimicubeModel().root.translateAndRotate(matrixStackIn);
            mimicubeModel().innerbody.translateAndRotate(matrixStackIn);
            matrixStackIn.translate(0, notAVanillaModel ? 0.25F : -0.75F, 0F);
            matrixStackIn.scale(1F + 0.3F * (1 - helmetSwap), 1F + 0.3F * (1 - helmetSwap), 1F + 0.3F * (1 - helmetSwap));
            boolean flag1 = itemstack.hasFoil();
            int clampedLight = helmetSwap > 0 ? (int) (-100 * helmetSwap) : packedLightIn;
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(360 * helmetSwap));
            if (itemstack.has(DataComponents.DYED_COLOR)) {
                int i = itemstack.get(DataComponents.DYED_COLOR).rgb();
                float f = (float) (i >> 16 & 255) / 255.0F;
                float f1 = (float) (i >> 8 & 255) / 255.0F;
                float f2 = (float) (i & 255) / 255.0F;
                renderArmor(cube, matrixStackIn, collector, clampedLight, flag1, a, f, f1, f2, getArmorResource(cube, itemstack, EquipmentSlot.HEAD, null), notAVanillaModel);
                renderArmor(cube, matrixStackIn, collector, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(cube, itemstack, EquipmentSlot.HEAD, "overlay"), notAVanillaModel);
            } else {
                renderArmor(cube, matrixStackIn, collector, clampedLight, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(cube, itemstack, EquipmentSlot.HEAD, null), notAVanillaModel);
            }
        }
        matrixStackIn.popPose();
    }

    private ModelMimicube mimicubeModel() {
        return (ModelMimicube) ((CitadelEntityModelBridge<?>) getParentModel()).citadel();
    }

    private void renderArmor(EntityMimicube entity, PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, boolean glintIn, HumanoidModel<?> modelIn, float red, float green, float blue, Identifier armorResource, boolean notAVanillaModel) {
        if (notAVanillaModel) {
            this.renderer.getModel().copyPropertiesTo(modelIn);
            modelIn.body.y = 0;
            modelIn.head.x = 0.0F;
            modelIn.head.y = 1.0F;
            modelIn.head.z = 0.0F;
            modelIn.hat.y = 0;
            modelIn.head.xRot = mimicubeModel().body.rotateAngleX;
            modelIn.head.yRot = mimicubeModel().body.rotateAngleY;
            modelIn.head.zRot = mimicubeModel().body.rotateAngleZ;
            modelIn.head.x = mimicubeModel().body.rotationPointX;
            modelIn.head.y = mimicubeModel().body.rotationPointY;
            modelIn.head.z = mimicubeModel().body.rotationPointZ;
            modelIn.hat.x = modelIn.head.x;
            modelIn.hat.y = modelIn.head.y;
            modelIn.hat.z = modelIn.head.z;
            modelIn.hat.xRot = modelIn.head.xRot;
            modelIn.hat.yRot = modelIn.head.yRot;
            modelIn.hat.zRot = modelIn.head.zRot;
            modelIn.body.x = modelIn.head.x;
            modelIn.body.y = modelIn.head.y;
            modelIn.body.z = modelIn.head.z;
            modelIn.body.xRot = modelIn.head.xRot;
            modelIn.body.yRot = modelIn.head.yRot;
            modelIn.body.zRot = modelIn.head.zRot;
        }
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
        Model replacement = new com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties()
                .getHumanoidArmorModel(itemStack, EquipmentClientInfo.LayerType.HUMANOID, model);
        return replacement instanceof HumanoidModel<?> hm ? hm : model;
    }
}
