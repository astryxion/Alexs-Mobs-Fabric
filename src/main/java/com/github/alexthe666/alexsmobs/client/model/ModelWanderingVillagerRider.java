package com.github.alexthe666.alexsmobs.client.model;

import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;

public class ModelWanderingVillagerRider extends VillagerModel {
    private ModelPart rightLegRider;
    private ModelPart leftLegRider;

    public ModelWanderingVillagerRider(ModelPart part) {
        super(part);
        this.rightLegRider = part.getChild("right_leg");
        this.leftLegRider = part.getChild("left_leg");
    }

    @Override
    public void setupAnim(VillagerRenderState state) {
        super.setupAnim(state);
        // This model is only installed while the trader is riding an elephant; match the old riding leg pose.
        this.rightLegRider.xRot = -1.4137167F;
        this.rightLegRider.yRot = 0.31415927F;
        this.rightLegRider.zRot = 0.07853982F;
        this.leftLegRider.xRot = -1.4137167F;
        this.leftLegRider.yRot = -0.31415927F;
        this.leftLegRider.zRot = -0.07853982F;
    }
}
