package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderUnderminer;
import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LayerUnderminerItem extends RenderLayer<HumanoidRenderState, CitadelEntityModelBridge<EntityUnderminer>> {

    private final RenderUnderminer underminerRenderer;

    public LayerUnderminerItem(RenderUnderminer render) {
        super(render);
        this.underminerRenderer = render;
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, HumanoidRenderState state, float netHeadYaw, float headPitch) {
        LivingEntity living = AlexsMobsClientKeys.getLiving(state);
        if (!(living instanceof EntityUnderminer entitylivingbaseIn) || entitylivingbaseIn.isFullyHidden()) {
            return;
        }
        ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
        if (RenderUnderminer.renderWithPickaxe) {
            itemstack = new ItemStack(AMItemRegistry.GHOSTLY_PICKAXE);
        }
        matrixStackIn.pushPose();
        matrixStackIn.pushPose();
        float f = entitylivingbaseIn.getMainArm() == HumanoidArm.LEFT ? 0.1F : -0.1F;
        float f1 = entitylivingbaseIn.isDwarf() ? 0.5F : 0.45F;
        if (entitylivingbaseIn.isDwarf()) {
            matrixStackIn.translate(0F, 1F, 0F);
            f *= 0.3F;
        } else {
            matrixStackIn.translate(0F, 0.2F, 0);
        }
        this.underminerRenderer.translateUnderminerHand(entitylivingbaseIn, entitylivingbaseIn.getMainArm(), matrixStackIn, state);
        matrixStackIn.translate(f, f1, -0.15F);

        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        int packedLight = itemstack.is(AMItemRegistry.GHOSTLY_PICKAXE) ? 15728880 : packedLightIn;
        renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, matrixStackIn, collector, packedLight);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}
