package com.github.alexthe666.alexsmobs.client.render.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.model.layered.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemModArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;

public class CustomArmorRenderProperties {

    private static boolean init;

    public static ModelAMElytra ELYTRA_MODEL;
    public static ModelRoadrunnerBoots ROADRUNNER_BOOTS_MODEL;
    public static ModelMooseHeadgear MOOSE_HEADGEAR_MODEL;
    public static ModelFrontierCap FRONTIER_CAP_MODEL;
    public static ModelSpikedTurtleShell SPIKED_TURTLE_SHELL_MODEL;
    public static ModelFedora FEDORA_MODEL;
    public static ModelSombrero SOMBRERO_MODEL;
    public static ModelSombrero SOMBRERO_GOOFY_FASHION_MODEL;
    public static ModelFroststalkerHelmet FROSTSTALKER_HELMET_MODEL;
    public static ModelCrocodileChestplate CROCODILE_CHESTPLATE_MODEL;
    public static ModelRockyChestplate ROCKY_CHESTPLATE_MODEL;
    public static ModelFlyingFishBoots FLYING_FISH_BOOTS_MODEL;
    public static ModelNoveltyHat NOVELTY_HAT_MODEL;
    public static ModelUnsettlingKimono UNSETTLING_KIMONO_MODEL;

