package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelLighter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LavaVisionFluidRenderer extends FluidRenderer {

    public LavaVisionFluidRenderer(FluidStateModelSet fluidStateModelSet) {
        super(fluidStateModelSet);
    }

    public static LavaVisionFluidRenderer createForClient() {
        return new LavaVisionFluidRenderer(Minecraft.getInstance().getModelManager().getFluidStateModelSet());
    }

    private static boolean isFaceOccludedByState(Direction direction, float height, BlockState state) {
        if (!state.canOcclude()) {
            return false;
        }
        VoxelShape occluder = state.getFaceOcclusionShape(direction.getOpposite());
        if (occluder == Shapes.empty()) {
            return false;
        }
        VoxelShape voxelshape = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double) height, 1.0D);
        return Shapes.joinIsNotEmpty(voxelshape, occluder, BooleanOp.AND);
    }

    @Override
    public void tesselate(BlockAndTintGetter lightReaderIn, BlockPos posIn, FluidRenderer.Output output, BlockState blockstateIn, FluidState fluidStateIn) {
        VertexConsumer vertexBuilderIn = output.getBuilder(ChunkSectionLayer.TRANSLUCENT);
        if (fluidStateIn.is(FluidTags.LAVA)) {
                FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidStateIn);
                TextureAtlasSprite[] atextureatlassprite = new TextureAtlasSprite[]{
                        fluidModel.stillMaterial().sprite(),
                        fluidModel.flowingMaterial().sprite()
                };
                final int i = -1;
                final float alpha = (float) AMConfig.lavaOpacity;
                final float f = (float)(i >> 16 & 255) / 255.0F;
                final float f1 = (float)(i >> 8 & 255) / 255.0F;
                final float f2 = (float)(i & 255) / 255.0F;
                BlockState blockstate = lightReaderIn.getBlockState(posIn.relative(Direction.DOWN));
                FluidState fluidstate = blockstate.getFluidState();
                BlockState blockstate1 = lightReaderIn.getBlockState(posIn.relative(Direction.UP));
                FluidState fluidstate1 = blockstate1.getFluidState();
                BlockState blockstate2 = lightReaderIn.getBlockState(posIn.relative(Direction.NORTH));
                FluidState fluidstate2 = blockstate2.getFluidState();
                BlockState blockstate3 = lightReaderIn.getBlockState(posIn.relative(Direction.SOUTH));
                FluidState fluidstate3 = blockstate3.getFluidState();
                BlockState blockstate4 = lightReaderIn.getBlockState(posIn.relative(Direction.WEST));
                FluidState fluidstate4 = blockstate4.getFluidState();
                BlockState blockstate5 = lightReaderIn.getBlockState(posIn.relative(Direction.EAST));
                FluidState fluidstate5 = blockstate5.getFluidState();
                boolean flag1 = !isNeighborSameFluidVanilla(fluidStateIn, fluidstate1);
                boolean flag2 = FluidRenderer.shouldRenderFace(fluidStateIn, blockstateIn, Direction.DOWN, fluidstate) && !isFaceOccludedByNeighborVanilla(Direction.DOWN, 0.8888889F, blockstate);
                boolean flag3 = FluidRenderer.shouldRenderFace(fluidStateIn, blockstateIn, Direction.NORTH, fluidstate2);
                boolean flag4 = FluidRenderer.shouldRenderFace(fluidStateIn, blockstateIn, Direction.SOUTH, fluidstate3);
                boolean flag5 = FluidRenderer.shouldRenderFace(fluidStateIn, blockstateIn, Direction.WEST, fluidstate4);
                boolean flag6 = FluidRenderer.shouldRenderFace(fluidStateIn, blockstateIn, Direction.EAST, fluidstate5);
                if (!flag1 && !flag2 && !flag6 && !flag5 && !flag3 && !flag4) {
                    return;
                } else {
                    CardinalLighting cardinalLighting = lightReaderIn.cardinalLighting();
                    float f3 = cardinalLighting.byFace(Direction.DOWN);
                    float f4 = cardinalLighting.up();
                    float f5 = cardinalLighting.byFace(Direction.NORTH);
                    float f6 = cardinalLighting.byFace(Direction.WEST);
                    Fluid fluid = fluidStateIn.getType();
                    float f11 = this.getFluidHeight(lightReaderIn, posIn, fluid);
                    float f7;
                    float f8;
                    float f9;
                    float f10;
                    if (f11 >= 1.0F) {
                        f7 = 1.0F;
                        f8 = 1.0F;
                        f9 = 1.0F;
                        f10 = 1.0F;
                    } else {
                        float f12 = this.getHeight(lightReaderIn, fluid, posIn.north(), blockstate2, fluidstate2);
                        float f13 = this.getHeight(lightReaderIn, fluid, posIn.south(), blockstate3, fluidstate3);
                        float f14 = this.getHeight(lightReaderIn, fluid, posIn.east(), blockstate5, fluidstate5);
                        float f15 = this.getHeight(lightReaderIn, fluid, posIn.west(), blockstate4, fluidstate4);
                        f7 = this.calculateAverageHeight(lightReaderIn, fluid, f11, f12, f14, posIn.relative(Direction.NORTH).relative(Direction.EAST));
                        f8 = this.calculateAverageHeight(lightReaderIn, fluid, f11, f12, f15, posIn.relative(Direction.NORTH).relative(Direction.WEST));
                        f9 = this.calculateAverageHeight(lightReaderIn, fluid, f11, f13, f14, posIn.relative(Direction.SOUTH).relative(Direction.EAST));
                        f10 = this.calculateAverageHeight(lightReaderIn, fluid, f11, f13, f15, posIn.relative(Direction.SOUTH).relative(Direction.WEST));
                    }

                    double d1 = (double)(posIn.getX() & 15);
                    double d2 = (double)(posIn.getY() & 15);
                    double d0 = (double)(posIn.getZ() & 15);
                    float f17 = flag2 ? 0.001F : 0.0F;
                    if (flag1 && !isFaceOccludedByNeighborVanilla(Direction.UP, Math.min(Math.min(f8, f10), Math.min(f9, f7)), blockstate1)) {
                        f8 -= 0.001F;
                        f10 -= 0.001F;
                        f9 -= 0.001F;
                        f7 -= 0.001F;
                        Vec3 vec3 = fluidStateIn.getFlow(lightReaderIn, posIn);
                        float f18;
                        float f19;
                        float f20;
                        float f21;
                        float f22;
                        float f23;
                        float f24;
                        float f25;
                        if (vec3.x == 0.0D && vec3.z == 0.0D) {
                            TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
                            f18 = (float)textureatlassprite1.getU((float)0.0);
                            f22 = (float)textureatlassprite1.getV((float)0.0);
                            f19 = f18;
                            f23 = (float)textureatlassprite1.getV((float)16.0);
                            f20 = (float)textureatlassprite1.getU((float)16.0);
                            f24 = f23;
                            f21 = f20;
                            f25 = f22;
                        } else {
                            TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
                            float f26 = (float)Mth.atan2(vec3.z, vec3.x) - Mth.HALF_PI;
                            float f27 = Mth.sin(f26) * 0.25F;
                            float f28 = Mth.cos(f26) * 0.25F;
                            f18 = textureatlassprite.getU(8.0F + (-f28 - f27) * 16.0F);
                            f22 = textureatlassprite.getV(8.0F + (-f28 + f27) * 16.0F);
                            f19 = textureatlassprite.getU(8.0F + (-f28 + f27) * 16.0F);
                            f23 = textureatlassprite.getV(8.0F + (f28 + f27) * 16.0F);
                            f20 = textureatlassprite.getU(8.0F + (f28 + f27) * 16.0F);
                            f24 = textureatlassprite.getV(8.0F + (f28 - f27) * 16.0F);
                            f21 = textureatlassprite.getU(8.0F + (f28 - f27) * 16.0F);
                            f25 = textureatlassprite.getV(8.0F + (-f28 - f27) * 16.0F);
                        }

                        float f49 = (f18 + f19 + f20 + f21) / 4.0F;
                        float f50 = (f22 + f23 + f24 + f25) / 4.0F;
                        float f51 = (float)atextureatlassprite[0].contents().width() / (atextureatlassprite[0].getU1() - atextureatlassprite[0].getU0());
                        float f52 = (float)atextureatlassprite[0].contents().height() / (atextureatlassprite[0].getV1() - atextureatlassprite[0].getV0());
                        float f53 = 4.0F / Math.max(f52, f51);
                        f18 = Mth.lerp(f53, f18, f49);
                        f19 = Mth.lerp(f53, f19, f49);
                        f20 = Mth.lerp(f53, f20, f49);
                        f21 = Mth.lerp(f53, f21, f49);
                        f22 = Mth.lerp(f53, f22, f50);
                        f23 = Mth.lerp(f53, f23, f50);
                        f24 = Mth.lerp(f53, f24, f50);
                        f25 = Mth.lerp(f53, f25, f50);
                        int j = this.getCombinedAverageLight(lightReaderIn, posIn);
                        float f30 = f4 * f;
                        float f31 = f4 * f1;
                        float f32 = f4 * f2;

                        this.vertexVanilla(vertexBuilderIn, d1 + 0.0D, d2 + (double)f8, d0 + 0.0D, f30, f31, f32, alpha, f18, f22, j);
                        this.vertexVanilla(vertexBuilderIn, d1 + 0.0D, d2 + (double)f10, d0 + 1.0D, f30, f31, f32, alpha, f19, f23, j);
                        this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f9, d0 + 1.0D, f30, f31, f32, alpha, f20, f24, j);
                        this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f7, d0 + 0.0D, f30, f31, f32, alpha, f21, f25, j);
                        if (fluidStateIn.shouldRenderBackwardUpFace(lightReaderIn, posIn.above())) {
                            this.vertexVanilla(vertexBuilderIn, d1 + 0.0D, d2 + (double)f8, d0 + 0.0D, f30, f31, f32, alpha, f18, f22, j);
                            this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f7, d0 + 0.0D, f30, f31, f32, alpha, f21, f25, j);
                            this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f9, d0 + 1.0D, f30, f31, f32, alpha, f20, f24, j);
                            this.vertexVanilla(vertexBuilderIn, d1 + 0.0D, d2 + (double)f10, d0 + 1.0D, f30, f31, f32, alpha, f19, f23, j);
                        }
                    }

                    if (flag2) {
                        float f40 = atextureatlassprite[0].getU0();
                        float f41 = atextureatlassprite[0].getU1();
                        float f42 = atextureatlassprite[0].getV0();
                        float f43 = atextureatlassprite[0].getV1();
                        int l = this.getCombinedAverageLight(lightReaderIn, posIn.below());
                        float f46 = f3 * f;
                        float f47 = f3 * f1;
                        float f48 = f3 * f2;

                        this.vertexVanilla(vertexBuilderIn, d1, d2 + (double)f17, d0 + 1.0D, f46, f47, f48, alpha, f40, f43, l);
                        this.vertexVanilla(vertexBuilderIn, d1, d2 + (double)f17, d0, f46, f47, f48, alpha, f40, f42, l);
                        this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f17, d0, f46, f47, f48, alpha, f41, f42, l);
                        this.vertexVanilla(vertexBuilderIn, d1 + 1.0D, d2 + (double)f17, d0 + 1.0D, f46, f47, f48, alpha, f41, f43, l);
                    }

                    int k = this.getCombinedAverageLight(lightReaderIn, posIn);

                    for(Direction direction : Direction.Plane.HORIZONTAL) {
                        float f44;
                        float f45;
                        double d3;
                        double d4;
                        double d5;
                        double d6;
                        boolean flag8;
                        switch (direction) {
                            case NORTH -> {
                                f44 = f8;
                                f45 = f7;
                                d3 = d1;
                                d5 = d1 + 1.0D;
                                d4 = d0 + (double) 0.001F;
                                d6 = d0 + (double) 0.001F;
                                flag8 = flag3;
                            }
                            case SOUTH -> {
                                f44 = f9;
                                f45 = f10;
                                d3 = d1 + 1.0D;
                                d5 = d1;
                                d4 = d0 + 1.0D - (double) 0.001F;
                                d6 = d0 + 1.0D - (double) 0.001F;
                                flag8 = flag4;
                            }
                            case WEST -> {
                                f44 = f10;
                                f45 = f8;
                                d3 = d1 + (double) 0.001F;
                                d5 = d1 + (double) 0.001F;
                                d4 = d0 + 1.0D;
                                d6 = d0;
                                flag8 = flag5;
                            }
                            default -> {
                                f44 = f7;
                                f45 = f9;
                                d3 = d1 + 1.0D - (double) 0.001F;
                                d5 = d1 + 1.0D - (double) 0.001F;
                                d4 = d0;
                                d6 = d0 + 1.0D;
                                flag8 = flag6;
                            }
                        }

                        if (flag8 && !isFaceOccludedByNeighborVanilla(direction, Math.max(f44, f45), lightReaderIn.getBlockState(posIn.relative(direction)))) {
                            BlockPos blockpos = posIn.relative(direction);
                            TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];

                            float f54 = (float)textureatlassprite2.getU((float)0.0);
                            float f55 = (float)textureatlassprite2.getU((float)8.0);
                            float f33 = textureatlassprite2.getV((1.0F - f44) * 16.0F * 0.5F);
                            float f34 = textureatlassprite2.getV((1.0F - f45) * 16.0F * 0.5F);
                            float f35 = (float)textureatlassprite2.getV((float)8.0);
                            float f36 = direction.getAxis() == Direction.Axis.Z ? f5 : f6;
                            float f37 = f4 * f36 * f;
                            float f38 = f4 * f36 * f1;
                            float f39 = f4 * f36 * f2;

                            this.vertexVanilla(vertexBuilderIn, d3, d2 + (double)f44, d4, f37, f38, f39, alpha, f54, f33, k);
                            this.vertexVanilla(vertexBuilderIn, d5, d2 + (double)f45, d6, f37, f38, f39, alpha, f55, f34, k);
                            this.vertexVanilla(vertexBuilderIn, d5, d2 + (double)f17, d6, f37, f38, f39, alpha, f55, f35, k);
                            this.vertexVanilla(vertexBuilderIn, d3, d2 + (double)f17, d4, f37, f38, f39, alpha, f54, f35, k);
                            if (false) {
                                this.vertexVanilla(vertexBuilderIn, d3, d2 + (double)f17, d4, f37, f38, f39, alpha, f54, f35, k);
                                this.vertexVanilla(vertexBuilderIn, d5, d2 + (double)f17, d6, f37, f38, f39, alpha, f55, f35, k);
                                this.vertexVanilla(vertexBuilderIn, d5, d2 + (double)f45, d6, f37, f38, f39, alpha, f55, f34, k);
                                this.vertexVanilla(vertexBuilderIn, d3, d2 + (double)f44, d4, f37, f38, f39, alpha, f54, f33, k);
                            }
                        }
                    }

                    return;
                }
        } else {
            super.tesselate(lightReaderIn, posIn, output, blockstateIn, fluidStateIn);
        }
    }

    private void vertexVanilla(VertexConsumer vertexBuilderIn, double x, double y, double z, float red, float green, float blue, float alpha, float u, float v, int packedLight) {
        vertexBuilderIn.addVertex((float)x, (float)y, (float)z)
            .setColor(red, green, blue, alpha)
            .setUv(u, v)
            .setLight(packedLight)
            .setNormal(1.0F, 0.0F, 0.0F);
    }

    private int getCombinedAverageLight(BlockAndTintGetter lightReaderIn, BlockPos posIn) {
        int i = new BlockModelLighter().getLightCoords(Blocks.AIR.defaultBlockState(), lightReaderIn, posIn);
        int j = new BlockModelLighter().getLightCoords(Blocks.AIR.defaultBlockState(), lightReaderIn, posIn.above());
        int k = i & 255;
        int l = j & 255;
        int i1 = i >> 16 & 255;
        int j1 = j >> 16 & 255;
        return (Math.max(k, l)) | (Math.max(i1, j1)) << 16;
    }

    private float getFluidHeight(BlockGetter reader, BlockPos pos, Fluid fluidIn) {
        int i = 0;
        float f = 0.0F;

        for (int j = 0; j < 4; ++j) {
            BlockPos blockpos = pos.offset(-(j & 1), 0, -(j >> 1 & 1));
            if (reader.getFluidState(blockpos.above()).getType().isSame(fluidIn)) {
                return 1.0F;
            }

            FluidState fluidstate = reader.getFluidState(blockpos);
            if (fluidstate.getType().isSame(fluidIn)) {
                float f1 = fluidstate.getHeight(reader, blockpos);
                if (f1 >= 0.8F) {
                    f += f1 * 10.0F;
                    i += 10;
                } else {
                    f += f1;
                    ++i;
                }
            } else if (!reader.getBlockState(blockpos).isSolid()) {
                ++i;
            }
        }

        return f / (float) i;
    }

    private static boolean isNeighborSameFluidVanilla(FluidState p_203186_, FluidState p_203187_) {
        return p_203187_.getType().isSame(p_203186_.getType());
    }

    private static boolean isFaceOccludedByNeighborVanilla(Direction direction, float height, BlockState neighborState) {
        return isFaceOccludedByState(direction, height, neighborState);
    }

    private float calculateAverageHeight(BlockAndTintGetter p_203150_, Fluid p_203151_, float p_203152_, float p_203153_, float p_203154_, BlockPos p_203155_) {
        if (!(p_203154_ >= 1.0F) && !(p_203153_ >= 1.0F)) {
            float[] afloat = new float[2];
            if (p_203154_ > 0.0F || p_203153_ > 0.0F) {
                float f = this.getHeight(p_203150_, p_203151_, p_203155_);
                if (f >= 1.0F) {
                    return 1.0F;
                }

                this.addWeightedHeight(afloat, f);
            }

            this.addWeightedHeight(afloat, p_203152_);
            this.addWeightedHeight(afloat, p_203154_);
            this.addWeightedHeight(afloat, p_203153_);
            return afloat[0] / afloat[1];
        } else {
            return 1.0F;
        }
    }

    private void addWeightedHeight(float[] p_203189_, float p_203190_) {
        if (p_203190_ >= 0.8F) {
            p_203189_[0] += p_203190_ * 10.0F;
            p_203189_[1] += 10.0F;
        } else if (p_203190_ >= 0.0F) {
            p_203189_[0] += p_203190_;
            p_203189_[1] += 1.0F;
        }

    }

    private float getHeight(BlockAndTintGetter p_203157_, Fluid p_203158_, BlockPos p_203159_) {
        BlockState blockstate = p_203157_.getBlockState(p_203159_);
        return this.getHeight(p_203157_, p_203158_, p_203159_, blockstate, blockstate.getFluidState());
    }

    private float getHeight(BlockAndTintGetter p_203161_, Fluid p_203162_, BlockPos p_203163_, BlockState p_203164_, FluidState p_203165_) {
        if (p_203162_.isSame(p_203165_.getType())) {
            BlockState blockstate = p_203161_.getBlockState(p_203163_.above());
            return p_203162_.isSame(blockstate.getFluidState().getType()) ? 1.0F : p_203165_.getOwnHeight();
        } else {
            return !p_203164_.isSolid() ? 0.0F : -1.0F;
        }
    }
}

