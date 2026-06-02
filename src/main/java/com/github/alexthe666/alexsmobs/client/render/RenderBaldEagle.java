package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

public class RenderBaldEagle extends MobRenderer<EntityBaldEagle, LivingEntityRenderState, CitadelEntityModelBridge<EntityBaldEagle>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/bald_eagle.png");
    private static final Identifier TEXTURE_CAP = Identifier.parse("alexsmobs:textures/entity/bald_eagle_hood.png");

    public RenderBaldEagle(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelBaldEagle()), 0.3F);
        this.addLayer(new CapLayer(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public boolean shouldRender(EntityBaldEagle baldEagle, Frustum p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
        if (baldEagle.isPassenger() && baldEagle.getVehicle() instanceof Player && Minecraft.getInstance().player == baldEagle.getVehicle() && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
            return false;
        }
        return super.shouldRender(baldEagle, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_);
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityBaldEagle eagle = AlexsMobsClientKeys.getLiving(state) instanceof EntityBaldEagle e ? e : null;
        if (eagle == null) {
            return;
        }
        if (eagle.isPassenger() && eagle.getVehicle() != null) {
            if (eagle.getVehicle() instanceof Player) {
                Player mount = (Player) eagle.getVehicle();
                boolean leftHand = false;
                if (mount.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE) {
                    leftHand = mount.getMainArm() == HumanoidArm.LEFT;
                } else if (mount.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE) {
                    leftHand = mount.getMainArm() != HumanoidArm.LEFT;
                }
                EntityRenderer playerRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(mount);
                if (Minecraft.getInstance().player == mount && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                    //handled via event
                } else if (playerRender instanceof LivingEntityRenderer<?, ?, ?> livingRenderer && livingRenderer.getModel() instanceof HumanoidModel<?> humanoidModel) {
                    if (leftHand) {
                        matrixStackIn.translate(-0.3F, -0.7F, 0.5F);
                        humanoidModel.leftArm.translateAndRotate(matrixStackIn);
                        matrixStackIn.translate(-0.2F, 0.5F, -0.18F);
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(40F));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(70F));
                    } else {
                        matrixStackIn.translate(0.3F, -0.7F, 0.5F);
                        humanoidModel.rightArm.translateAndRotate(matrixStackIn);
                        matrixStackIn.translate(0.2F, 0.5F, -0.18F);
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(40F));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-70F));
                    }
                }
            }
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    static class CapLayer extends RenderLayer<LivingEntityRenderState, CitadelEntityModelBridge<EntityBaldEagle>> {

        CapLayer(RenderBaldEagle renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack matrixStackIn, SubmitNodeCollector bufferIn, int packedLightIn,
                LivingEntityRenderState state, float netHeadYaw, float headPitch) {
            EntityBaldEagle entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityBaldEagle e ? e : null;
            if (entity == null || !entity.hasCap()) {
                return;
            }
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            this.getParentModel().setupAnim(state);
            bufferIn.submitCustomGeometry(matrixStackIn, RenderTypes.entityTranslucent(TEXTURE_CAP), (pose, lead) ->
                    this.getParentModel().submitCitadel(pose, lead, packedLightIn, overlay, -1));
        }
    }
}
