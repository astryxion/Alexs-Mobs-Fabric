package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelGeladaMonkey;
import com.github.alexthe666.alexsmobs.entity.EntityGeladaMonkey;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderGeladaMonkey extends MobRenderer<EntityGeladaMonkey, LivingEntityRenderState, CitadelEntityModelBridge<EntityGeladaMonkey>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/gelada_monkey.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.parse("alexsmobs:textures/entity/gelada_monkey_angry.png");
    private static final Identifier TEXTURE_LEADER = Identifier.parse("alexsmobs:textures/entity/gelada_monkey_leader.png");
    private static final Identifier TEXTURE_LEADER_ANGRY = Identifier.parse("alexsmobs:textures/entity/gelada_monkey_leader_angry.png");

    public RenderGeladaMonkey(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelGeladaMonkey()), 0.45F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityGeladaMonkey entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityGeladaMonkey g ? g : null;
        if (entity != null) {
            float s = entity.getGeladaScale();
            matrixStackIn.scale(s, s, s);
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityGeladaMonkey entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityGeladaMonkey g ? g : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isLeader() ? entity.isAggro() ? TEXTURE_LEADER_ANGRY : TEXTURE_LEADER : entity.isAggro() ? TEXTURE_ANGRY : TEXTURE;
    }
}
