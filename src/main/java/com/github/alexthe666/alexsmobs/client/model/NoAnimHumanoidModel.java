package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/**
 * Humanoid armor mesh whose pose is assigned manually before deferred draw.
 * Skips {@link HumanoidModel#setupAnim} so 26.1 replay does not reset custom mob armor poses.
 */
public class NoAnimHumanoidModel extends HumanoidModel<HumanoidRenderState> {

    public NoAnimHumanoidModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(HumanoidRenderState state) {
    }
}
