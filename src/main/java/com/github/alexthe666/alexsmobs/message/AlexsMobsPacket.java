package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/** Fabric 1.20.1: multiplexed channel wrapper (1:1 with 1.21.1 {@link AlexsMobs.AlexsMobsPayload}). */
public record AlexsMobsPacket(byte[] data) implements FabricPacket {

    public static final PacketType<AlexsMobsPacket> TYPE = PacketType.create(
            new ResourceLocation(AlexsMobs.MODID, "main_channel"),
            AlexsMobsPacket::new
    );

    public AlexsMobsPacket(FriendlyByteBuf buf) {
        this(buf.readByteArray());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeByteArray(data);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
