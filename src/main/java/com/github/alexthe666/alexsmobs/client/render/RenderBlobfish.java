package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelBlobfish;
import com.github.alexthe666.alexsmobs.client.model.ModelBlobfishDepressurized;
import com.github.alexthe666.alexsmobs.entity.EntityBlobfish;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderBlobfish extends MobRenderer<EntityBlobfish, LivingEntityRenderState, CitadelEntityModelBridge<EntityBlobfish>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/blobfish.png");
    private static final Identifier TEXTURE_DEPRESSURIZED = Identifier.parse("alexsmobs:textures/entity/blobfish_depressurized.png");
    private final CitadelEntityModelBridge<EntityBlobfish> fishBridge;
    private final CitadelEntityModelBridge<EntityBlobfish> depressurizedBridge;

    @SuppressWarnings("unchecked")
    public RenderBlobfish(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelBlobfish()), 0.35F);
        this.fishBridge = (CitadelEntityModelBridge<EntityBlobfish>) (Object) this.model;
        this.depressurizedBridge = new CitadelEntityModelBridge<>(new ModelBlobfishDepressurized());
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack matrixStackIn) {
        EntityBlobfish entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityBlobfish b ? b : null;
        if (entity != null) {
            this.model = entity.isDepressurized() ? this.depressurizedBridge : this.fishBridge;
            float s = entity.getBlobfishScale();
            matrixStackIn.scale(s, s, s);
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityBlobfish entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityBlobfish b ? b : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isDepressurized() ? TEXTURE_DEPRESSURIZED : TEXTURE;
    }
}
