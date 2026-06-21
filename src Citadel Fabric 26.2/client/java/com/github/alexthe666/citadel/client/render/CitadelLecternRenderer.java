package com.github.alexthe666.citadel.client.render;

import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CitadelLecternRenderer implements BlockEntityRenderer<CitadelLecternBlockEntity, CitadelLecternRenderer.CitadelLecternRenderState> {
    private final BookModel bookModel;
    public static final Identifier BOOK_PAGE_TEXTURE = Identifier.parse("citadel:textures/entity/lectern_book_pages.png");
    public static final Identifier BOOK_BINDING_TEXTURE = Identifier.parse("citadel:textures/entity/lectern_book_binding.png");
    private static final LecternBooks.BookData EMPTY_BOOK_DATA = new LecternBooks.BookData(0XC58439, 0XF4E9BF);
    private static final BookModel.State BOOK_ANIM_STATE = BookModel.State.forAnimation(0.0F, 0.1F, 0.9F, 1.2F);

    public CitadelLecternRenderer(BlockEntityRendererProvider.Context context) {
        this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public CitadelLecternRenderState createRenderState() {
        return new CitadelLecternRenderState();
    }

    @Override
    public void extractRenderState(
            CitadelLecternBlockEntity blockEntity,
            CitadelLecternRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        BlockState blockstate = blockEntity.getBlockState();
        state.hasBook = blockstate.getValue(LecternBlock.HAS_BOOK);
        if (state.hasBook) {
            LecternBooks.BookData bookData = LecternBooks.BOOKS.getOrDefault(BuiltInRegistries.ITEM.getKey(blockEntity.getBook().getItem()), EMPTY_BOOK_DATA);
            state.pageColor = bookData.getPageColor();
            state.bindingColor = bookData.getBindingColor();
            state.yRotDegrees = blockstate.getValue(LecternBlock.FACING).getClockWise().toYRot();
        }
    }

    @Override
    public void submit(CitadelLecternRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.hasBook) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.0625D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.yRotDegrees));
        poseStack.mulPose(Axis.ZP.rotationDegrees(67.5F));
        poseStack.translate(0.0D, -0.125D, 0.0D);
        submitNodeCollector.submitModel(
                this.bookModel,
                BOOK_ANIM_STATE,
                poseStack,
                RenderTypes.entityCutout(BOOK_PAGE_TEXTURE, false),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.pageColor,
                null);
        submitNodeCollector.submitModel(
                this.bookModel,
                BOOK_ANIM_STATE,
                poseStack,
                RenderTypes.entityCutout(BOOK_BINDING_TEXTURE, false),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.bindingColor,
                null);
        poseStack.popPose();
    }

    public static final class CitadelLecternRenderState extends BlockEntityRenderState {
        boolean hasBook;
        int pageColor;
        int bindingColor;
        float yRotDegrees;
    }
}
