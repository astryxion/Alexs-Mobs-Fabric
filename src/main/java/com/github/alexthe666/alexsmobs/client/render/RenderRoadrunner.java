package com.github.alexthe666.alexsmobs.client.render;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelRoadrunner;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderRoadrunner extends MobRenderer<EntityRoadrunner, LivingEntityRenderState, CitadelEntityModelBridge<EntityRoadrunner>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/roadrunner.png");
    private static final Identifier TEXTURE_MEEP = Identifier.parse("alexsmobs:textures/entity/roadrunner_meep.png");

    public RenderRoadrunner(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelRoadrunner()), 0.3F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public void extractRenderState(EntityRoadrunner entity, LivingEntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        getModel().setCitadelYoung(entity.isBaby());
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityRoadrunner entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityRoadrunner r ? r : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isMeep() ? TEXTURE_MEEP : TEXTURE;
    }
}
