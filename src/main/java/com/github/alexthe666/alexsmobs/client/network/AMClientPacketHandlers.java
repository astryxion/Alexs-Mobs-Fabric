package com.github.alexthe666.alexsmobs.client.network;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.network.*;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.Vec3;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;

/**
 * Client-side packet handlers. This class is only loaded on the client.
 */
@Environment(EnvType.CLIENT)
public final class AMClientPacketHandlers {

    private AMClientPacketHandlers() {}

    public static void handleSyncEntityPos(MessageSyncEntityPos payload, ClientPlayNetworking.Context context) {
        Level level = context.player().level();
        Entity entity = level.getEntity(payload.entityId());
        if (entity instanceof IFalconry ||
                entity instanceof EntityStraddleboard) {
            entity.setPos(payload.x(), payload.y(), payload.z());
            entity.teleportTo(payload.x(), payload.y(), payload.z());
        }
    }

    public static void handleStartDancing(MessageStartDancing payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.entityID());
            if (entity instanceof IDancingMob dancingMob) {
                dancingMob.setDancing(payload.dance());
                if (payload.dance()) {
                    dancingMob.setJukeboxPos(payload.jukeBox());
                } else {
                    dancingMob.setJukeboxPos(null);
                }
            }
        }
    }

    public static void handleTarantulaHawkSting(MessageTarantulaHawkSting payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.hawk());
            Entity spider = player.level().getEntity(payload.spider());
            if (entity instanceof EntityTarantulaHawk && spider instanceof LivingEntity livingSpider
                    && livingSpider.getType().builtInRegistryHolder().is(net.minecraft.tags.EntityTypeTags.ARTHROPOD)) {
                livingSpider.addEffect(new MobEffectInstance(net.minecraft.core.Holder.direct(AMEffectRegistry.DEBILITATING_STING),
                        EntityTarantulaHawk.STING_DURATION));
            }
        }
    }

    public static void handleMosquitoMountPlayer(MessageMosquitoMountPlayer payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.rider());
            Entity mountEntity = player.level().getEntity(payload.mount());
            if ((entity instanceof EntityCrimsonMosquito || entity instanceof EntityEnderiophage
                    || entity instanceof EntityBaldEagle)
                    && mountEntity instanceof Player && entity.distanceTo(mountEntity) < 16D) {
                entity.startRiding(mountEntity, true, true);
                if (entity instanceof EntityCrimsonMosquito mosquito) {
                    mosquito.setLatchedEntityId(mountEntity.getId());
                }
            }
        }
    }

    public static void handleCrowMountPlayer(MessageCrowMountPlayer payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.rider());
            Entity mountEntity = player.level().getEntity(payload.mount());
            if (entity instanceof EntityCrow crow && mountEntity instanceof Player mountPlayer
                    && entity.distanceTo(mountEntity) < 16D
                    && crow.getRemountCooldown() <= 0
                    && !mountPlayer.isShiftKeyDown()
                    && !mountPlayer.isCrouching()
                    && !mountPlayer.isInWater()) {
                entity.startRiding(mountEntity, true, true);
            }
        }
    }

    public static void handleCrowDismount(MessageCrowDismount payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.rider());
            Entity mountEntity = player.level().getEntity(payload.mount());
            if (entity instanceof EntityCrow crow && mountEntity != null) {
                crow.stopRiding();
                crow.setFlying(true);
            }
        }
    }

    public static void handleMungusBiomeChange(MessageMungusBiomeChange payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.mungusID());
            ResourceKey<Biome> biomeKey = ResourceKey.create(Registries.BIOME, Identifier.parse(payload.biomeOption()));
            Holder.Reference<Biome> holder = player.level().registryAccess().lookupOrThrow(Registries.BIOME).get(biomeKey).orElse(null);
            Biome biome = holder == null ? null : holder.value();
            if (AMConfig.mungusBiomeTransformationType == 2) {
                if (entity instanceof EntityMungus && entity.distanceToSqr(payload.posX(), entity.getY(), payload.posZ()) < 1000 && biome != null) {
                    LevelChunk chunk = player.level().getChunkAt(new BlockPos(payload.posX(), 0, payload.posZ()));
                    Level chunkLevel = chunk.getLevel();
                    int i = QuartPos.fromBlock(chunkLevel.getMinY());
                    int k = QuartPos.fromBlock(chunkLevel.getMaxY() - 1);
                    int l = Mth.clamp(QuartPos.fromBlock((int) entity.getY()), i, k);
                    int j = chunk.getSectionIndex(QuartPos.toBlock(l));
                    LevelChunkSection section = chunk.getSection(j);
                    if (section != null) {
                        PalettedContainer<Holder<Biome>> container = section.getBiomes().recreate();
                        for (int biomeX = 0; biomeX < 4; ++biomeX) {
                            for (int biomeY = 0; biomeY < 4; ++biomeY) {
                                for (int biomeZ = 0; biomeZ < 4; ++biomeZ) {
                                    container.getAndSetUnchecked(biomeX, biomeY, biomeZ, holder);
                                }
                            }
                        }
                    }
                    AlexsMobs.PROXY.updateBiomeVisuals(payload.posX(), payload.posZ());
                }
            }
        }
    }

    public static void handleUpdateCapsid(MessageUpdateCapsid payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            BlockPos pos = BlockPos.of(payload.blockPos());
            if (player.level().getBlockEntity(pos) instanceof TileEntityCapsid podium) {
                podium.setItem(0, payload.heldStack());
            }
        }
    }

    public static void handleKangarooInventorySync(MessageKangarooInventorySync payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.kangaroo());
            if (entity instanceof EntityKangaroo kangaroo && kangaroo.kangarooInventory != null) {
                if (payload.slotId() >= 0) {
                    kangaroo.kangarooInventory.setItem(payload.slotId(), payload.stack());
                }
            }
        }
    }

    public static void handleKangarooEat(MessageKangarooEat payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.kangaroo());
            if (entity instanceof EntityKangaroo kangaroo && kangaroo.kangarooInventory != null && !payload.stack().isEmpty()) {
                for (int i = 0; i < 7; i++) {
                    double d2 = kangaroo.getRandom().nextGaussian() * 0.02D;
                    double d0 = kangaroo.getRandom().nextGaussian() * 0.02D;
                    double d1 = kangaroo.getRandom().nextGaussian() * 0.02D;
                    entity.level().addParticle(
                            new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(payload.stack())),
                            entity.getX() + (double) (kangaroo.getRandom().nextFloat() * entity.getBbWidth()) - (double) entity.getBbWidth() * 0.5F,
                            entity.getY() + entity.getBbHeight() * 0.5F + (double) (kangaroo.getRandom().nextFloat() * entity.getBbHeight() * 0.5F),
                            entity.getZ() + (double) (kangaroo.getRandom().nextFloat() * entity.getBbWidth()) - (double) entity.getBbWidth() * 0.5F,
                            d0, d1, d2
                    );
                }
            }
        }
    }

    public static void handleSendVisualFlagFromServer(MessageSendVisualFlagFromServer payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.entityID());
            AlexsMobs.PROXY.processVisualFlag(entity, payload.flag());
        }
    }

    public static void handleSetPupfishChunkOnClient(MessageSetPupfishChunkOnClient payload, ClientPlayNetworking.Context context) {
        AlexsMobs.PROXY.setPupfishChunkForItem(payload.chunkX(), payload.chunkZ());
    }

    public static void handleUpdateTransmutablesToDisplay(MessageUpdateTransmutablesToDisplay payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player.getId() == payload.playerId()) {
            AlexsMobs.PROXY.setDisplayTransmuteResult(0, payload.stack1());
            AlexsMobs.PROXY.setDisplayTransmuteResult(1, payload.stack2());
            AlexsMobs.PROXY.setDisplayTransmuteResult(2, payload.stack3());
        }
    }

    public static void handleSyncEntityData(MessageSyncEntityData message, ClientPlayNetworking.Context context) {
        if (Minecraft.getInstance().level != null) {
            Entity entity = Minecraft.getInstance().level.getEntity(message.entityId());
            if (entity instanceof LivingEntity living) {
                CitadelEntityData.setCitadelTag(living, message.data());
            }
        }
    }

    public static void handleMosquitoDismount(MessageMosquitoDismount payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity entity = player.level().getEntity(payload.rider());
            Entity mountEntity = player.level().getEntity(payload.mount());
            if ((entity instanceof EntityCrimsonMosquito || entity instanceof EntityBaldEagle
                    || entity instanceof EntityEnderiophage) && mountEntity != null) {
                entity.stopRiding();
                if (entity instanceof EntityCrimsonMosquito mosquito) {
                    mosquito.setLatchedEntityId(-1);
                }
            }
        }
    }

    public static void handleHurtMultipart(MessageHurtMultipart payload, ClientPlayNetworking.Context context) {
        Player player = context.player();
        if (player != null && player.level() != null) {
            Entity part = player.level().getEntity(payload.part());
            Entity parent = player.level().getEntity(payload.parent());

            if (part instanceof IHurtableMultipart && parent instanceof LivingEntity) {
                ((IHurtableMultipart) part).onAttackedFromServer((LivingEntity) parent, payload.damage(), null);
            }

            if (payload.damageType() != null && !payload.damageType().isEmpty()) {
                ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.parse(payload.damageType()));
                player.level().registryAccess().lookup(Registries.DAMAGE_TYPE).flatMap(reg -> reg.get(key)).ifPresent(holder -> {
                    DamageSource source = new DamageSource(holder);
                    if (part == null && parent instanceof LivingEntity) {
                        parent.hurt(source, payload.damage());
                    }
                });
            }
        }
    }

    public static void handleInteractMultipart(MessageInteractMultipart payload, ClientPlayNetworking.Context context) {
        var player = context.player();
        if (player != null && player.level() != null) {
            Entity parent = player.level().getEntity(payload.parent());
            if (parent != null && player.distanceTo(parent) < 20 && parent instanceof Mob) {
                player.interactOn(parent, payload.offhand() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, Vec3.ZERO);
            }
        }
    }
}