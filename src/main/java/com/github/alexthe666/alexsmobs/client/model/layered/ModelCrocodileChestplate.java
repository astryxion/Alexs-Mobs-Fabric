package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ModelCrocodileChestplate extends HumanoidModel {

    public ModelCrocodileChestplate(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.35F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.getChild("body");
        PartDefinition leftArm = partdefinition.getChild("left_arm");
        PartDefinition rightArm = partdefinition.getChild("right_arm");

        body.addOrReplaceChild("crocodile_body",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformation)
                .texOffs(0, 16).addBox(-4.5F, 1.5F, 2.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.15F))
                .texOffs(22, 16).addBox(-1.5F, -1.0F, 2.0F, 3.0F, 13.0F, 2.0F, new CubeDeformation(0.1F)),
            PartPose.ZERO);
        leftArm.addOrReplaceChild("crocodile_left_arm",
            CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.55F)),
            PartPose.ZERO);
        rightArm.addOrReplaceChild("crocodile_right_arm",
            CubeListBuilder.create().texOffs(24, 0).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.55F)).mirror(false),
            PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
