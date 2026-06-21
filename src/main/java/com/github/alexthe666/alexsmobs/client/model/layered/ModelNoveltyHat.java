package com.github.alexthe666.alexsmobs.client.model.layered;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ModelNoveltyHat extends HumanoidModel {

    public ModelNoveltyHat(ModelPart part) {
        super(part);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");

        // Attach to head like other mod helmets (fedora/sombrero). The vanilla hat layer pose copy in 26.1
        // pulls hat-child geometry down; a direct head child keeps placement stable.
        PartDefinition cap = head.addOrReplaceChild("novelty_hat", CubeListBuilder.create().texOffs(0, 30).addBox(-4.5F, -5.0F, -4.5F, 9.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(31, 55).addBox(4.5F, -2.0F, -2.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(31, 55).mirror().addBox(-8.5F, -2.0F, -2.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 46).addBox(-5.5F, 1.0F, -9.5F, 11.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

        cap.addOrReplaceChild("pipes", CubeListBuilder.create().texOffs(0, 55).addBox(-7.5F, 0.0F, 0.0F, 15.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -0.5F, -0.6545F, 0.0F, 0.0F));

        head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
