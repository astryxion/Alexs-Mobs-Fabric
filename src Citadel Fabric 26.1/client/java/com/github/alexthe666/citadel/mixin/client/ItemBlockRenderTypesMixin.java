package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventGetFluidRenderType;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.TriState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 1.21.1 hooked {@code ItemBlockRenderTypes#getRenderLayer(FluidState)}; fluids now use {@link FluidStateModelSet} and
 * {@link FluidModel} with {@link ChunkSectionLayer}. This mixin preserves {@link EventGetFluidRenderType} by mapping
 * layer {@literal <->} moving-block {@link RenderType} and rebuilding the model when listeners override the type.
 */
@Mixin(FluidStateModelSet.class)
public class ItemBlockRenderTypesMixin {

    @ModifyReturnValue(method = "get", at = @At("RETURN"), remap = CitadelConstants.REMAPREFS)
    private FluidModel citadel_getFluidRenderLayer(FluidModel model, FluidState fluidState) {
        RenderType base = layerToRenderType(model.layer());
        EventGetFluidRenderType event = new EventGetFluidRenderType(fluidState, base);
        event.post();
        if (event.getResult() != TriState.TRUE) {
            return model;
        }
        ChunkSectionLayer newLayer = renderTypeToLayer(event.getRenderType());
        if (newLayer == model.layer()) {
            return model;
        }
        return new FluidModel(newLayer, model.stillMaterial(), model.flowingMaterial(), model.overlayMaterial(), model.tintSource());
    }

    private static RenderType layerToRenderType(ChunkSectionLayer layer) {
        return switch (layer) {
            case SOLID -> RenderTypes.solidMovingBlock();
            case CUTOUT -> RenderTypes.cutoutMovingBlock();
            case TRANSLUCENT -> RenderTypes.translucentMovingBlock();
        };
    }

    private static ChunkSectionLayer renderTypeToLayer(RenderType rt) {
        for (ChunkSectionLayer layer : ChunkSectionLayer.values()) {
            if (layerToRenderType(layer).pipeline() == rt.pipeline()) {
                return layer;
            }
        }
        return ChunkSectionLayer.TRANSLUCENT;
    }
}
