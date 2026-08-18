package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.AlexsMobsClientKeys;
import com.github.alexthe666.alexsmobs.client.color.AMSpawnEggTintSource;
import com.github.alexthe666.alexsmobs.client.ClientLayerRegistry;
import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.alexsmobs.client.gui.GUITransmutationTable;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.particle.*;
import com.github.alexthe666.alexsmobs.client.render.*;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import com.github.alexthe666.alexsmobs.client.render.item.AMItemRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.item.GhostlyPickaxeItemModel;
import com.github.alexthe666.alexsmobs.client.render.tile.RenderCapsid;
import com.github.alexthe666.alexsmobs.client.render.tile.RenderTransmutationTable;
import com.github.alexthe666.alexsmobs.client.render.tile.RenderVoidWormBeak;
import com.github.alexthe666.alexsmobs.client.sound.SoundBearMusicBox;
import com.github.alexthe666.alexsmobs.client.sound.SoundLaCucaracha;
import com.github.alexthe666.alexsmobs.client.sound.SoundWormBoss;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.inventory.AMMenuRegistry;
import com.github.alexthe666.alexsmobs.item.*;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.state.BlockState;
// FMLJavaModLoadingContext removed in 1.21

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.lang.reflect.Field;
// @Mod.EventBusSubscriber removed - register client listeners from client setup / proxy instead
public class ClientProxy extends CommonProxy {
    /** Vanilla inner leggings shell — same model HumanoidArmorLayer uses for the leggings slot. */
    private static HumanoidModel<?> vanillaLeggingsArmorModel;

    private static HumanoidModel<?> getVanillaLeggingsArmorModel() {
        if (vanillaLeggingsArmorModel == null) {
            vanillaLeggingsArmorModel = ArmorModelSet.bake(
                    net.minecraft.client.model.geom.ModelLayers.PLAYER_ARMOR,
                    Minecraft.getInstance().getEntityModels(),
                    HumanoidModel::new
            ).legs();
        }
        return vanillaLeggingsArmorModel;
    }

    private static void copyHumanoidPose(HumanoidModel<?> from, HumanoidModel<?> to) {
        to.head.loadPose(from.head.storePose());
        to.hat.loadPose(from.hat.storePose());
        to.body.loadPose(from.body.storePose());
        to.rightArm.loadPose(from.rightArm.storePose());
        to.leftArm.loadPose(from.leftArm.storePose());
        to.rightLeg.loadPose(from.rightLeg.storePose());
        to.leftLeg.loadPose(from.leftLeg.storePose());
    }

    public static final Int2ObjectMap<SoundBearMusicBox> BEAR_MUSIC_BOX_SOUND_MAP = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<SoundLaCucaracha> COCKROACH_SOUND_MAP = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<SoundWormBoss> WORMBOSS_SOUND_MAP = new Int2ObjectOpenHashMap<>();
    public static final List<UUID> currentUnrenderedEntities = new ArrayList<>();
    public static int voidPortalCreationTime = 0;
    /** Latest world-render camera; used by {@link AMItemstackRenderer} for entity-in-item preview. */
    public static volatile CameraRenderState lastCameraRenderState;
    public CameraType prevPOV = CameraType.FIRST_PERSON;
    public boolean initializedRainbowBuffers = false;
    private int pupfishChunkX = 0;
    private int pupfishChunkZ = 0;
    /** Mirrored for {@link PupfishLocatorInChunkProperty} (item model property; no entity reference). */
    private static volatile int pupfishChunkXModel = 0;
    private static volatile int pupfishChunkZModel = 0;
    private int singingBlueJayId = -1;
    private final ItemStack[] transmuteStacks = new ItemStack[3];

    @Override
    public void sendToServer(CustomPacketPayload message) {
        ClientPlayNetworking.send(message);
    }


    @Override
    public void init() {
    }

