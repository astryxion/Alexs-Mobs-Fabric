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

public class ModelEmuLeggings extends HumanoidModel {

    public ModelEmuLeggings(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.2F), 0.0F);
        PartDefinition root = meshdefinition.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition body = root.getChild("body");
        PartDefinition leftLeg = root.getChild("left_leg");
        PartDefinition rightLeg = root.getChild("right_leg");

        body.addOrReplaceChild("emu_waist",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 11.0F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.ZERO);
        leftLeg.addOrReplaceChild("emu_left_leg",
            CubeListBuilder.create()
                .texOffs(0, 7).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.35F))
                .texOffs(20, 7).addBox(-2.5F, 8.0F, -2.5F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.1F)),
            PartPose.ZERO);
        rightLeg.addOrReplaceChild("emu_right_leg",
            CubeListBuilder.create()
                .texOffs(0, 7).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.35F)).mirror(false)
                .texOffs(20, 7).mirror().addBox(-2.5F, 8.0F, -2.5F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.1F)).mirror(false),
            PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
