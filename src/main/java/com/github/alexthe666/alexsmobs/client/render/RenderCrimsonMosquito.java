package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelCrimsonMosquito;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCrimsonMosquitoBlood;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderCrimsonMosquito extends MobRenderer<EntityCrimsonMosquito, LivingEntityRenderState, CitadelEntityModelBridge<EntityCrimsonMosquito>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/crimson_mosquito.png");
    private static final Identifier TEXTURE_SICK = Identifier.parse("alexsmobs:textures/entity/crimson_mosquito_blue.png");
    private static final Identifier TEXTURE_FLY = Identifier.parse("alexsmobs:textures/entity/crimson_mosquito_fly.png");
    private static final Identifier TEXTURE_SICK_FLY = Identifier.parse("alexsmobs:textures/entity/crimson_mosquito_fly_blue.png");

    public RenderCrimsonMosquito(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelCrimsonMosquito()), 0.6F);
        this.addLayer(new LayerCrimsonMosquitoBlood(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityCrimsonMosquito entitylivingbaseIn = AlexsMobsClientKeys.getLiving(state) instanceof EntityCrimsonMosquito m ? m : null;
        if (entitylivingbaseIn == null) {
            super.scale(state, matrixStackIn);
            return;
        }
        float partialTickTime = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        float mosScale = entitylivingbaseIn.prevMosquitoScale + (entitylivingbaseIn.getMosquitoScale() - entitylivingbaseIn.prevMosquitoScale) * partialTickTime;
        matrixStackIn.scale(mosScale * 1.2F, mosScale * 1.2F, mosScale * 1.2F);
    }

    @Override
    protected boolean isShaking(LivingEntityRenderState state) {
        EntityCrimsonMosquito fly = AlexsMobsClientKeys.getLiving(state) instanceof EntityCrimsonMosquito f ? f : null;
        return fly != null && (fly.isSick() || fly.getFleeingEntityId() != -1);
    }

    @Override
    protected void setupRotations(LivingEntityRenderState state, PoseStack matrixStackIn, float ageInTicks, float rotationYaw) {
        EntityCrimsonMosquito entityLiving = AlexsMobsClientKeys.getLiving(state) instanceof EntityCrimsonMosquito m ? m : null;
        if (entityLiving != null && this.isShaking(state)) {
            rotationYaw += (float) (Math.cos((double) entityLiving.tickCount * 7F) * Math.PI * (double) 0.9F);
            float vibrate = 0.05F * entityLiving.getMosquitoScale();
            matrixStackIn.translate((entityLiving.getRandom().nextFloat() - 0.5F) * vibrate, (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate, (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate);
        }
        super.setupRotations(state, matrixStackIn, ageInTicks, rotationYaw);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityCrimsonMosquito entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityCrimsonMosquito m ? m : null;
        if (entity == null) {
            return TEXTURE;
        }
        if (entity.isSick()) {
            return entity.isFromFly() ? TEXTURE_SICK_FLY : TEXTURE_SICK;
        }
        return entity.isFromFly() ? TEXTURE_FLY : TEXTURE;
    }
}
