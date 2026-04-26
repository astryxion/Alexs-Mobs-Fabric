package com.github.alexthe666.alexsmobs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

/**
 * Hitbox-only multipart segments; the parent mob renderer draws the visible model.
 */
public class RenderMultipartCollisionPart extends EntityRenderer<Entity, EntityRenderState> {

    private static final Identifier DUMMY = Identifier.parse("minecraft:textures/misc/white.png");

    public RenderMultipartCollisionPart(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return DUMMY;
    }
}
