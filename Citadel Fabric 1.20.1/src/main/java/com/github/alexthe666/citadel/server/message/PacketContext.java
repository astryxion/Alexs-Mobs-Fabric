package com.github.alexthe666.citadel.server.message;

import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public interface PacketContext {
    void setPacketHandled(boolean handled);
    void enqueueWork(Runnable runnable);
    PacketDirection getDirection();
    Player getSender();

    enum PacketDirection {
        PLAY_TO_SERVER(ReceptionSide.SERVER),
        PLAY_TO_CLIENT(ReceptionSide.CLIENT);
        private final ReceptionSide receptionSide;
        PacketDirection(ReceptionSide receptionSide) { this.receptionSide = receptionSide; }
        public ReceptionSide getReceptionSide() { return receptionSide; }
    }

    enum ReceptionSide {
        CLIENT,
        SERVER
    }

    static Supplier<PacketContext> wrap(PacketContext context) {
        return () -> context;
    }
}
