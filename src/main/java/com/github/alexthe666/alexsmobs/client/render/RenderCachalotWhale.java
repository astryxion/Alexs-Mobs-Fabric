package com.github.alexthe666.alexsmobs.client.render;
import com.github.alexthe666.alexsmobs.client.render.CitadelEntityModelBridge;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;

import com.github.alexthe666.alexsmobs.client.model.ModelCachalotWhale;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCachalotWhaleCapturedSquid;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotPart;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderCachalotWhale extends MobRenderer<EntityCachalotWhale, LivingEntityRenderState, CitadelEntityModelBridge<EntityCachalotWhale>> {
    private static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/entity/cachalot/cachalot_whale.png");
    private static final Identifier TEXTURE_SLEEPING = Identifier.parse("alexsmobs:textures/entity/cachalot/cachalot_whale_sleeping.png");
    private static final Identifier TEXTURE_ALBINO = Identifier.parse("alexsmobs:textures/entity/cachalot/cachalot_whale_albino.png");
    private static final Identifier TEXTURE_ALBINO_SLEEPING = Identifier.parse("alexsmobs:textures/entity/cachalot/cachalot_whale_albino_sleeping.png");

    public RenderCachalotWhale(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CitadelEntityModelBridge<>(new ModelCachalotWhale()),  4.2F);
        this.addLayer(new LayerCachalotWhaleCapturedSquid(this));
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    protected void scale(EntityCachalotWhale entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    public boolean shouldRender(EntityCachalotWhale livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            for(EntityCachalotPart part : livingEntityIn.whaleParts){
                if(camera.isVisible(part.getBoundingBox())){
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        EntityCachalotWhale entity = AlexsMobsClientKeys.getLiving(state) instanceof EntityCachalotWhale w ? w : null;
        if (entity == null) {
            return TEXTURE;
        }
        if (entity.isAlbino()) {
            return entity.isSleeping() || entity.isBeached() ? TEXTURE_ALBINO_SLEEPING : TEXTURE_ALBINO;
        } else {
            return entity.isSleeping() || entity.isBeached() ? TEXTURE_SLEEPING : TEXTURE;
        }
    }
}
