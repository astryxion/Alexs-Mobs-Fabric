package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import com.github.alexthe666.alexsmobs.misc.AMChunkBiomeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import java.util.function.Supplier;

public class MessageMungusBiomeChange {

    public int mungusID;
    public int posX;
    public int posZ;
    public String biomeOption;

    public MessageMungusBiomeChange(int mungusID, int posX, int posY, String biomeOption) {
        this.mungusID = mungusID;
        this.posX = posX;
        this.posZ = posY;
        this.biomeOption = biomeOption;
    }

    public MessageMungusBiomeChange() {
    }

    public static MessageMungusBiomeChange read(FriendlyByteBuf buf) {
        return new MessageMungusBiomeChange(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUtf());
    }

    public static void write(MessageMungusBiomeChange message, FriendlyByteBuf buf) {
        buf.writeInt(message.mungusID);
        buf.writeInt(message.posX);
        buf.writeInt(message.posZ);
        buf.writeUtf(message.biomeOption);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageMungusBiomeChange message, Supplier<AlexsMobs.PacketContext> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (context.get().isClient()) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }

                if (player != null) {
                    if (player.level() != null) {
                        Entity entity = player.level().getEntity(message.mungusID);
                        Registry<Biome> registry = player.level().registryAccess().registryOrThrow(Registries.BIOME);
                        Holder<Biome> holder = registry.getHolder(ResourceKey.create(Registries.BIOME, new ResourceLocation(message.biomeOption))).orElse(null);
                        if (AMConfig.mungusBiomeTransformationType == 2 && !AMConfig.shadersCompat) {
                            if (entity instanceof EntityMungus && entity.distanceToSqr(message.posX, entity.getY(), message.posZ) < 1000 && holder != null) {
                                try {
                                    LevelChunk chunk = player.level().getChunkAt(new BlockPos(message.posX, 0, message.posZ));
                                    if (chunk == null) {
                                        return;
                                    }
                                    int i = QuartPos.fromBlock(chunk.getMinBuildHeight());
                                    int k = i + QuartPos.fromBlock(chunk.getHeight()) - 1;
                                    int l = Mth.clamp(QuartPos.fromBlock((int)entity.getY()), i, k);
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
                                        AMChunkBiomeUtil.setSectionBiomes(section, container);
                                    }
                                    AlexsMobs.PROXY.updateBiomeVisuals(message.posX, message.posZ);
                                } catch (Throwable t) {
                                    AlexsMobs.LOGGER.warn("Mungus biome visual update failed (shader compat?)", t);
                                }
                            }
                        }
                    }
                }
            });

        }
    }

}
