package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.item.ItemRainbowJelly;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class LayerRainbow<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {

    public LayerRainbow(RenderLayerParent<S, M> parent) {
        super(parent);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, S renderState, float netHeadYaw, float headPitch) {
        LivingEntity entity = AlexsMobsClientKeys.getLiving(renderState);
        if (entity == null) {
            return;
        }
        int i = RainbowUtil.getRainbowType(entity);
        if (i > 0) {
            ItemRainbowJelly.RainbowType rainbowType = ItemRainbowJelly.RainbowType.values()[Mth.clamp(i - 1, 0, ItemRainbowJelly.RainbowType.values().length - 1)];
            if (this.getParentModel() instanceof CitadelEntityModelBridge<?> citadel) {
                citadel.setupAnim(renderState);
                bufferIn.submitCustomGeometry(matrixStackIn, getRenderType(rainbowType), (pose, consumer) -> {
                    PoseStack stack = new PoseStack();
                    stack.pushPose();
                    stack.last().set(pose);
                    citadel.renderCitadelToBuffer(stack, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
                    stack.popPose();
                });
            } else {
                bufferIn.order(1).submitModel(
                        this.getParentModel(),
                        renderState,
                        matrixStackIn,
                        getRenderType(rainbowType),
                        packedLightIn,
                        OverlayTexture.NO_OVERLAY,
                        0);
            }
        }
    }

    private static net.minecraft.client.renderer.rendertype.RenderType getRenderType(ItemRainbowJelly.RainbowType rainbowType) {
        return switch (rainbowType) {
            case TRANS -> AMRenderTypes.TRANS_GLINT;
            case NONBI -> AMRenderTypes.NONBI_GLINT;
            case BI -> AMRenderTypes.BI_GLINT;
            case ACE -> AMRenderTypes.ACE_GLINT;
            case WEEZER -> AMRenderTypes.WEEZER_GLINT;
            case BRAZIL -> AMRenderTypes.BRAZIL_GLINT;
            default -> AMRenderTypes.RAINBOW_GLINT;
        };
    }
}
