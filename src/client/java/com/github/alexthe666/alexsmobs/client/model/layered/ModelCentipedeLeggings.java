package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class ModelCentipedeLeggings extends HumanoidModel {

    public ModelCentipedeLeggings(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.2F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.getChild("body");
        PartDefinition leftLeg = partdefinition.getChild("left_leg");
        PartDefinition rightLeg = partdefinition.getChild("right_leg");

        body.addOrReplaceChild("centipede_waist",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 11.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.45F)),
            PartPose.ZERO);
        leftLeg.addOrReplaceChild("centipede_left_leg",
            CubeListBuilder.create()
                .texOffs(0, 6).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.45F))
                .texOffs(0, 23).addBox(1.9F, 1.0F, -1.5F, 0.0F, 10.0F, 3.0F, CubeDeformation.NONE),
            PartPose.ZERO);
        rightLeg.addOrReplaceChild("centipede_right_leg",
            CubeListBuilder.create()
                .texOffs(16, 6).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.45F)).mirror(false)
                .texOffs(6, 23).addBox(-1.9F, 1.0F, -1.5F, 0.0F, 10.0F, 3.0F, CubeDeformation.NONE),
            PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