    private static void registerMenuScreens() {
        MenuScreens.register(AMMenuRegistry.TRANSMUTATION_TABLE, GUITransmutationTable::new);
        AlexsMobs.LOGGER.info("Registered Transmutation Table screen");
    }
    /**
     * Renders an entity using the 26.1 submit pipeline (used from entity-attached layers and {@link ClientEvents}).
     */
    public static <E extends Entity> void submitEntityInWorld(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, SubmitNodeCollector collector) {
        EntityRenderer<? super E, ?> render = null;
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        net.minecraft.client.renderer.state.level.CameraRenderState camera = lastCameraRenderState != null ? lastCameraRenderState : new net.minecraft.client.renderer.state.level.CameraRenderState();
        try {
            render = manager.getRenderer(entityIn);

            if (render != null) {
                try {
                    @SuppressWarnings("unchecked")
                    EntityRenderState state = manager.extractEntity(entityIn, partialTicks);
                    manager.submit(state, camera, x, y, z, matrixStack, collector);
                } catch (Throwable throwable1) {
                    throw new ReportedException(CrashReport.forThrowable(throwable1, "Rendering entity in world"));
                }
            }
        } catch (Throwable throwable3) {
            CrashReport crashreport = CrashReport.forThrowable(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
            entityIn.fillCrashReportCategory(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
            crashreportcategory1.setDetail("Assigned renderer", render);
            crashreportcategory1.setDetail("Rotation", Float.valueOf(yaw));
            crashreportcategory1.setDetail("Delta", Float.valueOf(partialTicks));
            throw new ReportedException(crashreport);
        }
    }
    private static void registerRenderers() {
        EntityRendererRegistry.register(AMEntityRegistry.GRIZZLY_BEAR, RenderGrizzlyBear::new);
        EntityRendererRegistry.register(AMEntityRegistry.ROADRUNNER, RenderRoadrunner::new);
        EntityRendererRegistry.register(AMEntityRegistry.BONE_SERPENT, RenderBoneSerpent::new);
        EntityRendererRegistry.register(AMEntityRegistry.BONE_SERPENT_PART, RenderBoneSerpentPart::new);
        EntityRendererRegistry.register(AMEntityRegistry.GAZELLE, RenderGazelle::new);
        EntityRendererRegistry.register(AMEntityRegistry.CROCODILE, RenderCrocodile::new);
        EntityRendererRegistry.register(AMEntityRegistry.FLY, RenderFly::new);
        EntityRendererRegistry.register(AMEntityRegistry.HUMMINGBIRD, RenderHummingbird::new);
        EntityRendererRegistry.register(AMEntityRegistry.ORCA, RenderOrca::new);
        EntityRendererRegistry.register(AMEntityRegistry.SUNBIRD, RenderSunbird::new);
        EntityRendererRegistry.register(AMEntityRegistry.GORILLA, RenderGorilla::new);
        EntityRendererRegistry.register(AMEntityRegistry.CRIMSON_MOSQUITO, RenderCrimsonMosquito::new);
        EntityRendererRegistry.register(AMEntityRegistry.MOSQUITO_SPIT, RenderMosquitoSpit::new);
        EntityRendererRegistry.register(AMEntityRegistry.RATTLESNAKE, RenderRattlesnake::new);
        EntityRendererRegistry.register(AMEntityRegistry.ENDERGRADE, RenderEndergrade::new);
        EntityRendererRegistry.register(AMEntityRegistry.HAMMERHEAD_SHARK, RenderHammerheadShark::new);
        EntityRendererRegistry.register(AMEntityRegistry.SHARK_TOOTH_ARROW, RenderSharkToothArrow::new);
        EntityRendererRegistry.register(AMEntityRegistry.LOBSTER, RenderLobster::new);
        EntityRendererRegistry.register(AMEntityRegistry.KOMODO_DRAGON, RenderKomodoDragon::new);
        EntityRendererRegistry.register(AMEntityRegistry.CAPUCHIN_MONKEY, RenderCapuchinMonkey::new);
        EntityRendererRegistry.register(AMEntityRegistry.TOSSED_ITEM, RenderTossedItem::new);
        EntityRendererRegistry.register(AMEntityRegistry.CENTIPEDE_HEAD, RenderCentipedeHead::new);
        EntityRendererRegistry.register(AMEntityRegistry.CENTIPEDE_BODY, RenderCentipedeBody::new);
        EntityRendererRegistry.register(AMEntityRegistry.CENTIPEDE_TAIL, RenderCentipedeTail::new);
        EntityRendererRegistry.register(AMEntityRegistry.WARPED_TOAD, RenderWarpedToad::new);
        EntityRendererRegistry.register(AMEntityRegistry.MOOSE, RenderMoose::new);
        EntityRendererRegistry.register(AMEntityRegistry.MIMICUBE, RenderMimicube::new);
        EntityRendererRegistry.register(AMEntityRegistry.RACCOON, RenderRaccoon::new);
        EntityRendererRegistry.register(AMEntityRegistry.BLOBFISH, RenderBlobfish::new);
        EntityRendererRegistry.register(AMEntityRegistry.SEAL, RenderSeal::new);
        EntityRendererRegistry.register(AMEntityRegistry.COCKROACH, RenderCockroach::new);
        EntityRendererRegistry.register(AMEntityRegistry.COCKROACH_EGG, (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRendererRegistry.register(AMEntityRegistry.SHOEBILL, RenderShoebill::new);
        EntityRendererRegistry.register(AMEntityRegistry.ELEPHANT, RenderElephant::new);
        EntityRendererRegistry.register(AMEntityRegistry.SOUL_VULTURE, RenderSoulVulture::new);
        EntityRendererRegistry.register(AMEntityRegistry.SNOW_LEOPARD, RenderSnowLeopard::new);
        EntityRendererRegistry.register(AMEntityRegistry.SPECTRE, RenderSpectre::new);
        EntityRendererRegistry.register(AMEntityRegistry.CROW, RenderCrow::new);
        EntityRendererRegistry.register(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, RenderAlligatorSnappingTurtle::new);
        EntityRendererRegistry.register(AMEntityRegistry.MUNGUS, RenderMungus::new);
        EntityRendererRegistry.register(AMEntityRegistry.MANTIS_SHRIMP, RenderMantisShrimp::new);
        EntityRendererRegistry.register(AMEntityRegistry.GUSTER, RenderGuster::new);
        EntityRendererRegistry.register(AMEntityRegistry.SAND_SHOT, RenderSandShot::new);
        EntityRendererRegistry.register(AMEntityRegistry.GUST, RenderGust::new);
        EntityRendererRegistry.register(AMEntityRegistry.WARPED_MOSCO, RenderWarpedMosco::new);
        EntityRendererRegistry.register(AMEntityRegistry.HEMOLYMPH, RenderHemolymph::new);
        EntityRendererRegistry.register(AMEntityRegistry.STRADDLER, RenderStraddler::new);
        EntityRendererRegistry.register(AMEntityRegistry.STRADPOLE, RenderStradpole::new);
        EntityRendererRegistry.register(AMEntityRegistry.STRADDLEBOARD, RenderStraddleboard::new);
        EntityRendererRegistry.register(AMEntityRegistry.EMU, RenderEmu::new);
        EntityRendererRegistry.register(AMEntityRegistry.EMU_EGG, (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRendererRegistry.register(AMEntityRegistry.PLATYPUS, RenderPlatypus::new);
        EntityRendererRegistry.register(AMEntityRegistry.DROPBEAR, RenderDropBear::new);
        EntityRendererRegistry.register(AMEntityRegistry.TASMANIAN_DEVIL, RenderTasmanianDevil::new);
        EntityRendererRegistry.register(AMEntityRegistry.KANGAROO, RenderKangaroo::new);
        EntityRendererRegistry.register(AMEntityRegistry.CACHALOT_WHALE, RenderCachalotWhale::new);
        EntityRendererRegistry.register(AMEntityRegistry.CACHALOT_ECHO, RenderCachalotEcho::new);
        EntityRendererRegistry.register(AMEntityRegistry.LEAFCUTTER_ANT, RenderLeafcutterAnt::new);
        EntityRendererRegistry.register(AMEntityRegistry.ENDERIOPHAGE, RenderEnderiophage::new);
        EntityRendererRegistry.register(AMEntityRegistry.ENDERIOPHAGE_ROCKET, (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRendererRegistry.register(AMEntityRegistry.BALD_EAGLE, RenderBaldEagle::new);
        EntityRendererRegistry.register(AMEntityRegistry.TIGER, RenderTiger::new);
        EntityRendererRegistry.register(AMEntityRegistry.TARANTULA_HAWK, RenderTarantulaHawk::new);
        EntityRendererRegistry.register(AMEntityRegistry.VOID_WORM, RenderVoidWormHead::new);
        EntityRendererRegistry.register(AMEntityRegistry.VOID_WORM_PART, RenderVoidWormBody::new);
        EntityRendererRegistry.register(AMEntityRegistry.VOID_WORM_SHOT, RenderVoidWormShot::new);
        EntityRendererRegistry.register(AMEntityRegistry.VOID_PORTAL, RenderVoidPortal::new);
        EntityRendererRegistry.register(AMEntityRegistry.FRILLED_SHARK, RenderFrilledShark::new);
        EntityRendererRegistry.register(AMEntityRegistry.MIMIC_OCTOPUS, RenderMimicOctopus::new);
        EntityRendererRegistry.register(AMEntityRegistry.SEAGULL, RenderSeagull::new);
        EntityRendererRegistry.register(AMEntityRegistry.FROSTSTALKER, RenderFroststalker::new);
        EntityRendererRegistry.register(AMEntityRegistry.ICE_SHARD, RenderIceShard::new);
        EntityRendererRegistry.register(AMEntityRegistry.TUSKLIN, RenderTusklin::new);
        EntityRendererRegistry.register(AMEntityRegistry.LAVIATHAN, RenderLaviathan::new);
        EntityRendererRegistry.register(AMEntityRegistry.COSMAW, RenderCosmaw::new);
        EntityRendererRegistry.register(AMEntityRegistry.TOUCAN, RenderToucan::new);
        EntityRendererRegistry.register(AMEntityRegistry.MANED_WOLF, RenderManedWolf::new);
        EntityRendererRegistry.register(AMEntityRegistry.ANACONDA, RenderAnaconda::new);
        EntityRendererRegistry.register(AMEntityRegistry.ANACONDA_PART, RenderAnacondaPart::new);
        EntityRendererRegistry.register(AMEntityRegistry.VINE_LASSO, RenderVineLasso::new);
        EntityRendererRegistry.register(AMEntityRegistry.ANTEATER, RenderAnteater::new);
        EntityRendererRegistry.register(AMEntityRegistry.ROCKY_ROLLER, RenderRockyRoller::new);
        EntityRendererRegistry.register(AMEntityRegistry.FLUTTER, RenderFlutter::new);
        EntityRendererRegistry.register(AMEntityRegistry.POLLEN_BALL, RenderPollenBall::new);
        EntityRendererRegistry.register(AMEntityRegistry.GELADA_MONKEY, RenderGeladaMonkey::new);
        EntityRendererRegistry.register(AMEntityRegistry.JERBOA, RenderJerboa::new);
        EntityRendererRegistry.register(AMEntityRegistry.TERRAPIN, RenderTerrapin::new);
        EntityRendererRegistry.register(AMEntityRegistry.COMB_JELLY, RenderCombJelly::new);
        EntityRendererRegistry.register(AMEntityRegistry.COSMIC_COD, RenderCosmicCod::new);
        EntityRendererRegistry.register(AMEntityRegistry.BUNFUNGUS, RenderBunfungus::new);
        EntityRendererRegistry.register(AMEntityRegistry.BISON, RenderBison::new);
        EntityRendererRegistry.register(AMEntityRegistry.GIANT_SQUID, RenderGiantSquid::new);
        EntityRendererRegistry.register(AMEntityRegistry.SQUID_GRAPPLE, RenderSquidGrapple::new);
        EntityRendererRegistry.register(AMEntityRegistry.SEA_BEAR, RenderSeaBear::new);
        EntityRendererRegistry.register(AMEntityRegistry.DEVILS_HOLE_PUPFISH, RenderDevilsHolePupfish::new);
        EntityRendererRegistry.register(AMEntityRegistry.CATFISH, RenderCatfish::new);
        EntityRendererRegistry.register(AMEntityRegistry.FLYING_FISH, RenderFlyingFish::new);
        EntityRendererRegistry.register(AMEntityRegistry.SKELEWAG, RenderSkelewag::new);
        EntityRendererRegistry.register(AMEntityRegistry.RAIN_FROG, RenderRainFrog::new);
        EntityRendererRegistry.register(AMEntityRegistry.POTOO, RenderPotoo::new);
        EntityRendererRegistry.register(AMEntityRegistry.MUDSKIPPER, RenderMudskipper::new);
        EntityRendererRegistry.register(AMEntityRegistry.MUD_BALL, RenderMudBall::new);
        EntityRendererRegistry.register(AMEntityRegistry.RHINOCEROS, RenderRhinoceros::new);
        EntityRendererRegistry.register(AMEntityRegistry.SUGAR_GLIDER, RenderSugarGlider::new);
        EntityRendererRegistry.register(AMEntityRegistry.FARSEER, RenderFarseer::new);
        EntityRendererRegistry.register(AMEntityRegistry.SKREECHER, RenderSkreecher::new);
        EntityRendererRegistry.register(AMEntityRegistry.UNDERMINER, RenderUnderminer::new);
        EntityRendererRegistry.register(AMEntityRegistry.MURMUR, RenderMurmurBody::new);
        EntityRendererRegistry.register(AMEntityRegistry.MURMUR_HEAD, RenderMurmurHead::new);
        EntityRendererRegistry.register(AMEntityRegistry.TENDON_SEGMENT, RenderTendonSegment::new);
        EntityRendererRegistry.register(AMEntityRegistry.SKUNK, RenderSkunk::new);
        EntityRendererRegistry.register(AMEntityRegistry.FART, RenderFart::new);
        EntityRendererRegistry.register(AMEntityRegistry.BANANA_SLUG, RenderBananaSlug::new);
        EntityRendererRegistry.register(AMEntityRegistry.BLUE_JAY, RenderBlueJay::new);
        EntityRendererRegistry.register(AMEntityRegistry.CAIMAN, RenderCaiman::new);
        EntityRendererRegistry.register(AMEntityRegistry.TRIOPS, RenderTriops::new);
        BlockEntityRendererRegistry.register(AMTileEntityRegistry.CAPSID, RenderCapsid::new);
        BlockEntityRendererRegistry.register(AMTileEntityRegistry.VOID_WORM_BEAK, RenderVoidWormBeak::new);
        BlockEntityRendererRegistry.register(AMTileEntityRegistry.TRANSMUTATION_TABLE, RenderTransmutationTable::new);
    }

    public void clientInit() {
        registerClientModelCodecs();
        registerItemModelWrappers();
        AMModelLayers.register();
        registerRenderers();
        registerArmorRenderers();
        registerMenuScreens();
        setupParticles();
        ClientLayerRegistry.register();
        ClientEvents.getInstance().registerFabricHandlers();
        initRainbowBuffers();
    }

    private static void registerArmorRenderers() {
        ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
            if (!(stack.getItem() instanceof ItemModArmor modArmor) || !(contextModel instanceof net.minecraft.client.model.Model baseModel)) {
                return;
            }
            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            if (equippable == null || equippable.slot() != slot) {
                return;
            }
            CustomArmorRenderProperties props = new CustomArmorRenderProperties();
            EquipmentClientInfo.LayerType layerType = slot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
            HumanoidModel<?> humanoidModel;
            if (slot == EquipmentSlot.LEGS && (stack.is(AMItemRegistry.CENTIPEDE_LEGGINGS) || stack.is(AMItemRegistry.EMU_LEGGINGS))
                    && baseModel instanceof HumanoidModel<?> posedBase) {
                humanoidModel = getVanillaLeggingsArmorModel();
                copyHumanoidPose(posedBase, humanoidModel);
            } else {
                net.minecraft.client.model.Model model = props.getGenericArmorModel(stack, layerType, baseModel);
                if (!(model instanceof HumanoidModel<?> resolved)) {
                    return;
                }
                humanoidModel = resolved;
            }
            Identifier texture = modArmor.getArmorTexture(stack, null, slot, null, slot == EquipmentSlot.LEGS);
            if (texture == null) {
                return;
            }
            vertexConsumers.submitCustomGeometry(matrices, RenderTypes.armorCutoutNoCull(texture), (pose, vc) -> {
                PoseStack stackPose = new PoseStack();
                stackPose.pushPose();
                stackPose.last().set(pose);
                renderHumanoidArmorPart(stackPose, vc, light, humanoidModel, slot, stack);
                stackPose.popPose();
            });
            if (stack.hasFoil()) {
                vertexConsumers.submitCustomGeometry(matrices, RenderTypes.armorEntityGlint(), (pose, vc) -> {
                    PoseStack stackPose = new PoseStack();
                    stackPose.pushPose();
                    stackPose.last().set(pose);
                    renderHumanoidArmorPart(stackPose, vc, light, humanoidModel, slot, stack);
                    stackPose.popPose();
                });
            }
        }, AMItemRegistry.ROADDRUNNER_BOOTS,
                AMItemRegistry.CROCODILE_CHESTPLATE,
                AMItemRegistry.CENTIPEDE_LEGGINGS,
                AMItemRegistry.MOOSE_HEADGEAR,
                AMItemRegistry.FRONTIER_CAP,
                AMItemRegistry.SOMBRERO,
                AMItemRegistry.SPIKED_TURTLE_SHELL,
                AMItemRegistry.EMU_LEGGINGS,
                AMItemRegistry.FEDORA,
                AMItemRegistry.FROSTSTALKER_HELMET,
                AMItemRegistry.ROCKY_CHESTPLATE,
                AMItemRegistry.FLYING_FISH_BOOTS,
                AMItemRegistry.NOVELTY_HAT,
                AMItemRegistry.UNSETTLING_KIMONO);
    }

    private static void renderHumanoidArmorPart(PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer consumer, int light, HumanoidModel<?> model, EquipmentSlot slot, net.minecraft.world.item.ItemStack armorStack) {
        boolean head = model.head.visible;
        boolean hat = model.hat.visible;
        boolean body = model.body.visible;
        boolean rightArm = model.rightArm.visible;
        boolean leftArm = model.leftArm.visible;
        boolean rightLeg = model.rightLeg.visible;
        boolean leftLeg = model.leftLeg.visible;
        applyArmorSlotVisibility(model, slot, armorStack);
        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, -1);
        model.head.visible = head;
        model.hat.visible = hat;
        model.body.visible = body;
        model.rightArm.visible = rightArm;
        model.leftArm.visible = leftArm;
        model.rightLeg.visible = rightLeg;
        model.leftLeg.visible = leftLeg;
    }

    private static void applyArmorSlotVisibility(HumanoidModel<?> model, EquipmentSlot slot, net.minecraft.world.item.ItemStack armorStack) {
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
                model.hat.visible = !armorStack.is(AMItemRegistry.NOVELTY_HAT);
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
            default -> {
            }
        }
    }

    private static void registerItemModelWrappers() {
        Identifier ghostlyPickaxeItem = BuiltInRegistries.ITEM.getKey(AMItemRegistry.GHOSTLY_PICKAXE);
        ModelLoadingPlugin.register(pluginContext -> pluginContext.modifyItemModelAfterBake().register((model, context) -> {
            if (ghostlyPickaxeItem.equals(context.itemId())) {
                return new GhostlyPickaxeItemModel(model);
            }
            return model;
        }));
    }

    private static void registerClientModelCodecs() {
        try {
            putLateBoundCodec(ItemTintSources.class, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "spawn_egg_layer"), AMSpawnEggTintSource.MAP_CODEC);
            putLateBoundCodec(ConditionalItemModelProperties.class, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "blood_sprayer_empty"), BloodSprayerEmptyProperty.MAP_CODEC);
            putLateBoundCodec(ConditionalItemModelProperties.class, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "hemolymph_blaster_empty"), HemolymphBlasterEmptyProperty.MAP_CODEC);
            putLateBoundCodec(ConditionalItemModelProperties.class, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "tendon_whip_active"), TendonWhipActiveProperty.MAP_CODEC);
            putLateBoundCodec(ConditionalItemModelProperties.class, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "pupfish_locator_in_chunk"), PupfishLocatorInChunkProperty.MAP_CODEC);
            putLateBoundCodec(ConditionalItemModelProperties.class, Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "sombrero_silly"), SombreroSillyProperty.MAP_CODEC);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register Alex's Mobs client model codecs", e);
        }
    }

    private static void putLateBoundCodec(Class<?> ownerClass, Identifier id, MapCodec<?> codec) throws ReflectiveOperationException {
        Field mapperField = ownerClass.getDeclaredField("ID_MAPPER");
        mapperField.setAccessible(true);
        Object mapper = mapperField.get(null);
        mapper.getClass().getMethod("put", Object.class, Object.class).invoke(mapper, id, codec);
    }

    private void initRainbowBuffers() {
        // BufferBuilder API changed in 1.21.1 - needs ByteBufferBuilder, Mode, and VertexFormat
        // Temporarily commented out until proper implementation
        // Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.COMBJELLY_RAINBOW_GLINT, new BufferBuilder(new ByteBufferBuilder(AMRenderTypes.COMBJELLY_RAINBOW_GLINT.bufferSize()), VertexFormat.Mode.QUADS, AMRenderTypes.COMBJELLY_RAINBOW_GLINT.format()));
        // Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.VOID_WORM_PORTAL_OVERLAY, new BufferBuilder(new ByteBufferBuilder(AMRenderTypes.VOID_WORM_PORTAL_OVERLAY.bufferSize()), VertexFormat.Mode.QUADS, AMRenderTypes.VOID_WORM_PORTAL_OVERLAY.format()));
        // Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.STATIC_PORTAL, new BufferBuilder(new ByteBufferBuilder(AMRenderTypes.STATIC_PORTAL.bufferSize()), VertexFormat.Mode.QUADS, AMRenderTypes.STATIC_PORTAL.format()));
        // Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.STATIC_PARTICLE, new BufferBuilder(new ByteBufferBuilder(AMRenderTypes.STATIC_PARTICLE.bufferSize()), VertexFormat.Mode.QUADS, AMRenderTypes.STATIC_PARTICLE.format()));
        // Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.STATIC_ENTITY, new BufferBuilder(new ByteBufferBuilder(AMRenderTypes.STATIC_ENTITY.bufferSize()), VertexFormat.Mode.QUADS, AMRenderTypes.STATIC_ENTITY.format()));
        initializedRainbowBuffers = true;
    }


    public void openBookGUI(ItemStack itemStackIn) {
        Minecraft.getInstance().setScreenAndShow(new GUIAnimalDictionary(itemStackIn));
    }

    public void openBookGUI(ItemStack itemStackIn, String page) {
        Minecraft.getInstance().setScreenAndShow(new GUIAnimalDictionary(itemStackIn, page));
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }
    public Object getArmorModel(int armorId, LivingEntity entity) {
        switch (armorId) {
            /*
            case 0:
                return ROADRUNNER_BOOTS_MODEL;
            case 1:
                return MOOSE_HEADGEAR_MODEL;
            case 2:
                return FRONTIER_CAP_MODEL.withAnimations(entity);
            case 3:
                return SOMBRERO_MODEL;
            case 4:
                return SPIKED_TURTLE_SHELL_MODEL;
            case 5:
                return FEDORA_MODEL;
            case 6:
                return ELYTRA_MODEL.withAnimations(entity);

             */
            default:
                return null;
        }
    }
    public void onEntityStatus(Entity entity, byte updateKind) {
        if (updateKind == 67) {
            if (entity instanceof EntityCockroach && entity.isAlive()) {
                SoundLaCucaracha sound;
                if (COCKROACH_SOUND_MAP.get(entity.getId()) == null) {
                    sound = new SoundLaCucaracha((EntityCockroach) entity);
                    COCKROACH_SOUND_MAP.put(entity.getId(), sound);
                } else {
                    sound = COCKROACH_SOUND_MAP.get(entity.getId());
                }
                if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound() && sound.isOnlyCockroach()) {
                    Minecraft.getInstance().getSoundManager().play(sound);
                }
            } else if (entity instanceof EntityVoidWorm && entity.isAlive()) {
                final float f2 = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);
                if (f2 <= 0) {
                    WORMBOSS_SOUND_MAP.clear();
                } else {
                    SoundWormBoss sound;
                    if (WORMBOSS_SOUND_MAP.get(entity.getId()) == null) {
                        sound = new SoundWormBoss((EntityVoidWorm) entity);
                        WORMBOSS_SOUND_MAP.put(entity.getId(), sound);
                    } else {
                        sound = WORMBOSS_SOUND_MAP.get(entity.getId());
                    }
                    if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.isNearest()) {
                        Minecraft.getInstance().getSoundManager().play(sound);
                    }
                }
            } else if (entity instanceof EntityGrizzlyBear && entity.isAlive()) {
                SoundBearMusicBox sound;
                if (BEAR_MUSIC_BOX_SOUND_MAP.get(entity.getId()) == null) {
                    sound = new SoundBearMusicBox((EntityGrizzlyBear) entity);
                    BEAR_MUSIC_BOX_SOUND_MAP.put(entity.getId(), sound);
                } else {
                    sound = BEAR_MUSIC_BOX_SOUND_MAP.get(entity.getId());
                }
                if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound() && sound.isOnlyMusicBox()) {
                    Minecraft.getInstance().getSoundManager().play(sound);
                }
            } else if (entity instanceof EntityBlueJay && entity.isAlive()) {
                singingBlueJayId = entity.getId();
            }
        }
        if (entity instanceof EntityBlueJay && entity.isAlive() && updateKind == 68) {
            singingBlueJayId = -1;
        }
    }

    public void updateBiomeVisuals(int x, int z) {
        var client = Minecraft.getInstance();
        if (client.levelRenderer != null) {
            client.levelRenderer.setBlocksDirty(x - 32, 0, z - 32, x + 32, 255, z + 32);
        }
    }

    private static void setupParticles() {
        ParticleProviderRegistry registry = ParticleProviderRegistry.getInstance();
        AlexsMobs.LOGGER.debug("Registered particle factories");
        registry.register(AMParticleRegistry.GUSTER_SAND_SPIN, ParticleGusterSandSpin.Factory::new);
        registry.register(AMParticleRegistry.GUSTER_SAND_SHOT, ParticleGusterSandShot.Factory::new);
        registry.register(AMParticleRegistry.GUSTER_SAND_SPIN_RED, ParticleGusterSandSpin.FactoryRed::new);
        registry.register(AMParticleRegistry.GUSTER_SAND_SHOT_RED, ParticleGusterSandShot.FactoryRed::new);
        registry.register(AMParticleRegistry.GUSTER_SAND_SPIN_SOUL, ParticleGusterSandSpin.FactorySoul::new);
        registry.register(AMParticleRegistry.GUSTER_SAND_SHOT_SOUL, ParticleGusterSandShot.FactorySoul::new);
        registry.register(AMParticleRegistry.HEMOLYMPH, ParticleHemolymph.Factory::new);
        registry.register(AMParticleRegistry.PLATYPUS_SENSE, ParticlePlatypus.Factory::new);
        registry.register(AMParticleRegistry.WHALE_SPLASH, ParticleWhaleSplash.Factory::new);
        registry.register(AMParticleRegistry.DNA, ParticleDna.Factory::new);
        registry.register(AMParticleRegistry.SHOCKED, ParticleSimpleHeart.Factory::new);
        registry.register(AMParticleRegistry.WORM_PORTAL, ParticleWormPortal.Factory::new);
        registry.register(AMParticleRegistry.INVERT_DIG, ParticleInvertDig.Factory::new);
        registry.register(AMParticleRegistry.TEETH_GLINT, ParticleTeethGlint.Factory::new);
        registry.register(AMParticleRegistry.SMELLY, ParticleSmelly.Factory::new);
        registry.register(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION, ParticleBunfungusTransformation.Factory::new);
        registry.register(AMParticleRegistry.FUNGUS_BUBBLE, ParticleFungusBubble.Factory::new);
        registry.register(AMParticleRegistry.BEAR_FREDDY, new ParticleBearFreddy.Factory());
        registry.register(AMParticleRegistry.SUNBIRD_FEATHER, ParticleSunbirdFeather.Factory::new);
        registry.register(AMParticleRegistry.STATIC_SPARK, new ParticleStaticSpark.Factory());
        registry.register(AMParticleRegistry.SKULK_BOOM, new ParticleSkulkBoom.Factory());
        registry.register(AMParticleRegistry.BIRD_SONG, ParticleBirdSong.Factory::new);
    }

    /** Preload standalone particle textures outside the framegraph so 26.1 does not upload mid-render-pass. */
    public static void preloadRenderTextures() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        minecraft.getTextureManager().getTexture(ParticleSkulkBoom.TEXTURE);
        AMRenderTypes.getSkulkBoom();
    }


    public void setRenderViewEntity(Entity entity) {
        prevPOV = Minecraft.getInstance().options.getCameraType();
        Minecraft.getInstance().setCameraEntity(entity);
        Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
    }

    public void resetRenderViewEntity() {
        Minecraft.getInstance().setCameraEntity(Minecraft.getInstance().player);
    }

    public int getPreviousPOV() {
        return prevPOV.ordinal();
    }

    public boolean isFarFromCamera(double x, double y, double z) {
        Minecraft lvt_1_1_ = Minecraft.getInstance();
        return lvt_1_1_.gameRenderer.getMainCamera().position().distanceToSqr(x, y, z) >= 256.0D;
    }

    public void resetVoidPortalCreation(Player player) {

    }


    @Override
    public Object getISTERProperties() {
        return new AMItemRenderProperties();
    }

    @Override
    public Object getArmorRenderProperties() {
        return new CustomArmorRenderProperties();
    }

    public void spawnSpecialParticle(int type) {
        if (type == 0) {
            Minecraft.getInstance().level.addParticle(AMParticleRegistry.BEAR_FREDDY, Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ(), 0, 0, 0);
        }
    }

    public void processVisualFlag(Entity entity, int flag) {
        if (entity == Minecraft.getInstance().player && flag == 87) {
            ClientEvents.renderStaticScreenFor = 60;
        }
    }

    public void setPupfishChunkForItem(int chunkX, int chunkZ) {
        this.pupfishChunkX = chunkX;
        this.pupfishChunkZ = chunkZ;
        pupfishChunkXModel = chunkX;
        pupfishChunkZModel = chunkZ;
    }

    public void setDisplayTransmuteResult(int slot, ItemStack stack){
        transmuteStacks[Mth.clamp(slot, 0, 2)] = stack;
    }

    public ItemStack getDisplayTransmuteResult(int slot){
        ItemStack stack = transmuteStacks[Mth.clamp(slot, 0, 2)];
        return stack == null ? ItemStack.EMPTY : stack;
    }

    public int getSingingBlueJayId() {
        return singingBlueJayId;
    }

    private record BloodSprayerEmptyProperty() implements ConditionalItemModelProperty {
        static final MapCodec<BloodSprayerEmptyProperty> MAP_CODEC = MapCodec.unit(new BloodSprayerEmptyProperty());

        @Override
        public boolean get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext ctx) {
            return !ItemBloodSprayer.isUsable(stack)
                    || entity instanceof Player p && p.getCooldowns().isOnCooldown(stack);
        }

        @Override
        public MapCodec<? extends ConditionalItemModelProperty> type() {
            return MAP_CODEC;
        }
    }

    private record HemolymphBlasterEmptyProperty() implements ConditionalItemModelProperty {
        static final MapCodec<HemolymphBlasterEmptyProperty> MAP_CODEC = MapCodec.unit(new HemolymphBlasterEmptyProperty());

        @Override
        public boolean get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext ctx) {
            return !ItemHemolymphBlaster.isUsable(stack)
                    || entity instanceof Player p && p.getCooldowns().isOnCooldown(stack);
        }

        @Override
        public MapCodec<? extends ConditionalItemModelProperty> type() {
            return MAP_CODEC;
        }
    }

    private record TendonWhipActiveProperty() implements ConditionalItemModelProperty {
        static final MapCodec<TendonWhipActiveProperty> MAP_CODEC = MapCodec.unit(new TendonWhipActiveProperty());

        @Override
        public boolean get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext ctx) {
            return ItemTendonWhip.isActive(stack, entity);
        }

        @Override
        public MapCodec<? extends ConditionalItemModelProperty> type() {
            return MAP_CODEC;
        }
    }

    private record PupfishLocatorInChunkProperty() implements ConditionalItemModelProperty {
        static final MapCodec<PupfishLocatorInChunkProperty> MAP_CODEC = MapCodec.unit(new PupfishLocatorInChunkProperty());

        @Override
        public boolean get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext ctx) {
            int x = ClientProxy.pupfishChunkXModel * 16;
            int z = ClientProxy.pupfishChunkZModel * 16;
            return entity != null && entity.getX() >= x && entity.getX() <= x + 16 && entity.getZ() >= z && entity.getZ() <= z + 16;
        }

        @Override
        public MapCodec<? extends ConditionalItemModelProperty> type() {
            return MAP_CODEC;
        }
    }

    private record SombreroSillyProperty() implements ConditionalItemModelProperty {
        static final MapCodec<SombreroSillyProperty> MAP_CODEC = MapCodec.unit(new SombreroSillyProperty());

        @Override
        public boolean get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext ctx) {
            return AlexsMobs.isAprilFools();
        }

        @Override
        public MapCodec<? extends ConditionalItemModelProperty> type() {
            return MAP_CODEC;
        }
    }

}
