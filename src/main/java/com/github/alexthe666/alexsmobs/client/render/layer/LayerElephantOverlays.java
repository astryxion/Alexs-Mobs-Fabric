package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelElephant;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.RenderElephant;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

public class LayerElephantOverlays extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityElephant>> {

    private static final Identifier[] ELEPHANT_DECOR_TEXTURES = new Identifier[]{Identifier.parse("alexsmobs:textures/entity/elephant/decor/white.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/orange.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/magenta.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/light_blue.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/yellow.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/lime.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/pink.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/gray.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/light_gray.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/cyan.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/purple.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/blue.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/brown.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/green.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/red.png"), Identifier.parse("alexsmobs:textures/entity/elephant/decor/black.png")};
    private static final Identifier TRADER_TEXTURE = Identifier.parse("alexsmobs:textures/entity/elephant/decor/trader.png");

    private static final Identifier TEXTURE_CHEST = Identifier.parse("alexsmobs:textures/entity/elephant/elephant_chest.png");
    private final ModelElephant model = new ModelElephant(0.5F);

    public LayerElephantOverlays(RenderElephant renderElephant) {
        super(renderElephant);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int packedLightIn, LivingEntityRenderState state, float netHeadYaw, float headPitch) {
        EntityElephant elephant = AlexsMobsClientKeys.getLiving(state) instanceof EntityElephant e ? e : null;
        if (elephant == null) {
            return;
        }
        float limbSwing = state.walkAnimationPos;
        float limbSwingAmount = Math.min(1.0F, state.walkAnimationSpeed);
        float ageInTicks = state.ageInTicks;
        if (elephant.isChested()) {
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            this.getParentModel().setupAnim(state);
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(TEXTURE_CHEST), (pose, consumer) ->
                    this.getParentModel().renderCitadelToBuffer(matrixStackIn, consumer, packedLightIn, overlay, -1));
        }
        DyeColor lvt_11_1_ = elephant.getColor();
        if (lvt_11_1_ != null || elephant.isTrader()) {
            Identifier lvt_12_3_;
            if (!elephant.isTrader()) {
                lvt_12_3_ = ELEPHANT_DECOR_TEXTURES[lvt_11_1_.getId()];
            } else {
                lvt_12_3_ = TRADER_TEXTURE;
            }

            ((ModelElephant) this.getParentModel().citadel()).copyPropertiesTo(this.model);
            this.model.setupAnim(elephant, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(lvt_12_3_), (pose, consumer) ->
                    this.model.renderToBuffer(matrixStackIn, consumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1));
        }
    }
}
