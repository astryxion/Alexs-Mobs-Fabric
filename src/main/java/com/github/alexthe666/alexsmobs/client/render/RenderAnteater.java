package com.github.alexthe666.alexsmobs.client.render;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;

import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.model.ModelAnteater;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerAnteaterTongueItem;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class RenderAnteater extends MobRenderer<EntityAnteater, LivingEntityRenderState, CitadelEntityModelBridge<EntityAnteater>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/anteater.png");
    private static final Identifier TEXTURE_PETER = Identifier.parse("alexsmobs:textures/entity/anteater_peter.png");

    public RenderAnteater(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelAnteater()), 0.45F);
        this.addLayer(new LayerAnteaterTongueItem(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityAnteater entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityAnteater e ? e : null;
        if (entity == null) {
            return TEXTURE;
        }
        return entity.isPeter() ? TEXTURE_PETER : TEXTURE;
    }
}
