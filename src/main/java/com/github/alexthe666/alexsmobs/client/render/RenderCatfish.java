package com.github.alexthe666.alexsmobs.client.render;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;

import com.github.alexthe666.alexsmobs.client.model.ModelCatfishLarge;
import com.github.alexthe666.alexsmobs.client.model.ModelCatfishMedium;
import com.github.alexthe666.alexsmobs.client.model.ModelCatfishSmall;
import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

public class RenderCatfish extends MobRenderer<EntityCatfish, LivingEntityRenderState, CitadelEntityModelBridge<EntityCatfish>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/catfish_small.png");
    private static final Identifier TEXTURE_MEDIUM = Identifier.parse("alexsmobs:textures/entity/catfish_medium.png");
    private static final Identifier TEXTURE_LARGE = Identifier.parse("alexsmobs:textures/entity/catfish_large.png");
    private static final Identifier TEXTURE_SPIT = Identifier.parse("alexsmobs:textures/entity/catfish_small_spit.png");
    private static final Identifier TEXTURE_SPIT_MEDIUM = Identifier.parse("alexsmobs:textures/entity/catfish_medium_spit.png");
    private static final Identifier TEXTURE_SPIT_LARGE = Identifier.parse("alexsmobs:textures/entity/catfish_large_spit.png");
    private final CitadelEntityModelBridge<EntityCatfish> bridgeSmall = new CitadelEntityModelBridge<>(new ModelCatfishSmall());
    private final CitadelEntityModelBridge<EntityCatfish> bridgeMedium = new CitadelEntityModelBridge<>(new ModelCatfishMedium());
    private final CitadelEntityModelBridge<EntityCatfish> bridgeLarge = new CitadelEntityModelBridge<>(new ModelCatfishLarge());

    public RenderCatfish(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelCatfishSmall()), 0.5F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public void submit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraState) {
        EntityCatfish entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityCatfish c ? c : null;
        if (entity != null) {
            if (entity.getCatfishSize() == 2) {
                this.model = this.bridgeLarge;
            } else if (entity.getCatfishSize() == 1) {
                this.model = this.bridgeMedium;
            } else {
                this.model = this.bridgeSmall;
            }
        }
        super.submit(state, poseStack, submitNodeCollector, cameraState);
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityCatfish entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityCatfish c ? c : null;
        if (entity == null) {
            return TEXTURE;
        }
        if (entity.getCatfishSize() == 2) {
            return entity.isSpitting() ? TEXTURE_SPIT_LARGE : TEXTURE_LARGE;
        }
        if (entity.getCatfishSize() == 1) {
            return entity.isSpitting() ? TEXTURE_SPIT_MEDIUM : TEXTURE_MEDIUM;
        }
        return entity.isSpitting() ? TEXTURE_SPIT : TEXTURE;
    }
}
