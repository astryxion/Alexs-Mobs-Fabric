package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelLeafcutterAnt;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import com.github.alexthe666.alexsmobs.client.render.OctopusColorRegistry;
import com.github.alexthe666.alexsmobs.client.render.RenderLeafcutterAnt;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class LayerLeafcutterAntLeaf extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityLeafcutterAnt>> {

    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/leafcutter_ant_leaf_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/leafcutter_ant_leaf_1.png");
    private static final Identifier TEXTURE_2 = Identifier.parse("alexsmobs:textures/entity/leafcutter_ant_leaf_2.png");

    public LayerLeafcutterAntLeaf(RenderLayerParent<LivingEntityRenderState, CitadelEntityModelBridge<EntityLeafcutterAnt>> render) {
        super(render);
    }

    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn, LivingEntityRenderState renderState, float netHeadYaw, float headPitch) {
        EntityLeafcutterAnt entitylivingbaseIn = AlexsMobsClientKeys.getLiving(renderState) instanceof EntityLeafcutterAnt a ? a : null;
        if (entitylivingbaseIn == null) {
            return;
        }
        if (!entitylivingbaseIn.hasLeaf() || entitylivingbaseIn.isQueen()) {
            return;
        }
        if (!(this.getParentModel().citadel() instanceof ModelLeafcutterAnt modelLeaf)) {
            return;
        }
        final int leafType = entitylivingbaseIn.getId() % 3;
        final Identifier res = switch (leafType) {
            case 2 -> TEXTURE_2;
            case 1 -> TEXTURE_1;
            default -> TEXTURE_0;
        };
        int leafColor = BiomeColors.getAverageFoliageColor((BlockAndTintGetter) entitylivingbaseIn.level(), entitylivingbaseIn.blockPosition());
        if (entitylivingbaseIn.getHarvestedPos() != null && entitylivingbaseIn.getHarvestedState() != null) {
            leafColor = OctopusColorRegistry.getBlockColor(entitylivingbaseIn.getHarvestedState());
        }
        matrixStackIn.pushPose();
        ((OrderedSubmitNodeCollector) bufferIn).submitCustomGeometry(matrixStackIn, AMRenderTypes.entityCutoutNoCull(res), (pose, ivertexbuilder) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            modelLeaf.renderToBuffer(modelStack, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), -1);
        });
        matrixStackIn.popPose();
    }
}
