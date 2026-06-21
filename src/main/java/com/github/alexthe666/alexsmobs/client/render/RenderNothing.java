package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * Renders no geometry (legacy empty {@code render}); name tags still follow {@link LivingEntityRenderer} / entity rules.
 */
public class RenderNothing extends LivingEntityRenderer<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> {

    private static final Identifier DUMMY_TEXTURE = Identifier.parse("minecraft:textures/block/white_concrete.png");

    public RenderNothing(EntityRendererProvider.Context context) {
        super(context, new EmptyLivingModel(), 0.0F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return DUMMY_TEXTURE;
    }

    @Override
    protected @Nullable RenderType getRenderType(LivingEntityRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing) {
        return null;
    }

    @Override
    protected boolean shouldShowName(LivingEntity entity, double distanceToCameraSq) {
        return super.shouldShowName(entity, distanceToCameraSq)
            && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }

    private static final class EmptyLivingModel extends EntityModel<LivingEntityRenderState> {
        EmptyLivingModel() {
            super(new ModelPart(Collections.emptyList(), Map.of()), RenderTypes::entityCutout);
        }

        @Override
        public void setupAnim(LivingEntityRenderState state) {
        }
    }
}
