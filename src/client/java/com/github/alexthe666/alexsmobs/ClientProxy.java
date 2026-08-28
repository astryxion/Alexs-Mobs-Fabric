package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.ClientLayerRegistry;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.alexsmobs.client.gui.GUITransmutationTable;
import com.github.alexthe666.alexsmobs.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.client.particle.*;
import com.github.alexthe666.alexsmobs.client.render.*;
import com.github.alexthe666.alexsmobs.client.render.item.AMItemRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.item.GhostlyPickaxeBakedModel;
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
import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeableLeatherItem;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientProxy extends CommonProxy {

    public static final Int2ObjectMap<SoundBearMusicBox> BEAR_MUSIC_BOX_SOUND_MAP = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<SoundLaCucaracha> COCKROACH_SOUND_MAP = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<SoundWormBoss> WORMBOSS_SOUND_MAP = new Int2ObjectOpenHashMap<>();
    public static final List<UUID> currentUnrenderedEntities = new ArrayList<>();
    public static int voidPortalCreationTime = 0;
    public CameraType prevPOV = CameraType.FIRST_PERSON;
    public boolean initializedRainbowBuffers = false;
    private int pupfishChunkX = 0;
    private int pupfishChunkZ = 0;
    private int singingBlueJayId = -1;
    private final ItemStack[] transmuteStacks = new ItemStack[3];
    /** Vanilla inner leggings shell — same model HumanoidArmorLayer uses for the leggings slot (NeoForge default). */
    private static HumanoidModel<?> vanillaLeggingsArmorModel;

    private static HumanoidModel<LivingEntity> getVanillaLeggingsArmorModel() {
        if (vanillaLeggingsArmorModel == null) {
            vanillaLeggingsArmorModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        }
        @SuppressWarnings("unchecked")
        HumanoidModel<LivingEntity> model = (HumanoidModel<LivingEntity>) vanillaLeggingsArmorModel;
        return model;
    }

    public void init() {
        // Fabric: client event registration done in clientInit / Fabric event callbacks
    }

    public void clientInit() {
        AMModelLayers.register();
        ClientEvents.register();
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::onClientTick);
        if (AMItemRegistry.STRADDLEBOARD != null) {
            net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register((stack, tint) -> {
                if (tint < 1) return -1;
                if (stack.getItem() instanceof DyeableLeatherItem dyeable) {
                    return dyeable.hasCustomColor(stack) ? dyeable.getColor(stack) : 0xA06540;
                }
                return 0xA06540;
            }, AMItemRegistry.STRADDLEBOARD);
        }
        net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.register((state, world, pos, tint) -> world != null && pos != null ? RainbowUtil.calculateGlassColor(pos) : -1, AMBlockRegistry.RAINBOW_GLASS);
        // NeoForge assigns translucent terrain layer from HalfTransparentBlock; Fabric needs explicit registration.
        net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock(AMBlockRegistry.RAINBOW_GLASS, RenderType.translucent());
        net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock(AMBlockRegistry.CAPSID, RenderType.translucent());
        net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock(AMBlockRegistry.ENDER_RESIDUE, RenderType.translucent());
        net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock(AMBlockRegistry.TRANSMUTATION_TABLE, RenderType.translucent());
        net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock(AMBlockRegistry.SKUNK_SPRAY, RenderType.translucent());
        net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock(AMBlockRegistry.BANANA_SLUG_SLIME_BLOCK, RenderType.translucent());
        net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock(AMBlockRegistry.CRYSTALIZED_BANANA_SLUG_MUCUS, RenderType.translucent());
        setupParticles();
        net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.register(pluginContext ->
                pluginContext.modifyModelAfterBake().register((model, context) -> {
                    if (context.id() != null && context.id().toString().contains("alexsmobs:ghostly_pickaxe")) {
                        return new GhostlyPickaxeBakedModel(model);
                    }
                    return model;
                }));
        initRainbowBuffers();
        ItemRenderer itemRendererIn = Minecraft.getInstance().getItemRenderer();
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.GRIZZLY_BEAR, RenderGrizzlyBear::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ROADRUNNER, RenderRoadrunner::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.BONE_SERPENT, RenderBoneSerpent::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.BONE_SERPENT_PART, RenderBoneSerpentPart::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.GAZELLE, RenderGazelle::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CROCODILE, RenderCrocodile::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.FLY, RenderFly::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.HUMMINGBIRD, RenderHummingbird::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ORCA, RenderOrca::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SUNBIRD, RenderSunbird::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.GORILLA, RenderGorilla::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CRIMSON_MOSQUITO, RenderCrimsonMosquito::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MOSQUITO_SPIT, RenderMosquitoSpit::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.RATTLESNAKE, RenderRattlesnake::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ENDERGRADE, RenderEndergrade::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.HAMMERHEAD_SHARK, RenderHammerheadShark::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SHARK_TOOTH_ARROW, RenderSharkToothArrow::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.LOBSTER, RenderLobster::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.KOMODO_DRAGON, RenderKomodoDragon::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CAPUCHIN_MONKEY, RenderCapuchinMonkey::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.TOSSED_ITEM, RenderTossedItem::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CENTIPEDE_HEAD, RenderCentipedeHead::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CENTIPEDE_BODY, RenderCentipedeBody::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CENTIPEDE_TAIL, RenderCentipedeTail::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.WARPED_TOAD, RenderWarpedToad::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MOOSE, RenderMoose::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MIMICUBE, RenderMimicube::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.RACCOON, RenderRaccoon::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.BLOBFISH, RenderBlobfish::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SEAL, RenderSeal::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.COCKROACH, RenderCockroach::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.COCKROACH_EGG, (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SHOEBILL, RenderShoebill::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ELEPHANT, RenderElephant::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SOUL_VULTURE, RenderSoulVulture::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SNOW_LEOPARD, RenderSnowLeopard::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SPECTRE, RenderSpectre::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CROW, RenderCrow::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, RenderAlligatorSnappingTurtle::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MUNGUS, RenderMungus::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MANTIS_SHRIMP, RenderMantisShrimp::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.GUSTER, RenderGuster::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SAND_SHOT, RenderSandShot::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.GUST, RenderGust::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.WARPED_MOSCO, RenderWarpedMosco::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.HEMOLYMPH, RenderHemolymph::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.STRADDLER, RenderStraddler::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.STRADPOLE, RenderStradpole::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.STRADDLEBOARD, RenderStraddleboard::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.EMU, RenderEmu::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.EMU_EGG, (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.PLATYPUS, RenderPlatypus::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.DROPBEAR, RenderDropBear::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.TASMANIAN_DEVIL, RenderTasmanianDevil::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.KANGAROO, RenderKangaroo::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CACHALOT_WHALE, RenderCachalotWhale::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CACHALOT_ECHO, RenderCachalotEcho::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.LEAFCUTTER_ANT, RenderLeafcutterAnt::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ENDERIOPHAGE, RenderEnderiophage::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ENDERIOPHAGE_ROCKET, RenderEnderiophageRocket::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.BALD_EAGLE, RenderBaldEagle::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.TIGER, RenderTiger::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.TARANTULA_HAWK, RenderTarantulaHawk::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.VOID_WORM, RenderVoidWormHead::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.VOID_WORM_PART, RenderVoidWormBody::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.VOID_WORM_SHOT, RenderVoidWormShot::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.VOID_PORTAL, RenderVoidPortal::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.FRILLED_SHARK, RenderFrilledShark::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MIMIC_OCTOPUS, RenderMimicOctopus::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SEAGULL, RenderSeagull::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.FROSTSTALKER, RenderFroststalker::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ICE_SHARD, RenderIceShard::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.TUSKLIN, RenderTusklin::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.LAVIATHAN, RenderLaviathan::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.COSMAW, RenderCosmaw::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.TOUCAN, RenderToucan::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MANED_WOLF, RenderManedWolf::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ANACONDA, RenderAnaconda::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ANACONDA_PART, RenderAnacondaPart::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.VINE_LASSO, RenderVineLasso::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ANTEATER, RenderAnteater::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.ROCKY_ROLLER, RenderRockyRoller::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.FLUTTER, RenderFlutter::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.POLLEN_BALL, RenderPollenBall::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.GELADA_MONKEY, RenderGeladaMonkey::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.JERBOA, RenderJerboa::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.TERRAPIN, RenderTerrapin::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.COMB_JELLY, RenderCombJelly::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.COSMIC_COD, RenderCosmicCod::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.BUNFUNGUS, RenderBunfungus::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.BISON, RenderBison::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.GIANT_SQUID, RenderGiantSquid::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SQUID_GRAPPLE, RenderSquidGrapple::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SEA_BEAR, RenderSeaBear::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.DEVILS_HOLE_PUPFISH, RenderDevilsHolePupfish::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CATFISH, RenderCatfish::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.FLYING_FISH, RenderFlyingFish::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SKELEWAG, RenderSkelewag::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.RAIN_FROG, RenderRainFrog::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.POTOO, RenderPotoo::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MUDSKIPPER, RenderMudskipper::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MUD_BALL, RenderMudBall::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.RHINOCEROS, RenderRhinoceros::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SUGAR_GLIDER, RenderSugarGlider::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.FARSEER, RenderFarseer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SKREECHER, RenderSkreecher::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.UNDERMINER, RenderUnderminer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MURMUR, RenderMurmurBody::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.MURMUR_HEAD, RenderMurmurHead::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.TENDON_SEGMENT, RenderTendonSegment::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.SKUNK, RenderSkunk::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.FART, RenderFart::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.BANANA_SLUG, RenderBananaSlug::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.BLUE_JAY, RenderBlueJay::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.CAIMAN, RenderCaiman::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(AMEntityRegistry.TRIOPS, RenderTriops::new);
        try {
            ItemProperties.register(AMItemRegistry.BLOOD_SPRAYER, new ResourceLocation("alexsmobs", "empty"), (stack, p_239428_1_, p_239428_2_, j) -> {
                return !ItemBloodSprayer.isUsable(stack) || p_239428_2_ instanceof Player && ((Player) p_239428_2_).getCooldowns().isOnCooldown(AMItemRegistry.BLOOD_SPRAYER) ? 1.0F : 0.0F;
            });
            ItemProperties.register(AMItemRegistry.HEMOLYMPH_BLASTER, new ResourceLocation("alexsmobs", "empty"), (stack, p_239428_1_, p_239428_2_, j) -> {
                return !ItemHemolymphBlaster.isUsable(stack) || p_239428_2_ instanceof Player && ((Player) p_239428_2_).getCooldowns().isOnCooldown(AMItemRegistry.HEMOLYMPH_BLASTER) ? 1.0F : 0.0F;
            });
            ItemProperties.register(AMItemRegistry.TARANTULA_HAWK_ELYTRA, new ResourceLocation("alexsmobs", "broken"), (stack, p_239428_1_, p_239428_2_, j) -> {
                return ItemTarantulaHawkElytra.isUsable(stack) ? 0.0F : 1.0F;
            });
            ItemProperties.register(AMItemRegistry.SHIELD_OF_THE_DEEP, new ResourceLocation("alexsmobs", "blocking"), (stack, p_239421_1_, p_239421_2_, j) -> {
                return p_239421_2_ != null && p_239421_2_.isUsingItem() && p_239421_2_.getUseItem() == stack ? 1.0F : 0.0F;
            });
            ItemProperties.register(AMItemRegistry.SOMBRERO, new ResourceLocation("alexsmobs", "silly"), (stack, p_239421_1_, p_239421_2_, j) -> {
                return AlexsMobs.isAprilFools() ? 1.0F : 0.0F;
            });
            ItemProperties.register(AMItemRegistry.TENDON_WHIP, new ResourceLocation("alexsmobs", "active"), (stack, p_239421_1_, holder, j) -> {
                return ItemTendonWhip.isActive(stack, holder) ? 1.0F : 0.0F;
            });
            ItemProperties.register(AMItemRegistry.PUPFISH_LOCATOR, new ResourceLocation("alexsmobs", "in_chunk"), (stack, world, entity, j) -> {
                int x = pupfishChunkX * 16;
                int z = pupfishChunkZ * 16;
                if (entity != null && entity.getX() >= x && entity.getX() <= x + 16 && entity.getZ() >= z && entity.getZ() <= z + 16) {
                    return 1.0F;
                }
                return 0.0F;
            });
            ItemProperties.register(AMItemRegistry.SKELEWAG_SWORD, new ResourceLocation("alexsmobs", "blocking"), (stack, p_239421_1_, p_239421_2_, j) -> {
                return p_239421_2_ != null && p_239421_2_.isUsingItem() && p_239421_2_.getUseItem() == stack ? 1.0F : 0.0F;
            });
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Could not load item models for weapons");
        }
        net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.register(AMTileEntityRegistry.CAPSID, RenderCapsid::new);
        net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.register(AMTileEntityRegistry.VOID_WORM_BEAK, RenderVoidWormBeak::new);
        net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.register(AMTileEntityRegistry.TRANSMUTATION_TABLE, RenderTransmutationTable::new);
        //TODO reimplement end pirate block entity renderers
        MenuScreens.register(AMMenuRegistry.TRANSMUTATION_TABLE, GUITransmutationTable::new);
        // Forge ISTER replacement: custom in-hand / GUI rendering for items that use AMItemstackRenderer
        registerBuiltinItemRenderer(AMBlockRegistry.TRANSMUTATION_TABLE.asItem());
        registerBuiltinItemRenderer(AMItemRegistry.SHATTERED_DIMENSIONAL_CARVER);
        registerBuiltinItemRenderer(AMItemRegistry.SHIELD_OF_THE_DEEP);
        registerBuiltinItemRenderer(AMItemRegistry.MYSTERIOUS_WORM);
        registerBuiltinItemRenderer(AMItemRegistry.FALCONRY_GLOVE);
        registerBuiltinItemRenderer(AMItemRegistry.VINE_LASSO);
        registerBuiltinItemRenderer(AMItemRegistry.SKELEWAG_SWORD);
        registerBuiltinItemRenderer(AMItemRegistry.STINK_RAY);
        registerBuiltinItemRenderer(AMItemRegistry.TAB_ICON);
        // Forge armor render hook replacement: all mod armor uses custom texture (and model where defined) via Fabric ArmorRenderer
        net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer.register(
                (matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
                    ResourceLocation texture = getModArmorTexture(stack, entity, slot);
                    if (texture == null) return;
                    net.minecraft.client.model.HumanoidModel<?> model;
                    if (slot == EquipmentSlot.LEGS && (stack.getItem() == AMItemRegistry.CENTIPEDE_LEGGINGS || stack.getItem() == AMItemRegistry.EMU_LEGGINGS)) {
                        model = getVanillaLeggingsArmorModel();
                        contextModel.copyPropertiesTo((HumanoidModel<LivingEntity>) model);
                    } else {
                        CustomArmorRenderProperties.initializeModels();
                        CustomArmorRenderProperties armorProps = new CustomArmorRenderProperties();
                        model = armorProps.getHumanoidArmorModel(entity, stack, slot, contextModel);
                        if (model != contextModel) {
                            contextModel.copyPropertiesTo((HumanoidModel<LivingEntity>) model);
                        }
                    }
                    model.setAllVisible(false);
                    switch (slot) {
                        case HEAD -> { model.head.visible = true; model.hat.visible = true; }
                        case CHEST -> {
                            model.body.visible = true;
                            model.leftArm.visible = true;
                            model.rightArm.visible = true;
                        }
                        case LEGS -> {
                            model.body.visible = true;
                            model.leftLeg.visible = true;
                            model.rightLeg.visible = true;
                        }
                        case FEET -> { model.leftLeg.visible = true; model.rightLeg.visible = true; }
                        default -> { }
                    }
                    net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, texture);
                },
                AMItemRegistry.TARANTULA_HAWK_ELYTRA,
                AMItemRegistry.ROADDRUNNER_BOOTS, AMItemRegistry.CROCODILE_CHESTPLATE, AMItemRegistry.CENTIPEDE_LEGGINGS,
                AMItemRegistry.MOOSE_HEADGEAR, AMItemRegistry.FRONTIER_CAP, AMItemRegistry.SOMBRERO,
                AMItemRegistry.SPIKED_TURTLE_SHELL, AMItemRegistry.EMU_LEGGINGS, AMItemRegistry.FEDORA,
                AMItemRegistry.FROSTSTALKER_HELMET, AMItemRegistry.ROCKY_CHESTPLATE, AMItemRegistry.FLYING_FISH_BOOTS,
                AMItemRegistry.NOVELTY_HAT, AMItemRegistry.UNSETTLING_KIMONO);
    }

    /** Returns the armor texture for mod armor items (getArmorTexture path); null if not a mod armor with custom texture. */
    private static ResourceLocation getModArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot) {
        if (stack.getItem() instanceof ItemModArmor armor) {
            String path = armor.getArmorTexture(stack, entity, slot, null);
            return path != null ? new ResourceLocation(path) : null;
        }
        if (stack.getItem() == AMItemRegistry.TARANTULA_HAWK_ELYTRA) {
            return new ResourceLocation("alexsmobs:textures/armor/tarantula_hawk_elytra.png");
        }
        return null;
    }

    private void initRainbowBuffers() {
        net.minecraft.client.renderer.RenderBuffers buffers = Minecraft.getInstance().renderBuffers();
        if (buffers == null) return;
        try {
            java.lang.reflect.Field mapField = net.minecraft.client.renderer.RenderBuffers.class.getDeclaredField("fixedBuffers");
            mapField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<net.minecraft.client.renderer.RenderType, BufferBuilder> map = (java.util.Map<net.minecraft.client.renderer.RenderType, BufferBuilder>) mapField.get(buffers);
            putBuffer(map, AMRenderTypes.COMBJELLY_RAINBOW_GLINT);
            putBuffer(map, AMRenderTypes.VOID_WORM_PORTAL_OVERLAY);
            putBuffer(map, AMRenderTypes.STATIC_PORTAL);
            putBuffer(map, AMRenderTypes.STATIC_PARTICLE);
            putBuffer(map, AMRenderTypes.STATIC_ENTITY);
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Could not register custom render type buffers", e);
        }
        initializedRainbowBuffers = true;
    }

    private static void putBuffer(java.util.Map<net.minecraft.client.renderer.RenderType, BufferBuilder> map, net.minecraft.client.renderer.RenderType renderType) {
        map.put(renderType, new BufferBuilder(renderType.bufferSize()));
    }

    public void openBookGUI(ItemStack itemStackIn) {
        Minecraft.getInstance().setScreen(new GUIAnimalDictionary(itemStackIn));
    }

    public void openBookGUI(ItemStack itemStackIn, String page) {
        Minecraft.getInstance().setScreen(new GUIAnimalDictionary(itemStackIn, page));
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
        Minecraft.getInstance().levelRenderer.setBlocksDirty(x - 32, 0, z - 32, x + 32, 255, z + 32);
    }

    private static void setupParticles() {
        AlexsMobs.LOGGER.debug("Registered particle factories");
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.GUSTER_SAND_SPIN, ParticleGusterSandSpin.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.GUSTER_SAND_SHOT, ParticleGusterSandShot.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.GUSTER_SAND_SPIN_RED, ParticleGusterSandSpin.FactoryRed::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.GUSTER_SAND_SHOT_RED, ParticleGusterSandShot.FactoryRed::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.GUSTER_SAND_SPIN_SOUL, ParticleGusterSandSpin.FactorySoul::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.GUSTER_SAND_SHOT_SOUL, ParticleGusterSandShot.FactorySoul::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.HEMOLYMPH, ParticleHemolymph.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.PLATYPUS_SENSE, ParticlePlatypus.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.WHALE_SPLASH, ParticleWhaleSplash.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.DNA, ParticleDna.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.SHOCKED, ParticleSimpleHeart.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.WORM_PORTAL, ParticleWormPortal.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.INVERT_DIG, ParticleInvertDig.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.TEETH_GLINT, ParticleTeethGlint.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.SMELLY, ParticleSmelly.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION, ParticleBunfungusTransformation.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.FUNGUS_BUBBLE, ParticleFungusBubble.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.BEAR_FREDDY, new ParticleBearFreddy.Factory());
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.SUNBIRD_FEATHER, ParticleSunbirdFeather.Factory::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.STATIC_SPARK, new ParticleStaticSpark.Factory());
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.SKULK_BOOM, new ParticleSkulkBoom.Factory());
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(AMParticleRegistry.BIRD_SONG, ParticleBirdSong.Factory::new);
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
        return lvt_1_1_.gameRenderer.getMainCamera().getPosition().distanceToSqr(x, y, z) >= 256.0D;
    }

    public void resetVoidPortalCreation(Player player) {

    }

    /** Fabric: layer definitions are set up per-renderer; no central event. */
    public void onRegisterEntityRenders() {
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

    private static void registerBuiltinItemRenderer(net.minecraft.world.item.Item item) {
        net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE.register(
                item,
                (stack, mode, matrices, vertexConsumers, light, overlay) ->
                        AMItemRenderProperties.getRenderer().renderByItem(stack, mode, matrices, vertexConsumers, light, overlay));
    }

}
