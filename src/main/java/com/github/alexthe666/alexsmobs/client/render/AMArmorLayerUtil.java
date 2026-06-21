package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.AlexAdvancedEntityModel;
import com.github.alexthe666.alexsmobs.item.ItemModArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Resolves 26.1 equipment-layer textures ({@code textures/entity/equipment/...}) for entity armor render layers.
 */
public final class AMArmorLayerUtil {

    private AMArmorLayerUtil() {
    }

    public record ArmorDraw(Identifier texture, int color) {
    }

    public static List<ArmorDraw> getDraws(
            EquipmentAssetManager equipmentAssets,
            ItemStack stack,
            EquipmentClientInfo.LayerType layerType,
            EquipmentSlot slot,
            @Nullable LivingEntity entity
    ) {
        if (stack.isEmpty()) {
            return List.of();
        }
        if (stack.getItem() instanceof ItemModArmor modArmor) {
            Identifier texture = modArmor.getArmorTexture(stack, entity, slot, null, layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS);
            return texture != null ? List.of(new ArmorDraw(texture, -1)) : List.of();
        }
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot() != slot || equippable.assetId().isEmpty()) {
            return List.of();
        }
        ResourceKey<EquipmentAsset> assetId = equippable.assetId().get();
        List<EquipmentClientInfo.Layer> layers = equipmentAssets.get(assetId).getLayers(layerType);
        if (layers.isEmpty()) {
            return List.of();
        }
        int dyeColor = DyedItemColor.getOrDefault(stack, 0);
        List<ArmorDraw> draws = new ArrayList<>();
        for (EquipmentClientInfo.Layer layer : layers) {
            int color = colorForLayer(layer, dyeColor);
            if (color != 0) {
                draws.add(new ArmorDraw(layer.getTextureLocation(layerType), color));
            }
        }
        return draws;
    }

    private static int colorForLayer(EquipmentClientInfo.Layer layer, int dyeColor) {
        Optional<EquipmentClientInfo.Dyeable> dyeable = layer.dyeable();
        if (dyeable.isPresent()) {
            int colorWhenUndyed = dyeable.get().colorWhenUndyed().map(ARGB::opaque).orElse(0);
            return dyeColor != 0 ? dyeColor : colorWhenUndyed;
        }
        return -1;
    }

    /**
     * Deferred draw for custom (non-vanilla-slot) humanoid armor models. Vanilla per-slot models should use
     * {@link net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer#renderLayers} instead.
     */
    public static void submitCustomHeadArmor(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            LivingEntityRenderState state,
            int packedLight,
            boolean glint,
            HumanoidModel<?> model,
            int color,
            Identifier texture
    ) {
        HumanoidSnapshot snapshot = HumanoidSnapshot.capture(model);
        submitCustomModel(poseStack, collector, glint, model, color, texture, snapshot, (stack, vc) ->
                model.renderToBuffer(stack, vc, packedLight, LivingEntityRenderer.getOverlayCoords(state, 0.0F), color)
        );
    }

    public static void submitCustomChestplateArmor(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            LivingEntityRenderState state,
            int packedLight,
            boolean glint,
            HumanoidModel<?> model,
            int color,
            Identifier texture
    ) {
        HumanoidSnapshot armsSnapshot = HumanoidSnapshot.capture(model);
        armsSnapshot.bodyVisible = false;
        submitCustomModel(poseStack, collector, glint, model, color, texture, armsSnapshot, (stack, vc) ->
                model.renderToBuffer(stack, vc, packedLight, LivingEntityRenderer.getOverlayCoords(state, 0.0F), color)
        );

        HumanoidSnapshot bodySnapshot = HumanoidSnapshot.capture(model);
        bodySnapshot.rightArmVisible = false;
        bodySnapshot.leftArmVisible = false;
        submitCustomModel(poseStack, collector, glint, model, color, texture, bodySnapshot, (stack, vc) -> {
            stack.pushPose();
            stack.scale(1.1F, 1.65F, 1.1F);
            model.renderToBuffer(stack, vc, packedLight, LivingEntityRenderer.getOverlayCoords(state, 0.0F), color);
            stack.popPose();
        });
    }

    private static void submitCustomModel(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            boolean glint,
            HumanoidModel<?> model,
            int color,
            Identifier texture,
            HumanoidSnapshot snapshot,
            BiPoseRenderer renderer
    ) {
        PoseStack scratch = new PoseStack();
        collector.submitCustomGeometry(poseStack, RenderTypes.armorCutoutNoCull(texture), (pose, vc) -> {
            snapshot.apply(model);
            AlexAdvancedEntityModel.withCitadelSubmitPose(pose, scratch, stack -> renderer.render(stack, vc));
        });
        if (glint) {
            collector.submitCustomGeometry(poseStack, RenderTypes.armorEntityGlint(), (pose, vc) -> {
                snapshot.apply(model);
                AlexAdvancedEntityModel.withCitadelSubmitPose(pose, scratch, stack -> renderer.render(stack, vc));
            });
        }
    }

    @FunctionalInterface
    private interface BiPoseRenderer {
        void render(PoseStack stack, VertexConsumer consumer);
    }

    public static final class HumanoidSnapshot {
        private final PartPose head;
        private final PartPose hat;
        private final PartPose body;
        private final PartPose rightArm;
        private final PartPose leftArm;
        private final PartPose rightLeg;
        private final PartPose leftLeg;
        private boolean headVisible;
        private boolean hatVisible;
        private boolean bodyVisible;
        private boolean rightArmVisible;
        private boolean leftArmVisible;
        private boolean rightLegVisible;
        private boolean leftLegVisible;

        private HumanoidSnapshot(HumanoidModel<?> model) {
            this.head = model.head.storePose();
            this.hat = model.hat.storePose();
            this.body = model.body.storePose();
            this.rightArm = model.rightArm.storePose();
            this.leftArm = model.leftArm.storePose();
            this.rightLeg = model.rightLeg.storePose();
            this.leftLeg = model.leftLeg.storePose();
            this.headVisible = model.head.visible;
            this.hatVisible = model.hat.visible;
            this.bodyVisible = model.body.visible;
            this.rightArmVisible = model.rightArm.visible;
            this.leftArmVisible = model.leftArm.visible;
            this.rightLegVisible = model.rightLeg.visible;
            this.leftLegVisible = model.leftLeg.visible;
        }

        public static HumanoidSnapshot capture(HumanoidModel<?> model) {
            return new HumanoidSnapshot(model);
        }

        public void apply(HumanoidModel<?> model) {
            model.head.loadPose(this.head);
            model.hat.loadPose(this.hat);
            model.body.loadPose(this.body);
            model.rightArm.loadPose(this.rightArm);
            model.leftArm.loadPose(this.leftArm);
            model.rightLeg.loadPose(this.rightLeg);
            model.leftLeg.loadPose(this.leftLeg);
            model.head.visible = this.headVisible;
            model.hat.visible = this.hatVisible;
            model.body.visible = this.bodyVisible;
            model.rightArm.visible = this.rightArmVisible;
            model.leftArm.visible = this.leftArmVisible;
            model.rightLeg.visible = this.rightLegVisible;
            model.leftLeg.visible = this.leftLegVisible;
        }
    }
}