    public static void initializeModels() {
        init = true;
        ROADRUNNER_BOOTS_MODEL = new ModelRoadrunnerBoots(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.ROADRUNNER_BOOTS));
        MOOSE_HEADGEAR_MODEL = new ModelMooseHeadgear(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.MOOSE_HEADGEAR));
        FRONTIER_CAP_MODEL = new ModelFrontierCap(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.FRONTIER_CAP));
        FEDORA_MODEL = new ModelFedora(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.FEDORA));
        SPIKED_TURTLE_SHELL_MODEL = new ModelSpikedTurtleShell(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.SPIKED_TURTLE_SHELL));
        SOMBRERO_MODEL = new ModelSombrero(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.SOMBRERO));
        SOMBRERO_GOOFY_FASHION_MODEL = new ModelSombrero(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.SOMBRERO_GOOFY_FASHION));
        FROSTSTALKER_HELMET_MODEL = new ModelFroststalkerHelmet(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.FROSTSTALKER_HELMET));
        ELYTRA_MODEL = new ModelAMElytra(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.AM_ELYTRA));
        CROCODILE_CHESTPLATE_MODEL = new ModelCrocodileChestplate(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.CROCODILE_CHESTPLATE));
        ROCKY_CHESTPLATE_MODEL = new ModelRockyChestplate(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.ROCKY_CHESTPLATE));
        FLYING_FISH_BOOTS_MODEL = new ModelFlyingFishBoots(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.FLYING_FISH_BOOTS));
        NOVELTY_HAT_MODEL = new ModelNoveltyHat(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.NOVELTY_HAT));
        UNSETTLING_KIMONO_MODEL = new ModelUnsettlingKimono(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.UNSETTLING_KIMONO));
    }

    public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
        if (!init) {
            initializeModels();
        }
        final var item = itemStack.getItem();
        if (item == AMItemRegistry.TARANTULA_HAWK_ELYTRA) {
            return ELYTRA_MODEL;
        }
        if (item == AMItemRegistry.ROADDRUNNER_BOOTS) {
            return ROADRUNNER_BOOTS_MODEL;
        }
        if (item == AMItemRegistry.MOOSE_HEADGEAR) {
            return MOOSE_HEADGEAR_MODEL;
        }
        if (item == AMItemRegistry.FRONTIER_CAP) {
            return FRONTIER_CAP_MODEL;
        }
        if (item == AMItemRegistry.FEDORA) {
            return FEDORA_MODEL;
        }
        if (item == AMItemRegistry.SPIKED_TURTLE_SHELL) {
            return SPIKED_TURTLE_SHELL_MODEL;
        }
        if (item == AMItemRegistry.SOMBRERO) {
            return AlexsMobs.isAprilFools() ? SOMBRERO_GOOFY_FASHION_MODEL : SOMBRERO_MODEL;
        }
        if (item == AMItemRegistry.FROSTSTALKER_HELMET) {
            return FROSTSTALKER_HELMET_MODEL;
        }
        if (item == AMItemRegistry.CROCODILE_CHESTPLATE) {
            return CROCODILE_CHESTPLATE_MODEL;
        }
        if (item == AMItemRegistry.ROCKY_CHESTPLATE) {
            return ROCKY_CHESTPLATE_MODEL;
        }
        if (item == AMItemRegistry.FLYING_FISH_BOOTS) {
            return FLYING_FISH_BOOTS_MODEL;
        }
        if (item == AMItemRegistry.NOVELTY_HAT) {
            return NOVELTY_HAT_MODEL;
        }
        if (item == AMItemRegistry.UNSETTLING_KIMONO) {
            return UNSETTLING_KIMONO_MODEL;
        }
        return original;
    }

    /**
     * Inner slot armor models only pose the roots for that slot and hide the rest; {@link ClientHooks#copyModelProperties}
     * copies those {@code visible} flags onto our full-mesh custom models and hides parents of nested cubes (horns,
     * feathers, etc.). Turning every part visible again draws the default {@link HumanoidModel#createMesh} hull on all
     * limbs with the armor texture (the “all over my body” glitch). After the pose copy, re-apply vanilla slot masks
     * so only the roots used for that equipment slot render (same grouping as {@code HumanoidModel.ADULT_ARMOR_PARTS_PER_SLOT}).
     */
    public Model getGenericArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
        Model replacement = this.getHumanoidArmorModel(itemStack, layerType, original);
        if (replacement != original && original instanceof HumanoidModel<?> originalHumanoid && replacement instanceof HumanoidModel<?> replacementHumanoid) {
            replacementHumanoid.head.loadPose(originalHumanoid.head.storePose());
            if (!itemStack.is(AMItemRegistry.NOVELTY_HAT)) {
                replacementHumanoid.hat.loadPose(originalHumanoid.hat.storePose());
            }
            replacementHumanoid.body.loadPose(originalHumanoid.body.storePose());
            replacementHumanoid.rightArm.loadPose(originalHumanoid.rightArm.storePose());
            replacementHumanoid.leftArm.loadPose(originalHumanoid.leftArm.storePose());
            replacementHumanoid.rightLeg.loadPose(originalHumanoid.rightLeg.storePose());
            replacementHumanoid.leftLeg.loadPose(originalHumanoid.leftLeg.storePose());
            applyArmorSlotVisibility(itemStack, replacementHumanoid);
            return replacementHumanoid;
        }
        return replacement;
    }

    private static void applyArmorSlotVisibility(ItemStack itemStack, HumanoidModel<?> model) {
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return;
        }
        EquipmentSlot slot = equippable.slot();
        model.head.visible = false;
        model.hat.visible = false;
        model.body.visible = false;
        model.rightArm.visible = false;
        model.leftArm.visible = false;
        model.rightLeg.visible = false;
        model.leftLeg.visible = false;
        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = !itemStack.is(AMItemRegistry.NOVELTY_HAT);
            }
            case CHEST -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case BODY -> model.body.visible = true;
            default -> {
            }
        }
    }

    public void setupModelAnimations(
            LivingEntity livingEntity,
            ItemStack itemStack,
            EquipmentSlot equipmentSlot,
            Model model,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {
        if (itemStack.is(AMItemRegistry.TARANTULA_HAWK_ELYTRA) && model instanceof ModelAMElytra elytra) {
            elytra.poseTarantulaWingsFromEntity(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
        if (itemStack.is(AMItemRegistry.FRONTIER_CAP) && model instanceof ModelFrontierCap cap) {
            cap.withAnimations(livingEntity);
        }
        if (itemStack.is(AMItemRegistry.FLYING_FISH_BOOTS) && model instanceof ModelFlyingFishBoots fish) {
            fish.withAnimations(livingEntity);
        }
    }

    @Nullable
    public Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, Identifier _default) {
        if (stack.getItem() instanceof ItemModArmor modArmor) {
            Equippable e = stack.get(DataComponents.EQUIPPABLE);
            if (e != null) {
                return modArmor.getArmorTexture(stack, null, e.slot(), null, false);
            }
        }
        if (stack.is(AMItemRegistry.TARANTULA_HAWK_ELYTRA)) {
            return Identifier.fromNamespaceAndPath("alexsmobs", "textures/armor/tarantula_hawk_elytra.png");
        }
        return null;
    }
}
