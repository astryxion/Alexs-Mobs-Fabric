package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelTerrapin;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;

public class RenderTerrapin extends MobRenderer<EntityTerrapin, LivingEntityRenderState, CitadelEntityModelBridge<EntityTerrapin>> {

    private static final Identifier[] SHELL_TEXTURES = {
            Identifier.parse("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_0.png"),
            Identifier.parse("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_1.png"),
            Identifier.parse("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_2.png"),
            Identifier.parse("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_3.png"),
            Identifier.parse("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_4.png"),
            Identifier.parse("alexsmobs:textures/entity/terrapin/overlay/terrapin_shell_pattern_5.png")
    };
    private static final Identifier[] SKIN_PATTERN_TEXTURES = {
            Identifier.parse("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_0.png"),
            Identifier.parse("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_1.png"),
            Identifier.parse("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_2.png"),
            Identifier.parse("alexsmobs:textures/entity/terrapin/overlay/terrapin_skin_pattern_3.png")
    };

    public RenderTerrapin(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelTerrapin()), 0.3F);
        this.addLayer(new TurtleOverlayLayer(this, 0));
        this.addLayer(new TurtleOverlayLayer(this, 1));
        this.addLayer(new TurtleOverlayLayer(this, 2));
    }

    private static Identifier baseTerrapinTexture(EntityTerrapin entity) {
        if (entity.isKoopa()) {
            return TerrapinTypes.KOOPA.getTexture();
        }
        return entity.getTurtleType().getTexture();
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public void extractRenderState(EntityTerrapin entity, LivingEntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        this.getModel().setCitadelYoung(entity.isBaby());
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityTerrapin entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityTerrapin t ? t : null;
        if (entity == null) {
            return TerrapinTypes.OVERLAY.getTexture();
        }
        return baseTerrapinTexture(entity);
    }

    @Override
    protected void setupRotations(LivingEntityRenderState state, PoseStack stack, float ageInTicks, float rotationYaw) {
        EntityTerrapin entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityTerrapin t ? t : null;
        if (entity == null) {
            super.setupRotations(state, stack, ageInTicks, rotationYaw);
            return;
        }
        float partialTickTime = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        if (this.isShaking(state)) {
            rotationYaw += (float) (Math.cos((double) entity.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }
        Pose pose = entity.getPose();
        if (pose != Pose.SLEEPING && !entity.isSpinning()) {
            stack.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
        }

        if (entity.deathTime > 0) {
            float f = ((float) entity.deathTime + partialTickTime - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            stack.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees()));
        } else if (entity.isAutoSpinAttack()) {
            stack.mulPose(Axis.XP.rotationDegrees(-90.0F - entity.getXRot()));
            stack.mulPose(Axis.YP.rotationDegrees(((float) entity.tickCount + partialTickTime) * -75.0F));
        } else if (pose == Pose.SLEEPING) {
        } else if (this.isEntityUpsideDown(entity)) {
            stack.translate(0.0D, (double) (entity.getBbHeight() + 0.1F), 0.0D);
            stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    static class TurtleOverlayLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityTerrapin>> {

        private final int layer;

        public TurtleOverlayLayer(RenderTerrapin render, int layer) {
            super(render);
            this.layer = layer;
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector buffer, int packedLightIn, LivingEntityRenderState renderState, float netHeadYaw, float headPitch) {
            EntityTerrapin turtle = AlexsMobsClientKeys.getLiving(renderState) instanceof EntityTerrapin t ? t : null;
            if (turtle == null) {
                return;
            }
            if (turtle.getTurtleType() == TerrapinTypes.OVERLAY && !turtle.isKoopa()) {
                Identifier tex = this.layer == 0 ? baseTerrapinTexture(turtle) : this.layer == 1 ? SHELL_TEXTURES[turtle.getShellType() % SHELL_TEXTURES.length] : SKIN_PATTERN_TEXTURES[turtle.getSkinType() % SKIN_PATTERN_TEXTURES.length];
                int color = this.layer == 0 ? turtle.getTurtleColor() : this.layer == 1 ? turtle.getShellColor() : turtle.getSkinColor();
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;
                int tint = ((int) (r * 255) << 24) | ((int) (g * 255) << 16) | ((int) (b * 255) << 8) | 255;
                int overlay = LivingEntityRenderer.getOverlayCoords(renderState, 0.0F);
                RenderLayer.renderColoredCutoutModel(this.getParentModel(), tex, matrixStackIn, buffer, packedLightIn, renderState, tint, overlay);
            }
        }
    }
}
