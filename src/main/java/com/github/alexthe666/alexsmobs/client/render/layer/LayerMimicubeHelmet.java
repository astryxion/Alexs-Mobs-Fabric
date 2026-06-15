package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelMimicube;
import com.github.alexthe666.alexsmobs.client.model.NoAnimHumanoidModel;
import com.github.alexthe666.alexsmobs.client.render.AMArmorLayerUtil;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.EntityArmorModelCache;
import com.github.alexthe666.alexsmobs.client.render.RenderMimicube;
import com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class LayerMimicubeHelmet extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityMimicube>> {

    private final EntityArmorModelCache armorModelCache;
    private final EquipmentAssetManager equipmentAssets;
    private final RenderMimicube renderer;
    private final CustomArmorRenderProperties armorRenderProperties = new CustomArmorRenderProperties();

    public LayerMimicubeHelmet(RenderMimicube render, EntityRendererProvider.Context context) {
        super(render);
        this.renderer = render;
        this.armorModelCache = new EntityArmorModelCache(context.getModelSet());
        this.equipmentAssets = context.getEquipmentAssets();
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityMimicube cube = AlexsMobsClientKeys.getLiving(state) instanceof EntityMimicube m ? m : null;
        if (cube == null) {
            return;
        }
        ItemStack itemstack = cube.getItemBySlot(EquipmentSlot.HEAD);
        if (!HumanoidArmorLayer.shouldRender(itemstack, EquipmentSlot.HEAD)) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float helmetSwap = Mth.lerp(partialTicks, cube.prevHelmetSwapProgress, cube.helmetSwapProgress) * 0.2F;
        ArmorModelSet<NoAnimHumanoidModel> slotModels = this.armorModelCache.get(cube);
        HumanoidModel<?> armorModel = resolveHeadArmorModel(itemstack, slotModels);
        boolean customModel = !(armorModel instanceof NoAnimHumanoidModel);
        matrixStackIn.pushPose();
        mimicubeModel().root.translateAndRotate(matrixStackIn);
        mimicubeModel().innerbody.translateAndRotate(matrixStackIn);
        matrixStackIn.translate(0, customModel ? 0.25F : 0.15F, 0F);
        matrixStackIn.scale(1F + 0.3F * (1 - helmetSwap), 1F + 0.3F * (1 - helmetSwap), 1F + 0.3F * (1 - helmetSwap));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(360 * helmetSwap));
        if (customModel) {
            renderMimicubeHelmetPose(armorModel);
        }
        boolean glint = itemstack.hasFoil();
        int clampedLight = helmetSwap > 0 ? (int) (-100 * helmetSwap) : packedLightIn;
        for (AMArmorLayerUtil.ArmorDraw draw : AMArmorLayerUtil.getDraws(this.equipmentAssets, itemstack, EquipmentClientInfo.LayerType.HUMANOID, EquipmentSlot.HEAD, cube)) {
            AMArmorLayerUtil.submitCustomHeadArmor(matrixStackIn, collector, state, clampedLight, glint, armorModel, draw.color(), draw.texture());
        }
        matrixStackIn.popPose();
    }

    private HumanoidModel<?> resolveHeadArmorModel(ItemStack itemstack, ArmorModelSet<NoAnimHumanoidModel> slotModels) {
        NoAnimHumanoidModel slotModel = slotModels.get(EquipmentSlot.HEAD);
        Model model = this.armorRenderProperties.getGenericArmorModel(itemstack, EquipmentClientInfo.LayerType.HUMANOID, slotModel);
        return model instanceof HumanoidModel<?> hm ? hm : slotModel;
    }

    private void renderMimicubeHelmetPose(HumanoidModel<?> modelIn) {
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

    private ModelMimicube mimicubeModel() {
        return (ModelMimicube) this.getParentModel().citadel();
    }
}
