package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelSkelewag;
import com.github.alexthe666.alexsmobs.entity.EntitySkelewag;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public class RenderSkelewag extends MobRenderer<EntitySkelewag, LivingEntityRenderState, CitadelEntityModelBridge<EntitySkelewag>> {
    private static final Identifier TEXTURE_0 = Identifier.parse("alexsmobs:textures/entity/skelewag_0.png");
    private static final Identifier TEXTURE_1 = Identifier.parse("alexsmobs:textures/entity/skelewag_1.png");

    public RenderSkelewag(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelSkelewag()), 0.5F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    protected int getBlockLightLevel(EntitySkelewag entityIn, BlockPos partialTicks) {
        return Math.max(2, super.getBlockLightLevel(entityIn, partialTicks));
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntitySkelewag entity = AlexsMobsClientKeys.getLiving(state) instanceof EntitySkelewag s ? s : null;
        if (entity == null) {
            return TEXTURE_0;
        }
        return entity.getVariant() == 1 ? TEXTURE_1 : TEXTURE_0;
    }
}
