package com.github.alexthe666.alexsmobs.client.render.item;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Fullbright translucent ghost pickaxe model for Fabric 1.20.1 item rendering. */
public class GhostlyPickaxeBakedModel implements BakedModel, FabricBakedModel {

    private final BakedModel originalModel;

    public GhostlyPickaxeBakedModel(BakedModel bakedModel) {
        this.originalModel = bakedModel;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        context.pushTransform(quad -> {
            // Fullbright lightmap on each vertex
            for (int i = 0; i < 4; i++) {
                quad.lightmap(i, 0x00F000F0);
            }
            return true;
        });
        context.bakedModelConsumer().accept(originalModel);
        context.popTransform();
    }

    @Override
    public void emitBlockQuads(net.minecraft.world.level.BlockAndTintGetter blockView, BlockState state, net.minecraft.core.BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        context.bakedModelConsumer().accept(originalModel);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return transformQuads(originalModel.getQuads(state, side, rand));
    }

    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
        return List.of(AMRenderTypes.getGhostPickaxe(TextureAtlas.LOCATION_BLOCKS));
    }

    public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        originalModel.getTransforms().getTransform(cameraTransformType).apply(applyLeftHandTransform, poseStack);
        return this;
    }

    @Override
    public boolean useAmbientOcclusion() { return false; }

    @Override
    public boolean isGui3d() { return originalModel.isGui3d(); }

    @Override
    public boolean usesBlockLight() { return false; }

    @Override
    public boolean isCustomRenderer() { return originalModel.isCustomRenderer(); }

    @Override
    public TextureAtlasSprite getParticleIcon() { return originalModel.getParticleIcon(); }

    @Override
    public ItemTransforms getTransforms() { return originalModel.getTransforms(); }

    @Override
    public ItemOverrides getOverrides() { return originalModel.getOverrides(); }

    private static List<BakedQuad> transformQuads(List<BakedQuad> oldQuads) {
        List<BakedQuad> quads = new ArrayList<>();
        for (BakedQuad quad : oldQuads) {
            quads.add(setFullbright(quad));
        }
        return quads;
    }

    private static BakedQuad setFullbright(BakedQuad quad) {
        int[] vertexData = quad.getVertices().clone();
        int step = vertexData.length / 4;

        vertexData[6] = 0x00F000F0;
        vertexData[6 + step] = 0x00F000F0;
        vertexData[6 + 2 * step] = 0x00F000F0;
        vertexData[6 + 3 * step] = 0x00F000F0;
        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), false);
    }

    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        return List.of(this);
    }
}
